package it.sbsoft.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class DateHelper {

	protected static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
	protected static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
	protected static SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");
	protected static SimpleDateFormat sdfWd = new SimpleDateFormat("u");
	
	public static String getDtAsStr(Date dt) {
		
		String ret="";
		try {
			if (dt == null) {
	
				ret = "2030/01/01";
			} else {
				
				ret = sdf.format(dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String getWeekDay(String dt) {
		// given format 20130311
		String ret="";
		try {
				
			Date d = sdf2.parse(dt);
			
			switch (new Integer(sdfWd.format(d))) {
				case 7: ret = "DOM"; break;
				case 1: ret = "LUN"; break;
				case 2: ret = "MAR"; break;
				case 3: ret = "MER"; break;
				case 4: ret = "GIO"; break;
				case 5: ret = "VEN"; break;
				case 6: ret = "SAB"; break;
				default: ret= "---"; break;
			}
			

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
	
	public static List<String> getYearList() {
		
		List<String> yearList = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.YEAR, false);
		
		yearList.add(sdfY.format(cal.getTime()));
		System.out.println("Formated Date -> " + sdfY.format(cal.getTime()));
		cal.roll(Calendar.YEAR, true);
		
		yearList.add(sdfY.format(cal.getTime()));
		System.out.println("Formated Date -> " + sdfY.format(cal.getTime()));
		cal.roll(Calendar.YEAR, true);
		
		yearList.add(sdfY.format(cal.getTime()));
		System.out.println("Formated Date -> " + sdfY.format(cal.getTime()));
		return yearList;
		
	}
	
	public static void main(String[] args) {
		
		//Test1
		Date dt = new Date();
		System.out.println("Formated Date -> " + DateHelper.getDtAsStr(dt));
		//Test2
		System.out.println("Test2 -> " + DateHelper.getDate4Query("20130311"));
		//Test3
		DateHelper.getYearList();
		//Test4
		System.out.println("Test4 -> " + DateHelper.getWeekDay("20130311"));
	}

}
