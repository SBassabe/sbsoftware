package it.sbsoft.beans;

import java.util.HashMap;
import java.util.Map;

public class CleanByDate {

	private String date;
	private Map<String, CleanByLoc> cleanByLoc = new HashMap<String, CleanByLoc>();
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, CleanByLoc> getCleanByLoc() {
		return cleanByLoc;
	}
	public void setCleanByLoc(Map<String, CleanByLoc> cleanByLoc) {
		this.cleanByLoc = cleanByLoc;
	}

	
}
