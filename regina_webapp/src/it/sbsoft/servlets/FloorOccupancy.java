package it.sbsoft.servlets;

import it.sbsoft.beans.Bean2cli;
import it.sbsoft.beans.Errore;
import it.sbsoft.beans.Occ2cli;
import it.sbsoft.beans.OccByBed;
import it.sbsoft.beans.OccByBedMap;
import it.sbsoft.cleaning.CleaningSchedule;
import it.sbsoft.db.DBTools;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.utility.DateHelper;
import it.sbsoft.utility.LoggerUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class FloorOccupancy extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	static String simulatorMode;
	private PropertiesCommon prop = PropertiesCommon.getPropertiesFile();
	static Gson gson = new Gson();
	static Logger log = LoggerUtils.getLogger("sbsoftware"); 
	static DBTools db = new DBTools();
	private static CleaningSchedule cSched;
	
    public FloorOccupancy() {
        super();
    }

    public void init() {
    	log.info("entered ");
		cSched = new CleaningSchedule();
		Connection conn = (Connection)getServletContext().getAttribute("connection");
		cSched.h2Help.initConn(conn);
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Gson gson = new Gson();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		Bean2cli ret = new Bean2cli();
		Occ2cli o2c = new Occ2cli();
		
		log.info(" into doGet => params as GSON: " + gson.toJson(request.getParameterMap()));
		
		String buildId = request.getParameter("buildId");
		String dt = request.getParameter("dt");
		String action = request.getParameter("action");
		String bed = request.getParameter("bed_num");
		String currVis = request.getParameter("currVis");
		
		try {
			
			ret.setDate(dt);
			if (action != null && bed != null) {
				// Used to get specific bed vacancy info
				ret.setRet2cli(db.getVacantBedRange(buildId, dt, bed));
			} else {
				
				// Used to get entire floor vacancy info
				o2c.setOccByBedMp(db.getOcc4FloorByDateMap(prop.getRoomKeySet(buildId), DateHelper.getDate4Query(dt)));
				
				if (currVis != null && "clean".compareTo(currVis) == 0) {
					OccByBedMap obbm = new OccByBedMap();
					obbm.setOccByBedMp(o2c.getOccByBedMp());
					o2c.setCleanByLocMp(cSched.getCleaningScheduleByDate(dt, buildId, obbm));
				}
				
				ret.setRet2cli(o2c);
				//ret.setRet2cli(getOccupancyMap(buildId, dt));
			}
			
		} catch (Exception e) {
			
			ret.setError(new Errore());
			
			if (e instanceof SBException){
				ret.getError().setErrorCode("1");
				ret.getError().setErrorDesc(e.getMessage());
			} else {
				ret.getError().setErrorCode("2");
				ret.getError().setErrorDesc("See Tomocat log files");
				e.printStackTrace();
			}
		}
		
		//normal response
		log.debug(" returning -> [" + gson.toJson(ret) + "]");
		out.print(gson.toJson(ret));
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//log.debug(" into doPost "); 
		doGet(request, response);
	}

	private Map<String, OccByBed> getOccupancyMap(String buildId, String dt) throws Exception {
		
		log.info(" called with params buildId/dt ->" + buildId +"/" + dt);			
		
		// Get configured beds for given floor
		Set<String> setBedKeyset = new HashSet<String>(prop.floorMaps.get(buildId).keySet());
		log.debug(" setBedKeyset.toString -> " + setBedKeyset.toString());
		if (setBedKeyset.isEmpty()) throw new SBException("FLOORMAP_EMPTY");

		//occ = db.getOcc4FloorByDate(setBedKeyset, timestamp);
		Map<String, OccByBed> occByBed = db.getOcc4FloorByDateMap(setBedKeyset, DateHelper.getDate4Query(dt));
	
		return occByBed; //bedOccMap;
	}

}
