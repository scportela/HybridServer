package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import java.util.Map;
import java.util.UUID;


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
	public void createPage(String content) {
		// TODO Auto-generated method stub
		UUID randomUuid = UUID.randomUUID();
		String uuid = randomUuid.toString();

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
	public List<String> listPages() {
		// TODO Auto-generated method stub
		return (List<String>) this.db.keySet();
	}

}
