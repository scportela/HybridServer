package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public interface Page {
	public String getPage(String uuid) throws SQLException;
	public void createPage(String uuid,HTTPRequest request)throws SQLException;
	public boolean deletePage(String uuid)throws SQLException;
	public String listPages()throws SQLException;
	public String createLink(String uuid)throws SQLException;
	public boolean exists(String uuid)throws SQLException;
}
