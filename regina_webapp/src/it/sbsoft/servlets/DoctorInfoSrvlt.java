package it.sbsoft.servlets;

import it.sbsoft.beans.*;
import it.sbsoft.doctors.DocOccupancy;
import it.sbsoft.propfiles.PropertiesRooms;
import it.sbsoft.utility.DateHelper;
import it.sbsoft.utility.LoggerUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * Servlet implementation class DoctorInfo
 */
public class DoctorInfoSrvlt extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	static Gson gson = new Gson();
	static Logger log = LoggerUtils.getLogger("sbsoftware");
	private static DocOccupancy dOcc = new DocOccupancy();
       
    public DoctorInfoSrvlt() {
        super();
    }

	@Override
	public void init() throws ServletException {
	
		log.info("initialize servlet");
		Connection conn = (Connection)getServletContext().getAttribute("connection");
		dOcc.h2Help.initConn(conn);
		dOcc.deleteAllTables();
		dOcc.createMissingTables();
		dOcc.populateTables();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Gson gson = new Gson();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Bean2cli ret = new Bean2cli();
		List<DoctorInfoBean> dibList=null;
		
		try {
			
			String floor = request.getParameter("floor");
			String date = request.getParameter("date");
			String action = request.getParameter("action");
			String rqst = request.getParameter("request");
			
			Bean2serv b2s = new Bean2serv();
			b2s = gson.fromJson(rqst, Bean2serv.class);
			if (b2s != null) {
				floor = b2s.getFloor();
				action = b2s.getAction();
				date = b2s.getDate(); // format -> YYYY-MM-DD 2013-03-11
			}
			
//			String url = "jdbc:h2:file:" + System.getProperty("catalina.home") + "\\h2db\\localdb";
//			String usr = "sa";
//			String pwd = "";
//			dOcc.h2Help.initConn(url, usr, pwd);
			
			if (action.compareToIgnoreCase("get") == 0) {
				
				if (floor != null) {
					
					String prKey="";
					String prVal="";
					
					dibList = dOcc.getRoomMapByFloor(floor);
					PropertiesRooms pr = PropertiesRooms.getPropertiesFile();
					for (DoctorInfoBean dib : dibList) {
						prKey=floor+"_"+dib.getNumStanza();
						prVal = pr.getProperty(prKey);
												
						if (prVal == null) {
							prVal = "47,66,64,67,92,104,93,119,18,119,17,105"; // default shape
						}
						dib.setPolyPoints(prVal.split(","));
					}
					ret.setRet2cli(dibList);
				}
				
			} else if (action.compareToIgnoreCase("save") == 0) {
				
				String prKey="";
				PropertiesRooms pr = PropertiesRooms.getPropertiesFile();
				for (DoctorInfoBean dib : b2s.getDoctorMap()) {
					prKey=floor+"_"+dib.getNumStanza();
					pr.setProperty(prKey, dib.getPolyPointsAsString());	
				}
			    pr.store(new FileOutputStream(pr.cHome), "#justcomment");
			    pr.sortFile();
			
			} else if (action.compareToIgnoreCase("occ") == 0) {
				
				//SELECT  max(gmadal),  min(gmaal) FROM DOCDTMAP where flOORID = 'A0' and (GMADAL <= cast('2012-09-13' as date) and  GMAAL >= cast('2012-09-13' as date))
				//SELECT * FROM DOCDTMAP where flOORID = 'A0' and (GMADAL <= cast('2012-06-24' as date) and  GMAAL >= cast('2012-06-24' as date))
				// TODO: create new method to get MIN_MAX info
				
				dibList = dOcc.getDoctorOccupancy(floor, DateHelper.getDate4Query(date));
				ret.setFloor(floor);
				ret.setDate(date);
				ret.setRet2cli(dibList);
			
			} else if (action.compareToIgnoreCase("refresh") == 0) {
				
				dOcc.purgeAllTables();
				dOcc.populateTables();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info(" exiting doGet ");
		log.debug(" returning -> [" + gson.toJson(ret) + "]");
		out.print(gson.toJson(ret));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
