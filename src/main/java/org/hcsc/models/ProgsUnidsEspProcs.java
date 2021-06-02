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
		
		String posiciones[] = {
				"",
				"Proceso Psicosis (No intervenci�n precoz)",
				"Programa Intervenci�n Precoz Psicosis",
				"Proceso Depresi�n",
				"Proceso TCA",
				"Unidad de Trastorno de Personalidad",
				"Unidad de Psicogeriatr�a",
				"Tratamiento ambulatorio intensivo SMNyA",
				"Programa Transici�n",
				"Programa C"
			};
		
		csvRow.add(Integer.toString(idProgramasUnidadesProcesos));
		csvRow.add(Integer.toString(idActuacion));
//		csvRow.add(Integer.toString(posicion));
		for (int i = 1; i < posiciones.length; i++) {
			if (posicion == i)
				csvRow.add(valor.isEmpty() ? "" : valor);
			else
				csvRow.add("");
		}
//		csvRow.add(valor);
//		csvRow.add(csvDescripcion);
		
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
