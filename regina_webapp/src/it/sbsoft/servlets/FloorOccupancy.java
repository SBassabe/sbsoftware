package it.sbsoft.servlets;

import it.sbsoft.beans.Bean2cli;
import it.sbsoft.beans.BedOccupancy;
import it.sbsoft.beans.Errore;
import it.sbsoft.beans.Floor;
import it.sbsoft.db.DBTools;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.utility.LoggerUtils;
import it.sbsoft.utility.PropertiesFile;
import it.sbsoft.utility.constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class FloorOccupancy extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Random generator = new Random();
	static String simulatorMode;
	private PropertiesFile prop = PropertiesFile.getPropertiesFile();
	static Gson gson = new Gson();
	static Logger log = LoggerUtils.getLogger("sbsoftware"); 
	
    public FloorOccupancy() {
        super();
    }

    public void init() {
    	
    	log.info("entered ");
		simulatorMode = this.getInitParameter("simulatorMode");
		log.info(" simulatorMode = " + simulatorMode);
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Gson gson = new Gson();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		Bean2cli ret = new Bean2cli();
		Floor floor = new Floor();
		
		log.info(" into doGet => params as GSON: " + gson.toJson(request.getParameterMap()));
		
		String buildId = request.getParameter("buildId");
		String dt = request.getParameter("dt");
		
		try {
			
			floor.setId(buildId);
			floor.setDt(dt);
			if ("true".compareTo(simulatorMode) == 0) {
				floor.setOccMap(getOccupancySimu(buildId, dt));
			} else {
				floor.setOccMap(getOccupancyDB(buildId, dt));
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
		ret.setRet2cli(floor);
		log.debug(" returning -> [" + gson.toJson(ret) + "]");
		out.print(gson.toJson(ret));
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//log.debug(" into doPost "); 
		doGet(request, response);
	}

	private List<BedOccupancy> getOccupancy(String buildId, String dt) {
		
		String mp;
		List<BedOccupancy> bedOccMap = new ArrayList<BedOccupancy>();
		BedOccupancy bOcc;
		
		log.debug(" called with params buildId/dt ->" + buildId +"/" + dt);

		mp = prop.getProperty(buildId+".bed_map");
		log.debug("mp ->" + mp); //A0;143;733;3;0;200,A0;144;733;3;12;200,A0
		
		String[] sp = mp.split(",");
		for (int i=0; i<sp.length; i++) {
			
			String[] sCoords = sp[i].split(";");
			log.trace(" sCoords as Gson -> " + gson.toJson(sCoords));
			if (sCoords.length < 5) continue;
			bOcc = new BedOccupancy();
			bOcc.setBed(sCoords[1]);
			bOcc.setGender(generator.nextBoolean() ? "M" : "F");
			bOcc.setStatus(generator.nextInt(3)+"");
			bOcc.setName("Nome_" + i);
			bOcc.setSurname("Surname_" + i);
			
			bedOccMap.add(bOcc);
		}
		
		return bedOccMap;
	}
	
	private List<BedOccupancy> getOccupancyDB(String buildId, String dt) throws Exception {
		
		log.info(" called with params buildId/dt ->" + buildId +"/" + dt);
		
		String strBMap, sex;
		List<BedOccupancy> bedOccMap = new ArrayList<BedOccupancy>();
		BedOccupancy bOcc;
		
		strBMap = prop.getProperty(buildId+".bed_map");
		log.debug("strBMap ->" + strBMap); //A0;143;733;3;0;200,A0;144;733;3;12;200,A0
		
		if (strBMap == null) throw new SBException("PROP_NOTCONFIG");
		
		//dt = 20110116   I need -> Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff] 
		String year, day, month, timestamp;
		year = dt.substring(0, 4);
		month = dt.substring(4,6);
		day = dt.substring(6,8);
		//timestamp = year +"-"+ month +"-"+ day +" 00:00:00";
		timestamp = year +"-"+ month +"-"+ day;
				
//		String str = prop.floorMaps.get(buildId).keySet().toString();
//		str = str.replace("[", "");
//		str = str.replace("]", ""); 
		
		// Check to see if there are any beds configured ... 
		Set<String> setBedKeyset = new HashSet<String>(prop.floorMaps.get(buildId).keySet());
		log.debug(" setBedKeyset.toString -> " + setBedKeyset.toString());
		if (setBedKeyset.isEmpty()) throw new SBException("FLOORMAP_EMPTY");
		
		DBTools db = new DBTools();
		Map<String, String> occ;

		occ = db.getOcc4FloorByDate(setBedKeyset, timestamp);
	
		String[] sp = strBMap.split(",");
		for (int i=0; i<sp.length; i++) {
			
			String[] sCoords = sp[i].split(";");
			log.trace(" sCoords as Gson -> " + gson.toJson(sCoords));
			if (sCoords.length < 5) continue;
			bOcc = new BedOccupancy();
			bOcc.setBed(sCoords[1]);
			if (occ.containsKey(sCoords[1])) {
				bOcc.setStatus(occ.get(sCoords[1]).split(";")[4]); // Occupato
				sex = occ.get(sCoords[1]).split(";")[0];
				bOcc.setGender(sex);
				bOcc.setName(occ.get(sCoords[1]).split(";")[3]);
				
			} else {
				bOcc.setStatus("0"); // Libero
				bOcc.setName("");
			}
						
			log.trace(" bOcc as Gson -> " + gson.toJson(bOcc));
			bedOccMap.add(bOcc);
		}
				
		return bedOccMap;
	}
	
	private List<BedOccupancy> getOccupancySimu(String buildId, String dt) {
		
		String mp;
		List<BedOccupancy> bedOccMap = new ArrayList<BedOccupancy>();
		BedOccupancy bOcc;
		
		System.out.println("buildId ->" + buildId);
		//CODSTAN ;	NUMSTANZA ;	CODLETTO ; IDSEDE ;	X ;	Y
		if (buildId.compareTo("A0") == 0) {
			// 733;3;143;A0;675;389,733;3;144;A0;709;389
			//mp = "733;3;143;A0;700;388,733;3;144;A0;740;388,736;4;155;A0;760;388,736;4;156;A0;780;388,736;4;198;A0;790;388,698;19;70;A0;980;265,698;19;71;A0;990;265,698;19;186;A0;1000;265,699;21;72;A0;970;265,699;21;73;A0;960;265,699;21;188;A0;950;265,699;21;190;A0;940;265,700;22;74;A0;770;240,700;22;75;A0;815;240,700;22;76;A0;770;270,700;22;196;A0;815;270";
			mp = constants.A0mp;
		} else if (buildId.compareTo("A1") == 0) {
			mp = constants.A1mp;
		} else if (buildId.compareTo("A2") == 0) {
			mp = constants.A2mp;
		} else if (buildId.compareTo("B1") == 0) {
			mp = constants.B1mp;
		} else {
			mp = "200A;181;299,201A;289;166,201B;289;198,202A;377;171,202B;377;203,203A;441;167,203B;441;202,204A;504;171,204B;503;201,205A;559;167,205B;559;196,206A;623;163,206B;623;192,207A;700;166,207B;700;194,208A;749;167,208B;749;199,209A;820;162,209B;820;193,210A;905;163,210B;905;191,211A;1024;180,211B;1024;210,212A;1150;175,212B;1150;205,213A;1235;175,213B;1235;203,214A;1300;174,214B;1300;202,215A;1373;174,215B;1373;199,216A;1420;173,216B;1420;200,217A;1491;171,217B;1491;200,218A;1627;268,218B;1657;268,219A;1613;390,219B;1652;390,220A;1486;344,220B;1486;377,221A;1435;345,221B;1435;378,222A;1287;350,222B;1287;380,223A;1238;350,223B;1238;382,224A;1086;350,224B;1089;381,225A;1025;350,225B;1025;381,226A;737;344,226B;737;374,227A;616;348,227B;616;378,228A;501;343,228B;501;371,229A;387;370,230A;273;364,230B;273;394,231A;223;365,231B;223;394";
		}
		
		String[] sp = mp.split(",");
		for (int i=0; i<sp.length; i++) {
			
			String[] sCoords = sp[i].split(";");
			
			bOcc = new BedOccupancy();
			bOcc.setBed(sCoords[2]);
			bOcc.setGender(generator.nextBoolean() ? "M" : "F");
			bOcc.setStatus(generator.nextInt(3)+"");
			bOcc.setName("Nome_" + i);
			bOcc.setSurname("Surname_" + i);
			
			bedOccMap.add(bOcc);
		}
		
		return bedOccMap;
	}

}
