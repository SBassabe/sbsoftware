package it.sbsoft.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

import it.sbsoft.utility.LoggerUtils;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class StaticObj {
	
	private static StaticObj ref;
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	static Logger logDB = LoggerUtils.getLogger("db");
	//static FBDBHelper fbdbH = new FBDBHelper();
	static Gson gson = new Gson();
    Map<String, String> bedMap = new TreeMap<String, String>();
    Map<String, String> roomMap = new TreeMap<String, String>();
	
	private StaticObj() {}
	
	private void loadObjects() {
		
		log.info("loading mapper ...");
		
		StringBuffer sbQuery;
		PreparedStatement pstmt;
		ResultSet rs;
		Connection c = null;
		
		try {
			
			// bedMaps
			//c = fbdbH.getCBADATIIBConn();
			c = FBDBHelperAsync.getCBADATIIBConn();
			sbQuery = new StringBuffer();
			sbQuery.append("SELECT distinct a.CODSTAN, b.NUMSTANZA, a.CODLETTO ");
			sbQuery.append("FROM GELETTI a, GESTANZE b ");
			sbQuery.append("WHERE a.CODSTAN = b.CODSTAN ");
			
		    logDB.debug(" loadBedMaps Query sql -> " + sbQuery.toString());
		      
			pstmt = c.prepareStatement(sbQuery.toString()); 
			rs = pstmt.executeQuery();
			
			 while (rs.next ()) {
				  
				 bedMap.put(rs.getString("CODLETTO"), rs.getString("NUMSTANZA"));
				 roomMap.put(rs.getString("CODSTAN"), rs.getString("NUMSTANZA"));
	         }

			 log.info("bedMap.size()" + bedMap.size());
			 log.info("roomMap.size()" + roomMap.size());
			 
			 log.info("bedMap as gson -> " + gson.toJson(bedMap));
			 log.info("roomMap as gson -> " + gson.toJson(roomMap));
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			//c=null;
			if (c !=null) {
				try {
				  c.close();
				} catch (Exception e) {e.printStackTrace();} 
			}
		}
	}
	
	public static synchronized StaticObj getStaticObj() {
		
		if (ref == null) {
			log.info("StaticObj -> creating new object.");
			ref = new StaticObj();
			ref.loadObjects();
			
		} else {
			//log.info("StaticObj -> returning existing object.");
		}
		
		return ref;
	}

}
