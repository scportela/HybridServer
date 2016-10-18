package es.uvigo.esei.dai.hybridserver;

import java.util.List;

public interface Page {
	public String getPage(String uuid);
	public void createPage(String content);
	public boolean deletePage(String uuid);
	public List<String> listPages();
}
