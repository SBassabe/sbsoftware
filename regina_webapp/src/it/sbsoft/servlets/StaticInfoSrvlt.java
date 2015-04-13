package it.sbsoft.servlets;

import it.sbsoft.beans.*;
import it.sbsoft.beans.todeprecate.DoctorInfoBean;
import it.sbsoft.beans.todeprecate.Floor;
import it.sbsoft.cleaning.CleaningSchedule;
import it.sbsoft.db.H2DBHelper;
import it.sbsoft.doctors.DocOccupancy;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.propfiles.PropertiesRooms;
import it.sbsoft.utility.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class StaticInfoSrvlt extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static PropertiesCommon propCommon;
	static PropertiesRooms propRooms;
	static String simulatorMode;
	static String persistProp;
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	static Set<String> availBeds;
	static CleaningSchedule cSched;
	private static DocOccupancy dOcc = new DocOccupancy();
	
    public StaticInfoSrvlt() {
        super();
    }
    
    public void init() {
    	
    	log.info("entered ");
		
		propRooms = PropertiesRooms.getPropertiesFile();
		propCommon = PropertiesCommon.getPropertiesFile();
		Connection conn = (Connection)getServletContext().getAttribute("connection");
		dOcc.h2Help.initConn(conn);
		cSched = new CleaningSchedule();
		cSched.h2Help.initConn(conn);
		
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info(" into doGet");
		Bean2cli ret = new Bean2cli();		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String maint = request.getParameter("maint");
		log.debug(" maint value -> " + maint);
		Map<String, FloorObj> floorArr = new HashMap<String, FloorObj>();
		
		try {
			
			// Get year List
			ret.setYearList(DateHelper.getYearList());
			
			// Get list of floors
			String floorProp=propCommon.getPropertySB("Floors");
			String[] floors = floorProp.split(",");
			
			// For every floor do bed and room maps
			for (int i=0; i<floors.length; i++ ){
				
				FloorObj floorObj = new FloorObj();			
				floorObj.setFloor_id(floors[i]);
				floorObj.setImg_src(propCommon.getPropertySB(floors[i] + ".src"));
				floorObj.setDescription(propCommon.getPropertySB(floors[i] + ".desc"));
				
				floorObj.setBedArr(getBedArrFromFile(floorObj.getFloor_id()));
				floorObj.setNum_beds(floorObj.getBedArr().size()+"");
				floorObj.setFeatureArr(getFeatureMapFromFile(floorObj.getFloor_id()));
				
				//Location
				floorObj.setLocArr(getLocMapH2db(floorObj));
				floorArr.put(floorObj.getFloor_id(), floorObj);
			}
			
		} catch (Exception e) {
			ret.setError(new Errore());
			if (e instanceof SBException){
				ret.getError().setErrorCode("1");
				ret.getError().setErrorDesc(e.getMessage());
			} else {
				ret.getError().setErrorCode("2");
				ret.getError().setErrorDesc("See Tomcat log files");
				e.printStackTrace();
			}
		
		} finally {
			
			ret.setRet2cli(floorArr);
			log.info(" exiting doGet ");
			log.debug(" returning -> [" + gson.toJson(ret) + "]");
			out.print(gson.toJson(ret));
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private Map<String, BedObj> getBedArrFromFile(String buildingId) {
		
		log.info(" called for buildingId -> " + buildingId);
		Map<String, BedObj> bedArr = new HashMap<String, BedObj>();
		BedObj bedObj;
		
		try {
			
			Map<String, String> map2 = propCommon.floorMaps.get(buildingId);
			String key, value, ret;
			String[] sCoords;
			
			Iterator<String> it = map2.keySet().iterator();
			
			while (it.hasNext()) {
							
				key = (String)it.next();
				
				value = map2.get(key);
				value = value.replaceAll("\"", "");
				ret = buildingId+";"+key+";"+value+";P";	
				sCoords = ret.split(";");
				
				bedObj = new BedObj();
				bedObj.setBed_num(sCoords[1]);
				bedObj.setRoom_num(sCoords[3]);
				bedObj.setX_val(Integer.parseInt(sCoords[4]));
				bedObj.setY_val(Integer.parseInt(sCoords[5]));
				
				bedArr.put(bedObj.getBed_num(), bedObj);
			}				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bedArr;
		
	}
	
	private Map<String, FeatureObj> getFeatureMapFromFile(String buildingId) {
		
		Map<String,String> featTypeMap = getFeatureTypeMap();
		Map<String, FeatureObj> featureArr = new HashMap<String, FeatureObj>();
		String mp;
		FeatureObj featObj;
		
		try {
			
			mp = propCommon.getProperty(buildingId+".floor_feat");
			if (mp != null) { 
				mp = mp.replaceAll("\"", "");
				String[] sp = mp.split(",");
				for (int i=0; i<sp.length; i++) {
					
					featObj = new FeatureObj();
					String[] sCoords = sp[i].split(";");
					
					featObj.setType(sCoords[0]);
					featObj.setDesc(featTypeMap.get(featObj.getType()));
					featObj.setRoom(sCoords[1]);
					featObj.setX_val(sCoords[2]);
					featObj.setY_val(sCoords[3]);
					featureArr.put(i+"", featObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return featureArr;
	}
	
	private Map<String,String> getFeatureTypeMap() {
		
		Map<String,String> featTypeMap = new HashMap<String,String>();
		String mp = propCommon.getProperty("FeatureType");
		mp = mp.replaceAll("\"", "");
		String[] sp = mp.split(",");
		for (int i=0; i<sp.length; i++) {
			String[] s = sp[i].split(";");
			featTypeMap.put(s[0], s[1]);
		}
		return featTypeMap;
	}
	
	private TreeMap<String, LocObj> getLocMapH2db(FloorObj floorObj) {
		
		log.info(" into getLocMapH2db");
		TreeMap<String, LocObj> locArr = new TreeMap<String, LocObj>();
		
		Statement h2Stmt;
		ResultSet rs;
		StringBuffer sql;
		LocObj locObj;
		
		try {
			
			h2Stmt = dOcc.h2Help.getConn().createStatement();
			sql = new StringBuffer();
			sql.append("SELECT A.FLOOR_ID, A.NUM_STAN, B.LOC_ID, B.POLY_PTNS FROM LOCATION A JOIN LOCATION_POLY B ON A.LOC_ID = B.LOC_ID AND FLOOR_ID = '" + floorObj.getFloor_id() + "'");
			sql.append(" WHERE B.POLY_PTNS IS NOT NULL ");
			log.debug("sql.toString -> " + sql.toString());
			rs = h2Stmt.executeQuery(sql.toString());
			
			while (rs.next()) {
				
				locObj = new LocObj();
				locObj.setRoom_num(rs.getString("NUM_STAN"));
				locObj.setPoly_points(rs.getString("POLY_PTNS"));
				locObj.setLoc_id(rs.getString("LOC_ID"));
				locArr.put(locObj.getLoc_id(), locObj);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			h2Stmt = null;
			sql = null;
		}
			
		return locArr;
	}
	
}
