package it.sbsoft.servlets;

import it.sbsoft.beans.*;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FloorList extends HttpServlet {
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static Properties prop = null;
    
    public FloorList() {
        super();
    }
    
    private void initProps() {
		
		String persistProp =  this.getInitParameter("persistProperties");
		System.out.println("persistProp = " + persistProp);
		if (persistProp != null && persistProp.compareTo("true") == 0 && prop != null) {
			System.out.println("Skip Configuring properties ...");
			return;
		}

		try {
			System.out.println("Configuring properties ...");
			prop = new Properties();
            //load a properties file
    		//String cHome = System.getProperty("catalina.home");
    		//cHome = cHome + "\\conf\\regina.properties";
    		ServletContext sc =  this.getServletContext();
    		String cHome = sc.getContextPath();
    		cHome = this.getServletContext().getRealPath("\\WEB-INF\\regina.properties");
    		System.out.println("realPath ->" + cHome );
    		prop.load(new FileInputStream(cHome));
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("FloorList into doGet");
		Bean2cli ret = new Bean2cli();
		
		List<Floor> floorList;
		Floor floorBean;
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		initProps();
		 
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
				floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
				floorBean.setFeatureMap(getFeatureMap(floorBean.getId()));
				floorBean.setDoctorMap(getDoctorMap(floorBean.getId()));
				floorList.add(floorBean);
				
			}
			
			ret.setRet2cli(floorList);
			out.print(gson.toJson(ret));
			System.out.println("FloorList exiting doGet [" + gson.toJson(ret) + "]");
			
		} catch (Exception e) {
			ret.setError(new Errore());
			ret.getError().setErrorCode("1");
			ret.getError().setErrorDesc("See Tomocat log files");
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private List<FloorMap> getMapCoordinates(String buildingId) {
		
		FloorMap fMap;
		String mp;
		List<FloorMap> floorMapList = new ArrayList<FloorMap>();
		mp = prop.getProperty(buildingId+".bed_map");
		mp = mp.replaceAll("\"", "");
		
		try {

			String[] sp = mp.split(",");
			for (int i=0; i<sp.length; i++) {
				
				String[] sCoords = sp[i].split(";");
				
				fMap = new FloorMap();
				fMap.setCodStanza(sCoords[0]);
				fMap.setRoom(sCoords[1]);
				fMap.setBed(sCoords[2]);
				fMap.setBuilding(sCoords[3]);
				 
				fMap.setxVal(sCoords[4]);
				fMap.setyVal(sCoords[5]);
				floorMapList.add(fMap); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return floorMapList;
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
