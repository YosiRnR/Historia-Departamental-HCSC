package org.hcsc.models;

import com.google.gson.JsonObject;


public class Diagnostico {
	
	private int idDiagnostico;
	private int idActuacion;
	
	private int tipoDiagnostico;	// 1 -> DIAGNOSTICO PSIQUIATRICO PRINCIPAL (3 combobox)
									// 2 -> DIAGNOSTICO PSIQUIATRICO SECUNDARIO (6 combobox)
									// 3 -> DIAGNOSTICO NO PSIQUIATRICO (6 combobox)
									// 4 -> ESTADIAJE CLINICO DE LA DEPRESION / PSICOSIS (1 combobox)
	private String cieDiagnostico;  // Valor CIE 10 del diagnostico
	private int posCombo;			// Posicion del combo dentro del tipo de diagnostico

//	private String descripcion;		// Campo extra para devolver la descripcion del diagnostico
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		csvRow.add(Integer.toString(idDiagnostico));
		csvRow.add(Integer.toString(idActuacion));
		csvRow.add(Integer.toString(tipoDiagnostico));
		csvRow.add(cieDiagnostico);
		csvRow.add(Integer.toString(posCombo));
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		
		result.addProperty("tipoDiagnostico", tipoDiagnostico);
		result.addProperty("cieDiagnostico", cieDiagnostico);
		result.addProperty("posCombo", posCombo);
		
		return result;
	}

	
	
	/** GETTERS & SETTERS **/
	public int getIdDiagnostico() {
		return idDiagnostico;
	}
	public void setIdDiagnostico(int idDiagnostico) {
		this.idDiagnostico = idDiagnostico;
	}

	public int getIdActuacion() {
		return idActuacion;
	}
	public void setIdActuacion(int idActuacion) {
		this.idActuacion = idActuacion;
	}

	public int getTipoDiagnostico() {
		return tipoDiagnostico;
	}
	public void setTipoDiagnostico(int tipoDiagnostico) {
		this.tipoDiagnostico = tipoDiagnostico;
	}

	public String getCieDiagnostico() {
		return cieDiagnostico;
	}
	public void setCieDiagnostico(String cieDiagnostico) {
		this.cieDiagnostico = cieDiagnostico;
	}

	public int getPosCombo() {
		return posCombo;
	}
	public void setPosCombo(int posCombo) {
		this.posCombo = posCombo;
	}

//	public String getDescripcion() {
//		return descripcion;
//	}
//	public void setDescripcion(String descripcion) {
//		this.descripcion = descripcion;
//	}
	
}
