package it.sbsoft.beans;

import java.util.HashMap;
import java.util.Map;

public class LocObj {
	
	private String loc_id;
    private String room_num;
    private String loc_desc;
    private String poly_points;
    private double cvalue; // range from 0 to 2 
    //private Map<String, BedObj> bedArr = new HashMap<String, BedObj>();
    
	public String getLoc_id() {
		return loc_id;
	}
	public void setLoc_id(String loc_id) {
		this.loc_id = loc_id;
	}
	public String getRoom_num() {
		return room_num;
	}
	public void setRoom_num(String room_num) {
		this.room_num = room_num;
	}
	public String getLoc_desc() {
		return loc_desc;
	}
	public void setLoc_desc(String loc_desc) {
		this.loc_desc = loc_desc;
	}
	public String getPoly_points() {
		return poly_points;
	}
	public void setPoly_points(String poly_points) {
		this.poly_points = poly_points;
	}
	public double getCvalue() {
		return cvalue;
	}
	public void setCvalue(double cvalue) {
		this.cvalue = cvalue;
	}
	
	

}
