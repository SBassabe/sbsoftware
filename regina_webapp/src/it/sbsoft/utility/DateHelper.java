package it.sbsoft.utility;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DateHelper {

	protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
	
	public String getDtAsStr(Date dt) {
		
		String ret="";
		try {
			ret = sdf.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String getDate4Query(String str) {
		// from 20130311 to 2013-03-11
		String ret = "";
		ret=str.substring(0, 4)+"-"+str.substring(4, 6)+"-"+str.substring(6, 8);
		return ret;		
	}
	
	public static void main(String[] args) {
		
		//Test1
		DateHelper dh = new DateHelper();
		Date dt = new Date();
		System.out.println("Formated Date -> " + dh.getDtAsStr(dt));
		//Test2
		System.out.println("Test2 -> " + DateHelper.getDate4Query("20130311"));
		
	}

}
