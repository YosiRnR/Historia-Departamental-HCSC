package org.hcsc.models;

import com.google.gson.JsonObject;


public class TratamientosMup {
	private int IdTratamientoMup;
	private int IdActuacion;
	private String Descripcion;
	private String TratamientoRecomendacion;
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		csvRow.add(Integer.toString(IdTratamientoMup));
		csvRow.add(Integer.toString(IdActuacion));
		csvRow.add(Descripcion == null ? "" : Descripcion.replaceAll("\n", " "));
		csvRow.add(TratamientoRecomendacion == null ? "" : TratamientoRecomendacion.replaceAll("\n", " "));
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject jsonMup  = new JsonObject();
	
		jsonMup.addProperty("idTratamientoMUP"  , this.getIdTratamientoMup());
		jsonMup.addProperty("idActuacion"       , this.getIdActuacion());
		jsonMup.addProperty("descripcion"       , this.getDescripcion());
		jsonMup.addProperty("tratamientoRecomendacion", this.getTratamientoRecomendacion());
		
		return jsonMup;
	}

	
	
	/** GETTERS & SETTERS **/
	public int getIdTratamientoMup() {
		return IdTratamientoMup;
	}
	public void setIdTratamientoMup(int idTratamientoMup) {
		IdTratamientoMup = idTratamientoMup;
	}
	
	public int getIdActuacion() {
		return IdActuacion;
	}
	public void setIdActuacion(int idActuacion) {
		IdActuacion = idActuacion;
	}
	
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	
	public String getTratamientoRecomendacion() {
		return TratamientoRecomendacion;
	}
	public void setTratamientoRecomendacion(String trataRecomen) {
		TratamientoRecomendacion = trataRecomen;
	}	
	
}
