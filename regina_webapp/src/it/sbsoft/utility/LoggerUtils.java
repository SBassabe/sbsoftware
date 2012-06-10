package it.sbsoft.utility;

import org.apache.log4j.Logger;

public class LoggerUtils extends SecurityManager
{
    public static Logger getLogger()
    {
        String className = new LoggerUtils().getClassName();
        Logger logger = Logger.getLogger(className);
        return logger;
    }

    public static Logger getLogger(String className)
    {
        Logger logger = Logger.getLogger(className);
        return logger;
    }
    
    private String getClassName()
    {
        return getClassContext()[2].getName();
    }
}
