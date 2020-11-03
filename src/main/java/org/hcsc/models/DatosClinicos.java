package org.hcsc.models;

import com.google.gson.JsonObject;


public class DatosClinicos {

	private int IdActuacion;
	private int IdPaciente;
	private String Antecedentes;
	private String EnfermedadActual;
	private String EvolucionComentarios;
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		csvRow.add(Integer.toString(IdActuacion));
		csvRow.add(Integer.toString(IdPaciente));
		csvRow.add(Antecedentes);
		csvRow.add(EnfermedadActual);
		csvRow.add(EvolucionComentarios);
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		
		result.addProperty("idActuacion", IdActuacion);
		result.addProperty("idPaciente", IdPaciente);
		result.addProperty("antecedentes",  Antecedentes);
		result.addProperty("enfermedadActual", EnfermedadActual);
		result.addProperty("evolucionComentarios",  EvolucionComentarios);
		
		return result;
	}
	

	public int getIdActuacion() {
		return IdActuacion;
	}
	public void setIdActuacion(int idActuacion) {
		IdActuacion = idActuacion;
	}
	
	public int getIdPaciente() {
		return IdPaciente;
	}
	public void setIdPaciente(int idPaciente) {
		IdPaciente = idPaciente;
	}

	public String getAntecedentes() {
		return Antecedentes;
	}
	public void setAntecedentes(String antecedentes) {
		Antecedentes = antecedentes;
	}

	public String getEnfermedadActual() {
		return EnfermedadActual;
	}
	public void setEnfermedadActual(String enfermedadActual) {
		EnfermedadActual = enfermedadActual;
	}

	public String getEvolucionComentarios() {
		return EvolucionComentarios;
	}
	public void setEvolucionComentarios(String evolucionComentarios) {
		EvolucionComentarios = evolucionComentarios;
	}
	
	public boolean isEqual(DatosClinicos other) {
		boolean equal = true;
		
		if (equal && !this.Antecedentes.equalsIgnoreCase(other.getAntecedentes())) equal = false;
		if (equal && !this.EnfermedadActual.equalsIgnoreCase(other.getEnfermedadActual())) equal = false;
		if (equal && !this.EvolucionComentarios.equalsIgnoreCase(other.getEvolucionComentarios())) equal = false;
		
		return equal;
	}
		
}
