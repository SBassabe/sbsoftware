package it.sbsoft.servlets;

import com.google.gson.Gson;

import it.sbsoft.beans.Bean2cli;
import it.sbsoft.beans.DoctorMap;
import it.sbsoft.beans.Errore;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.utility.PropertiesFile;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class MaintSrvlt extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static PropertiesFile prop;
	static String cHome;
	static Logger log = LoggerUtils.getLogger("sbsoftware"); 
    
	public void init() {
		prop = PropertiesFile.getPropertiesFile();
		cHome = prop.cHome;
	}
	
    public MaintSrvlt() {
        super();
    }    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info("called");
		response.setContentType("text/html");
		
		String pKey, pVal, doc2del; 
		
		Bean2cli ret = new Bean2cli();
		PrintWriter out = response.getWriter();
		String act = "";
		act = request.getParameter("action");
		doc2del = request.getParameter("doc2del");
		act = act == null ? "" : act;
		doc2del = doc2del == null ? "" : doc2del;
		
		try {
			
			backupFile();
			
			ret.setError(new Errore());
			ret.getError().setErrorCode("0");

			String featureMap = request.getParameter("featureMap");
			String doctorMap = request.getParameter("doctorMap");
			String currFloor = request.getParameter("currFloor");
			String floorMap = request.getParameter("floorMap");
	
			log.info("MaintSrvlt param action --> " + act);
			log.info("MaintSrvlt param doc2del --> " + doc2del);
			log.info("MaintSrvlt param currFloor --> " + currFloor);
			log.info("MaintSrvlt param featureMap --> " + featureMap);
			log.info("MaintSrvlt param doctorMap --> " + doctorMap);
			log.info("MaintSrvlt param floorMap --> " + floorMap);
			
			if (featureMap != null && featureMap.length() > 0) {
				pKey = currFloor + ".floor_feat";
				pVal = prop.getProperty(pKey);
				if (pVal != null) prop.setProperty(pKey, featureMap);
			}

			if (doctorMap != null && doctorMap.length() > 0) {	
				pKey = currFloor + ".doctor";
				pVal = prop.getProperty(pKey);
				
				if (act.contains("newDoc")) {
					if (pVal.length() > 0) {
						doctorMap = pVal + "," + doctorMap;
					}
					
				} else if (act.contains("delDoc")) {
					doctorMap = deleteDoc(doc2del, pVal);
				}
				
				if (pVal != null) prop.setProperty(pKey, doctorMap);
			}
			
			if (floorMap != null && floorMap.length() > 0) {
				pKey = currFloor + ".bed_map";
				pVal = prop.getProperty(pKey);
				if (pVal != null) prop.setProperty(pKey, floorMap);
			}

			StringBuffer sb = new StringBuffer();
			sb.append("# 1 Configuration file for ReginaWeb app \n");
			sb.append("# 2 must be located in ${catalina.home}\\conf\\regina.properties \n");
			sb.append("# 3 Formats: \n");
			sb.append("# 4 [bed_map] CODSTAN ;NUMSTANZA ;CODLETTO ; IDSEDE ;X ;Y \n");
			sb.append("# 5 [floor_feat] TYPE; ROOM; X; Y \n");
			sb.append("# 6 [doctor] DOCID; POLYPOINTS \n");
			
			prop.store(new FileOutputStream(cHome), sb.toString());
			sortFile();
			
			prop.cleanFloorMaps();
			
		} catch (Exception e) {
			ret.getError().setErrorCode("1");
			ret.getError().setErrorDesc("See Tomocat log files");
			e.printStackTrace();
		}
		
		out.print(gson.toJson(ret));
		
	}
	
	private String deleteDoc(String key, String pval) {
		String ret = "";
		try {
			
			// pval -> A0Doc0;9900ff;DRSS. NAME;1-22;634;346;827;346;929;346;1005;343;1006;423;639;421,A0Doc2...
			
			String[] sp = pval.split(",");
			for (int i=0; i<sp.length; i++) {
				
				String[] sCoords = sp[i].split(";");
				// [doctor] DOCID; COLOR; DOCNAME; ROOMS; POLYPOINTS 
				//          A0Doc0;00ff1e;DSSA. ZANOTTI;123-134;635;423;635;347;822;346;1001;344;1004;424;738;418
				//if (sCoords[0] != key) {
				if (sCoords[0].compareTo(key) != 0) {	
					ret = ret + sp[i] + "," ;
				}
			}
			
			// Get rid of last ',' if it exists ....
			if (ret.length() > 0) {
				//char lastChar = ret.charAt(ret.length()-1);
				//String str = Character.toString(lastChar);
				
				String str = ret.substring(ret.length()-1, ret.length());
				if (",".contains(str)) {
					ret = ret.substring(0, ret.length()-1);
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret; 
	}
	
	
	
	private void backupFile() {
		log.info("called");
		try {
			Calendar cal = Calendar.getInstance();
			String DATE_FORMAT = "yyyyMMdd_hhmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			String timeStamp = sdf.format(cal.getTime());
			
			
			ArrayList<String> rows = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(cHome));
						
		    String s;
		    while((s = reader.readLine())!=null)
		        rows.add(s);
		    
		    FileWriter writer = new FileWriter(cHome+"_"+timeStamp+".txt");
		    for(String cur: rows)
		        writer.write(cur+"\n");
		    reader.close();
		    writer.close();
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MaintSrvlt sr = new MaintSrvlt();
		
		String str = "A01;9900ff;1-22;634;346;827,A02;9900ff;1-22;634;346;827,A03;9900ff;1-22;634;346;827";
		System.out.println(sr.deleteDoc("A01", str));
	};
	
	private void sortFile() {
		log.info("called");
		try {
		
			ArrayList<String> rows = new ArrayList<String>();
		    BufferedReader reader = new BufferedReader(new FileReader(cHome));
	
		    String s;
		    while((s = reader.readLine())!=null)
		        rows.add(s);
	
		    Collections.sort(rows);
	
		    FileWriter writer = new FileWriter(cHome);
		    for(String cur: rows)
		        writer.write(cur+"\n");
		    reader.close();
		    writer.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
