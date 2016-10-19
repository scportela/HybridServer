package es.uvigo.esei.dai.hybridserver;

public interface Page {
	public String getPage(String uuid);
	public void createPage(String uuid,String content);
	public boolean deletePage(String uuid);
	public String listPages();
	public String createLink(String uuid);
	public boolean exists(String uuid);
}
