package org.hcsc.models;

import com.google.gson.JsonObject;


public class SitClinicaDAS {
	
	private int idSituacionClinicaFuncional;
	private int idActuacion;
	private int posicion;
	private String valor;
	private int tipoSCFDAS;
	

	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		csvRow.add(Integer.toString(idSituacionClinicaFuncional));
		csvRow.add(Integer.toString(idActuacion));
		csvRow.add(Integer.toString(posicion));
		csvRow.add(valor);
		csvRow.add(Integer.toString(tipoSCFDAS));
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject jsonSCFDAS = new JsonObject();
		
		jsonSCFDAS.addProperty("idSituacionClinicaFuncional", this.getIdSituacionClinicaFuncional());
		jsonSCFDAS.addProperty("idActuacion"                , this.getIdActuacion());
		jsonSCFDAS.addProperty("posicion"                   , this.getPosicion());
		jsonSCFDAS.addProperty("valor"                      , this.getValor());
		jsonSCFDAS.addProperty("tipoSCFDAS"                 , this.getTipoSCFDAS());
		
		return jsonSCFDAS;
	}
	
	
	
	/** GETTERS & SETTERS **/
	public int getIdSituacionClinicaFuncional() {
		return idSituacionClinicaFuncional;
	}	
	public void setIdSituacionClinicaFuncional(int idSituacionClinicaFuncional) {
		this.idSituacionClinicaFuncional = idSituacionClinicaFuncional;
	}
	
	public int getIdActuacion() {
		return idActuacion;
	}
	public void setIdActuacion(int idActuacion) {
		this.idActuacion = idActuacion;
	}
		
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public int getTipoSCFDAS() {
		return tipoSCFDAS;
	}
	public void setTipoSCFDAS(int tipoSCFDAS) {
		this.tipoSCFDAS = tipoSCFDAS;
	}

}
