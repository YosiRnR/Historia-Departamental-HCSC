package org.hcsc.servlet;


public class DALoginState {
	private String state;
	private boolean validate;
	
	
	public DALoginState() {
		state = "";
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean logged) {
		validate = logged;
	}
}
