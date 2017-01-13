package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Hilo implements Runnable {

	private Page pages;
	private Properties properties = new Properties();
	private Socket socket;
	private Configuration config;
	private Connection connect;
	private Controller controller=new Controller();
	private boolean db;

	public Hilo(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.properties.setProperty("numClients", "50");
		this.properties.setProperty("port", "8888");
		this.properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.properties.setProperty("db.user", "hsdb");
		this.properties.setProperty("db.password", "hsdbpass");
		this.db=false;
	}

	public Hilo(Socket socket, Page pages) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.pages = pages;
		this.db=true;
	}

	public Hilo(Socket socket, Properties properties) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.properties = properties;
		try {
			this.connect = DriverManager.getConnection(this.properties.getProperty("db.url"),
					this.properties.getProperty("db.user"), this.properties.getProperty("db.password"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.db=true;
	}

	public Hilo(Socket socket, Configuration config) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.config = config;
		try {
			this.connect = DriverManager.getConnection(this.config.getDbURL(),
					this.config.getDbUser(), this.config.getDbPassword());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.db=true;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {

			// Responder al cliente
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			HTTPResponse response = new HTTPResponse();
			
			if(!db){
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
								else
									response.setContent(pages.getPage(request.getResourceParameters().get("uuid")));

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
			}else{
			
			response=this.controller.getResponse(request, connect, pages, config);
			
			}
			
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
			response.print(osw);
			osw.flush();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (HTTPParseException e) {
			e.printStackTrace();
		}

	}

}
