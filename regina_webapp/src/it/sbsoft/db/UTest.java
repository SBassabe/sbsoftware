package it.sbsoft.db;

import it.sbsoft.utility.LoggerUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UTest {
	
	public static void main(String[] args) {
		
		// Set property for both: propfile and logger
		System.setProperty("catalina.home","C:\\Program Files\\apache-tomcat-7.0.14");
		
		//Initialize logger
		String file = System.getProperty("catalina.home") + "\\conf\\log4j_regina.properties";
		LoggerUtils.initLogger(file);

		// Set helper object
		Connection con;
		
		try {
			
			// Test FireBird
			FBDBHelper fbh = new FBDBHelper();
			con = fbh.getCBADATIIBConn();
			PreparedStatement ps=  con.prepareStatement("SELECT COUNT(*) FROM GELETTI");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ()) {
				System.out.println(rs.getInt(1));
			}			
			
			// Test H2
			H2DBHelper h2dbh = new H2DBHelper();
			String url = "jdbc:h2:file:" + System.getProperty("catalina.home") + "\\h2db\\localdb";
			String usr = "sa";
			String pwd = "";
			h2dbh.initConn(url, usr, pwd);
			con = h2dbh.getConn();
			
			// Create Table
			String sql = "CREATE TABLE SBTEST(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '')";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			
			// Read Table			
			ps = con.prepareStatement("SELECT COUNT(*) FROM SBTEST");
			rs = ps.executeQuery();
			
			while (rs.next ()) {
				System.out.println(rs.getInt(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con = null;
		}
	}
   
}
