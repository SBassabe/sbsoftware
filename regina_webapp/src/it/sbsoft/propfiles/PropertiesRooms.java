package it.sbsoft.propfiles;

import it.sbsoft.exceptions.SBException;
import it.sbsoft.utility.LoggerUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import com.google.gson.Gson;

public class PropertiesRooms extends Properties {
	
	  private static final long serialVersionUID = 5821562996047331039L;
	  private static PropertiesRooms ref;
	  public String cHome;
	  public Map<String, Map<String, String>> floorMaps;
	  static Logger log = LoggerUtils.getLogger("sbsoftware");
	  static Gson gson = new Gson();
	  
	  private PropertiesRooms() {}
	  
	  private void initializePropFile() {
		
		log.info("called");
		try {
			
	  		cHome = System.getProperty("catalina.home");
	  		cHome = cHome + "\\conf\\reginaRooms.properties";
			System.out.println("using realPath ->" + cHome);
	  		log.info("using realPath ->" + cHome );
			ref.load(new FileInputStream(cHome));
			
		} catch (Exception e){
			log.info("Failed to load propeties file");
			e.printStackTrace();
		}
	  }
	 
	  public static synchronized PropertiesRooms getPropertiesFile() {
		
		log.info("called");  
	    if (ref == null) {
	        // it's ok, we can call this constructor
	    	System.out.println("PropertiesFile getPropertiesFile() -> generating new object ...");
	        ref = new PropertiesRooms();
	        ref.initializePropFile();
	        
	    } else {
	    	System.out.println("PropertiesFile getPropertiesFile() -> returning existing object ...");
	    }

	    return ref;
	  }
	  
	  public String getPropertySB(String propKey) throws SBException {
		  
		  log.debug("called");
		  String ret;
		  
		  ret = this.getProperty(propKey);
		  if (ret == null) {
			  log.debug("Property: '" + propKey + "' not found, please fix this...");
			  throw new SBException("PROPFILE_MISSINGKEY");
		  }
		  return ret;
	  }
	  
	  public void sortFile() {
		log.info("called");
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
	  
	 public List<String> getKeyListForPrefix(String pref) {
		 
		 List<String> ret = new ArrayList<String>();
		 try {
			 
			 Set<Object> st = super.keySet();
			 for (Object o : st) {
				 String key = (String)o;
				 if (key != null && key.startsWith(pref)) {
					 ret.add(key);
				 }
			 }
			 
		 } catch(Exception e) {
			 e.printStackTrace();
		 }
		 return ret;
	 }
	 
	 public static void main (String[] args) {
		 
		 // Test me often...
		 System.setProperty("catalina.home", "C:\\Program Files\\apache-tomcat-7.0.14");
		 PropertiesRooms pr = PropertiesRooms.getPropertiesFile();
		 
		 List<String> lst = pr.getKeyListForPrefix("A0");
		 for (String s : lst) { System.out.println("s-> " + s);}
	 }
}
