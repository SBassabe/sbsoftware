package it.sbsoft.beans;

import java.util.Map;
import java.util.TreeMap;

public class OccByBedMap {
	
	private Map<String, OccByBed> occByBedMp = new TreeMap<String, OccByBed>();

	public Map<String, OccByBed> getOccByBedMp() {
		return occByBedMp;
	}

	public void setOccByBedMp(Map<String, OccByBed> occByBedMp) {
		this.occByBedMp = occByBedMp;
	}
	
}
