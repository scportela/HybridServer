package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class HTTPRequest {
	
	private String resourceChain;
	private String[] resourcePath;
	private String resourceName;
	private Map<String, String> resourceParameters;
	private String httpVersion;
	private Map<String, String> headerParameters;
	private String content;
	

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		int car;
		while((car=reader.read())!=-1){
			
		}
	}

	public HTTPRequestMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
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
		return content.length();
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
