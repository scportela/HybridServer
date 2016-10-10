package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;

public class HTTPResponse {
	private HTTPResponseStatus status;
	private String version;
	private String content;
	private Map<String, String> parameters = new LinkedHashMap<>();

	public HTTPResponse() {
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
		this.putParameter("Content-Length", String.valueOf(this.content.length()));
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		this.parameters.put(name, value);
		return this.parameters.get(name);
	}

	public boolean containsParameter(String name) {
		if (this.parameters.get(name) != null)
			return true;
		return false;
	}

	public String removeParameter(String name) {
		return this.parameters.remove(name);
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {
		List<String> listaParametros = new ArrayList<String>(parameters.values());
		return listaParametros;
	}

	public void print(Writer writer) throws IOException {
		writer.append(this.getVersion());
		writer.append(" ");
		writer.append(String.valueOf(this.getStatus().getCode()));
		writer.append(" ");
		writer.append(this.getStatus().getStatus());
		writer.append("\r\n");
		for (Map.Entry<String, String> map : this.getParameters().entrySet()) {
			writer.append(map.getKey() + ": " + map.getValue());
			writer.append("\r\n");
		}
		writer.append("\r\n");
		if (this.getContent() != null)
			writer.append(this.getContent());
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
