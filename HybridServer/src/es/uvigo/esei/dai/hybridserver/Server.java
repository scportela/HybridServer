package es.uvigo.esei.dai.hybridserver;

import java.util.Map;
import java.util.Set;


public class Server implements Page {
	private Map<String, String> db;

	public Server(Map<String, String> pages) {
		this.db = pages;
	}

	@Override
	public String getPage(String uuid) {
		// TODO Auto-generated method stub
		return this.db.get(uuid);
	}

	@Override
	public void createPage(String uuid,String content) {
		// TODO Auto-generated method stub

		this.db.put(uuid, content);
	}

	@Override
	public boolean deletePage(String uuid) {
		// TODO Auto-generated method stub
		if (this.db.remove(uuid) != null)
			return true;
		return false;
	}

	@Override
	public String listPages() {
		// TODO Auto-generated method stub
		StringBuilder list = new StringBuilder();
		for (String page : this.db.keySet()) {
			list.append(createLink(page));
		}
		return this.db.keySet().toString();
	}

	@Override
	public String createLink(String uuid) {
		// TODO Auto-generated method stub
		StringBuilder link = new StringBuilder();
		link.append("<a href=\"html?uuid=");
		link.append(uuid);
		link.append("\">");
		link.append(uuid);
		link.append("</a>\r\n");
		return link.toString();
	}

	@Override
	public boolean exists(String uuid) {
		// TODO Auto-generated method stub
		return this.db.containsKey(uuid);
	}

}
