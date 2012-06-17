package it.sbsoft.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import it.sbsoft.exceptions.*;
import it.sbsoft.utility.CodeEncodeString;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.utility.PropertiesFile;

public class DBTools {
	
    static Logger log = LoggerUtils.getLogger("sbsoftware");
    static Logger logDB = LoggerUtils.getLogger("db");
    static CodeEncodeString decode = CodeEncodeString.getInstance();
	private PropertiesFile propFile = PropertiesFile.getPropertiesFile();
    
	public Map<String, String> getOcc4FloorByDate(String bedRange, String dt) throws Exception {
		
		  log.info("called with params dt/bedRange ->" + dt+"/...");
		logDB.info("called with params dt/bedRange ->" + dt+"/...");
		log.debug("bedRange ->" + bedRange);
		
		Map<String, String> mp = new HashMap<String, String>();
		String SESSO, STANZA, COD_LETTO, NOME, value="", key="";
		int recNum=0;
		Connection c = null;
		
		try {
			
//            CODSTAN ;	NUMSTANZA ;	CODLETTO ; IDSEDE ;	X ;	Y
//			  CBAREGINA
//			  SELECT a.SESSO, a.REPARTO, a.STANZA, a.CODICE_LETTO, a.DESCR
//			  FROM REGINA_LOGISTICA_V a

			  Timestamp tstamp = new Timestamp(000000);
			  
			  Class.forName ("org.firebirdsql.jdbc.FBDriver");
			  //FIREDB.CBAREGINA=jdbc:firebirdsql:localhost/3050:C:/FBDB/CBAOSPITIB.FDB
			  String databaseURL = propFile.getPropertySB("FIREDB.CBAREGINA");
			  logDB.debug(" databaseURL -> " + databaseURL);
			  
			  // retreive and decode credentials
			  String cred = propFile.getPropertySB("FIREDB.CBAREGINA.credentials");
			  String user = cred.split(";")[0];
			  String password = cred.split(";")[1];
			  
			  user = decode.decrypt(user);
			  password = decode.decrypt(password);
			  //logDB.trace(" usingCredentials -> " + user +"/"+ password);
			  
		      c = java.sql.DriverManager.getConnection (databaseURL, user, password);
		      
		      StringBuffer sbQuery = new StringBuffer();
		      sbQuery.append("SELECT a.sesso,d.stanza,d.codice_letto,a.gmadim,d.gmainizioutili,d.gmafineutili,a.codospite,a.nomeospite,d.sede,d.reparto ");
		      sbQuery.append("FROM ospiti_a a ");
		      sbQuery.append("JOIN ospiti_d d on (a.codospite=d.codospite) ");
		      sbQuery.append("LEFT JOIN clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<= CAST('" + dt + "' AS DATE) and (m.gmaal is null or (m.gmaal >= CAST('" + dt + "' AS DATE)))) ");
		      //sbQuery.append("LEFT JOIN teanapers t on (t.progr=m.progmedico) ");
		      sbQuery.append("WHERE a.gmaing<=CAST('" + dt + "' AS DATE) ");
		      sbQuery.append("AND (a.gmadim is null or (a.gmadim>CAST('" + dt + "' AS DATE))) ");
		      sbQuery.append("AND d.gmainizioutili<=CAST('" + dt + "' AS DATE) ");
		      sbQuery.append("AND ((d.gmafineutili>CAST('" + dt + "' AS DATE)) or d.gmafineutili is null) ");
		      sbQuery.append("and d.codice_letto IN ("  + bedRange + ")" );
		      
		      logDB.trace(" params -> dt=" + dt + "  bedRange=" + bedRange);
		      logDB.debug(" sql (gmaal= "+ dt + ")-> " + sbQuery.toString());
      		      
			  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
			  ResultSet rs = pstmt.executeQuery();
			  
			  while (rs.next ()) {
				  
	         	  SESSO = rs.getString ("sesso");        	  
	        	  STANZA = rs.getString ("stanza");
	        	  COD_LETTO = rs.getString ("codice_letto");
	        	  NOME = rs.getString("nomeospite");
	        	  
	        	  value = SESSO+";"+STANZA+";"+COD_LETTO+";"+NOME;
	        	  key = COD_LETTO;
	        	  logDB.trace("[<key>COD_LETTO <value>SESSO;STANZA;COD_LETTO;NOME] -> "+ ++recNum + value);
	        	  
	        	  mp.put(key, value);
	          }

		} catch (Exception e) {
			if (e instanceof SQLException) {
				e.printStackTrace();
				log.info("DB_UNREACHABLE");
				logDB.info("DB_UNREACHABLE");
				throw new SBException("DB_UNREACHABLE");
			} else {
				throw e;
			}
		} finally {
			c=null;
		}
		log.info("Total records -> " + recNum);
		logDB.info("Total records -> " + recNum);
		return mp;
	}
	
