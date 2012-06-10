package it.sbsoft.servlets;

import com.google.gson.Gson;

import it.sbsoft.beans.Bean2cli;
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
		
		Bean2cli ret = new Bean2cli();
		PrintWriter out = response.getWriter();
		
		try {
			
			backupFile();
			
			ret.setError(new Errore());
			ret.getError().setErrorCode("0");

			String featureMap = request.getParameter("featureMap");
			String doctorMap = request.getParameter("doctorMap");
			String currFloor = request.getParameter("currFloor");
			String floorMap = request.getParameter("floorMap");
	
			log.info("MaintSrvlt param currFloor --> " + currFloor);
			log.info("MaintSrvlt param featureMap --> " + featureMap);
			log.info("MaintSrvlt param doctorMap --> " + doctorMap);
			log.info("MaintSrvlt param floorMap --> " + floorMap);
			
			String pKey = currFloor + ".floor_feat";
			String pVal = prop.getProperty(pKey);
			if (pVal != null) prop.setProperty(pKey, featureMap);
			
			pKey = currFloor + ".doctor";
			pVal = prop.getProperty(pKey);
			if (pVal != null) prop.setProperty(pKey, doctorMap);
			
			pKey = currFloor + ".bed_map";
			pVal = prop.getProperty(pKey);
			if (pVal != null) prop.setProperty(pKey, floorMap);

			prop.store(new FileOutputStream(cHome), "myComment");
			sortFile();
			
			prop.cleanFloorMaps();
		
			
		} catch (Exception e) {
			ret.getError().setErrorCode("1");
			ret.getError().setErrorDesc("See Tomocat log files");
			e.printStackTrace();
		}
		
		out.print(gson.toJson(ret));
		
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
