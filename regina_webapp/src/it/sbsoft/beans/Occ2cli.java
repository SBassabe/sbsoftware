package it.sbsoft.beans;

import java.util.Map;
import java.util.TreeMap;

public class Occ2cli {

	private Map<String, OccByBed> occByBedMp = new TreeMap<String, OccByBed>();
	private Map<String, CleanByLoc> cleanByLocMp = new TreeMap<String, CleanByLoc>();
	
	public Map<String, OccByBed> getOccByBedMp() {
		return occByBedMp;
	}
	public void setOccByBedMp(Map<String, OccByBed> occByBedMp) {
		this.occByBedMp = occByBedMp;
	}
	public Map<String, CleanByLoc> getCleanByLocMp() {
		return cleanByLocMp;
	}
	public void setCleanByLocMp(Map<String, CleanByLoc> cleanByLocMp) {
		this.cleanByLocMp = cleanByLocMp;
	}

}
