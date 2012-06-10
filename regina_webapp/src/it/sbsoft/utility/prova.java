package it.sbsoft.utility;

import it.sbsoft.db.DBTools;
import it.sbsoft.servlets.FloorList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class prova {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String act = args[0];
		System.out.println("arg[0] -> " + act);
		
		PropertiesFile prop = PropertiesFile.getPropertiesFile();
		DBTools db = new DBTools();
	
		
		if ("doFloorList".compareTo(act) == 0) {
			
			System.out.println("prop.floorMaps.toString -> " + prop.floorMaps.toString());
			System.out.println("prop.floorMaps.keySet().toArray() -> " + prop.floorMaps.keySet().toString());
			System.out.println("prop.floorMaps.get(A0) -> " + prop.floorMaps.get("A0"));
			
		} else if ("do_DB".compareTo(act) == 0 ) {
			
			
			String dt, year, day, month, timestamp;
			dt="20110116";
			year = dt.substring(0, 4);
			month = dt.substring(4,6);
			day = dt.substring(6,8);
			timestamp = year +"-"+ month +"-"+ day +" 00:00:00";
			
			System.out.println(timestamp);
			
			Set st = prop.floorMaps.get("A1").keySet();
			String str = st.toString();
			str = str.replace("[", "");
			str = str.replace("]", "");
			System.out.println(str);
			
			//db.getOcc4FloorByDate(str, "2012-02-01 00:00:00.00");
		
		} else if ("else".compareTo(act) == 0 ) {
			
				
			
			Object[] obj = prop.floorMaps.keySet().toArray();
			
			for (int i=0 ; i<obj.length; i++) {
				
				System.out.println("obj.toString() -> " + obj[i].toString());
				
				Map<String, String> mp2 = (Map<String, String>)prop.floorMaps.get(obj[i].toString());
				Iterator it2 = mp2.keySet().iterator();
				
				//System.out.println("mp2.keySet().toArray() -> " + mp2.keySet().toString());
				while (it2.hasNext()) {
					
					String str = (String)it2.next();
					System.out.println("mp2.get(" + str + ") -> " + mp2.get(str));
				}
			}
			
			// String str = prop.getMapGivenFloorAndBed("B1", "160");
			//System.out.println("prop.getMapGivenFloorAndBed -> " + str);
			

			//System.out.println("db.getBedMap4Floor -> " + db.getBedMap4Floor(0, 100));
			
			
		//		Iterator it = prop.floorMaps.keySet().iterator();
		//		
		//		while (it.hasNext()) {
		//			
		//			Map<String, String> mp2 = (Map<String, String>)prop.floorMaps.get(it.next());
		//			Iterator it2 = mp2.keySet().iterator();
		//			
		//			System.out.println("mp2.keySet().toArray() -> " + mp2.keySet().toString());
		//			while (it2.hasNext()) {
		//				
		//				String str = (String)it2.next();
		//				System.out.println("(String)it2.next() -> " + str);
		//				System.out.println("mp2.get(" + str + ") -> " + mp2.get(str));
		//			}
		//			
		//			System.out.println("mp2.keySet().toArray() -> " + mp2.keySet().toString());
		//		}
			
		}
	}

}
