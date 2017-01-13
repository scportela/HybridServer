package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.SimpleErrorHandler;

public class Controller {

	public HTTPResponse getResponse(HTTPRequest request, Connection connect, Page pages, Configuration config) {

		HTTPResponse response = new HTTPResponse();
		if (connect != null) {
			switch (request.getResourceName()) {
			case "html":
				pages = new ServerDAO(connect);
				response.putParameter("Content-Type", "text/html");
				break;
			case "xml":
				pages = new ServerDAOxml(connect);
				response.putParameter("Content-Type", "application/xml");
				break;
			case "xsd":
				pages = new ServerDAOxsd(connect);
				response.putParameter("Content-Type", "application/xml");
				break;
			case "xslt":
				pages = new ServerDAOxslt(connect);
				response.putParameter("Content-Type", "application/xml");
				break;
			}
		}
		response.setStatus(HTTPResponseStatus.S200);
		response.setVersion(request.getHttpVersion());
		try {
			if (request.getMethod() == HTTPRequestMethod.POST) {
				UUID randomUuid = UUID.randomUUID();
				String uuid = randomUuid.toString();
				if (request.getResourceName().equals("xslt")) {
					if (request.getResourceParameters().get("xsd") == null) {
						response.setStatus(HTTPResponseStatus.S400);
					} else if (!((ServerDAOxslt) pages).existsXSD(request.getResourceParameters().get("xsd"))) {
						response.setStatus(HTTPResponseStatus.S404);
					}
				}
				if (response.getStatus() == HTTPResponseStatus.S200) {
					if (request.getResourceParameters().get(request.getResourceName()) == null)
						response.setStatus(HTTPResponseStatus.S400);
					else {
						pages.createPage(uuid, request);
						response.setContent(pages.createLink(uuid));
					}
				}
			}

			if (request.getMethod() == HTTPRequestMethod.GET) {

				if (!request.getResourceName().equals("html") && !request.getResourceName().equals("xml")
						&& !request.getResourceName().equals("xsd") && !request.getResourceName().equals("xslt")
						&& !request.getResourceChain().equals("/")) {
					response.setStatus(HTTPResponseStatus.S400);
				} else {
					if (request.getResourceChain().equals("/"))
						response.setContent("Hybrid Server");
					else if (request.getResourceChain().equals("/html") || request.getResourceChain().equals("/xml")
							|| request.getResourceChain().equals("/xsd")
							|| request.getResourceChain().equals("/xslt")) {
						response.setContent(pages.listPages());
					} else {
						if (!pages.exists(request.getResourceParameters().get("uuid")))
							response.setStatus(HTTPResponseStatus.S404);
						else {
							if (request.getResourceName().equals("xml")
									&& request.getResourceParameters().get("xslt") != null) {
								String xml = request.getResourceParameters().get("uuid");
								String xslt = request.getResourceParameters().get("xslt");
								ServerDAOxslt auxDAO = new ServerDAOxslt(connect);
								String xsd = auxDAO.getXSD(xslt);
								if (auxDAO.exists(xslt)) {
									try {
										if (validar(connect, xml, xsd) == null) {
											response.setStatus(HTTPResponseStatus.S400);
										} else {
											response.removeParameter("Content-Type");
											response.putParameter("Content-Type", "text/html");
											response.setContent(transformXSLT(connect, xml, xslt));
										}
									} catch (TransformerException e) {
										response.setStatus(HTTPResponseStatus.S400);
									} catch (IOException e) {
										response.setStatus(HTTPResponseStatus.S400);
									} catch (ParserConfigurationException e) {
										// TODO Auto-generated catch block
										response.setStatus(HTTPResponseStatus.S400);
									} catch (SAXException e) {
										// TODO Auto-generated catch block
										response.setStatus(HTTPResponseStatus.S400);
									} catch (TransformerFactoryConfigurationError e) {
										// TODO Auto-generated catch block
										response.setStatus(HTTPResponseStatus.S400);
									}
								} else {
									response.setStatus(HTTPResponseStatus.S404);
								}
							} else {
								response.setContent(pages.getPage(request.getResourceParameters().get("uuid")));

							}
						}
					}
				}
			}

			if (request.getMethod() == HTTPRequestMethod.DELETE) {
				if (!pages.exists(request.getResourceParameters().get("uuid")))
					response.setStatus(HTTPResponseStatus.S404);
				else
					pages.deletePage(request.getResourceParameters().get("uuid"));
			}
		} catch (SQLException e) {
			response.setStatus(HTTPResponseStatus.S500);
		}
		return response;
	}

	public Document validar(Connection connect, String xml, String xsd)
			throws ParserConfigurationException, SAXException, IOException, SQLException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		ServerDAOxml auxDAOXML = new ServerDAOxml(connect);
		ServerDAOxsd auxDAOXSD = new ServerDAOxsd(connect);

		BufferedReader readerXML = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(auxDAOXML.getPage(xml).getBytes())));
		BufferedReader readerXSD = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(auxDAOXSD.getPage(xsd).getBytes())));

		Source sourceXSD = new StreamSource(readerXSD);
		InputSource inputSourceXML = new InputSource(readerXML);
		Schema schema = factory.newSchema(sourceXSD);

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setValidating(false);
		docFactory.setNamespaceAware(true);
		docFactory.setSchema(schema);

		DocumentBuilder doc = docFactory.newDocumentBuilder();
		doc.setErrorHandler(new SimpleErrorHandler());

		return doc.parse(inputSourceXML);

	}

	public String transformXSLT(Connection connect, String xml, String xslt)
			throws SQLException, TransformerFactoryConfigurationError, TransformerException {
		ServerDAOxml auxDAOXML = new ServerDAOxml(connect);
		ServerDAOxslt auxDAOXSLT = new ServerDAOxslt(connect);

		BufferedReader readerXML = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(auxDAOXML.getPage(xml).getBytes())));
		BufferedReader readerXSLT = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(auxDAOXSLT.getPage(xslt).getBytes())));

		Transformer transform = TransformerFactory.newInstance().newTransformer(new StreamSource(readerXSLT));

		StringWriter writer = new StringWriter();
		transform.transform(new StreamSource(readerXML), new StreamResult(writer));

		return writer.toString();
	}
}
