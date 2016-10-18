package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private Server pages;

	public HybridServer() {
		// TODO Auto-generated constructor stub
	}

	public HybridServer(Map<String, String> pages) {
		// TODO Auto-generated constructor stub
		this.pages = new Server(pages);
	}

	public HybridServer(Properties properties) {
		// TODO Auto-generated constructor stub
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
							if(request.getMethod()==HTTPRequestMethod.POST){
								if(request.getResourceParameters().get("html")==null) 
									response.setStatus(HTTPResponseStatus.S400);
								pages.createPage(request.getContent());
							}
							if (request.getMethod() == HTTPRequestMethod.GET) {
								String content = pages.getPage(request.getResourceParameters().get("uuid"));
								response.setStatus(HTTPResponseStatus.S200);
								response.setVersion(request.getHttpVersion());
								response.setContent(content);
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
