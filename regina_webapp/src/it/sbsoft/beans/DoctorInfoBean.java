package it.sbsoft.beans;

public class DoctorInfoBean {
	
	// TODO: rename this bean please ...
	private String floor;
	private String gmadal;
	private String gmaal;
	private String daterange; //YYYYMMDD_YYYMMDD
	private String docId;
	private String docName;
	private String codStanza;
	private String numStanza;
	private String[] polyPoints;
	
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getGmadal() {
		return gmadal;
	}
	public void setGmadal(String gmadal) {
		this.gmadal = gmadal;
	}
	public String getGmaal() {
		return gmaal;
	}
	public void setGmaal(String gmaal) {
		this.gmaal = gmaal;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getCodStanza() {
		return codStanza;
	}
	public void setCodStanza(String codStanza) {
		this.codStanza = codStanza;
	}
	public String getNumStanza() {
		return numStanza;
	}
	public void setNumStanza(String numStanza) {
		this.numStanza = numStanza;
	}
	public String[] getPolyPoints() {
		return polyPoints;
	}
	public void setPolyPoints(String[] polyPoints) {
		this.polyPoints = polyPoints;
	}
	
	public String getDaterange() {
		return daterange;
	}
	public void setDaterange(String daterange) {
		this.daterange = daterange;
	}
	public String getPolyPointsAsString() {	
		String str="";
		for (String i : this.polyPoints) {
			str=str+i+",";
		}
		if (str.endsWith(",")) {
			str=str.substring(0, str.length()-1);
		}
		return str;
	} 
}
