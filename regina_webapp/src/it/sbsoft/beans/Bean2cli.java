package it.sbsoft.beans;

import java.util.List;

public class Bean2cli {
	
	private Object ret2cli;
    private Errore error;
    private String floor;
	private String date;
	private String message;
	private List<String> yearList; 
	
	public Object getRet2cli() {
		return ret2cli;
	}
	public void setRet2cli(Object ret2cli) {
		this.ret2cli = ret2cli;
	}
	public Errore getError() {
		return error;
	}
	public void setError(Errore error) {
		this.error = error;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<String> getYearList() {
		return yearList;
	}
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    
}
