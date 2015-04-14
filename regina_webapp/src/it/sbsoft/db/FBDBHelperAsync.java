package it.sbsoft.db;

import it.sbsoft.utility.CodeEncodeString;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.firebirdsql.pool.*;

public class FBDBHelperAsync {
	
	private static FBDBHelperAsync ref;
	private static FBConnectionPoolDataSource oPool;
	private static FBConnectionPoolDataSource dPool;
	
    static Logger log = LoggerUtils.getLogger("sbsoftware");
    static Logger logDB = LoggerUtils.getLogger("db");
    static CodeEncodeString decode = CodeEncodeString.getInstance();
	private PropertiesCommon propFile = PropertiesCommon.getPropertiesFile();
	
	public FBDBHelperAsync() {}
	
	private void initializePools() {
		
		logDB.info("no FDBHelperAsync object found, initializing pools ...");
		String cred, user, password, databaseURL;
		
		try {
			
			logDB.info("CBAOSPITI Pool");
			// retreive and decode credentials
			cred = propFile.getPropertySB("FIREDB.CBAREGINA.credentials");
			user = cred.split(";")[0];
			password = cred.split(";")[1];
			databaseURL = propFile.getPropertySB("FIREDB.CBAREGINA");
			databaseURL = databaseURL.substring(17, databaseURL.length());
			//databaseURL = "localhost/3050:C:/PCMigration_private/SBSoftware/sbsoftware/Archive/FBDB/CBAOSPITIB.FDB";
			
			user = decode.decrypt(user);
			password = decode.decrypt(password);
			
			logDB.info("USING -> cred="+cred+", user="+user+", password="+password+", databaseURL="+databaseURL);
			
			oPool = new FBConnectionPoolDataSource();
			oPool.setMaxPoolSize(5);
			oPool.setMinPoolSize(2);
			oPool.setMaxStatements(10);
			oPool.setMaxIdleTime(30 * 60 * 60);
			oPool.setDatabase(databaseURL);
			oPool.setUserName(user);
			oPool.setPassword(password);
			
			logDB.info("CBADATI Pool ...");
			// retreive and decode credentials
			cred = propFile.getPropertySB("FIREDB.CBADATIIB.credentials");
			user = cred.split(";")[0];
			password = cred.split(";")[1];
			databaseURL = propFile.getPropertySB("FIREDB.CBADATIIB");
			databaseURL = databaseURL.substring(17, databaseURL.length());
			//databaseURL = "localhost/3050:C:/PCMigration_private/SBSoftware/sbsoftware/Archive/FBDB/CBADATIIB.FDB";
			
			user = decode.decrypt(user);
			password = decode.decrypt(password);
			
			logDB.info("USING -> cred="+cred+", user="+user+", password="+password+", databaseURL="+databaseURL);
			
			dPool = new FBConnectionPoolDataSource();
			dPool.setMaxPoolSize(5);
			dPool.setMinPoolSize(2);
			dPool.setMaxStatements(10);
			dPool.setMaxIdleTime(30 * 60 * 60);
			dPool.setDatabase(databaseURL);
			dPool.setUserName(user);
			dPool.setPassword(password);
			
		} catch (SBException e) {
			e.printStackTrace();
		}
	
	}
	
	public static synchronized Connection getCBAOSPITIBConn() throws Exception {
		
		logDB.info(" obtain conn -> getCBAOSPITIBConn ...");
		if (ref == null) {
			ref = new FBDBHelperAsync();
			ref.initializePools();
		}
		return oPool.getPooledConnection().getConnection();	

	}
	
	public static synchronized Connection getCBADATIIBConn() throws Exception {
		
		logDB.info(" obtain conn -> getCBADATIIBConn ...");
		if (ref == null) {
			ref = new FBDBHelperAsync();
			ref.initializePools();
		}
		return dPool.getPooledConnection().getConnection();
		
	}
	
}
