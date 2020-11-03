package org.hcsc.models;

import com.google.gson.JsonObject;
import java.sql.Date;
import java.text.SimpleDateFormat;


public class Tratamiento {
	
	private int idTratamiento;
	private int idActuacion;
	private int posicion;
	private String valor;
	private Date fechaInicio;
	private Date fechaFin;
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		csvRow.add(Integer.toString(idTratamiento));
		csvRow.add(Integer.toString(idActuacion));
		csvRow.add(Integer.toString(posicion));
		csvRow.add(valor);
		csvRow.add(sdf.format(fechaInicio));
		csvRow.add(sdf.format(fechaFin));
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject jsonTratamiento = new JsonObject();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		jsonTratamiento.addProperty("idTratamiento", this.getIdTratamiento());
		jsonTratamiento.addProperty("idActuacion"  , this.getIdActuacion());
		jsonTratamiento.addProperty("posicion"     , this.getPosicion());
		jsonTratamiento.addProperty("valor"        , this.getValor());
		jsonTratamiento.addProperty("fechaInicio"  , this.getFechaInicio() == null ? "" : sdf.format(this.getFechaInicio()).toString());
		jsonTratamiento.addProperty("fechaFin"     , this.getFechaFin() == null ? "" : sdf.format(this.getFechaFin()).toString());

		return jsonTratamiento;
	}		

	
	
	/** GETTERS & SETTERS **/
	public int getIdTratamiento() {
		return idTratamiento;
	}
	public void setIdTratamiento(int idTratamiento) {
		this.idTratamiento = idTratamiento;
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
	
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

}
