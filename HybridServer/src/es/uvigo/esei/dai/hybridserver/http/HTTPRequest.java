package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private HTTPRequestMethod method;
	private String resourceChain;
	private String[] resourcePath = new String[0];
	private String resourceName;
	private Map<String, String> resourceParameters = new LinkedHashMap<>();
	private String httpVersion;
	private Map<String, String> headerParameters = new LinkedHashMap<>();
	private String content;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		String[] aux = line.split(" ");
		try {
			this.method = HTTPRequestMethod.valueOf(aux[0]);
		} catch (Exception e) {
			throw new HTTPParseException(e.getMessage());
		}
		if (aux.length < 2)
			throw new HTTPParseException("Missing resource");
		this.resourceChain = aux[1];
		if (aux.length < 3)
			throw new HTTPParseException("Missing version");
		this.httpVersion = aux[2];
		while (!(line = br.readLine()).isEmpty()) {
			aux = line.split(": ");
			if (aux.length < 2)
				throw new HTTPParseException("Invalid header");
			this.headerParameters.put(aux[0], aux[1]);
		}
		aux = this.resourceChain.split("\\?");
		this.resourceName = aux[0].substring(1);
		if (aux[0].length() > 1) {
			this.resourcePath = aux[0].substring(1).split("/");
		}

		if (aux.length > 1) {
			aux = aux[1].split("&");
			String[] aux1;
			for (int i = 0; i < aux.length; i++) {
				aux1 = aux[i].split("=");
				this.resourceParameters.put(aux1[0], aux1[1]);
			}
		}

		if (this.headerParameters.containsKey("Content-Length")) {
			char[] cont = new char[Integer.parseInt(this.headerParameters.get("Content-Length"))];
			br.read(cont);
			this.content = new String(cont);

			String type = this.headerParameters.get("Content-Type");
			if (type != null && type.startsWith("application/x-www-form-urlencoded")) {
				content = URLDecoder.decode(content, "UTF-8");
			}
			aux = content.split("&");
			String[] aux1;
			for (int i = 0; i < aux.length; i++) {
				aux1 = aux[i].split("=");
				this.resourceParameters.put(aux1[0], aux1[1]);
			}
		}

	}

	public HTTPRequestMethod getMethod() {
		// TODO Auto-generated method stub
		return method;
	}

	public String getResourceChain() {
		// TODO Auto-generated method stub
		return resourceChain;
	}

	public String[] getResourcePath() {
		// TODO Auto-generated method stub
		return resourcePath;
	}

	public String getResourceName() {
		// TODO Auto-generated method stub
		return resourceName;
	}

	public Map<String, String> getResourceParameters() {
		// TODO Auto-generated method stub
		return resourceParameters;
	}

	public String getHttpVersion() {
		// TODO Auto-generated method stub
		return httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		// TODO Auto-generated method stub
		return headerParameters;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return content;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		if (content == null)
			return 0;
		return Integer.parseInt(this.getHeaderParameters().get("Content-Length"));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
