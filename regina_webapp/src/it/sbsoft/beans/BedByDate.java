package it.sbsoft.beans;

import java.util.HashMap;
import java.util.Map;

public class BedByDate {
	
	private String date;
	private Map<String, OccByBed> occByBed = new HashMap<String, OccByBed>();
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, OccByBed> getOccByBed() {
		return occByBed;
	}
	public void setOccByBed(Map<String, OccByBed> occByBed) {
		this.occByBed = occByBed;
	}	
}
