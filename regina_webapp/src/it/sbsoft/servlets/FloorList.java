package it.sbsoft.servlets;

import it.sbsoft.beans.*;
import it.sbsoft.db.DBTools;
import it.sbsoft.doctors.DocOccupancy;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.propfiles.PropertiesRooms;
import it.sbsoft.utility.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class FloorList extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static PropertiesCommon propCommon;
	static PropertiesRooms propRooms;
	static String simulatorMode;
	static String persistProp;
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	static Set<String> availBeds;
	private static DocOccupancy dOcc = new DocOccupancy();
    
    public FloorList() {
        super();
    }
    
    public void init() {
    	
    	log.info("entered ");
    	
		simulatorMode = this.getInitParameter("simulatorMode");
		log.info(" simulatorMode = " + simulatorMode);
    	
		persistProp =  this.getInitParameter("persistProperties");
		log.info(" persistProp = " + persistProp);
		
		if (persistProp != null && persistProp.compareTo("true") == 0 && propCommon != null) {
			log.info("Skip Configuring properties ...");
			return;
		}
		
		propRooms = PropertiesRooms.getPropertiesFile();
		propCommon = PropertiesCommon.getPropertiesFile();
		Connection conn = (Connection)getServletContext().getAttribute("connection");
		dOcc.h2Help.initConn(conn);
		
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info(" into doGet");
		Bean2cli ret = new Bean2cli();
		
		List<Floor> floorList = new ArrayList<Floor>();
		Floor floorBean;
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String maint = request.getParameter("maint");
		log.debug(" maint value -> " + maint);
		//initProps();
		 
		try {
			
			// Just purge in memory db tables...
			dOcc.purgeAllTables();
			dOcc.populateTables();
			
			// Get list of floors
			String floorProp=propCommon.getPropertySB("Floors");
			String[] floorArr = floorProp.split(",");
			
			// For every floor do bed and room maps
			for (int i=0; i<floorArr.length; i++ ){
				
				String floorId=floorArr[i];
				floorBean = new Floor();
				
				// bed map
				floorBean.setId(floorId);
				floorBean.setDescription(propCommon.getProperty(floorId+".desc"));
				floorBean.setImgSrc(propCommon.getProperty(floorId+".src"));
				//floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
				if ("true".compareTo(maint) == 0) {
					floorBean.setFloorMap(getMapCoordinatesFromDB(floorBean.getId()));
				} else {
					floorBean.setFloorMap(getMapCoordinatesFromFile(floorBean.getId()));
				}
				floorBean.setFeatureMap(getFeatureMap(floorBean.getId()));
				floorBean.setDoctorMap(getDoctorMap(floorBean.getId()));
				
				// room map
				floorBean.getRoomMap(); // just call it to create the empty list object
				for (String s : propRooms.getKeyListForPrefix(floorId)) {
					String valS =  propRooms.getPropertySB(s);
					if (valS != null) {
						DoctorInfoBean dib = new DoctorInfoBean();
						dib.setNumStanza(s.substring((floorId+"_").length()));
						dib.setPolyPoints(valS.split(","));
						floorBean.getRoomMap().add(dib);
					}
				}
				floorList.add(floorBean);
			}
			
			availBeds=null;
			
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
			
			ret.setRet2cli(floorList);
			log.info(" exiting doGet ");
			log.debug(" returning -> [" + gson.toJson(ret) + "]");
			out.print(gson.toJson(ret));
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public List<FloorMap> getMapCoordinatesFromFile(String buildingId) {
		
		log.info(" called for buildingId -> " + buildingId);
		List<FloorMap> floorMapList = new ArrayList<FloorMap>();
		
		Map<String, String> map2 = propCommon.floorMaps.get(buildingId);
		String key, value, ret;
		
		try {
					
			if ("true".compareTo(simulatorMode) != 0) {
				if (availBeds == null) {
					DBTools db = new DBTools();
					availBeds =  db.getAvailableBeds();
				}
			}
			
			Iterator<String> it = map2.keySet().iterator();
			
			while (it.hasNext()) {
				
				key = (String)it.next();
				if (availBeds == null || availBeds.contains(key)) {	
					value = map2.get(key);
					value = value.replaceAll("\"", "");
					
					ret = buildingId+";"+key+";"+value+";P";
					log.trace(" ret -> " + ret);
					floorMapList.add(createFMobjectFromString(ret));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info(" beds mapped -> " + floorMapList.size());
		return floorMapList;
	}
	
	public List<FloorMap> getMapCoordinatesFromDB(String buildingId) throws Exception {
		
		log.info(" called for buildingId -> " + buildingId);
		DBTools db = new DBTools();
		Map<String, String> mapFloorDB = new HashMap<String, String>();
		
		List<FloorMap> floorMapList = new ArrayList<FloorMap>();
		String roomRange, sProp, sCodStanNumStan, concat;
		int roomMin, roomMax;
		int totalA=0, totalP=0, totalD=0;
		

		try {
			
			// Collect room_range
			roomRange = propCommon.getProperty(buildingId+".room_range");
			roomMin = new Integer(roomRange.split(",")[0]);
			roomMax = new Integer(roomRange.split(",")[1]);
			
			// Call DB and collect bedInfo
			if ("true".compareTo(simulatorMode) != 0) mapFloorDB = db.getBeds4Floor(roomMin, roomMax); // -> Map<CODLETTO, CODSTAN;NUMSTANZA>
			
			// Collect keys from current floormap
			Object[] tstMpKeys = propCommon.floorMaps.get(buildingId).keySet().toArray();
			log.debug(" prop.floorMaps.get(buildingId).keySet() size -> " + tstMpKeys.length);
			
			// For every key in prop.floorMaps keyset confront with db data
			for (int c=0; c<tstMpKeys.length; c++) {
				
				sProp = propCommon.floorMaps.get(buildingId).get(tstMpKeys[c]);  // -> buildId;codBed;codRoom;guiRoom;xVal;yVal;DB
				log.trace(" sProp -> " + sProp);
				String[] aProp = sProp.split(";");
				String xyVals =  aProp[2] + ";" + aProp[3];
				
				// contained in PropFile and DB
				sCodStanNumStan = mapFloorDB.get(tstMpKeys[c]);
				if (sCodStanNumStan != null) {
					
					concat = buildingId + ";" + tstMpKeys[c] + ";" + sCodStanNumStan + ";" + xyVals + ";A";
					totalA++;
					mapFloorDB.remove(tstMpKeys[c]);
					
				// contained only in PropFile
				} else {
					
					totalP++;
					concat = buildingId + ";" + tstMpKeys[c] + ";" + aProp[1] + ";" + xyVals + ";P";
					
				}
				
				log.trace(" concat1 -> " + concat);
				floorMapList.add(createFMobjectFromString(concat));
			}
			
			// Whaterver is left in DB just print
			Object[] mapFloorDBKS = mapFloorDB.keySet().toArray();
			log.debug(" mapFloorDB.keySet().keySet() size -> " + mapFloorDBKS.length);
			
			for (int b=0; b<mapFloorDBKS.length; b++) {
				
				// contained in DB and Propfile
				sCodStanNumStan = mapFloorDB.get(mapFloorDBKS[b]);
			
				concat = buildingId + ";" + mapFloorDBKS[b] + ";" + sCodStanNumStan + ";" + b*12 + ";" + 450 + ";D";
				mapFloorDB.remove(mapFloorDBKS[b]);
				
				totalD++;
				log.trace(" concat2 -> " + concat);
				floorMapList.add(createFMobjectFromString(concat));
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		log.info(" totals A/P/D -> " + totalA+"/"+totalP+"/"+totalD + " for a grand total of -> " +  floorMapList.size());
		log.info(" beds mapped -> " + floorMapList.size());
		return floorMapList;
	}
	
	private FloorMap createFMobjectFromString(String strArr) {
		
		FloorMap fMap = new FloorMap();
		
		try {
			
			String[] sCoords = strArr.split(";");
			
			fMap.setBuilding(sCoords[0]);
			fMap.setBed(sCoords[1]);
			fMap.setCodStanza(sCoords[2]);
			fMap.setRoom(sCoords[3]);
			fMap.setxVal(sCoords[4]);
			fMap.setyVal(sCoords[5]);
			fMap.setStatus(sCoords[6]);
			
		} catch (Exception e) {
			
		}
		return fMap;
		
	}
	
	private List<FeatureMap> getFeatureMap(String buildingId) {
		
		FeatureMap fMap;
		String mp;
		Map<String,String> featTypeMap = getFeatureTypeMap();

		List<FeatureMap> featMapList = new ArrayList<FeatureMap>();
		try {
			//B1.floor_feat="I;509;10;10,S;515;11;11,T;508;12;12,B;500;13;13"
			mp = propCommon.getProperty(buildingId+".floor_feat");
			mp = mp.replaceAll("\"", "");
			String[] sp = mp.split(",");
			for (int i=0; i<sp.length; i++) {
				
				String[] sCoords = sp[i].split(";");
				
				fMap = new FeatureMap();
				fMap.setBuilding(buildingId);
				fMap.setFeatType(sCoords[0]);
				fMap.setFeatDesc(featTypeMap.get(fMap.getFeatType()));
				fMap.setRoom(sCoords[1]);
				fMap.setxVal(sCoords[2]);
				fMap.setyVal(sCoords[3]);
				featMapList.add(fMap); 
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return featMapList;
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
	
	private List<DoctorMap> getDoctorMap(String buildingId) {
		
		DoctorMap dMap;
		String mp;
		List<DoctorMap> docMapList = new ArrayList<DoctorMap>();

		try {
			mp = propCommon.getProperty(buildingId+".doctor");
			if (mp != null && mp.length() > 0) { 
				mp = mp.replaceAll("\"", "");
	
				String[] sp = mp.split(",");
				for (int i=0; i<sp.length; i++) {
					
					String[] sCoords = sp[i].split(";");
					// [doctor] DOCID; COLOR; DOCNAME; ROOMS; POLYPOINTS 
					//          A0Doc0;00ff1e;DSSA. ZANOTTI;123-134;635;423;635;347;822;346;1001;344;1004;424;738;418
					dMap = new DoctorMap();
					dMap.setBuilding(buildingId);
					dMap.setDocId(sCoords[0]);
					dMap.setColor(sCoords[1]);
					dMap.setDocName(sCoords[2]);
					dMap.setRooms(sCoords[3]);
					
					String polyPoints="";
					for (int j=4; j<sCoords.length; j++) {
						if (j == 4) {
							polyPoints = sCoords[j];
						} else {
							polyPoints = polyPoints + "," + sCoords[j];
						}
					};
										
					dMap.setPolyPoints(polyPoints);
					docMapList.add(dMap); 
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return docMapList;
	}
		
}
