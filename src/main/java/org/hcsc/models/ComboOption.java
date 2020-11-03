package org.hcsc.models;


public class ComboOption implements Comparable<ComboOption> {
	private String key;
	private String value;
	
	public ComboOption(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	
	@Override
	public int compareTo(ComboOption combo) {
		return this.getKey().compareTo(
				combo.getKey()) > 0 ? 1 : this.getKey().compareTo(combo.getKey()) == 0 ? 0 : -1;
	}
	
	
	/** GETTERS & SETTERS **/
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
