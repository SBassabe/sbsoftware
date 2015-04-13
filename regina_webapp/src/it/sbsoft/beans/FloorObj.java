package it.sbsoft.beans;

import java.util.HashMap;
import java.util.Map;

public class FloorObj {
	
	// Static Objects
    private String floor_id;
	private String img_src = "jpgfile";
	private String description;
	private String num_beds;
	private Map<String, LocObj> locArr = new HashMap<String, LocObj>();
	private Map<String, BedObj> bedArr = new HashMap<String, BedObj>();
	private Map<String, FeatureObj> featureArr = new HashMap<String, FeatureObj>();
	
	// Occupation Objects (indexable by date)
	private Map<String, BedByDate> bedByDate = new HashMap<String, BedByDate>();
	private Map<String, DocByDate> docByDate = new HashMap<String, DocByDate>();
	private Map<String, CleanByDate> cleanByDate = new HashMap<String, CleanByDate>();
	public String getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = floor_id;
	}
	public String getImg_src() {
		return img_src;
	}
	public void setImg_src(String img_src) {
		this.img_src = img_src;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNum_beds() {
		return num_beds;
	}
	public void setNum_beds(String num_beds) {
		this.num_beds = num_beds;
	}
	public Map<String, LocObj> getLocArr() {
		return locArr;
	}
	public void setLocArr(Map<String, LocObj> locArr) {
		this.locArr = locArr;
	}
	public Map<String, BedObj> getBedArr() {
		return bedArr;
	}
	public void setBedArr(Map<String, BedObj> bedArr) {
		this.bedArr = bedArr;
	}
	public Map<String, FeatureObj> getFeatureArr() {
		return featureArr;
	}
	public void setFeatureArr(Map<String, FeatureObj> featureArr) {
		this.featureArr = featureArr;
	}
	public Map<String, BedByDate> getBedByDate() {
		return bedByDate;
	}
	public void setBedByDate(Map<String, BedByDate> bedByDate) {
		this.bedByDate = bedByDate;
	}
	public Map<String, DocByDate> getDocByDate() {
		return docByDate;
	}
	public void setDocByDate(Map<String, DocByDate> docByDate) {
		this.docByDate = docByDate;
	}
	public Map<String, CleanByDate> getCleanByDate() {
		return cleanByDate;
	}
	public void setCleanByDate(Map<String, CleanByDate> cleanByDate) {
		this.cleanByDate = cleanByDate;
	}	
	
}
