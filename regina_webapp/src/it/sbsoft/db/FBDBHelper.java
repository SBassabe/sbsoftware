package it.sbsoft.db;

import it.sbsoft.utility.CodeEncodeString;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.propfiles.PropertiesCommon;
import java.sql.Connection;
import org.apache.log4j.Logger;

public class FBDBHelper {
	
    static Logger log = LoggerUtils.getLogger("sbsoftware");
    static Logger logDB = LoggerUtils.getLogger("db");
    static CodeEncodeString decode = CodeEncodeString.getInstance();
	private PropertiesCommon propFile = PropertiesCommon.getPropertiesFile();
	
	public FBDBHelper() {
		try {
			Class.forName ("org.firebirdsql.jdbc.FBDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getCBAOSPITIBConn() {
		
		 //FIREDB.CBAREGINA=jdbc:firebirdsql:localhost/3050:C:/FBDB/CBAOSPITIB.FDB
		return getConn("FIREDB.CBAREGINA", "FIREDB.CBAREGINA.credentials");

	}
	
	public Connection getCBADATIIBConn() {
		
		 //FIREDB.CBADATIIB=jdbc:firebirdsql:localhost/3050:C:/FBDB/CBADATIIB.FDB
		return getConn("FIREDB.CBADATIIB", "FIREDB.CBADATIIB.credentials");
		
	}
	
	private Connection getConn(String propDbName, String propDbCred) {
		
		Connection con = null;
		
		try {
			  String databaseURL = propFile.getPropertySB(propDbName);
			  
			  // retreive and decode credentials
			  String cred = propFile.getPropertySB(propDbCred);
			  String user = cred.split(";")[0];
			  String password = cred.split(";")[1];
			  
			  user = decode.decrypt(user);
			  password = decode.decrypt(password);
			  //logDB.trace(" usingCredentials -> " + user +"/"+ password);
			  
			  logDB.debug(" databaseURL -> " + databaseURL);
			  con = java.sql.DriverManager.getConnection (databaseURL, user, password);
		  
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return con;
	}
}
