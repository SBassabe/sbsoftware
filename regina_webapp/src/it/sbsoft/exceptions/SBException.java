package it.sbsoft.exceptions;

public class SBException extends Exception {
	
	private String message;
	public SBException(String errMsg) {
		this.message = errMsg;
	}
	
	public String getMessage() {
		return message;
	}
}
