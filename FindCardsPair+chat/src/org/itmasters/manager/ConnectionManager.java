package org.itmasters.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
	private static Properties properties = PropertiesManager.getProperties();
	
	static {
		try {
			Class.forName(properties.getProperty("DRIVER"));
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(properties.getProperty("URL"), 
					properties.getProperty("USER"), 
					properties.getProperty("PASS"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void close(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