	public Map<String, String> getBeds4Floor(int bedFrom, int bedTo) throws Exception {
		
		log.info(" called ... with values bedFrom/bedTo -> " + bedFrom+"/"+bedTo);
		logDB.info(" called ... with values bedFrom/bedTo -> " + bedFrom+"/"+bedTo);
		Map<String, String> retMap = new HashMap<String, String>();
		
		String CODSTAN, NUMSTANZA, CODLETTO, tmp;
		int numBeds = 0;
		Connection c = null;
		
		try {
						
//          CBADATIIB
//			SELECT a.CODLETTO, b.CODSTAN, b.NUMSTANZA
//			FROM GELETTI a, GESTANZE b
//			where a.codstan = b.codstan
//			     and b.numstanza between 100 and 200
//			order by 1			
		  
		  Class.forName ("org.firebirdsql.jdbc.FBDriver");
		  //FIREDB.CBADATIIB=jdbc:firebirdsql:localhost/3050:C:/FBDB/CBADATIIB.FDB
		  String databaseURL = propFile.getPropertySB("FIREDB.CBADATIIB");
		  
		  // retreive and decode credentials
		  String cred = propFile.getPropertySB("FIREDB.CBADATIIB.credentials");
		  String user = cred.split(";")[0];
		  String password = cred.split(";")[1];
		  
		  user = decode.decrypt(user);
		  password = decode.decrypt(password);
		  //logDB.trace(" usingCredentials -> " + user +"/"+ password);
		  
		  logDB.debug(" databaseURL -> " + databaseURL);
		  c = java.sql.DriverManager.getConnection (databaseURL, user, password);
	      
	      StringBuffer sbQuery = new StringBuffer();
	      sbQuery.append("SELECT a.CODLETTO, b.CODSTAN, b.NUMSTANZA ");
	      sbQuery.append("FROM GELETTI a, GESTANZE b ");
	      sbQuery.append("where a.codstan = b.codstan ");
	      sbQuery.append("and b.numstanza between cast(? as integer) and cast(? as integer) ");
	      sbQuery.append("order by 1, 2 ");
		  
	      logDB.debug(" query -> " + sbQuery.toString());
		  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
		  pstmt.setString(1, bedFrom+"");
		  pstmt.setString(2, bedTo+"");

		  ResultSet rs = pstmt.executeQuery();
		  
          while (rs.next ()) {

         	  CODLETTO = rs.getString ("CODLETTO");        	  
        	  NUMSTANZA = rs.getString ("NUMSTANZA");
        	  CODSTAN = rs.getString ("CODSTAN");
        	          	  
        	  numBeds++;
        	  tmp = CODSTAN  + ";" + NUMSTANZA;
        	  logDB.trace(" [Map<CODLETTO>, <CODSTAN ; NUMSTANZA>] -> "  + CODLETTO+";"+tmp);
        	  retMap.put(CODLETTO, tmp);
          }
			
		} catch (Exception e) {
			if (e instanceof SQLException) {
				e.printStackTrace();
				log.info("DB_UNREACHABLE");
				logDB.info("DB_UNREACHABLE");
				throw new SBException("DB_UNREACHABLE");
			} else {
				throw e;
			}
		} finally {
			c=null;
		}
		log.info(" total number of beds processed -> " + numBeds+"");
		logDB.info(" total number of beds processed -> " + numBeds+"");
		return retMap;
	}
}
