package org.hcsc.models;

import com.google.gson.JsonObject;


public class ProgsUnidsEspProcs {
	
	private int idProgramasUnidadesProcesos;
	private int idActuacion;
	private int posicion;
	private String valor;
	
	private String csvDescripcion;
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		csvRow.add(Integer.toString(idProgramasUnidadesProcesos));
		csvRow.add(Integer.toString(idActuacion));
		csvRow.add(Integer.toString(posicion));
		csvRow.add(valor);
		csvRow.add(csvDescripcion);
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject jsonPUEP = new JsonObject();

		jsonPUEP.addProperty("idTratamiento", this.getIdProgramasUnidadesProcesos());
		jsonPUEP.addProperty("idActuacion"  , this.getIdActuacion());
		jsonPUEP.addProperty("posicion"     , this.getPosicion());
		jsonPUEP.addProperty("valor"        , this.getValor());

		return jsonPUEP;
	}		

	
	
	
	/** GETTERS & SETTERS **/
	public int getIdProgramasUnidadesProcesos() {
		return idProgramasUnidadesProcesos;
	}
	public void setIdProgramasUnidadesProcesos(int idProgramasUnidadesProcesos) {
		this.idProgramasUnidadesProcesos = idProgramasUnidadesProcesos;
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

	public String getCsvDescripcion() {
		return csvDescripcion;
	}
	public void setCsvDescripcion(String csvDescripcion) {
		this.csvDescripcion = csvDescripcion;
	}

}
