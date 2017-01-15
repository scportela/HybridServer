package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class ServerDAO implements Page {

	private Connection connect;

	public ServerDAO(Connection connect) {
		// TODO Auto-generated constructor stub
		this.connect = connect;
	}

	@Override
	public String getPage(String uuid)throws SQLException {
		// TODO Auto-generated method stub
		try(PreparedStatement statement=connect.prepareStatement("SELECT content FROM HTML WHERE uuid=?;")){
			
			statement.setString(1, uuid);
			ResultSet res=statement.executeQuery();
			if(res.next()) return res.getString("content");
		
		}
		return null;
	}

	@Override
	public void createPage(String uuid, HTTPRequest request) throws SQLException{
		// TODO Auto-generated method stub
		try(PreparedStatement statement=connect.prepareStatement("INSERT INTO HTML (uuid,content) VALUES(?,?);")){
			
			statement.setString(1, uuid);
			statement.setString(2, request.getResourceParameters().get(request.getResourceName()));
			statement.executeUpdate();
		}

	}

	@Override
	public boolean deletePage(String uuid) throws SQLException{
		// TODO Auto-generated method stub
		try(PreparedStatement statement=connect.prepareStatement("DELETE FROM HTML WHERE uuid=?")){
			
			statement.setString(1, uuid);
			int conf = statement.executeUpdate();
			if(conf==1)return true;
		
		}
		return false;
	}

	@Override
	public String listPages()throws SQLException {
		// TODO Auto-generated method stub
		StringBuilder list=new StringBuilder();
		try(PreparedStatement statement=connect.prepareStatement("SELECT uuid FROM HTML;")){
			
			ResultSet res=statement.executeQuery();
			while(res.next()){
				list.append(this.createLink(res.getString("uuid")));
			}
			return list.toString();
		
		}
	}

	@Override
	public String createLink(String uuid)throws SQLException {
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
	public boolean exists(String uuid) throws SQLException{
		// TODO Auto-generated method stub
		try(PreparedStatement statement=connect.prepareStatement("SELECT count(content) as c FROM HTML WHERE uuid=? GROUP BY uuid;")){
			int cont=0;
			statement.setString(1, uuid);
			ResultSet res=statement.executeQuery();
			if(res.next()) cont = Integer.parseInt(res.getString("c"));
			if(cont==1)return true;
		
		}
		return false;
	}

}
