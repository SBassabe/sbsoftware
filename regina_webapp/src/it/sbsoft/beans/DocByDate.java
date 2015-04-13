package it.sbsoft.beans;

import java.util.HashMap;
import java.util.Map;

public class DocByDate {

	private String date;
	private Map<String, DocByLoc> occByBed = new HashMap<String, DocByLoc>();
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, DocByLoc> getOccByBed() {
		return occByBed;
	}
	public void setOccByBed(Map<String, DocByLoc> occByBed) {
		this.occByBed = occByBed;
	}


}
