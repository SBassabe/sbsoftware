package it.sbsoft.servlets;

import com.google.gson.Gson;

import it.sbsoft.beans.Bean2cli;
import it.sbsoft.beans.Errore;
import it.sbsoft.beans.FeatureMap;
import it.sbsoft.beans.Floor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MaintSrvlt extends HttpServlet {
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static Properties prop = null;
	static String cHome;
    
    public MaintSrvlt() {
        super();
    }
    
    private void initProps() {
		
		String persistProp =  this.getInitParameter("persistProperties");
		System.out.println("MaintSrvlt -> persistProp = " + persistProp);
		if (persistProp != null && persistProp.compareTo("true") == 0 && prop != null) {
			System.out.println("Skip Configuring properties ...");
			return;
		}

		try {
			System.out.println("Configuring properties ...");
			prop = new Properties();
            //load a properties file
    		cHome = System.getProperty("catalina.home");
    		cHome = cHome + "\\conf\\regina.properties";
    		//ServletContext sc =  this.getServletContext();
    		//cHome = sc.getContextPath();
    		//cHome = this.getServletContext().getRealPath("\\WEB-INF\\regina.properties");
    		System.out.println("realPath ->" + cHome );
    		prop.load(new FileInputStream(cHome));
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("FloorList into doPost");
		response.setContentType("text/html");
		
		Bean2cli ret = new Bean2cli();
		PrintWriter out = response.getWriter();
	    initProps();
		
		try {
			
			backupFile();
			
			ret.setError(new Errore());
			ret.getError().setErrorCode("0");

			String featureMap = request.getParameter("featureMap");
			String doctorMap = request.getParameter("doctorMap");
			String currFloor = request.getParameter("currFloor");
			String floorMap = request.getParameter("floorMap");
	
			System.out.println("FloorList param currFloor --> " + currFloor);
			System.out.println("FloorList param featureMap --> " + featureMap);
			System.out.println("FloorList param doctorMap --> " + doctorMap);
			System.out.println("FloorList param floorMap --> " + floorMap);
			
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
			
		} catch (Exception e) {
			ret.getError().setErrorCode("1");
			ret.getError().setErrorDesc("See Tomocat log files");
			e.printStackTrace();
		}
		
		out.print(gson.toJson(ret));
		
	}
	
	private void backupFile() {
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
