package it.sbsoft.doctors;

import it.sbsoft.beans.DoctorInfoBean;
import it.sbsoft.db.FBDBHelper;
import it.sbsoft.db.H2DBHelper;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.utility.LoggerUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class DocOccupancy {
	
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	public H2DBHelper h2Help = new H2DBHelper();
	private FBDBHelper fbHelp = new FBDBHelper();
	private PropertiesCommon propCommon = PropertiesCommon.getPropertiesFile();
	public Map<String, String> mpTables = new HashMap<String, String>();
	
	public DocOccupancy() {
		
		mpTables.put("SBTEST", "CREATE TABLE SBTEST(ID INT PRIMARY KEY, NAME VARCHAR(255) DEFAULT '')");
		mpTables.put("DOCDTMAP", "CREATE TABLE DOCDTMAP(FLOORID VARCHAR(5), GMADAL DATE, GMAAL DATE, DOCID INT, DOCNAME VARCHAR(255), CODSTAN VARCHAR(5), NUMSTANZA VARCHAR(5))");
	}
	
	public boolean checkTableExistance() {
		
		log.debug("into method ...");
		boolean ret=true;
		boolean b=false;
		try {
					
			for (String str : mpTables.keySet()) {
				b=doesTableExists(str);
				if (ret && !b) ret=false;
			}
				
			System.out.println("all tables found -> " + ret);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private boolean doesTableExists(String tabName) {
		
		log.info("into method ...");
		ResultSet rs;
		boolean ret=false;
		try {
			
			Statement stmt = h2Help.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT COUNT(*) CNT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='@'";
			rs = stmt.executeQuery(sql.replaceAll("@", tabName));
			rs.first();
			ret=rs.getInt(1)>0;
			log.debug("table '" + tabName + "' exists -> " + ret);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public boolean createMissingTables() {
		
		log.debug("into method ...");
		boolean ret=false;
		try {
			
			Statement stmt = h2Help.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			for (String s : mpTables.keySet()) {
				try {
					stmt.executeUpdate(mpTables.get(s));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public boolean populateTables() {
	
		log.info("into method ...");
		boolean ret=false;
		Connection fbOspitiCon, fbDaticon;
		Date dtStart, dtEnd;
		StringBuffer sql;
		int i;
		
		try {
			
			// Select and insert from CBAOSPITIIB to DOCDTMAP
			log.info("Select from CBAOSPITIIB and INSERT INTO DOCDTMAP ...");
			fbOspitiCon = fbHelp.getCBAOSPITIBConn();
			Statement fbStmt = fbOspitiCon.createStatement();
			
			sql = new StringBuffer();
			sql.append("SELECT a.GMADAL, a.GMAAL, b.PROGR as DOCID, b.DESCR as DOCNAME, a.CODSTAN");
			sql.append("  FROM CLIN_MEDICO_STANZA a, TEANAPERS b");
			sql.append(" WHERE a.PROGMEDICO = b.PROGR");
			sql.append(" ORDER BY 5, 1 desc, 2 desc");
			
			log.debug("sql.toString() " + sql.toString());
			ResultSet rs = fbStmt.executeQuery(sql.toString());
			i=0;
			
			if (rs.next()) { // if the result set is not empty do this
			
				log.info("Records found now just cleanup DOCDTMAP ...");
				Statement h2Stmt = h2Help.getConn().createStatement();
				sql = new StringBuffer();
				sql.append("DELETE DOCDTMAP");
				log.debug("sql.toString -> " + sql.toString());
				h2Stmt.executeUpdate(sql.toString());
				h2Help.getConn().commit();
				
				while (rs.next()) {
					sql.setLength(0);
					sql.append("insert into DOCDTMAP (GMADAL, GMAAL, DOCID, DOCNAME, CODSTAN) values (");
					
					dtStart = rs.getDate("GMADAL");
					if (dtStart != null) {
						sql.append("DATE '" + dtStart.toString() + "', ");
					} else {
						sql.append("null, ");
					}
						
					dtEnd = rs.getDate("GMAAL");
					if (dtEnd != null) {
						sql.append("DATE '" + dtEnd.toString() + "', ");
					} else {
						sql.append("DATE '2030-01-01', ");
					}
					
					sql.append(rs.getInt("DOCID") + ", ");
					sql.append("'"+rs.getString("DOCNAME") + "', ");
					sql.append(rs.getInt("CODSTAN") + ") ");
					log.debug("sql.toString -> " + sql.toString());
					h2Stmt.executeUpdate(sql.toString());
					i++;
				}
				log.info("Records processed -> " + i);
				
				// Get room number CODSTAN NUMSTANZA conversion from CBADATI schema
				log.info("Get room number CODSTAN NUMSTANZA conversion from CBADATI schema and UPDATE DOCDTMAP");
				fbDaticon = fbHelp.getCBADATIIBConn();
				fbStmt = fbDaticon.createStatement();
				
				sql = new StringBuffer();
				sql.append("SELECT distinct b.CODSTAN, b.NUMSTANZA FROM GESTANZE b");
				
				log.debug("sql.toString() " + sql.toString());
				rs = fbStmt.executeQuery(sql.toString());
				i=0;
				
				while (rs.next()) {
					sql.setLength(0);
					sql.append("UPDATE DOCDTMAP SET NUMSTANZA='" + rs.getString("NUMSTANZA") + "' WHERE CODSTAN='"+ rs.getString("CODSTAN") +"'");
					log.debug("sql.toString() [update] " + sql.toString());
					h2Stmt.executeUpdate(sql.toString());
					i++;
				}
				log.info("Records processed -> " + i);
				
				fbDaticon.close();
				
				// Get floor list and room ranges from 'regina.properties'
				log.info(" Get floor list and room ranges from 'regina.properties' and UPDATE DOCDTMAP ...");
				String[] rangeArr;
				i=0;
				
				for (String s : propCommon.getProperty("Floors").split(",")) {
					
					rangeArr = propCommon.getProperty(s+".room_range").split(","); // Eg -> A0.room_range=1,22
					sql.setLength(0);
					sql.append("UPDATE DOCDTMAP SET FLOORID='" + s + "' WHERE NUMSTANZA BETWEEN "+ rangeArr[0] +" AND " + rangeArr[1]);
					log.debug("sql.toString() [update] " + sql.toString());
					h2Stmt.executeUpdate(sql.toString());
					i++;
				}
				log.info("Records processed -> " + i);
				
				h2Help.getConn().commit();
			}
			
			fbOspitiCon.close();
			ret=true;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fbOspitiCon=null;
			fbDaticon=null;
		}
		return ret;
	}
	
	public void deleteAllTables() {
		
		log.info("into method ...");
		
		try {

			Statement stmt = h2Help.getConn().createStatement();
			
			for (String s : mpTables.keySet()) {
				try {
					stmt.executeUpdate("DROP TABLE " + s);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					//e.printStackTrace();
				}
			}
			
			h2Help.getConn().commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void purgeAllTables() {
		
		log.info("into method ...");
		
		try {

			Statement stmt = h2Help.getConn().createStatement();
			
			for (String s : mpTables.keySet()) {
				try {
					stmt.executeUpdate("DELETE " + s);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					//e.printStackTrace();
				}
			}
			
			h2Help.getConn().commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<DoctorInfoBean> getRoomMapByFloor(String floorId) {
		
		List<DoctorInfoBean> retList = new ArrayList<DoctorInfoBean>();
		try {
					
			//SELECT distinct FLOORID, CODSTAN, cast(NUMSTANZA as int) as NUMSTANZA FROM DOCDTMAP where FLOORID = 'A0'
			Statement h2Stmt = h2Help.getConn().createStatement();
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT distinct FLOORID, CODSTAN, cast(NUMSTANZA as int) as NUMSTANZA FROM DOCDTMAP where FLOORID = '"+ floorId +"'");
			log.debug("sql.toString -> " + sql.toString());
			ResultSet rs = h2Stmt.executeQuery(sql.toString());
			
			while (rs.next()) {
				DoctorInfoBean dib = new DoctorInfoBean();
				dib.setFloor(rs.getString("FLOORID"));
				dib.setCodStanza(rs.getString("CODSTAN"));
				dib.setNumStanza(rs.getString("NUMSTANZA"));
				// Ad-interim code ....
				//int[] iArr = {244,132,287,137,291,254,146,258,144,183,243,177};
				//dib.setPolyPoints(iArr);
				retList.add(dib);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retList;
	}
	
	public List<DoctorInfoBean> getDoctorOccupancy(String floor, String date) {
		
		List<DoctorInfoBean> dibList = new ArrayList<DoctorInfoBean>();
		try {
			
			Statement h2Stmt = h2Help.getConn().createStatement();
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM DOCDTMAP WHERE FLOORID='"+floor+"'  AND (GMADAL <= CAST('"+date+"' AS DATE) AND GMAAL >= CAST('"+date+"' AS DATE))");
			sql.append(" order by NUMSTANZA, GMADAL desc ");
			log.debug("sql.toString -> " + sql.toString());
			ResultSet rs = h2Stmt.executeQuery(sql.toString());
			
			//FLOORID  	GMADAL  	GMAAL  	DOCID  	DOCNAME  	CODSTAN  	NUMSTANZA
			
			while (rs.next()) {
				DoctorInfoBean dib = new DoctorInfoBean();
				dib.setFloor(rs.getString("FLOORID"));
				dib.setGmadal(rs.getDate("GMADAL").toString());
				dib.setGmaal(rs.getDate("GMAAL").toString());
				dib.setDocId(rs.getString("DOCID"));
				dib.setCodStanza(rs.getString("CODSTAN"));
				dib.setNumStanza(rs.getString("NUMSTANZA"));
				dib.setDocName(rs.getString("DOCNAME"));
				dibList. add(dib);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dibList;
	}
	
	public static void main(String[] args) {
		
		System.out.println("main started ....");
		System.setProperty("catalina.home","C:\\Program Files\\apache-tomcat-7.0.14");
		String file = System.getProperty("catalina.home") + "\\conf\\log4j_regina.properties";
		LoggerUtils.initLogger(file);
		DocOccupancy doc = new DocOccupancy();
		
		String url = "jdbc:h2:file:" + System.getProperty("catalina.home") + "\\h2db\\localdb";
		String usr = "sa";
		String pwd = "";
		doc.h2Help.initConn(url, usr, pwd);
		doc.deleteAllTables();
		doc.checkTableExistance();
		doc.createMissingTables();
	    doc.checkTableExistance();
		doc.populateTables();
		doc.h2Help.closeConn();
		doc=null;
	}
}
