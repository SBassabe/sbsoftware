package it.sbsoft.servlets;

import org.apache.log4j.PropertyConfigurator;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.IOException;

public class Log4jInit extends HttpServlet {

  public
  void init() {
	 
    String prefix =  getServletContext().getRealPath("/");
    String file = getInitParameter("log4j-init-file");
    // if the log4j-init-file is not set, then no point in trying
    System.out.println("Log4jInit called ... looking for -> " + file); 
    if(file != null) {
      //PropertyConfigurator.configure(prefix+file);
    	PropertyConfigurator.configure(file);
    }
  }

  public
  void doGet(HttpServletRequest req, HttpServletResponse res) {
  }
}