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
	
	public static void main(String[] args) {
		
		DateHelper dh = new DateHelper();
		Date dt = new Date();
		System.out.println("Formated Date -> " + dh.getDtAsStr(dt));
	}

}
