package it.sbsoft.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import it.sbsoft.exceptions.SBException;
import it.sbsoft.utility.CodeEncodeString;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.utility.PropertiesFile;

public class DBTools {
	
    static Logger log = LoggerUtils.getLogger("sbsoftware");
    static Logger logDB = LoggerUtils.getLogger("db");
    static CodeEncodeString decode = CodeEncodeString.getInstance();
	private PropertiesFile propFile = PropertiesFile.getPropertiesFile();
	
	public Map<String, String> getOcc4FloorByDate(Set<String> setBedKeyset, String dt) throws Exception {
		
		log.info("called with params dt/bedSet ->" + dt+"/...");
		
		StringBuffer sbQuery;
		PreparedStatement pstmt;
		ResultSet rs;
		Map<String, String> mp = new HashMap<String, String>();
		String SESSO, STANZA, COD_LETTO, NOME, STATUS, value="", key="";
		int occNum=0, preNum=0;
		Connection c = null;
		
		try {
			
			  // Get the connection
			  //FIREDB.CBAREGINA=jdbc:firebirdsql:localhost/3050:C:/FBDB/CBAOSPITIB.FDB			  
			  Class.forName ("org.firebirdsql.jdbc.FBDriver");
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
		      
		      // Query for 'Occupato'
			  String bedRange = setBedKeyset.toString();
			  
			  bedRange = bedRange.replace("[", "");
			  bedRange = bedRange.replace("]", "");
		      
		      sbQuery = new StringBuffer();
		      sbQuery.append("SELECT a.sesso,d.stanza,d.codice_letto,a.gmadim,d.gmainizioutili,d.gmafineutili,a.codospite,a.nomeospite,d.sede,d.reparto ");
		      sbQuery.append("FROM ospiti_a a ");
		      sbQuery.append("JOIN ospiti_d d on (a.codospite=d.codospite) ");
		      sbQuery.append("LEFT JOIN clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<= CAST('" + dt + "' AS DATE) and (m.gmaal is null or (m.gmaal >= CAST('" + dt + "' AS DATE)))) ");
		      sbQuery.append("WHERE a.gmaing<=CAST('" + dt + "' AS DATE) ");
		      sbQuery.append("AND (a.gmadim is null or (a.gmadim>CAST('" + dt + "' AS DATE))) ");
		      sbQuery.append("AND d.gmainizioutili<=CAST('" + dt + "' AS DATE) ");
		      sbQuery.append("AND ((d.gmafineutili>CAST('" + dt + "' AS DATE)) or d.gmafineutili is null) ");
		      sbQuery.append("and d.codice_letto IN ("  + bedRange + ")" );
		      
		      logDB.trace(" Occupati Query params -> dt=" + dt + "  bedRange=" + bedRange);
		      logDB.debug(" Occupati Query sql -> " + sbQuery.toString());
      		      
			  pstmt = c.prepareStatement(sbQuery.toString()); 
			  rs = pstmt.executeQuery();
			  
			  
			  logDB.info("coll.size = " + setBedKeyset.size());
			  while (rs.next ()) {
				  
	         	  SESSO = rs.getString ("sesso");        	  
	        	  STANZA = rs.getString ("stanza");
	        	  COD_LETTO = rs.getString ("codice_letto");
	        	  NOME = rs.getString("nomeospite");
	        	  STATUS="2"; // 2=occupato
	        	  
	        	  value = SESSO+";"+STANZA+";"+COD_LETTO+";"+NOME+";"+STATUS;
	        	  key = COD_LETTO;
	        	  logDB.trace("[<key>COD_LETTO <value>SESSO;STANZA;COD_LETTO;NOME;STATUS] -> "+ ++occNum + value);
	        	  
	        	  mp.put(key, value);
	        	  setBedKeyset.remove(COD_LETTO);
	        	 
	          }
			  logDB.info("coll.size = " + setBedKeyset.size());
			  
			  // Query for 'Prenotato'
			  if (!setBedKeyset.isEmpty()) {
				  
				  bedRange = setBedKeyset.toString();
				  bedRange = bedRange.replace("[", "");
				  bedRange = bedRange.replace("]", "");
			      
			      sbQuery = new StringBuffer();
			      
			      sbQuery.append("SELECT distinct a.sesso,p.stanza,p.letto codice_letto,p.dal,p.al,a.CODOSPITE,a.NOMEOSPITE,p.sede,p.reparto  "); 
			      sbQuery.append("from laospita a  ");
			      sbQuery.append("join laregole r on (a.codente=r.codente and a.codospite=r.codospite and r.stato='P')  ");
			      sbQuery.append("join clin_prenotazioni p on (a.codospite=p.codospite ");
			      sbQuery.append("     and p.al >= CAST('" + dt + "' AS DATE)  ");
			      sbQuery.append("     and p.dal <= CAST('" + dt + "' AS DATE) and p.letto IN ("  + bedRange + ")) ");

			      logDB.trace(" Prenotati Query params -> dt=" + dt + "  bedRange=" + bedRange);
			      logDB.debug(" Prenotati Query sql (dt= "+ dt + ")-> " + sbQuery.toString());
	      		      
				  pstmt = c.prepareStatement(sbQuery.toString()); 
				  rs = pstmt.executeQuery();
				  
				  while (rs.next ()) {
					  
		         	  SESSO = rs.getString ("sesso");        	  
		        	  STANZA = rs.getString ("stanza");
		        	  COD_LETTO = rs.getString ("codice_letto");
		        	  NOME = rs.getString("nomeospite");
		        	  STATUS="1"; //1=prenotato
		        	  
		        	  value = SESSO+";"+STANZA+";"+COD_LETTO+";"+NOME+";"+STATUS;
		        	  key = COD_LETTO;
		        	  logDB.trace("[<key>COD_LETTO <value>SESSO;STANZA;COD_LETTO;NOME;STATUS] -> "+ ++preNum + value);
		        	  
		        	  mp.put(key, value);
		        	 
		          }
			  }
			  
		} catch (SBException e) {
			throw new SBException(e.getMessage());
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
			rs=null;
			pstmt=null;
			c=null;
		}
		log.info("Total records occ/pre-> " + occNum+"/"+preNum);
		logDB.info("Total records occ/pre-> " + occNum+"/"+preNum);
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
