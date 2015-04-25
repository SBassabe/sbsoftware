package it.sbsoft.servlets;

import it.sbsoft.beans.*;
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


public class CleaningSrvlt extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    static Gson gson = new Gson();
	static PropertiesCommon propCommon;
	static PropertiesRooms propRooms;
	static String simulatorMode;
	static String persistProp;
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	static Set<String> availBeds;
	static Connection conn;
	private static CleaningSchedule cSched;
	
    public CleaningSrvlt() {
        super();
    }
    
    public void init() {
    	
    	log.info("entered ");
		
		propRooms = PropertiesRooms.getPropertiesFile();
		propCommon = PropertiesCommon.getPropertiesFile();
		cSched = new CleaningSchedule();
		Connection conn = (Connection)getServletContext().getAttribute("connection");
		cSched.h2Help.initConn(conn);
		
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info(" into doGet => params as GSON: " + gson.toJson(request.getParameterMap()));
		Bean2cli ret = new Bean2cli();		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String floor="noval", date="nodate", action="noval";
		Object retObj = new Object();
		CleanByLoc cbl;
		
		try {
			
			String rqst = request.getParameter("request");;
			Bean2serv b2s = new Bean2serv();
			b2s = gson.fromJson(rqst, Bean2serv.class);
			if (b2s != null) {
				floor = b2s.getFloor();
				action = b2s.getAction();
				date = b2s.getDate(); // format -> YYYY-MM-DD 2013-03-11
				cbl = b2s.getCbl();
			}
			
			if (action.compareToIgnoreCase("get") == 0) {
				
				OccByBedMap obbm = cSched.getAllRoomOcc4GivenDTFloor(date, floor);
				retObj = cSched.getCleaningScheduleByDate(date, floor, obbm);
				
			} else if (action.compareToIgnoreCase("save") == 0) {
				
				Map<String, CleanByLoc> cblMap = new HashMap<String, CleanByLoc>();
				cblMap.put(b2s.getCbl().getLoc_id(), b2s.getCbl());
				b2s.setCleanByLocMap(cblMap);
				
				if (b2s.getCleanByLocMap() != null) {
					cSched.mergeCleaningDays(b2s.getCleanByLocMap(), floor, date);
				}
				
			} else if (action.compareToIgnoreCase("excel") == 0) {
				
				    //cSched.mergeCleaningDays(b2s.getCleanByLocMap(), floor, date);
				    //cSched.populateTempTable(
				    //cSched.calculateRoomOccupancy(
				    //cSched.getAllRoomOcc4GivenDT(date)), date);
				    System.out.println("getExcelChosenFile -> " + b2s.getExcelChosenFile());
				    
				    ret.setMessage(cSched.addDay2Excel(b2s.getDtSet(), b2s.getExcelChosenFile()));
				    
			} else if (action.compareToIgnoreCase("getExcelFileList") == 0) {
				
				FileHelper fh = new FileHelper();
				retObj = fh.get3MonthFileList();
					
			} else if (action.compareToIgnoreCase("test") == 0) {
				
				    cSched.populateTempTable(cSched.calculateRoomOccupancy(cSched.getAllRoomOcc4GivenDT(date)), date);  ;
				    
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
			
			ret.setRet2cli(retObj);
			log.info(" exiting doGet ");
			log.debug(" returning -> [" + gson.toJson(ret) + "]");
			out.print(gson.toJson(ret));
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
