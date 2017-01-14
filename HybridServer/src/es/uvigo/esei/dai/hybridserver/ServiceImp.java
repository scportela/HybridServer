package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.jws.WebService;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridService", serviceName = "HybridServerService")
public class ServiceImp implements HybridService {

	private Configuration config;
	private Connection connect;

	public ServiceImp(Configuration config) {
		this.config = config;
		this.connect=null;
	}

	@Override
	public String listPages(String type) throws SQLException {
		// TODO Auto-generated method stub
		String query = null;
		switch (type) {
		case "html":
			query = "SELECT uuid FROM HTML";
			break;
		case "xml":
			query = "SELECT uuid FROM XML";
			break;
		case "xsd":
			query = "SELECT uuid FROM XSD";
			break;
		case "xslt":
			query = "SELECT uuid FROM XSLT";
			break;
		}

		StringBuilder list = new StringBuilder();
		this.connect = DriverManager.getConnection(this.config.getDbURL(), this.config.getDbUser(),
				this.config.getDbPassword());
		try (PreparedStatement statement = connect.prepareStatement(query)) {

			ResultSet res = statement.executeQuery();

			while (res.next()) {
				list.append("<a href=\"" + type + "?uuid=" + res.getString("uuid") + "\">" + res.getString("uuid")
						+ "</a><br/>");
			}

		}

		return list.toString();
	}

	@Override
	public String getPage(String type, String uuid) throws SQLException {
		// TODO Auto-generated method stub
		String query = null;
		switch (type) {
		case "html":
			query = "SELECT * FROM HTML WHERE uuid=?";
			break;
		case "xml":
			query = "SELECT * FROM XML WHERE uuid=?";
			break;
		case "xsd":
			query = "SELECT * FROM XSD WHERE uuid=?";
			break;
		case "xslt":
			query = "SELECT * FROM XSLT WHERE uuid=?";
			break;
		}

		this.connect = DriverManager.getConnection(this.config.getDbURL(), this.config.getDbUser(),
				this.config.getDbPassword());
		try (PreparedStatement statement = connect.prepareStatement(query)) {

			statement.setString(1, uuid);
			ResultSet res = statement.executeQuery();
			if (res.next())
				return res.getString("content");

		}
		return null;
	}

	@Override
	public String getXSD(String uuid) throws SQLException {
		// TODO Auto-generated method stub
		String xsd = null;
		this.connect = DriverManager.getConnection(this.config.getDbURL(), this.config.getDbUser(),
				this.config.getDbPassword());
		try (PreparedStatement statement = connect.prepareStatement("SELECT xsd FROM XSLT WHERE uuid=?;")) {
			statement.setString(1, uuid);
			ResultSet res = statement.executeQuery();
			if (res.next())
				xsd = res.getString("xsd");
		}
		return xsd;
	}

	@Override
	public boolean exists(String type,String uuid) throws SQLException {
		// TODO Auto-generated method stub
		String query = null;
		this.connect = DriverManager.getConnection(this.config.getDbURL(), this.config.getDbUser(),
				this.config.getDbPassword());
		switch (type) {
		case "html":
			query = "SELECT count(content) as c FROM HTML WHERE uuid=? GROUP BY uuid;";
			break;
		case "xml":
			query ="SELECT count(content) as c FROM XML WHERE uuid=? GROUP BY uuid;";
			break;
		case "xsd":
			query ="SELECT count(content) as c FROM XSD WHERE uuid=? GROUP BY uuid;";
			break;
		case "xslt":
			query ="SELECT count(content) as c FROM XSLT WHERE uuid=? GROUP BY uuid;";
			break;
		}
		
		try(PreparedStatement statement=connect.prepareStatement(query)){
			int cont=0;
			statement.setString(1, uuid);
			ResultSet res=statement.executeQuery();
			if(res.next()) cont = Integer.parseInt(res.getString("c"));
			if(cont==1)return true;
		
		}
		return false;
	}

}
