package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HybridServer {
	private int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private Page pages;
	private Properties properties = new Properties();
	private int tipo;
	private Configuration config= new Configuration();

	public HybridServer() {
		// TODO Auto-generated constructor stub
		this.pages = new ServerMap(new LinkedHashMap<>());
		this.properties.setProperty("numClients", "50");
		this.properties.setProperty("port", "8888");
		this.properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.properties.setProperty("db.user", "hsdb");
		this.properties.setProperty("db.password", "hsdbpass");
		this.config.setDbPassword(this.properties.getProperty("db.password"));
		this.config.setDbUser(this.properties.getProperty("db.user"));
		this.config.setDbURL(this.properties.getProperty("db.url"));
		this.config.setHttpPort(Integer.parseInt(this.properties.getProperty("port")));
		this.config.setNumClients(Integer.parseInt(this.properties.getProperty("numClients")));
		this.tipo=1;
	}

	public HybridServer(Map<String, String> pages) {
		// TODO Auto-generated constructor stub
		this.pages = new ServerMap(pages);
		this.config.setDbPassword("hsdbpass");
		this.config.setDbUser("hsdb");
		this.config.setDbURL("jdbc:mysql://localhost:3306/hstestdb");
		this.config.setHttpPort(8888);
		this.config.setNumClients(50);
		this.tipo=2;
	}

	public HybridServer(Properties properties) {
		// TODO Auto-generated constructor stub
		this.properties = properties;
		this.SERVICE_PORT = Integer.parseInt(this.properties.get("port").toString());
		this.config.setDbPassword(this.properties.getProperty("db.password"));
		this.config.setDbUser(this.properties.getProperty("db.user"));
		this.config.setDbURL(this.properties.getProperty("db.url"));
		this.config.setHttpPort(Integer.parseInt(this.properties.getProperty("port")));
		this.config.setNumClients(Integer.parseInt(this.properties.getProperty("numClients")));
		this.tipo=3;
	}
	
	public HybridServer(Configuration config) {
		// TODO Auto-generated constructor stub
		this.config=config;
		this.SERVICE_PORT=this.config.getHttpPort();
		this.tipo=4;
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					ExecutorService pool = Executors.newFixedThreadPool(config.getNumClients());
					while (true) {
						Socket socket = serverSocket.accept();
						if(stop) break;
						switch (tipo) {
							case 1:
								pool.execute(new Hilo(socket));
								break;
							case 2:
								pool.execute(new Hilo(socket,pages));
								break;
							case 3:
								pool.execute(new Hilo(socket,properties));
								break;
							case 4:
								pool.execute(new Hilo(socket,config));
								break;
						}
					}
				} catch (IOException e) {

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
