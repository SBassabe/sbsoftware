package it.sbsoft.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DocOccupancyHelper {

	// Map<FloorId, Map<DateRange, List<DocBean>>>
	private Map<String, Map<String, List<DocBean>>> mpDoc = new TreeMap<String, Map<String, List<DocBean>>>();
	
	public class DocBean {
		String idDoc;
		String idRoom;
		String polyPoints;
	}
	
	public DocOccupancyHelper() {}


	public static void main(String[] args) {
		// Please test me often
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			
			String floorId, dtRange = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			Calendar cal = Calendar.getInstance();
			System.out.println("cal -> " + sdf.format(cal.getTime()));

			List<DocBean> lst = null;
			
			DocOccupancyHelper doh = new DocOccupancyHelper();
			// 5 Floors			
			for (int i=0; i<5; i++) {
				
				floorId="FLOOR_0"+i;
				Map<String, List<DocBean>> inMap = new TreeMap<String, List<DocBean>>();
				
				// 5 Date Ranges
				for (int j=0; j<5; j++) {
					
					dtRange=sdf.format(cal.getTime());
					cal.add(Calendar.DATE, 5);
					if (j==4) {
						dtRange=dtRange+"_NULL";
					} else {
						dtRange=dtRange+"_"+sdf.format(cal.getTime());
					}
					cal.add(Calendar.DATE, 1);
					
					lst = new ArrayList<DocBean>();
					
					// 3 doctor details
					for (int k=0; k<2; k++) {
						DocBean db = doh.new DocBean();
						db.idDoc="doc_"+k;
						db.idRoom="Room_"+k;
						db.polyPoints="12,34,45,56";
						
						lst.add(db);
					}
				    inMap.put(dtRange.toString(), lst);
				}
				doh.mpDoc.put(floorId, inMap);
			}
			
			System.out.println(gson.toJson(doh.mpDoc));
			
			//Just get keys
			System.out.println(gson.toJson(doh.mpDoc.keySet()));
			
			for (String str : doh.mpDoc.keySet()) {
				Map mp = (Map)doh.mpDoc.get(str);
				System.out.println(str + gson.toJson(mp.keySet()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
