package it.sbsoft.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import it.sbsoft.beans.OccByBed;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.utility.CodeEncodeString;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.utility.DateHelper;

public class DBTools {
	
    static Logger log = LoggerUtils.getLogger("sbsoftware");
    static Logger logDB = LoggerUtils.getLogger("db");
    static CodeEncodeString decode = CodeEncodeString.getInstance();
    //static DateHelper dh = new DateHelper();
    static FBDBHelper fbdbH = new FBDBHelper();
	private PropertiesCommon propFile = PropertiesCommon.getPropertiesFile();
	static Gson gson = new Gson();
	
    public Map<String, OccByBed> getOcc4FloorByDateMap(Set<String> setBedKeyset, String dt) throws Exception {
		
		log.info("called with params dt/bedSet ->" + dt+"/...");
		
		StringBuffer sbQuery;
		PreparedStatement pstmt;
		ResultSet rs;
		int occNum=0, preNum=0, nonAtt=0, libNum=0;
		Connection c = null;
		
		OccByBed occObj;
		Map<String, OccByBed> occByBed = new HashMap<String, OccByBed>();
		
		try {
    			
			  String bedRange = setBedKeyset.toString();
			  
			  if (bedRange.length() > 2) {
				  bedRange = bedRange.replace("[", "");
				  bedRange = bedRange.replace("]", "");
				  
				  // Get bed availability from the 'other DB' CBADATIIB (what a shity way of doing this !!!)
	              // START
				  c = fbdbH.getCBADATIIBConn();
				  
			      sbQuery = new StringBuffer();
			      sbQuery.append("SELECT a.CODSTAN, b.NUMSTANZA, a.CODLETTO, a.NUMERO_LETTO, b.NUMLETTI, b.ANNULLATO GSTANZE_ANULL, a.ANNULLATO GELETTI_ANULL ");
			      sbQuery.append("FROM GELETTI a, GESTANZE b ");
			      sbQuery.append("WHERE a.CODSTAN = b.CODSTAN ");
			      sbQuery.append("AND a.CODLETTO IN ( " + bedRange + " ) ");
			      sbQuery.append("AND (b.ANNULLATO = 'T' OR a.ANNULLATO = 'T') ");
			      
			      logDB.trace(" Stanza/Letto libero -> bedRange=" + bedRange);
			      logDB.debug(" Stanza/Letto libero Query sql -> " + sbQuery.toString());
			      
				  pstmt = c.prepareStatement(sbQuery.toString()); 
				  rs = pstmt.executeQuery();
				  
				  logDB.info("coll.size = " + setBedKeyset.size());
				  while (rs.next ()) {
					  
					  occObj = new OccByBed();
					  occObj.setRoom(rs.getString("NUMSTANZA")+"");
					  occObj.setBed_num(rs.getString ("CODLETTO"));
					  occObj.setStatus("3"); // 3=Non Attiva
					  occByBed.put(occObj.getBed_num(), occObj);
					  nonAtt++;
		          }
				
				  c = null;
			  }

			  //log.info("occByBed (avail) -> " + gson.toJson(occByBed));
			  
			  System.out.println("setBedKeyset.size() -> " + setBedKeyset.size());
			  setBedKeyset.removeAll(occByBed.keySet());
			  System.out.println("setBedKeyset.size() -> " + setBedKeyset.size());
			  
			  // Query for 'Occupato'
			  if (!setBedKeyset.isEmpty()) {
				  
				  bedRange = setBedKeyset.toString();
				  bedRange = bedRange.replace("[", "");
				  bedRange = bedRange.replace("]", "");
				  
				  c = fbdbH.getCBAOSPITIBConn();
			      
			      sbQuery = new StringBuffer();
			      sbQuery.append("SELECT a.sesso,d.stanza,d.codice_letto,a.gmadim,d.gmainizioutili,d.gmafineutili,a.codospite,a.nomeospite,d.sede,d.reparto,coalesce(d.FINECONFERMATA,'F') as fineconfermata ");
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
					  
					  occObj = new OccByBed();
					  occObj.setSesso(rs.getString("sesso"));
					  occObj.setRoom(StaticObj.getStaticObj().roomMap.get(rs.getString("stanza")+""));
					  //occObj.setStanza();
					  occObj.setBed_num(rs.getString ("codice_letto"));
					  occObj.setNome(rs.getString("nomeospite"));
					  occObj.setStatus("2"); // 2=occupato
					  occObj.setData_dal(DateHelper.getDtAsStr(rs.getDate("gmainizioutili")));
					  occObj.setData_al(DateHelper.getDtAsStr(rs.getDate("gmafineutili")));
					  
					  occByBed.put(occObj.getBed_num(), occObj);
					  occNum++;
		          }
			  }
			  
			  //log.info("occByBed (occ) -> " + gson.toJson(occByBed));
			  
			  System.out.println("setBedKeyset.size() -> " + setBedKeyset.size());
			  setBedKeyset.removeAll(occByBed.keySet());
			  System.out.println("setBedKeyset.size() -> " + setBedKeyset.size());
			  
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
					  
					  occObj = new OccByBed();
					  occObj.setSesso(rs.getString("sesso"));;
					  occObj.setRoom(StaticObj.getStaticObj().roomMap.get(rs.getString("stanza")));
					  //if (occObj.getRoom() == null) { occObj.setRoom("0"); } 
					  occObj.setBed_num(rs.getString ("codice_letto"));
					  occObj.setNome(rs.getString("nomeospite"));
					  occObj.setStatus("1"); //1=prenotato
					  occObj.setData_dal(DateHelper.getDtAsStr(rs.getDate("dal")));
					  occObj.setData_al(DateHelper.getDtAsStr(rs.getDate("al")));
					  
					  occByBed.put(occObj.getBed_num(), occObj);
					  preNum++;

		          }
			  }
			  
			  //log.info("occByBed (prenotato) -> " + gson.toJson(occByBed));
			  
			  // Query for Libero
			  if (!setBedKeyset.isEmpty()) {
				  
				  System.out.println("setBedKeyset.size() -> " + setBedKeyset.size());
				  setBedKeyset.removeAll(occByBed.keySet());
				  System.out.println("setBedKeyset.size() -> " + setBedKeyset.size());
				  
				  for (String s : setBedKeyset) {
					  occObj = new OccByBed();
					  occObj.setBed_num(s);
					  occObj.setRoom(StaticObj.getStaticObj().bedMap.get(occObj.getBed_num()));
					  occObj.setStatus("0"); //0=Libero
					  occByBed.put(occObj.getBed_num(), occObj);
					  libNum++;
				  }
				  
			  }
			  
			  //log.info("occByBed (libero) -> " + gson.toJson(occByBed));
			  
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
		log.info("Total records occ/pre/nonAtt/lib -> " +  occNum+"/"+preNum+"/"+nonAtt+"/"+libNum);
		logDB.info("Total records occ/pre/nonAtt/lib -> " +occNum+"/"+preNum+"/"+nonAtt+"/"+libNum);
		return occByBed;
	}
    
	public OccByBed getVacantBedRange(String buildId, String dt, String bed) throws Exception {
		
		OccByBed occByBed = new OccByBed();
		StringBuffer sbQuery = new StringBuffer();
		PreparedStatement pstmt;
		ResultSet rs;
		Connection c;
		
		try {

			c = fbdbH.getCBAOSPITIBConn();
			occByBed.setBed_num(bed);
			occByBed.setData_dal("---");
			occByBed.setData_al("---");
			
			// Do max
			sbQuery.append("SELECT distinct d.codice_letto, max(d.GMAFINEUTILI) as mx "); 
			sbQuery.append("FROM ospiti_d d ");
			sbQuery.append("WHERE d.gmafineutili <= CAST('" + DateHelper.getDate4Query(dt) + "' AS DATE) ");
		    sbQuery.append("AND d.codice_letto = " + bed + " ");
			sbQuery.append("group by d.codice_letto ");         
		
		    logDB.debug(" Max Vacant Query sql -> " + sbQuery.toString());
			
			pstmt = c.prepareStatement(sbQuery.toString()); 
			rs = pstmt.executeQuery();
			
			while (rs.next ()) {
				occByBed.setData_dal(DateHelper.getDtAsStr(rs.getDate("mx")));
			}
		
			// Do min
			sbQuery.delete(0, sbQuery.length());
			sbQuery.append("SELECT distinct d.codice_letto, min(d.GMAFINEUTILI) as mn "); 
			sbQuery.append("FROM ospiti_d d ");
			sbQuery.append("WHERE d.gmainizioutili >= CAST('" + DateHelper.getDate4Query(dt) + "' AS DATE) ");
		    sbQuery.append("AND d.codice_letto = " + bed + " ");
			sbQuery.append("group by d.codice_letto ");         
		
			logDB.debug(" Max Vacant Query sql -> " + sbQuery.toString());
			
			pstmt = c.prepareStatement(sbQuery.toString()); 
			rs = pstmt.executeQuery();
			
			while (rs.next ()) {
				occByBed.setData_al(DateHelper.getDtAsStr(rs.getDate("mn")));
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
			rs=null;
			pstmt=null;
			c=null;
		}
		
		return occByBed;
		
	}
	
	public Map<String, String> getBeds4Floor(int bedFrom, int bedTo) throws Exception {
		
		log.info(" called ... with values bedFrom/bedTo -> " + bedFrom+"/"+bedTo);
		logDB.info(" called ... with values bedFrom/bedTo -> " + bedFrom+"/"+bedTo);
		Map<String, String> retMap = new HashMap<String, String>();
		
		String CODSTAN, NUMSTANZA, CODLETTO, tmp;
		int numBeds = 0;
		Connection c = null;
		
		try {

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
	      sbQuery.append("and cast(b.numstanza as integer) between ? and ? ");
	      //sbQuery.append("and ((b.ANNULLATO IS NULL or b.ANNULLATO != 'T') and (a.ANNULLATO IS NULL or a.ANNULLATO != 'T')) ");
	      sbQuery.append("order by cast(b.NUMSTANZA as integer), numero_letto ");
		  
	      logDB.debug(" query -> " + sbQuery.toString());
		  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
		  pstmt.setInt(1, bedFrom);
		  pstmt.setInt(2, bedTo);

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
	
	public Set<String> getAvailableBeds() throws Exception {
		
		log.info(" getAvailableBeds called ... ");
		logDB.info(" getAvailableBeds called ... ");
		Set<String> retSet = new HashSet<String>();

		Connection c = null;
		
		try {

		  Class.forName ("org.firebirdsql.jdbc.FBDriver");
		  //FIREDB.CBADATIIB=jdbc:firebirdsql:localhost/3050:C:/FBDB/CBADATIIB.FDB
		  String databaseURL = propFile.getPropertySB("FIREDB.CBADATIIB");
		  
		  // retreive and decode credentials
		  String cred = propFile.getPropertySB("FIREDB.CBADATIIB.credentials");
		  String user = cred.split(";")[0];
		  String password = cred.split(";")[1];
		  
		  user = decode.decrypt(user);
		  password = decode.decrypt(password);
		  
		  logDB.debug(" databaseURL -> " + databaseURL);
		  c = java.sql.DriverManager.getConnection (databaseURL, user, password);
	      
	      StringBuffer sbQuery = new StringBuffer();
	      sbQuery.append("SELECT a.CODLETTO, b.CODSTAN, b.NUMSTANZA ");
	      sbQuery.append("FROM GELETTI a, GESTANZE b ");
	      sbQuery.append("where a.codstan = b.codstan ");
	      sbQuery.append("and ((b.ANNULLATO IS NULL or b.ANNULLATO != 'T') and (a.ANNULLATO IS NULL or a.ANNULLATO != 'T')) ");
	      sbQuery.append("order by 1, 2 ");
		  
	      logDB.debug(" query -> " + sbQuery.toString());
		  PreparedStatement pstmt = c.prepareStatement(sbQuery.toString()); 
		  ResultSet rs = pstmt.executeQuery();
		  
          while (rs.next ()) {
        	  retSet.add(rs.getString ("CODLETTO"));
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

		return retSet;
	}
	
}
