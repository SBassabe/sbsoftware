package it.sbsoft.utility;

import it.sbsoft.beans.Bean2cli;
import it.sbsoft.beans.FileListObj;
//import it.sbsoft.beans.FileListObj.FileObj;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class HelloClass {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println("HelloWorld");
		String root = "/home/sbassabe/sandbox/months";
		Path p = Paths.get(root);
		
		System.out.println("p.toString() -> " + p.toString());
		p = Paths.get(root+FileSystems.getDefault().getSeparator()+"GENNAIO");
		System.out.println("p.toString() -> " + p.toString());
		
		System.out.println("Using FileHelper object ...");
		
		FileHelper fh = new FileHelper();
		//fh.printMonths();
		
		/*
		FileListObj flo = new FileListObj();
		Map<String, String> mp = fh.get3MonthFileList();
		
		String regx = "_dal_(\\d+)_al_(\\d+)";
		Pattern pat = Pattern.compile(regx);
		Matcher m;
		
		for (String s : mp.keySet()) {
			
			FileObj fo = flo.new FileObj();
			
			m = pat.matcher(s);
			while (m.find()) {
				fo.setDtFirst(m.group(1));
				fo.setDtLast(m.group(2));
			}
			
			fo.setFileUri(mp.get(s));
			flo.getMp().put(s, fo);
			System.out.println("s -> " + s);
		}
		*/
		
		
		Gson gson = new Gson();
		Bean2cli ret = new Bean2cli();
		
		ret.setRet2cli(fh.get3MonthFileList());
		System.out.println(gson.toJson(ret));
			
	}
}
