package it.sbsoft.cleaning;

import it.sbsoft.beans.CleanByLoc;
import it.sbsoft.beans.CleanByLocMap;
import it.sbsoft.beans.OccByBed;
import it.sbsoft.beans.OccByBedMap;
import it.sbsoft.db.DBTools;
import it.sbsoft.db.H2DBHelper;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.utility.DateHelper;
import it.sbsoft.utility.LoggerUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook.*;

import com.google.gson.Gson;

public class CleaningSchedule {
	
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	static Logger logDB = LoggerUtils.getLogger("db");
	public H2DBHelper h2Help = new H2DBHelper();
	static PropertiesCommon propCommon = PropertiesCommon.getPropertiesFile();
	static DBTools db = new DBTools();
	static Gson gson = new Gson();
	
	public CleaningSchedule() {
	}
	
	public double getCVal4Loc(String loc_id) {
		
		double d = 0;
		Statement h2Stmt;
		StringBuffer sql;
		ResultSet rs;
		
		try {
			
			h2Stmt = h2Help.getConn().createStatement();
			sql = new StringBuffer();
			sql.append("SELECT DEFAULT_VAL FROM LOCATION WHERE LOC_ID='"+loc_id+"'");
			log.debug("sql.toString -> " + sql.toString());
			rs = h2Stmt.executeQuery(sql.toString());
			
			while (rs.next()) { //it should be only one !!
				d = rs.getDouble(1);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			h2Stmt = null;
			sql = null;
			rs = null;
		}
		
		return d;
	}
	
	public Map<String, CleanByLoc> getCleaningScheduleByDate(String dt, String floor_id, OccByBedMap obbm) {
		
		log.info("called");
		Map<String, CleanByLoc> mp = new HashMap<String, CleanByLoc>();
		Statement h2Stmt;
		StringBuffer sql;
		String cval=null, dval=null;
		ResultSet rs;
		CleanByLoc locObj, locObj2;
		int i=0;
		
		try {
			
			// Load Room occupancy here
			//OccByBedMap obbm = getAllRoomOcc4GivenDTFloor(dt, floor_id);
			Map<String, Map<String, OccByBed>> mp2 = new TreeMap<String, Map<String, OccByBed>>();
			mp2.put(floor_id, obbm.getOccByBedMp());
			
			log.info("calculateRoomOccupancy inputObj obj2gson -> " + gson.toJson(mp2));
			Map<String, Map<String, CleanByLoc>> mp3 = calculateRoomOccupancy(mp2);
			
			log.info("calculateRoomOccupancy obj2gson -> " + gson.toJson(mp3));
			
			h2Stmt = h2Help.getConn().createStatement();
			sql = new StringBuffer();
			//sql.append("SELECT LOC_ID, CVALUE FROM CLEANING_SCHEDULE WHERE CDATE= '"+ DateHelper.getDate4Query(dt) +"' AND FLOOR_ID = '"+ floor_id + "'");
			sql.append("SELECT A.LOC_ID, NVL(B.CVALUE,-1) as CVALUE, A."+ DateHelper.getWeekDay(dt)+" as DVALUE, A.EXCEL_ROW ");
			sql.append("  FROM LOCATION A LEFT JOIN CLEANING_SCHEDULE B ON A.LOC_ID = B.LOC_ID AND CDATE= '"+ DateHelper.getDate4Query(dt) +"' ");
			sql.append(" WHERE A.FLOOR_ID = '"+ floor_id + "'; ");
			
			log.debug("sql.toString -> " + sql.toString());
			rs = h2Stmt.executeQuery(sql.toString());
			
			while (rs.next()) {
				
				locObj = new CleanByLoc();
				locObj.setLoc_id(rs.getString("LOC_ID"));
				locObj.setExcelRow(rs.getString("EXCEL_ROW"));
				
				cval = rs.getBigDecimal("CVALUE").toString();
				dval = rs.getBigDecimal("DVALUE").toString();
				/*
				if (cval.compareTo("-1") == 0) { // This means no record was found in the CLEANING_SCHEDULE table
					locObj.setCvalue(dval);
				} else {
					locObj2 = mp3.get(floor_id).get(locObj.getLoc_id());
					if (locObj2 != null) {
						if (locObj2.getCvalue() == "2") {
							locObj.setCvalue("1"); // Room is occupied, then clean-it
						} else {
							locObj.setCvalue("0"); // Room is vacant, then DO NOT clean-it
						}	
					} else {
						locObj.setCvalue(dval);
					}
				}
				*/
				if (cval.compareTo("-1") == 0) {      // This means no record was found in the CLEANING_SCHEDULE table
					
					locObj2 = mp3.get(floor_id).get(locObj.getLoc_id());
					
					if (locObj2 != null) {            // This means an Room object was found
						
						if (locObj2.getCvalue() == "2") {
							locObj.setCvalue("1");    // Room is occupied, then clean-it
						} else {
							locObj.setCvalue("0");    // Room is vacant, then DO NOT clean-it
						}
					} else {
						locObj.setCvalue(dval);       // This means the room was not and Occ obj, just print the default cleaning value
					}

				} else {
					locObj.setCvalue(cval);
				}
				
				
				mp.put(locObj.getLoc_id(), locObj);
				i++;
				cval=null;
				dval=null;
			}
			
			log.info("select FROM CLEANING_SCHEDULE : Records processed -> " + i);
			log.info(" cc as gson -> " + gson.toJson(mp));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			h2Stmt = null;
			sql = null;
			rs = null;
		}
		return mp;
	}
	
	public void mergeCleaningDays(Map<String, CleanByLoc> cblMap, String floorId, String dt) throws Exception {
		
		Statement h2Stmt;
		StringBuffer sql;
		CleanByLoc cbd;
		int r = 0;
		try {
			
			Iterator<String> it = cblMap.keySet().iterator();
			h2Stmt = h2Help.getConn().createStatement();
			sql = new StringBuffer();
			
			while (it.hasNext()) {
			
				cbd = cblMap.get(it.next());
				sql.append("MERGE INTO CLEANING_SCHEDULE (CDATE, FLOOR_ID, LOC_ID, CVALUE) VALUES ('"+ DateHelper.getDate4Query(dt) +"', '"+ floorId +"', '" + cbd.getLoc_id() + "', "+ cbd.getCvalue() +")");
				//MERGE INTO CLEANING_SCHEDULE (CDATE, FLOOR_ID, LOC_ID, CVALUE) VALUES ('2014-09-20', 'A0', 'A0_2', 2.5);
				//sql.append("SELECT LOC_ID, CVALUE  FROM CLEANING_SCHEDULE WHERE CDATE= '"+ DateHelper.getDate4Query(dt) +"' AND FLOOR_ID = '"+ floor_id + "'");
				log.debug("sql.toString -> " + sql.toString());
				r = h2Stmt.executeUpdate(sql.toString());
				h2Help.getConn().commit();
				System.out.println("executeUpdate result " + r );
				sql.delete(0, sql.length());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			h2Stmt = null;
			sql = null;
		}
		
	}
	
	private Map<String, CleanByLoc> getDays4Excel(String dt) throws Exception {
		
		// Please refractor this code ... it does not belong here ....
		Map<String, CleanByLoc> mp = new HashMap<String, CleanByLoc>();
		CleanByLoc locObj;
		
		try {
			
			// Get list of floors
			String floorProp=propCommon.getPropertySB("Floors");
			String[] floorArr = floorProp.split(",");
			
			// For every floor get Occupancy for by date
			for (int i=0; i<floorArr.length; i++ ){
				
				OccByBedMap obbm = getAllRoomOcc4GivenDTFloor(dt, floorArr[i]);
				mp.putAll(getCleaningScheduleByDate(dt, floorArr[i], obbm));
				log.info("return total map size -> " + mp.size());

			}	
			
			log.info("mp structure after load -> " + gson.toJson(mp));
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return mp;
	}
	
	private Map<String, CleanByLoc> getDays4ExcelOrig(String dt) throws Exception {
		
		// Please refractor this code ... it does not belong here ....
		Map<String, CleanByLoc> mp = new HashMap<String, CleanByLoc>();
		Statement h2Stmt;
		StringBuffer sql;
		ResultSet rs;
		CleanByLoc locObj;
		
		try {
			
			h2Stmt = h2Help.getConn().createStatement();
			sql = new StringBuffer();
			String dt4Query = DateHelper.getDate4Query(dt);
			String dayWeek  = DateHelper.getWeekDay(dt);
			
			/*
			sql.append("SELECT NVL(B.CVALUE, A.DEFAULT_VAL) AS CALC_VAL,  A.LOC_ID, A.FLOOR_ID, A.NUM_STANZA, A.LOC_DESC, A.EXCEL_ROW ");
			sql.append("FROM LOCATION A ");
			sql.append(" LEFT OUTER JOIN CLEANING_SCHEDULE B ON A.LOC_ID = B.LOC_ID AND NVL(B.CDATE, '"+ DateHelper.getDate4Query(dt) + "') = '"+ DateHelper.getDate4Query(dt) + "' ");
			//sql.append(" WHERE A.FLOOR_ID = 'A0' ");
			*/
			//                                              dayWeek
			sql.append("SELECT NVL(B.CVALUE, NVL(C.STATO, A.DEFAULT_VAL)) CALC_VAL, A.LOC_ID, A.FLOOR_ID, A.NUM_STANZA, A.LOC_DESC, A.EXCEL_ROW ");
			sql.append("  FROM LOCATION_ORIG A ");
			sql.append("       LEFT JOIN CLEANING_SCHEDULE B ON A.LOC_ID = B.LOC_ID AND NVL(B.CDATE, '"+ dt4Query +"') = '"+ dt4Query +"' ");
			sql.append("       LEFT JOIN TMP C ON C.LOC_ID = A.LOC_ID AND C.STATO = '2' AND  NVL(C.CDATE, '"+ dt4Query +"') = '"+ dt4Query +"' ");
			
			log.debug("sql.toString -> " + sql.toString());
			rs = h2Stmt.executeQuery(sql.toString());
			
			while (rs.next()) {
				locObj = new CleanByLoc();
				locObj.setCvalue(rs.getBigDecimal("CALC_VAL").toString());
				locObj.setLoc_id(rs.getString("LOC_ID"));
				locObj.setExcelRow(rs.getString("EXCEL_ROW"));
				mp.put(locObj.getExcelRow(), locObj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			h2Stmt = null;
			sql = null;
			rs = null;
		}
		return mp;
	}
	
	public String addDay2Excel(String dt) throws Exception {
		
		log.info("Start method");
		String ret = "";
		PropertiesCommon pc = PropertiesCommon.getPropertiesFile();
		String path = pc.getPropertySB("Excel.path");
		//String path = "C:\\PCMigration_private\\SBSoftware\\sbsoftware\\Archive\\ExcelWorkDir\\";
		String fileNamePrefix = "prog_pulizie_mese_";
		int dayOneColumn = 2;
		int wCol = (new Integer(dt.substring(6, 8)) + dayOneColumn), rowNum=0;
		double dblValue = 0.00;
		CleanByLoc cbl;
		
		int dtN = new Integer(dt);
		dtN--;
		/*
		if (dt.endsWith("01")) {
			// If 1st of month do not look for previews day
			// For demo just make sure file exists
		} else { dtN--; }
		*/
		String inFile = path + fileNamePrefix + dtN + ".xls";
		String outFile = path + fileNamePrefix + dt + ".xls";
		
		//log.info("fileName -> " + inFile);
		//log.info("outFile -> " + outFile);
		
		try {
			
			HSSFWorkbook wb;
			try {
				log.info("getting FileInputStream -> " + inFile);
				wb = new HSSFWorkbook(new FileInputStream(inFile));
			} catch (Exception e) {
				 
				 log.info("Into Exception from FileInputStream method: getMessage -> " + e.getMessage());
				 e.printStackTrace();
				 throw new SBException("\bFile: '"+ fileNamePrefix + dtN + ".xls'  non trovato ...");
			}
			
			FileOutputStream stream;
			try {
				log.info("getting FileOutputStream -> " + outFile);
				stream = new FileOutputStream(outFile);
			} catch (Exception e) { 
				
				log.info("Into Exception from FileOutputStream method: getMessage -> " + e.getMessage());
				e.printStackTrace();
				wb.close(); throw new SBException("\bFile: '"+ fileNamePrefix + dt + ".xls' non gestibile probabilmente aperto.");}
				
			HSSFSheet sheet = wb.getSheetAt(0);
			
			log.info("getting getDays4Excel ...");
			Map<String, CleanByLoc> mp = getDays4Excel(dt);
			if (mp != null) {
				
				Iterator<String> it = mp.keySet().iterator();
				
				while (it.hasNext()) {
					cbl = mp.get(it.next());
					
					System.out.println(cbl.getExcelRow() + " < - cbl.getExcelRow(), cbl.getLoc_id() -> "  + cbl.getLoc_id() + ", cbl.getCvalue() -> " + new Double(cbl.getCvalue()) ); 
					
					rowNum = new Integer(cbl.getExcelRow());
					dblValue =  new Double(cbl.getCvalue());
					
					HSSFRow row = sheet.getRow(rowNum - 1);
					HSSFCell cell = row.getCell(wCol);
					//cell.setCellValue(new Double(cbl.getCvalue()));
					cell.setCellValue(dblValue);
				}
				
				HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
				wb.write(stream);
				stream.close();
			}
			wb.close();
			ret = "file: '"+ outFile +"' prodotto correttamente.";
			log.info("End method success");
			
		} catch (Exception e) {
			
			log.info("Into Exception getMessage -> " + e.getMessage());
			//log.info("Into Exception getMessage -> " + e.getCause().getLocalizedMessage());
			e.printStackTrace();
			if (e instanceof SBException) {
				throw new SBException(e.getMessage());
			} else {
				throw new Exception(e.getMessage());
			}
		}
		return ret;
	}
	
	/*
	 * Given a specific date, retrieve all occupancy for every floor
	 * Create a Map having the FloorId ad key.
	 * */
	
	public Map<String, Map<String, OccByBed>> getAllRoomOcc4GivenDT(String dt) throws Exception {
	
		log.info("entered");
		// floorId      bedNum    occObj
		Map<String, Map<String, OccByBed>> retMap = new TreeMap<String, Map<String, OccByBed>>();
		int n=0;
		
		try {
			
			// Get list of floors
			String floorProp=propCommon.getPropertySB("Floors");
			String[] floorArr = floorProp.split(",");
			
			// For every floor get Occupancy for by date
			for (int i=0; i<floorArr.length; i++ ){
				
				retMap.put(floorArr[i], getAllRoomOcc4GivenDTFloor(dt, floorArr[i]).getOccByBedMp());
				n = n + retMap.get(floorArr[i]).size();
			}
			log.info("return total map size -> " + n);
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new Exception(e.getMessage());
	    }
		return retMap;
	}

	/*
	 * Given a specific date and floor retreive all occupancy info
	 * */
	
	public OccByBedMap getAllRoomOcc4GivenDTFloor(String dt, String floor) throws Exception {
	
		log.info("entered");
		OccByBedMap occByBedMp = new OccByBedMap();
		
		try {
				
			occByBedMp.setOccByBedMp(db.getOcc4FloorByDateMap(propCommon.getRoomKeySet(floor), DateHelper.getDate4Query(dt)));
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new Exception(e.getMessage());
	    }
		log.info("return map size for floor: " + floor  + " -> " + occByBedMp.getOccByBedMp().size());
		return occByBedMp;
	}
	
	/*
	 *  Given a Map (having the floorId as key) create a map of occupied rooms
	 *  Where a room is flagged as occupied if at least one bed is occupied.
	 * */
	//                                                                    floorId      bedNum    occObj
	public Map<String,  Map<String, CleanByLoc>> calculateRoomOccupancy(Map<String, Map<String, OccByBed>> mp) throws Exception {
		
		log.info("called");
		// floorId       locId     roomObj
		Map<String, Map<String, CleanByLoc>> retMap = new TreeMap<String, Map<String, CleanByLoc>>();
		
		//   locId     roomObj
		Map<String, CleanByLoc> roomMap;
		String floorId, locId;
		CleanByLoc cbl;
		
		try {
			
		// For every floorId get the mapped-bed-object	
		Iterator<String> it = mp.keySet().iterator();
		while (it.hasNext()) {
			
			floorId = it.next();
			roomMap = new TreeMap<String, CleanByLoc>();
			Map<String, OccByBed> bedMap = mp.get(floorId);
			
			// Iterating the mapped-bed-object create a new map of only the rooms
			Iterator<String> it2 = bedMap.keySet().iterator();
			while (it2.hasNext()) {
				
			    OccByBed occObj = bedMap.get(it2.next());
			    locId = floorId + "_" + occObj.getRoom();
			    log.info("working loc_id:" + locId);	    
			    // Add a new CleanByLoc object only if it does not exist
			    // If it does exists add only of status is > 0
				if (roomMap.containsKey(locId)) {
					
					//System.out.println("locId already Mapped");
					if (occObj.getStatus() == "2") {
						System.out.println("upgrading to status 2 --- occupato");
						roomMap.get(locId).setCvalue("2");
					}	
				} else {
					//System.out.println("Create new CleanByDoc object with locId -> " + locId);
					cbl = new CleanByLoc();
					cbl.setCvalue(occObj.getStatus());
					cbl.setLoc_id(locId);
					roomMap.put(locId, cbl);
					//System.out.println("roomMap.size() -> " + roomMap.size());
				}
			
			}
			retMap.put(floorId, roomMap);
			
		}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		return retMap;
	}
	                              // floorId       locId     roomObj
	public void populateTempTable(Map<String, Map<String, CleanByLoc>> mp, String dt) throws Exception {
		
		log.info("entered");
		String floorId, locId, sqlStmt;
		PreparedStatement pstmt;
		Statement stmt;
		Map<String, CleanByLoc> innerMp;
		CleanByLoc cbl;
		
		try {
			
			stmt = h2Help.getConn().createStatement();
			stmt.executeUpdate("TRUNCATE TABLE TMP");
			
			sqlStmt = "INSERT INTO TMP (CDATE, FLOOR_ID, LOC_ID, STATO) VALUES (?,?,?,?)";
			
			Iterator<String> it = mp.keySet().iterator();
			while (it.hasNext()) {
				
				floorId = it.next();
				
				innerMp = mp.get(floorId);
				
				Iterator<String> it2 = innerMp.keySet().iterator();
				while (it2.hasNext()) {
					
					locId = it2.next();
					cbl = innerMp.get(locId);
					pstmt = h2Help.getConn().prepareStatement(sqlStmt);
					pstmt.setString(1, DateHelper. getDate4Query(dt));
					pstmt.setString(2, floorId);
					pstmt.setString(3, locId);
					pstmt.setString(4, cbl.getCvalue());
					pstmt.executeUpdate();
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt = null;
			pstmt = null;
		}
		
		
	}
	
	public static void main(String[] args) {
		
		System.out.println("main started ....");
		String dt = "20150101";
		System.out.println("dt = " + dt);
		int dtN = new Integer(dt);
		dtN--;
		System.out.println("dtN = " + dtN + ".xls");
		System.out.println("dt.substring(6, 8) ->" + dt.substring(6, 8));
		/*
		int currentDayColumn = (new Integer(dt.substring(6, 8)) + 4);
		System.out.println("currentDayColumn -> " + currentDayColumn);
		
		CleaningSchedule cs = new CleaningSchedule();
		
		String url = "jdbc:h2:tcp://localhost/C:\\Program Files\\apache-tomcat-7.0.54\\conf\\reginadb\\dbfile";
		cs.h2Help.initConn(url, "sa",  "");
	    try {
			Object obj = cs.getDays4Excel("20110101");
			System.out.println("hi!!");
			cs.addDay2Excel(dt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
	
}
