package it.sbsoft.servlets;

import it.sbsoft.beans.*;
import it.sbsoft.db.DBTools;
import it.sbsoft.utility.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class FloorList extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static PropertiesFile prop;
	static String simulatorMode;
	static String persistProp;
	static Logger log = LoggerUtils.getLogger("sbsoftware"); 
    
    public FloorList() {
        super();
    }
    
    public void init() {
    	
    	log.info("entered ");
    	
		simulatorMode = this.getInitParameter("simulatorMode");
		log.info(" simulatorMode = " + simulatorMode);
    	
		persistProp =  this.getInitParameter("persistProperties");
		log.info(" persistProp = " + persistProp);
		
		if (persistProp != null && persistProp.compareTo("true") == 0 && prop != null) {
			log.info("Skip Configuring properties ...");
			return;
		}
		
		prop = PropertiesFile.getPropertiesFile();
		
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info(" into doGet");
		Bean2cli ret = new Bean2cli();
		
		List<Floor> floorList;
		Floor floorBean;
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String maint = request.getParameter("maint");
		log.debug(" maint value -> " + maint);
		//initProps();
		 
		try {
			
			// Get list of floors
			String floorProp=prop.getProperty("Floors");
			String[] floorArr = floorProp.split(",");
			floorList = new ArrayList<Floor>();
			
			// For every floor do bed map
			for (int i=0; i<floorArr.length; i++ ){
				
				String floorId=floorArr[i];
				floorBean = new Floor();
				floorBean.setId(floorId);
				floorBean.setDescription(prop.getProperty(floorId+".desc"));
				floorBean.setImgSrc(prop.getProperty(floorId+".src"));
				//floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
				if ("true".compareTo(maint) == 0) {
					floorBean.setFloorMap(getMapCoordinatesFromDB(floorBean.getId()));
				} else {
					floorBean.setFloorMap(getMapCoordinatesFromFile(floorBean.getId()));
				}
				floorBean.setFeatureMap(getFeatureMap(floorBean.getId()));
				floorBean.setDoctorMap(getDoctorMap(floorBean.getId()));
				floorList.add(floorBean);
				
			}
			
			ret.setRet2cli(floorList);
			log.info(" exiting doGet ");
			log.debug(" returning -> [" + gson.toJson(ret) + "]");
			
		} catch (Exception e) {
			ret.setError(new Errore());
			ret.getError().setErrorCode("1");
			ret.getError().setErrorDesc("See Tomocat log files");
			e.printStackTrace();
		}
		
		out.print(gson.toJson(ret));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public List<FloorMap> getMapCoordinatesFromFile(String buildingId) {
		
		log.info(" called for buildingId -> " + buildingId);
		List<FloorMap> floorMapList = new ArrayList<FloorMap>();
		
		Map<String, String> map2 = prop.floorMaps.get(buildingId);
		String key, value, ret;
		
		try {
			
			Iterator<String> it = map2.keySet().iterator();
			
			while (it.hasNext()) {
				
				key = (String)it.next();
				value = map2.get(key);
				value = value.replaceAll("\"", "");
				
				ret = buildingId+";"+key+";"+value+";P";
				log.trace(" ret -> " + ret);
				floorMapList.add(createFMobjectFromString(ret));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info(" beds mapped -> " + floorMapList.size());
		return floorMapList;
	}
	
	public List<FloorMap> getMapCoordinatesFromDB(String buildingId) {
		
		log.info(" called for buildingId -> " + buildingId);
		DBTools db = new DBTools();
		Map<String, String> mapFloorDB;
		
		List<FloorMap> floorMapList = new ArrayList<FloorMap>();
		String roomRange, sProp, sCodStanNumStan, concat;
		int roomMin, roomMax;
		int totalA=0, totalP=0, totalD=0;
		

		try {
			
			// Collect room_range
			roomRange = prop.getProperty(buildingId+".room_range");
			roomMin = new Integer(roomRange.split(",")[0]);
			roomMax = new Integer(roomRange.split(",")[1]);
			
			// Call DB and collect bedInfo
			mapFloorDB = db.getBeds4Floor(roomMin, roomMax); // -> Map<CODLETTO, CODSTAN;NUMSTANZA>
			
			// Collect keys from current floormap
			Object[] tstMpKeys = prop.floorMaps.get(buildingId).keySet().toArray();
			log.debug(" prop.floorMaps.get(buildingId).keySet() size -> " + tstMpKeys.length);
			
			// For every key in prop.floorMaps keyset confront with db data
			for (int c=0; c<tstMpKeys.length; c++) {
				
				sProp = prop.floorMaps.get(buildingId).get(tstMpKeys[c]);  // -> buildId;codBed;codRoom;guiRoom;xVal;yVal;DB
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
			
				concat = buildingId + ";" + mapFloorDBKS[b] + ";" + sCodStanNumStan + ";" + b*20 + ";" + 450 + ";D";
				mapFloorDB.remove(mapFloorDBKS[b]);
				
				totalD++;
				log.trace(" concat2 -> " + concat);
				floorMapList.add(createFMobjectFromString(concat));
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
			mp = prop.getProperty(buildingId+".floor_feat");
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
		String mp = prop.getProperty("FeatureType");
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
			mp = prop.getProperty(buildingId+".doctor");
			if (mp != null) { 
				mp = mp.replaceAll("\"", "");
	
				String[] sp = mp.split(",");
				for (int i=0; i<sp.length; i++) {
					
					String[] sCoords = sp[i].split(";");
					
					dMap = new DoctorMap();
					dMap.setBuilding(buildingId);
					dMap.setDocId(sCoords[0]);
					
					String polyPoints="";
					for (int j=1; j<sCoords.length; j++) {
						if (j == 1) {
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
