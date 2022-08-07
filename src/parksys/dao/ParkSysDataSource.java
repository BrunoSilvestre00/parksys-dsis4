package parksys.dao;

import java.sql.*;

public class ParkSysDataSource {
	private static final String URL_TEMPLATE = "jdbc:mysql://localhost/%s?user=%s&password=%s&useTimezone=true&serverTimezone=GMT-3";
	
	private Connection getConnection(String dbName, String username, String password) throws SQLException {
		String url = String.format(URL_TEMPLATE, dbName, username, password);
		return DriverManager.getConnection(url);
	}

	public Connection getConnection() throws SQLException {
		//return getConnection("parksys_db", "root", "alexandre"); // Alexandre
		return getConnection("parksys_db", "bruno", "3006956"); // Bruno
	}
}
