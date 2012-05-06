package it.sbsoft.db;

import it.sbsoft.utility.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.codehaus.jackson.map.ObjectMapper;

public final class dbExample
{
  static public void main (String args[]) throws Exception {
	  System.out.println(getValsRS());
  }
  
  @SuppressWarnings("finally")
  public static List getVals() {

	    String databaseURL = "jdbc:firebirdsql:localhost/3050:C:/FBDB/CBAOSPITIB.FDB";
	    String user = "sysdba";
	    String password = "masterkey";
	    
	    Connection c = null;
	    Statement s = null;
	    ResultSet rs = null;
	    List lst = new ArrayList();
	    
	    try {
	    	
	    	Class.forName ("org.firebirdsql.jdbc.FBDriver");
	        c = java.sql.DriverManager.getConnection (databaseURL, user, password);
	        c.setAutoCommit (false);
            s = c.createStatement ();
            rs = s.executeQuery ("select nomeospite from REGINA_LOGISTICA_V");
            while (rs.next ()) {
            	lst.add(rs.getString("nomeospite"));
                System.out.println (rs.getString ("nomeospite"));
            }
	          
	    } catch (Exception e) {
	    	  e.printStackTrace();
	    } finally {

	      // Now that we're all finished, let's release database resources.
	      try { if (rs!=null) rs.close (); } catch (java.sql.SQLException e) { e.printStackTrace(); }
	      try { if (s!=null) s.close (); } catch (java.sql.SQLException e) { e.printStackTrace(); }

	      // Before we close the connection, let's rollback any changes we may have made.
	      try { if (c!=null) c.rollback (); } catch (java.sql.SQLException e) { e.printStackTrace(); }
	      try { if (c!=null) c.close (); } catch (java.sql.SQLException e) { e.printStackTrace(); }
	      return lst;
	    }
  }
  
  @SuppressWarnings("finally")
  public static String getValsRS() {

	    String databaseURL = "jdbc:firebirdsql:localhost/3050:C:/FBDB/CBAOSPITIB.FDB";
	    String user = "sysdba";
	    String password = "masterkey";
	    
	    Connection c = null;
	    Statement s = null;
	    ResultSet rs = null;
	    String rsRet = null;
	    //ObjectMapper mapper = new ObjectMapper();
	    
	    try {
	    	
	    	Class.forName ("org.firebirdsql.jdbc.FBDriver");
	        c = java.sql.DriverManager.getConnection (databaseURL, user, password);
	        c.setAutoCommit (false);
            s = c.createStatement ();
            rs = s.executeQuery ("select nomeospite from REGINA_LOGISTICA_V");
			tools tls = new tools();
			JSONArray js = tls.convertResultSetToJSON(rs); 
	        rsRet = "Hello";
	        System.out.println(rsRet);
			
	    } catch (Exception e) {
	    	  e.printStackTrace();
	    } finally {
    	
	      // Now that we're all finished, let's release database resources. 
	      try { if (rs!=null) rs.close (); } catch (java.sql.SQLException e) { e.printStackTrace(); }
	      try { if (s!=null) s.close (); } catch (java.sql.SQLException e) { e.printStackTrace(); }

	      // Before we close the connection, let's rollback any changes we may have made.
	      try { if (c!=null) c.rollback (); } catch (java.sql.SQLException e) { e.printStackTrace(); }
	      try { if (c!=null) c.close (); } catch (java.sql.SQLException e) { e.printStackTrace(); }
	      return rsRet;
	    }
  }
}