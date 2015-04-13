package it.sbsoft.beans;

import it.sbsoft.beans.todeprecate.DoctorInfoBean;

import java.util.List;
import java.util.Map;

public class Bean2serv {

	private String floor;
	private String action;
	private String date;
	private CleanByLoc cleanByLoc;
    private List<DoctorInfoBean> doctorMap;
    private Map<String, LocObj> locArr;
    private Map<String, CleanByLoc> cleanByLocMap;
    
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<DoctorInfoBean> getDoctorMap() {
		return doctorMap;
	}
	public void setDoctorMap(List<DoctorInfoBean> doctorMap) {
		this.doctorMap = doctorMap;
	}
	public Map<String, LocObj> getLocArr() {
		return locArr;
	}
	public void setLocArr(Map<String, LocObj> locArr) {
		this.locArr = locArr;
	}
	public Map<String, CleanByLoc> getCleanByLocMap() {
		return cleanByLocMap;
	}
	public void setCleanByLocMap(Map<String, CleanByLoc> cleanByLocMap) {
		this.cleanByLocMap = cleanByLocMap;
	}
	public CleanByLoc getCbl() {
		return cleanByLoc;
	}
	public void setCbl(CleanByLoc cbl) {
		this.cleanByLoc = cbl;
	}
	
	
}
