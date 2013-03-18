package it.sbsoft.utility;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtils extends SecurityManager {
	
    public static Logger getLogger() {
    	
        String className = new LoggerUtils().getClassName();
        Logger logger = Logger.getLogger(className);
        return logger;
    }

    public static Logger getLogger(String className) {
        Logger logger = Logger.getLogger(className);
        return logger;
    }
    
    public static void initLogger(String file){
    	
        // if the log4j-init-file is not set, then no point in trying
        System.out.println("Log4jInit called ... looking for -> " + file); 
        if(file != null) {
        	PropertyConfigurator.configure(file);
        }
    }
    
    private String getClassName(){
        return getClassContext()[2].getName();
    }
}
