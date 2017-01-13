/**
 *  HybridServer
 *  Copyright (C) 2014 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfigurationLoader {
	
	private String configuracionXSD = "configuration.xsd";
	
	public Document validar(String document, String schemaxsd) throws SAXException, ParserConfigurationException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		Schema schema = factory.newSchema(new File(schemaxsd));

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setValidating(false);
		docFactory.setNamespaceAware(true);
		docFactory.setSchema(schema);

		DocumentBuilder doc = docFactory.newDocumentBuilder();
		doc.setErrorHandler(new SimpleErrorHandler());

		return doc.parse(document);

	}
	
	public Configuration load(File xmlFile)
	throws Exception {
		// Implementar en la semana 9.
		
		Configuration config = new Configuration();
		Document doc = validar(xmlFile.getAbsolutePath(),configuracionXSD);
		
		int httpPort=Integer.parseInt(doc.getElementsByTagName("http").item(0).getTextContent().trim());
		String url= doc.getElementsByTagName("webservice").item(0).getTextContent().trim();
		int clients = Integer.parseInt(doc.getElementsByTagName("numClients").item(0).getTextContent().trim());
		String user = doc.getElementsByTagName("user").item(0).getTextContent().trim();
		String password = doc.getElementsByTagName("password").item(0).getTextContent().trim();
		String dburl = doc.getElementsByTagName("url").item(0).getTextContent().trim();
		
		List<ServerConfiguration> servers = new ArrayList<>();
		NodeList serversList = doc.getElementsByTagName("server");
		
		for (int i = 0; i < serversList.getLength(); i++){
			Element item = (Element) serversList.item(i);
			servers.add(new ServerConfiguration(item.getAttribute("name"),
												item.getAttribute("wsdl"),
												item.getAttribute("namespace"),
												item.getAttribute("service"),
												item.getAttribute("httpAddress")));
		}
		
		config.setDbPassword(password);
		config.setDbURL(dburl);
		config.setDbUser(user);
		config.setHttpPort(httpPort);
		config.setNumClients(clients);
		config.setServers(servers);
		config.setWebServiceURL(url);
		
		return config;
		
	}
	
	public String transformXSLT(File xml, File xslt) throws TransformerFactoryConfigurationError, TransformerException {
		
		Transformer transform = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));

		StringWriter writer = new StringWriter();
		transform.transform(new StreamSource(xml), new StreamResult(writer));

		return writer.toString();
	}
}
