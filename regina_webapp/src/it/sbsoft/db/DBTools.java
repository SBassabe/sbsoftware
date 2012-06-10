package it.sbsoft.db;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.utility.tools;

public class DBTools {
	
	private static String schemaCBADATIIB = "C:/FBDB/CBADATIIB.FDB";
	private static String schemaCBAREGINA = "C:/FBDB/CBAOSPITIB.FDB";
	private static String db = "localhost/3050";		  
                          //db = "192.168.0.14/3050:C:/FBDB/CBAOSPITIB.FDB";

    //jdbc:subprotocol:subname
    //private static String databaseURL = "jdbc:firebirdsql:"+db;
    private static String user = "sysdba";
    private static String password = "masterkey";
    StringBuffer sbQuery = new StringBuffer();
    static Logger log = LoggerUtils.getLogger("sbsoftware");
	
	public Map<String, String> getOcc4FloorByDate(String bedRange, String dt) {
		
		log.info("called with params dt/bedRange ->" + dt+"/...");
		log.debug("bedRange ->" + bedRange);
		
		Map<String, String> mp = new HashMap<String, String>();
		String SESSO, STANZA, COD_LETTO, value="", key="";
		
		try {
			
//          CODSTAN ;	NUMSTANZA ;	CODLETTO ; IDSEDE ;	X ;	Y
//			CBAREGINA
//			SELECT a.SESSO, a.REPARTO, a.STANZA, a.CODICE_LETTO, a.DESCR
//			FROM REGINA_LOGISTICA_V a

			Timestamp tstamp = new Timestamp(000000);
			Connection c = null;
			  
			  Class.forName ("org.firebirdsql.jdbc.FBDriver");
			  String databaseURL = "jdbc:firebirdsql:" + db + ":" + schemaCBAREGINA;
			  log.debug(" databaseURL -> " + databaseURL);
		      c = java.sql.DriverManager.getConnection (databaseURL, user, password);
		      
		      sbQuery.append("SELECT sesso, stanza, codice_letto ");
		      sbQuery.append("FROM REGINA_LOGISTICA_V ");
		      sbQuery.append("WHERE ");
		      sbQuery.append("gmadal <= ? AND (gmaal is null or (gmaal >= ?))");
		      sbQuery.append("AND codice_letto IN ( "  + bedRange + " ) ");
		      
		      System.out.println(sbQuery.toString());
      
		      
		      log.debug(" query -> " + sbQuery.toString());
			  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
			  pstmt.setTimestamp(1,tstamp.valueOf(dt));
			  pstmt.setTimestamp(2,tstamp.valueOf(dt));
			  //pstmt.setInt(2, iBed);
			  //pstmt.setArray(2, iBed);

			  ResultSet rs = pstmt.executeQuery();
			  
			  while (rs.next ()) {

	         	  SESSO = rs.getString ("sesso");        	  
	        	  STANZA = rs.getString ("stanza");
	        	  COD_LETTO = rs.getString ("codice_letto");
	        	  
	        	  value = SESSO+";"+STANZA;
	        	  key = COD_LETTO;
	        	  System.out.println(SESSO+";"+STANZA+";"+COD_LETTO);
	        	  
	        	  mp.put(key, value);
	          }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mp;
	}
	
	public Map<String, String> getBeds4Floor(int bedFrom, int bedTo) {
		
		log.info(" called ... with values bedFrom/bedTo -> " + bedFrom+"/"+bedTo);
		Map<String, String> retMap = new HashMap<String, String>();
		
		String CODSTAN, NUMSTANZA, CODLETTO, tmp;
		int numBeds = 0;
		
		try {
						
//          CBADATIIB
//			SELECT a.CODLETTO, b.CODSTAN, b.NUMSTANZA
//			FROM GELETTI a, GESTANZE b
//			where a.codstan = b.codstan
//			     and b.numstanza between 100 and 200
//			order by 1			
			
		  Connection c = null;
		  
		  Class.forName ("org.firebirdsql.jdbc.FBDriver");
		  String databaseURL = "jdbc:firebirdsql:" + db + ":" + schemaCBADATIIB;
		  log.debug(" databaseURL -> " + databaseURL);
	      c = java.sql.DriverManager.getConnection (databaseURL, user, password);
	      
	      sbQuery.append("SELECT a.CODLETTO, b.CODSTAN, b.NUMSTANZA ");
	      sbQuery.append("FROM GELETTI a, GESTANZE b ");
	      sbQuery.append("where a.codstan = b.codstan ");
	      sbQuery.append("and b.numstanza between cast(? as integer) and cast(? as integer) ");
	      sbQuery.append("order by 1, 2 ");
	      //sbQuery.append("and b.numstanza between ? and ?");
		  
	      log.debug(" query -> " + sbQuery.toString());
		  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
		  //pstmt.setTimestamp(1,tstamp.valueOf("2012-03-23 00:00:00.00"));
		  pstmt.setString(1, bedFrom+"");
		  pstmt.setString(2, bedTo+"");


		  ResultSet rs = pstmt.executeQuery();
		  
          while (rs.next ()) {

         	  CODLETTO = rs.getString ("CODLETTO");        	  
        	  NUMSTANZA = rs.getString ("NUMSTANZA");
        	  CODSTAN = rs.getString ("CODSTAN");
        	          	  
        	  numBeds++;
        	  tmp = CODSTAN  + ";" + NUMSTANZA;
        	  log.trace(" CODLETTO;CODSTAN;NUMSTANZA -> "  + CODLETTO+";"+tmp);
        	  retMap.put(CODLETTO, tmp);
          }
          
          c=null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug(" Format ->  [Map<CODLETTO>, <CODSTAN ; NUMSTANZA>]");
		log.info(" total number of beds processed -> " + numBeds+"");
		//return sb.toString().substring(0, sb.length()-1);
		return retMap;
	}

}
