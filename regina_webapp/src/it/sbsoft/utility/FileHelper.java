package it.sbsoft.utility;

import it.sbsoft.beans.FileListObj;
//import it.sbsoft.beans.FileListObj.FileObj;

import it.sbsoft.beans.FileListObj.FileObj;
import it.sbsoft.propfiles.PropertiesCommon;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {

 	private String rootPath;                 //RootPath from properties file
	private String pattern;                  //Pattern from properties file
	//private Map<String, String> month_year;  //The 3 months
	private Set<DtObj> dtObjSet;
	private Calendar cal;                    //The current date
	private FileVisitorObj fvo;
	private int id=0;
	
	class DtObj {
		String monthUpper = "";
		String monthLower = "";
		String monthYear = "";
		String year = "";
		String mm_year = "";
	}
	
	public FileHelper() {
		
		
		try {
			
			PropertiesCommon pc = PropertiesCommon.getPropertiesFile();
			// Calculate months: where CM=CurrentMonth. 1)CM-1,  2)CM,  3)CM+1
			//month_year = new LinkedHashMap<String, String>();
			dtObjSet = new LinkedHashSet<DtObj>();
			Locale itLoc = Locale.ITALIAN;
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -2);
			//String my = "";
			DtObj dtO;
			
			for (int i=1; i <= 3; i++) {
				
				dtO = new DtObj();
				cal.add(Calendar.MONTH, 1);
				dtO.monthLower = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, itLoc).toLowerCase();
				dtO.monthUpper = dtO.monthLower.toUpperCase();
				dtO.year = cal.get(Calendar.YEAR)+"";
				dtO.mm_year = dtO.monthLower + "_" + dtO.year;
				dtObjSet.add(dtO);
				
				//my = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, itLoc).toLowerCase() +"_"+cal.get(Calendar.YEAR);
				//month_year.put(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, itLoc).toUpperCase(), my);
			}
			
			// Get statics from Properties File
			rootPath = pc.getPropertySB("Excel.path");//"C:\\PCMigration_private\\SBSoftware\\sbsoftware\\Archive\\ExcelWork Dir\\months";
			pattern =  pc.getPropertySB("Excel.fnRegx");//"Tabella_Base_dal_[0-3][0-9]_al_[0-3][0-9]_@month_year@.xls";
			
			// Create FileVisitor object (for pattern matching)
			fvo = new FileVisitorObj();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FileListObj get3MonthFileList() {
		
		Path p = null;
		FileListObj ret = new FileListObj();
		
		try {
		
			// Populate object SetCollection inside FVO object
			//for (String d : month_year.keySet()) {
			for (DtObj d : dtObjSet) {
				p = Paths.get(rootPath+FileSystems.getDefault().getSeparator()+d.monthUpper+" "+d.year);
				fvo.setMatcher(pattern.replace("@month_year@", d.mm_year));
				System.out.println("walking tree using path -> " + p.getFileName());
				Files.walkFileTree(p, fvo);
			}
			
			// Process object to get start and end dates
			id = 0;
			for (String s : fvo.st.keySet()) {
				ret.getMp().put(s, populateFileObject(s, fvo.st.get(s)));
			}
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private FileObj populateFileObject(String filename, FileObj fo) {
		
		//FileListObj.FileObj fo = new FileListObj.FileObj();
		String regx = "";
		Pattern pat = null;
		Matcher m = null;
		
		try {
		
			regx = PropertiesCommon.getPropertiesFile().getPropertySB("Excel.dtRegx"); //"_dal_(\\d+)_al_(\\d+)_([a-z]+)_(\\d{4})"; // get from Properties file
			pat = Pattern.compile(regx);
			m = pat.matcher(filename);
			while (m.find()) {
				fo.setDtFirst(m.group(1));
				fo.setDtLast(m.group(2));
				fo.setMonth(m.group(3));
				fo.setYear(m.group(4));
			}
			
			//dateFormat: Display the date in ISO format. Produces "2007-01-26". "dd/mm/yy"
			fo.setDtFirst(fo.getYear()+DateHelper.get2DigitMonth(fo.getMonth())+fo.getDtFirst());
			fo.setDtLast(fo.getYear()+DateHelper.get2DigitMonth(fo.getMonth())+fo.getDtLast());
			fo.setId(++id +"");
			//fo.setFileUri(strUrl);
			
		} catch (Exception e) {
			
		}		
		return fo;
		
	}
	
}
