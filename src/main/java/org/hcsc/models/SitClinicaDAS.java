package org.hcsc.models;

import com.google.gson.JsonObject;


public class SitClinicaDAS {
	
	private int idSituacionClinicaFuncional;
	private int idActuacion;
	private int posicion;
	private String valor;
	private int tipoSCFDAS;
	
	private String csvDescripcion;
	

	public java.util.List<String> toCSVStrings() {
		/**/
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		String posicionesSCF[] = {
				"",
				"Escala CGI",
				"GAF",
				"CGAF"
			};
		String posicionesDAS[] = {
				"",
				"Cuidado Personal",
				"Funcionamiento Ocupacional",
				"Funcionamiento Familiar",
				"Funcionamiento Social Amplio"
			};
		
		csvRow.add(Integer.toString(idSituacionClinicaFuncional));
		csvRow.add(Integer.toString(idActuacion));
//		csvRow.add(Integer.toString(posicion));
		if (tipoSCFDAS == 0) {
			for (int i = 1; i < posicionesSCF.length; i++) {
				if (posicion == i)
					csvRow.add(valor);
				else
					csvRow.add("");
			}
		}
		else if (tipoSCFDAS == 1) {
			csvRow.add("");
			csvRow.add("");
			csvRow.add("");
			for (int i = 1; i < posicionesDAS.length; i++) {
				if (posicion == i)
					csvRow.add(valor);
				else
					csvRow.add("");
			}
		}
//		csvRow.add(valor);
//		csvRow.add(Integer.toString(tipoSCFDAS));
//		csvRow.add(csvDescripcion);
		
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
	
	public String getCsvDescripcion() {
		return csvDescripcion;
	}
	public void setCsvDescripcion(String csvDescripcion) {
		this.csvDescripcion = csvDescripcion;
	}
	
}
