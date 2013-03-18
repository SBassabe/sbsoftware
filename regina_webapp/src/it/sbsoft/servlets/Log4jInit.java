package it.sbsoft.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.sbsoft.utility.LoggerUtils;

public class Log4jInit extends HttpServlet {

	private static final long serialVersionUID = 1L;

    public void init() {
	 
	    String file = getInitParameter("log4j-init-file");
	    System.out.println("Log4jInit called ... looking for -> " + file); 
	    if(file != null) {
	       LoggerUtils.initLogger(file);
	    }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {}
 }