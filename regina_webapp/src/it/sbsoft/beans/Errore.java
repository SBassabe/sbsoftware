package it.sbsoft.beans;

public class Errore {
	
	private String errorCode; // 0-NoError; 1-SBException; 2-unknown
	private String errorDesc;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

}
