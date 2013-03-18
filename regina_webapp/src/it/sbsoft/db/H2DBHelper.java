package it.sbsoft.db;

import it.sbsoft.utility.LoggerUtils;
import java.sql.Connection;
import org.apache.log4j.Logger;

public class H2DBHelper {
	
	private Connection con = null;
    static Logger log = LoggerUtils.getLogger("sbsoftware");
    static Logger logDB = LoggerUtils.getLogger("db");
    // Parameters for in-memory
    private String url;
    private String usr;
    private String pwd;
	
	public H2DBHelper() {
		try {
			org.h2.Driver.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initConn() {
		url = "jdbc:h2:mem:sb";
		usr = "sa";
		pwd = "";
		prepareConn();
	}
	
	public void initConn(String _url, String _usr, String _pwd) {
		url = _url;
		usr = _usr;
		pwd = _pwd;
		prepareConn();
	}
	
	public void initConn(Connection _con) {
		con = _con;
	}
	
	public Connection getConn() {
		return con;
	}
	
	private void prepareConn() {
		
		try {
			
			logDB.debug(" using -> url=" + url + ", usr=" + usr + ", pwd="+ pwd );
			con = java.sql.DriverManager.getConnection (url, usr, pwd);
		  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeConn() {
		try {
			if (!con.isClosed()) con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
