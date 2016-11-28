package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

public class HybridServer {
	private int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private Page pages;
	private Properties properties = new Properties();

	public HybridServer() {
		// TODO Auto-generated constructor stub
		this.pages = new ServerMap(new LinkedHashMap<>());
		this.properties.setProperty("numClients", "50");
		this.properties.setProperty("port", "8888");
		this.properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.properties.setProperty("db.user", "hsdb");
		this.properties.setProperty("db.password", "hsdbpass");
	}

	public HybridServer(Map<String, String> pages) {
		// TODO Auto-generated constructor stub
		this.pages = new ServerMap(pages);
	}

	public HybridServer(Properties properties) {
		// TODO Auto-generated constructor stub
		this.properties = properties;
		this.SERVICE_PORT = Integer.parseInt(this.properties.get("port").toString());
		this.pages = new ServerDAO(properties);
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					while (true) {
						try (Socket socket = serverSocket.accept()) {
							if (stop)
								break;
							// Responder al cliente
							HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
							HTTPResponse response = new HTTPResponse();

							switch (request.getResourceName()) {
							case "html":
								pages = new ServerDAO(properties);
								response.putParameter("Content-Type", "text/html");
								break;
							case "xml":
								pages = new ServerDAOxml(properties);
								response.putParameter("Content-Type", "application/xml");
								break;
							case "xsd":
								pages = new ServerDAOxsd(properties);
								response.putParameter("Content-Type", "application/xml");
								break;
							case "xslt":
								pages = new ServerDAOxslt(properties);
								response.putParameter("Content-Type", "application/xml");
								break;
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
										} else if (!((ServerDAOxslt) pages)
												.existsXSD(request.getResourceParameters().get("xsd"))) {
											response.setStatus(HTTPResponseStatus.S404);
										}
									}
									if (response.getStatus() == HTTPResponseStatus.S200) {
										if (request.getResourceParameters().get(request.getResourceName()) == null)
											response.setStatus(HTTPResponseStatus.S400);
										else {
											System.out.println("CREATE");
											System.out.println(request.getContent());
											pages.createPage(uuid, request);
											response.setContent(pages.createLink(uuid));
											System.out.println(response);
										}
									}
								}

								if (request.getMethod() == HTTPRequestMethod.GET) {
									if (!request.getResourceName().equals("html")
											&& !request.getResourceName().equals("xml")
											&& !request.getResourceName().equals("xsd")
											&& !request.getResourceName().equals("xslt")
											&& !request.getResourceChain().equals("/")) {
										response.setStatus(HTTPResponseStatus.S400);
									} else {
										if (request.getResourceChain().equals("/"))
											response.setContent("Hybrid Server");
										else if (request.getResourceChain().equals("/html")
												|| request.getResourceChain().equals("/xml")
												|| request.getResourceChain().equals("/xsd")
												|| request.getResourceChain().equals("/xslt")) {
											response.setContent(pages.listPages());
										} else {
											if (!pages.exists(request.getResourceParameters().get("uuid")))
												response.setStatus(HTTPResponseStatus.S404);
											else
												response.setContent(
														pages.getPage(request.getResourceParameters().get("uuid")));

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
							OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
							response.print(osw);
							osw.flush();
						}
					}
				} catch (IOException e) {

					e.printStackTrace();
				} catch (HTTPParseException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();

	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo
			// servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
	}
}
