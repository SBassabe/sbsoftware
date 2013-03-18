package it.sbsoft.beans;

import java.util.List;

public class Bean2serv {

	private String floor;
	private String action;
	private String date;
    private List<DoctorInfoBean> doctorMap;

	public List<DoctorInfoBean> getDoctorMap() {
		return doctorMap;
	}

	public void setDoctorMap(List<DoctorInfoBean> doctorMap) {
		this.doctorMap = doctorMap;
	}

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
	
}
