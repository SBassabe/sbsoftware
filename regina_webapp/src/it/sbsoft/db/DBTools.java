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
    static Logger logDB = LoggerUtils.getLogger("db");
	
	public Map<String, String> getOcc4FloorByDate(String bedRange, String dt) {
		
		log.info("called with params dt/bedRange ->" + dt+"/...");
		log.debug("bedRange ->" + bedRange);
		
		Map<String, String> mp = new HashMap<String, String>();
		String SESSO, STANZA, COD_LETTO, value="", key="";
		int recNum=0;
		
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
		      
		      sbQuery.append("select a.codospite,a.nomeospite,a.sesso,d.sede,d.reparto,d.stanza,d.codice_letto,t.descr  ");
		      sbQuery.append("from ospiti_a a join ospiti_d d on (a.codospite=d.codospite)  ");
		      sbQuery.append("left join clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<=? and (m.gmaal is null or (m.gmaal>=?)))  ");
		      sbQuery.append("left join teanapers t on (t.progr=m.progmedico)  ");
		      sbQuery.append("where a.gmaing<=?  ");
		      sbQuery.append("and (a.gmadim is null or (a.gmadim>?))  ");
		      sbQuery.append("and d.gmainizioutili<=?  ");
		      sbQuery.append("and ((d.gmafineutili>?) or d.gmafineutili is null)  ");
		      sbQuery.append("and d.codice_letto IN ("  + bedRange + ")" );
		      
//		      sbQuery.append("SELECT sesso, stanza, codice_letto ");
//		      sbQuery.append("FROM REGINA_LOGISTICA_V ");
//		      sbQuery.append("WHERE ");
//		      sbQuery.append("gmadal <= ? AND (gmaal is null or (gmaal >= ?))");
//		      sbQuery.append("AND codice_letto IN ( "  + bedRange + " ) ");
		      
		      logDB.trace(" params -> dt=" + dt + "  bedRange=" + bedRange);
		      logDB.debug(" sql (gmaal= "+ dt + ")-> " + sbQuery.toString());
      		      
			  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
			  pstmt.setTimestamp(1,tstamp.valueOf(dt));
			  pstmt.setTimestamp(2,tstamp.valueOf(dt));
			  pstmt.setTimestamp(3,tstamp.valueOf(dt));
			  pstmt.setTimestamp(4,tstamp.valueOf(dt));
			  pstmt.setTimestamp(5,tstamp.valueOf(dt));
			  pstmt.setTimestamp(6,tstamp.valueOf(dt));
			  //pstmt.setInt(2, iBed);
			  //pstmt.setArray(2, iBed);
			  ResultSet rs = pstmt.executeQuery();
			  
			  while (rs.next ()) {

	         	  SESSO = rs.getString ("sesso");        	  
	        	  STANZA = rs.getString ("stanza");
	        	  COD_LETTO = rs.getString ("codice_letto");
	        	  
	        	  value = SESSO+";"+STANZA;
	        	  key = COD_LETTO;
	        	  logDB.trace("[SESSO;STANZA;COD_LETTO] -> "+ ++recNum +" " + SESSO+";"+STANZA+";"+COD_LETTO);
	        	  
	        	  mp.put(key, value);
	          }

			  logDB.debug("Total records -> " + recNum);
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
