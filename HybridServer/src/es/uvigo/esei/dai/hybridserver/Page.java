package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;

public interface Page {
	public String getPage(String uuid) throws SQLException;
	public void createPage(String uuid,String content)throws SQLException;
	public boolean deletePage(String uuid)throws SQLException;
	public String listPages()throws SQLException;
	public String createLink(String uuid)throws SQLException;
	public boolean exists(String uuid)throws SQLException;
}
