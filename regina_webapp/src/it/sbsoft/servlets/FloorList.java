package it.sbsoft.servlets;

import it.sbsoft.beans.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FloorList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public FloorList() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Bean2cli ret = new Bean2cli();
		
		List<Floor> floorList;
		Floor floorBean;
		
		Gson gson = new Gson();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		try {
			
			// Create List Object
			floorList = new ArrayList<Floor>();
			
			// Do Building
			floorBean = new Floor();
			floorBean.setDescription("Edificio A Terra");
			floorBean.setId("A0");
			floorBean.setImgSrc("./images/EDIFICIO_A_PIANO_TERRA.jpg");
			
			floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
			floorList.add(floorBean);

			// Do Building
			floorBean = new Floor();
			floorBean.setDescription("Edificio A Piano Primo");
			floorBean.setId("A1");
			floorBean.setImgSrc("./images/EDIFICIO_A_PIANO_PRIMO.jpg");
			
			floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
			floorList.add(floorBean);
			
			// Do Building
			floorBean = new Floor();
			floorBean.setDescription("Edificio A Piano Secondo");
			floorBean.setId("A2");
			floorBean.setImgSrc("./images/EDIFICIO_A_PIANO_SECONDO.jpg");
			
			floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
			floorList.add(floorBean);

			// Do Building
			floorBean = new Floor();
			floorBean.setDescription("Edificio B Piano Rialzato");
			floorBean.setId("B1");
			floorBean.setImgSrc("./images/EDIFICIO_B_DEPENDANCE_PIANO_RIALZATO.jpg");
			
			floorBean.setFloorMap(getMapCoordinates(floorBean.getId()));
			floorList.add(floorBean);
			
			//normal response
			ret.setRet2cli(floorList);
			out.print(gson.toJson(ret));
			
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
		double scale=1;
		double xVal;
		
		if ("B1".compareTo(buildingId) == 0) {
			mp = "100A;245;186,100B;245;217,101A;314;186,101B;314;217,102A;389;186,102B;389;217,103A;453;179,103B;453;207,104A;588;176,104B;588;206,105A;655;179,105B;655;210,106A;726;180,106B;726;210,107A;800;189,107B;800;212,108A;863;165,109A;925;144,109B;925;173,110A;1007;135,110B;1002;164,111A;670;346,111B;703;345,111C;819;357,111D;849;357";
		} else {
			scale = 0.6;
			mp = "200A;181;299,201A;289;166,201B;289;198,202A;377;171,202B;377;203,203A;441;167,203B;441;202,204A;504;171,204B;503;201,205A;559;167,205B;559;196,206A;623;163,206B;623;192,207A;700;166,207B;700;194,208A;749;167,208B;749;199,209A;820;162,209B;820;193,210A;905;163,210B;905;191,211A;1024;180,211B;1024;210,212A;1150;175,212B;1150;205,213A;1235;175,213B;1235;203,214A;1300;174,214B;1300;202,215A;1373;174,215B;1373;199,216A;1420;173,216B;1420;200,217A;1491;171,217B;1491;200,218A;1627;268,218B;1657;268,219A;1613;390,219B;1652;390,220A;1486;344,220B;1486;377,221A;1435;345,221B;1435;378,222A;1287;350,222B;1287;380,223A;1238;350,223B;1238;382,224A;1086;350,224B;1089;381,225A;1025;350,225B;1025;381,226A;737;344,226B;737;374,227A;616;348,227B;616;378,228A;501;343,228B;501;371,229A;387;370,230A;273;364,230B;273;394,231A;223;365,231B;223;394";
		}
		
		String[] sp = mp.split(",");
		for (int i=0; i<sp.length; i++) {
			
			String[] sCoords = sp[i].split(";");
			
			fMap = new FloorMap();
			fMap.setBed(sCoords[0]);
			fMap.setBuilding(buildingId);
			fMap.setRoom("ROOM_"+i);
			fMap.setxVal(sCoords[1]);
			if ("2000".compareTo(buildingId) == 0) {
				xVal=new Integer(sCoords[1]);
				xVal=scale*xVal;
				fMap.setxVal(xVal+"");
			}
			
			fMap.setyVal(sCoords[2]);
			
			floorMapList.add(fMap);
		}
		
		return floorMapList;
	}
}
