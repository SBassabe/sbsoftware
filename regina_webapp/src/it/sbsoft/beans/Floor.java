package it.sbsoft.beans;

import java.util.List;

public class Floor {
	
	// common
	private String id;
	private String description;
	
	// map context
	private String imgSrc;
	private List<FloorMap> floorMap;
	private List<FeatureMap> featureMap;
	private List<DoctorMap> doctorMap;
	
	// occupancy context
	private String dt; // <yyyymmdd>
	private List<BedOccupancy> occMap;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public List<FloorMap> getFloorMap() {
		return floorMap;
	}
	public void setFloorMap(List<FloorMap> floorMap) {
		this.floorMap = floorMap;
	}
	public List<FeatureMap> getFeatureMap() {
		return featureMap;
	}
	public void setFeatureMap(List<FeatureMap> featureMap) {
		this.featureMap = featureMap;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public List<BedOccupancy> getOccMap() {
		return occMap;
	}
	public void setOccMap(List<BedOccupancy> occMap) {
		this.occMap = occMap;
	}
	public List<DoctorMap> getDoctorMap() {
		return doctorMap;
	}
	public void setDoctorMap(List<DoctorMap> doctorMap) {
		this.doctorMap = doctorMap;
	}
}
