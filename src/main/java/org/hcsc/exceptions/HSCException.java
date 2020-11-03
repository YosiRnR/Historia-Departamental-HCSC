package org.hcsc.exceptions;


public class HSCException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String message  = "Error genérico";
	private String exName   = this.getClass().getName();
	private Throwable cause = null;
	
	
	public HSCException(String message, String exName, Throwable cause) {
		super(message, cause);
		
		this.setMessageException(message);
		this.setExName(exName);
		this.setCauseException(cause);
	}
	
	
	public HSCException(String message, Throwable cause) {
		super(message, cause);
		
		this.setMessageException(message);
		this.setCauseException(cause);
	}


	
	/** GETTERS & SETTERS **/
	public Throwable getCauseException() {
		return cause;
	}
	public void setCauseException(Throwable cause) {
		this.cause = cause;
	}

	
	public String getExName() {
		return exName;
	}
	public void setExName(String exName) {
		this.exName = exName;
	}


	public String getMessageException() {
		return message;
	}
	public void setMessageException(String message) {
		this.message = message;
	}

}
