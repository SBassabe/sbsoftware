package it.sbsoft.servlets;

import it.sbsoft.beans.Bean2cli;
import it.sbsoft.beans.BedOccupancy;
import it.sbsoft.beans.Errore;
import it.sbsoft.beans.Floor;
import it.sbsoft.db.DBTools;
import it.sbsoft.exceptions.SBException;
import it.sbsoft.propfiles.PropertiesCommon;
import it.sbsoft.utility.LoggerUtils;

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
	private PropertiesCommon prop = PropertiesCommon.getPropertiesFile();
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
				
				String[] arrVals = ((String)occ.get(sCoords[1])).split(";");
				
				bOcc.setStatus(arrVals[4]); // Occupato
				bOcc.setGender(arrVals[0]);
				bOcc.setName(arrVals[3]);
				bOcc.setAltro(arrVals[5]);
				
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
		
		String A0mp = "733;3;143;A0;675;389,733;3;144;A0;709;389,736;4;155;A0;745;373,736;4;156;A0;764;384,736;4;198;A0;746;397,698;19;70;A0;979;254,698;19;71;A0;942;275,698;19;186;A0;942;251,699;21;72;A0;830;251,699;21;73;A0;830;273,699;21;188;A0;909;274,699;21;190;A0;909;251,700;22;74;A0;746;250,700;22;75;A0;771;274,700;22;76;A0;745;274,700;22;196;A0;771;250";
		//public static String A0mp = "733;3;143;A0;690;370,733;3;144;A0;740;370,736;4;155;A0;780;340,736;4;156;A0;780;360,736;4;198;A0;780;380,698;19;70;A0;1015;260,698;19;71;A0;990;260,698;19;186;A0;965;260,699;21;72;A0;853;260,699;21;73;A0;863;260,699;21;188;A0;873;260,699;21;190;A0;944;260,700;22;74;A0;770;240,700;22;75;A0;815;240,700;22;76;A0;770;270,700;22;196;A0;815;270";
		String A1mp = "661;103;1;A1;443;397,661;103;2;A1;453;375,662;104;3;A1;399;397,662;104;4;A1;413;377,663;105;5;A1;358;396,663;105;6;A1;369;374,664;106;7;A1;316;394,664;106;8;A1;329;372,665;107;9;A1;266;395,665;107;10;A1;281;375,666;108;11;A1;195;398,666;108;12;A1;209;380,667;110;13;A1;131;410,668;111;14;A1;52;411,668;111;15;A1;94;411,669;112;16;A1;52;323,670;114;17;A1;53;246,670;114;18;A1;52;269,671;119;19;A1;127;268,671;119;20;A1;141;250,672;121;21;A1;189;271,672;121;22;A1;203;249,673;123;23;A1;229;271,673;123;24;A1;241;252,674;124;25;A1;269;268,674;124;26;A1;283;249,675;125;27;A1;315;264,675;125;28;A1;332;247,676;126;29;A1;365;246,677;127;30;A1;414;247,677;127;31;A1;398;264,678;129;32;A1;450;258,678;129;33;A1;469;243,679;130;34;A1;498;260,679;130;35;A1;511;243,680;131;36;A1;542;261,680;131;37;A1;558;243,681;132;38;A1;588;263,681;132;39;A1;601;243,682;133;40;A1;658;244,682;133;41;A1;636;245,682;133;42;A1;637;268,683;134;43;A1;690;247,683;134;44;A1;690;270,684;136;45;A1;737;250,685;137;46;A1;789;247,685;137;192;A1;774;263,686;138;47;A1;823;262,687;140;48;A1;867;260,687;140;49;A1;867;282,688;143;50;A1;971;285,688;143;51;A1;971;261,689;149;52;A1;1083;390,689;149;53;A1;1083;412,690;150;54;A1;1008;411,690;150;55;A1;1008;389,691;151;56;A1;955;396,691;151;57;A1;954;373,692;152;58;A1;908;396,692;152;59;A1;908;374,693;153;60;A1;862;375,693;153;61;A1;862;397,694;154;62;A1;822;375,694;154;63;A1;822;397,695;156;64;A1;747;376,695;156;65;A1;747;398,696;157;66;A1;709;376,696;157;67;A1;708;397,697;158;68;A1;683;376,697;158;69;A1;683;397,751;159;202;A1;631;396";
		//public static String A1mp = "661;103;1;A1;35;200,661;103;2;A1;70;200,662;104;3;A1;105;200,662;104;4;A1;140;200,663;105;5;A1;175;200,663;105;6;A1;210;200,664;106;7;A1;245;200,664;106;8;A1;280;200,665;107;9;A1;315;200,665;107;10;A1;350;200,666;108;11;A1;385;200,666;108;12;A1;420;200,667;110;13;A1;455;200,668;111;14;A1;490;200,668;111;15;A1;525;200,669;112;16;A1;560;200,670;114;17;A1;595;200,670;114;18;A1;630;200,671;119;19;A1;665;200,671;119;20;A1;700;200,672;121;21;A1;35;300,672;121;22;A1;70;300,673;123;23;A1;105;300,673;123;24;A1;140;300,674;124;25;A1;175;300,674;124;26;A1;210;300,675;125;27;A1;245;300,675;125;28;A1;280;300,676;126;29;A1;315;300,677;127;30;A1;350;300,677;127;31;A1;385;300,678;129;32;A1;420;300,678;129;33;A1;455;300,679;130;34;A1;490;300,679;130;35;A1;525;300,680;131;36;A1;560;300,680;131;37;A1;595;300,681;132;38;A1;630;300,681;132;39;A1;665;300,682;133;40;A1;700;300,682;133;41;A1;35;400,682;133;42;A1;70;400,683;134;43;A1;105;400,683;134;44;A1;140;400,684;136;45;A1;175;400,685;137;46;A1;210;400,685;137;192;A1;245;400,686;138;47;A1;280;400,687;140;48;A1;315;400,687;140;49;A1;350;400,688;143;50;A1;385;400,688;143;51;A1;420;400,689;149;52;A1;455;400,689;149;53;A1;490;400,690;150;54;A1;525;400,690;150;55;A1;560;400,691;151;56;A1;595;400,691;151;57;A1;630;400,692;152;58;A1;665;400,692;152;59;A1;700;400,693;153;60;A1;35;400,693;153;61;A1;70;400,694;154;62;A1;105;400,694;154;63;A1;140;400,695;156;64;A1;175;400,695;156;65;A1;210;400,696;157;66;A1;245;400,696;157;67;A1;280;400,697;158;68;A1;315;400,697;158;69;A1;350;400,751;159;202;A1;385;400";
		String A2mp = "701;221;77;A2;35;200,701;221;78;A2;70;200,702;222;79;A2;105;200,702;222;80;A2;140;200,703;224;81;A2;175;200,703;224;82;A2;210;200,704;226;83;A2;245;200,704;226;84;A2;280;200,705;227;85;A2;315;200,705;227;86;A2;350;200,706;228;87;A2;385;200,706;228;88;A2;420;200,707;229;89;A2;455;200,707;229;90;A2;490;200,708;234;91;A2;525;200,708;234;92;A2;560;200,709;235;93;A2;595;200,709;235;94;A2;630;200,710;237;95;A2;665;200,710;237;96;A2;700;200,711;238;97;A2;35;300,711;238;98;A2;70;300,712;239;99;A2;105;300,712;239;100;A2;140;300,713;241;101;A2;175;300,713;241;102;A2;210;300,714;242;103;A2;245;300,714;242;104;A2;280;300,715;246;105;A2;315;300,715;246;106;A2;350;300,716;247;107;A2;385;300,716;247;108;A2;420;300,717;248;109;A2;455;300,717;248;110;A2;490;300,718;250;111;A2;525;300,719;252;112;A2;560;300,719;252;113;A2;595;300,720;253;114;A2;630;300,720;253;115;A2;665;300,720;253;116;A2;700;300,721;254;117;A2;35;400,721;254;118;A2;70;400,721;254;119;A2;105;400,722;255;120;A2;140;400,722;255;121;A2;175;400,722;255;122;A2;210;400,723;256;123;A2;245;400,723;256;124;A2;280;400,723;256;194;A2;315;400,724;257;125;A2;350;400,724;257;126;A2;385;400,724;257;200;A2;420;400,725;258;127;A2;455;400,725;258;128;A2;490;400,726;260;129;A2;525;400,726;260;130;A2;560;400,727;261;131;A2;595;400,727;261;132;A2;630;400,728;262;133;A2;665;400,728;262;134;A2;700;400,729;264;135;A2;35;400,729;264;136;A2;70;400,730;265;137;A2;105;400,730;265;138;A2;140;400,731;267;139;A2;175;400,731;267;140;A2;210;400,732;268;141;A2;245;400,732;268;142;A2;280;400";
		//public static String B1mp = "738;501;159;B1;245;186,738;501;160;B1;245;217,739;502;161;B1;314;186,739;502;162;B1;314;217,740;503;163;B1;389;186,740;503;164;B1;389;217,741;504;165;B1;453;179,741;504;166;B1;453;207,742;505;167;B1;588;176,742;505;168;B1;588;206,743;506;169;B1;655;179,743;506;170;B1;655;210,744;507;171;B1;726;180,744;507;172;B1;726;210,745;510;173;B1;800;189,745;510;174;B1;800;212,746;511;175;B1;863;165,746;511;176;B1;925;144,747;512;177;B1;925;173,747;512;178;B1;1007;135,748;514;179;B1;1002;164,748;514;180;B1;670;346,749;516;181;B1;703;345,749;516;182;B1;819;357,750;518;183;B1;849;357,750;518;184;B1;245;186";
		String B1mp = "738;501;159;B1;848;225,738;501;160;B1;851;200,739;502;161;B1;795;212,739;502;162;B1;779;231,740;503;163;B1;734;217,740;503;164;B1;749;232,741;504;165;B1;689;240,741;504;166;B1;708;255,742;505;167;B1;640;237,742;505;168;B1;657;255,743;506;169;B1;593;239,743;506;170;B1;609;259,744;507;171;B1;549;239,744;507;172;B1;562;258,745;510;173;B1;455;240,745;510;174;B1;469;260,746;511;175;B1;424;260,746;511;176;B1;406;247,747;512;177;B1;374;261,747;512;178;B1;357;247,748;514;179;B1;326;261,748;514;180;B1;310;246,749;516;181;B1;620;341,749;516;182;B1;642;351,750;518;183;B1;732;343,750;518;184;B1;748;359";

		
		System.out.println("buildId ->" + buildId);
		//CODSTAN ;	NUMSTANZA ;	CODLETTO ; IDSEDE ;	X ;	Y
		if (buildId.compareTo("A0") == 0) {
			// 733;3;143;A0;675;389,733;3;144;A0;709;389
			//mp = "733;3;143;A0;700;388,733;3;144;A0;740;388,736;4;155;A0;760;388,736;4;156;A0;780;388,736;4;198;A0;790;388,698;19;70;A0;980;265,698;19;71;A0;990;265,698;19;186;A0;1000;265,699;21;72;A0;970;265,699;21;73;A0;960;265,699;21;188;A0;950;265,699;21;190;A0;940;265,700;22;74;A0;770;240,700;22;75;A0;815;240,700;22;76;A0;770;270,700;22;196;A0;815;270";
			mp = A0mp;
		} else if (buildId.compareTo("A1") == 0) {
			mp = A1mp;
		} else if (buildId.compareTo("A2") == 0) {
			mp = A2mp;
		} else if (buildId.compareTo("B1") == 0) {
			mp = B1mp;
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
