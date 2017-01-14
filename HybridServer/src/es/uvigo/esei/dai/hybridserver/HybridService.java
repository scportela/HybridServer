package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HybridService {
	@WebMethod
	public String listPages(String type) throws SQLException;

	@WebMethod
	public String getPage(String type, String uuid) throws SQLException;
	
	@WebMethod
	public boolean exists(String type,String uuid) throws SQLException;

	@WebMethod
	public String getXSD(String uuid) throws SQLException;
}
