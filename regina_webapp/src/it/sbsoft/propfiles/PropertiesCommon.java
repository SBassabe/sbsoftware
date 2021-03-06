package it.sbsoft.propfiles;

import it.sbsoft.exceptions.SBException;
import it.sbsoft.utility.LoggerUtils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class PropertiesCommon extends Properties {
	
	  private static final long serialVersionUID = 5821562996047331039L;
	  private static PropertiesCommon ref;
	  public String cHome;
	  public Map<String, Map<String, String>> floorMaps;
	  static Logger log = LoggerUtils.getLogger("sbsoftware");
	  static Gson gson = new Gson();
	  
	  private PropertiesCommon() {}
	  
	  private void initializePropFile() {
		
		log.info("called");
		try {
			
	  		cHome = System.getProperty("catalina.home");
	  		cHome = cHome + "\\conf\\regina2.properties";
			System.out.println("using realPath ->" + cHome);
	  		log.info("using realPath ->" + cHome );
			ref.load(new FileInputStream(cHome));
			
		} catch (Exception e){
			log.info("Failed to load propeties file");
			e.printStackTrace();
		}
	  }
	 
	  public static synchronized PropertiesCommon getPropertiesFile() {
		
		//log.info("called");  
	    if (ref == null) {
	        // it's ok, we can call this constructor
	    	log.info("PropertiesFile getPropertiesFile() -> generating new object ...");
	        ref = new PropertiesCommon();
	        ref.initializePropFile();
	        ref.cleanFloorMaps();
	        
	    } else {
	    	//System.out.println("PropertiesFile getPropertiesFile() -> returning existing object ...");
	    }

	    return ref;
	  }
	  
	  public String getPropertySB(String propKey) throws SBException {
		  
		  //log.debug("called");
		  String ret;
		  
		  ret = this.getProperty(propKey);
		  if (ret == null) {
			  log.debug("Property: '" + propKey + "' not found, please fix this...");
			  throw new SBException("PROPFILE_MISSINGKEY");
		  }
		  return ret;
	  }
	  
	  public Set<String> getRoomKeySet(String floorId) throws SBException {
		  
		  Set<String> setBedKeySet = null;
		  try {
			  
			  setBedKeySet = new HashSet<String>(this.floorMaps.get((String) floorId).keySet());
			  log.info("keySet empty -> " + setBedKeySet.isEmpty());
			  
		  } catch(Exception e) {
			  e.printStackTrace();
			  throw new SBException("Broken KeySet");
		  }
		  return setBedKeySet;
	  }
	  
	  public void cleanFloorMaps() {
		  
			 log.info("called");
			  
			  try {
				  
				  String floorId, bedsStr, tmp;
				  String codstanza, room, bed, xval, yval;
				  String[] bedsStrArr, detailArr;
				  
				  // 733;3;143;A0;680;388
				  // floorMaps<"floorId", mp<"bedCode", "roomCode;roomGui;X;Y">>
				  Map<String, String> mp = new HashMap<String, String>();
				  floorMaps = new TreeMap<String, Map<String, String>>();
				  
				  // Get list of floors
				  String floorProp=ref.getProperty("Floors");
				  String[] floorArr = floorProp.split(",");
				  
				  // For every floor do map
				  for (int i=0; i<floorArr.length; i++ ){
					  
				     floorId=floorArr[i];
				     bedsStr = ref.getProperty(floorId+".bed_map");
				     bedsStr = bedsStr.replaceAll("\"", "");
				     log.debug("ref.getProperty(floorId+\".bed_map\"); -> " + bedsStr);
			
				     // codstanza, room, bed, building, xval, yval
				     //                                          CODLETTO, CODSTANZA, NUMSTANZA
				     // cleanFloorMaps detailArr asGson -> ["A0","186",    "698",     "19",     "229","353"]
				     bedsStrArr = bedsStr.split(",");
				     
				     // For every bed in floor do map
				     for (int j=0; j<bedsStrArr.length; j++) {
				    	 
				    	 detailArr = bedsStrArr[j].split(",")[0].split(";");
				    	 log.trace("detailArr asGson -> " + gson.toJson(detailArr));
				    	 if (detailArr.length < 5) continue;
				    	 codstanza=detailArr[2];
				    	 room=detailArr[3];
				    	 bed=detailArr[1];
				    	 //detailArr[3]; building
				    	 xval=detailArr[4];
				    	 yval=detailArr[5];		
				    	 // floorMaps<"floorId", mp<"bedCode", "roomCode;roomGui;X;Y">>
				    	 tmp=codstanza +";"+ room +";"+xval+";"+yval;
				    	 
				    	 log.trace("[codstanza;room;xval;yval] -> " + tmp);
				    	 mp.put(bed, tmp);
				    	 log.trace("mp asGson -> " + gson.toJson(mp));
				    	 
				     }
				     // Make map entries
				     floorMaps.put(floorId, new HashMap(mp));
				     mp.clear();	
				     
				  }
				  
			  } catch (Exception e) {
				  System.out.println("PropertiesFile cleanFloorMaps -> into Exception");
				  e.printStackTrace();
			  }
			  
		  }
}
