package org.hcsc.models;


import java.sql.Date;
import java.text.SimpleDateFormat;

import com.google.gson.JsonObject;


public class Actuacion {
	
	private int idActuacion;
	private int idPaciente;
	private int numRegistro;
	private Date fecha;
	private boolean incapacidadTotal;
	private boolean curatelaSalud;
	private boolean curatelaEconomica;
	private boolean programaContinuidadCuidados;
	private boolean programaJoven;
	private Date fechaInicioProgramaJoven;
	private Date fechaFinProgramaJoven;
	
	private String codigoAgenda;
	private String lugarAtencion;
	private String idProfesional;
	private String codEmpleado;
	private String tipoPrestacion;
	
	private boolean equipoDeCalle;
	
	private Date fechaAlta;
	private int motivoAlta;
	
	private int numeroCita;
	private String numeroICU;

	private Date fechaAltaEquipoCalle;
	
	private boolean medidaProteccion;
	private boolean residencia;
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		csvRow.add(Integer.toString(idActuacion));
		csvRow.add(Integer.toString(idPaciente));
		csvRow.add(Integer.toString(numRegistro));
		String fechaStr = fecha == null ? "" : sdf.format(fecha);
		csvRow.add(fechaStr == null ? "" : fechaStr);
		csvRow.add(incapacidadTotal ? "true" : "false");
		csvRow.add(curatelaSalud ? "true" : "false");
		csvRow.add(curatelaEconomica ? "true" : "false");
		csvRow.add(programaContinuidadCuidados ? "true" : "false");
		csvRow.add(programaJoven ? "true" : "false");
		String fechaIniPJStr = fechaInicioProgramaJoven == null ? "" : sdf.format(fechaInicioProgramaJoven);
		csvRow.add(fechaIniPJStr == null ? "" : fechaIniPJStr);
		String fechaFinPJStr = fechaFinProgramaJoven == null ? "" : sdf.format(fechaFinProgramaJoven);
		csvRow.add(fechaFinPJStr == null ? "" : fechaFinPJStr);
		
		csvRow.add(codigoAgenda);
		csvRow.add(lugarAtencion);
		csvRow.add(idProfesional);
		csvRow.add(tipoPrestacion);
		
		csvRow.add(equipoDeCalle ? "true" : "false");
		String fechaAltaStr = fechaAlta == null ? "" : sdf.format(fechaAlta);
		csvRow.add(fechaAltaStr == null ? "" : fechaAltaStr);
		csvRow.add(Integer.toString(motivoAlta));
		
		csvRow.add(Integer.toString(numeroCita));
		csvRow.add(numeroICU);
		
		String fechaAltaEquiCalleStr = fechaAltaEquipoCalle == null ? "" : sdf.format(fechaAltaEquipoCalle);
		csvRow.add(fechaAltaEquiCalleStr == null ? "" : fechaAltaEquiCalleStr);
		
		csvRow.add(medidaProteccion ? "true" : "false");
		csvRow.add(residencia ? "true" : "false");
		
		return csvRow;
	}
	
	
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		result.addProperty("idActuacion", idActuacion);
		result.addProperty("numRegistro", numRegistro);
		result.addProperty("fecha", fecha == null ? "" : sdf.format(fecha).toString());
		result.addProperty("incapacidadTotal", incapacidadTotal);
		result.addProperty("curatelaEconomica", curatelaEconomica);
		result.addProperty("curatelaSalud", curatelaSalud);
		result.addProperty("programaContinuidadCuidados", programaContinuidadCuidados);
		result.addProperty("programaJoven", programaJoven);
		result.addProperty("fechaInicioProgramaJoven", fechaInicioProgramaJoven == null ? "" : sdf.format(fechaInicioProgramaJoven).toString());
		result.addProperty("fechaFinProgramaJoven", fechaFinProgramaJoven == null ? "" : sdf.format(fechaFinProgramaJoven).toString());
		
		result.addProperty("codigoAgenda", codigoAgenda);
		result.addProperty("lugarAtencion", lugarAtencion);
		result.addProperty("idProfesional", idProfesional);
		result.addProperty("codEmpleado", codEmpleado);
		result.addProperty("tipoPrestacion", tipoPrestacion);
		
		result.addProperty("equipoDeCalle", equipoDeCalle);
		
		JsonObject jsonAlta = new JsonObject();
		if (fechaAlta != null) {
			jsonAlta.addProperty("check", true);
			jsonAlta.addProperty("fechaAlta", sdf.format(fechaAlta).toString());
		}
		else {
			jsonAlta.addProperty("check", false);
			jsonAlta.addProperty("fechaAlta", "");
		}
		jsonAlta.addProperty("motivoAlta", motivoAlta);
		
		result.add("Alta", jsonAlta);
		
		result.addProperty("numeroCita", numeroCita);
		result.addProperty("numeroICU", numeroICU);
		
		result.addProperty("fechaAltaEquipoCalle", fechaAltaEquipoCalle == null ? "" : sdf.format(fechaAltaEquipoCalle).toString());
		
		result.addProperty("medidaProteccion", medidaProteccion);
		result.addProperty("residencia", residencia);
		
		return result;
	}
	
	

	public int getIdActuacion() {
		return idActuacion;
	}
	public void setIdActuacion(int idActuacion) {
		this.idActuacion = idActuacion;
	}

	public int getIdPaciente() {
		return idPaciente;
	}
	public void setIdPaciente(int idPaciente) {
		this.idPaciente = idPaciente;
	}

	public int getNumRegistro() {
		return numRegistro;
	}
	public void setNumRegistro(int numRegistro) {
		this.numRegistro = numRegistro;
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public boolean isIncapacidadTotal() {
		return incapacidadTotal;
	}
	public void setIncapacidadTotal(boolean incapacidadTotal) {
		this.incapacidadTotal = incapacidadTotal;
	}

	public boolean isCuratelaSalud() {
		return curatelaSalud;
	}
	public void setCuratelaSalud(boolean curatelaSalud) {
		this.curatelaSalud = curatelaSalud;
	}

	public boolean isCuratelaEconomica() {
		return curatelaEconomica;
	}
	public void setCuratelaEconomica(boolean curatelaEconomica) {
		this.curatelaEconomica = curatelaEconomica;
	}

	public boolean isProgramaContinuidadCuidados() {
		return programaContinuidadCuidados;
	}
	public void setProgramaContinuidadCuidados(boolean programaContinuidadCuidados) {
		this.programaContinuidadCuidados = programaContinuidadCuidados;
	}

	public boolean isProgramaJoven() {
		return programaJoven;
	}
	public void setProgramaJoven(boolean programaJoven) {
		this.programaJoven = programaJoven;
	}

	public Date getFechaInicioProgramaJoven() {
		return fechaInicioProgramaJoven;
	}
	public void setFechaInicioProgramaJoven(Date fechaInicioProgramaJoven) {
		this.fechaInicioProgramaJoven = fechaInicioProgramaJoven;
	}

	public Date getFechaFinProgramaJoven() {
		return fechaFinProgramaJoven;
	}
	public void setFechaFinProgramaJoven(Date fechaFinProgramaJoven) {
		this.fechaFinProgramaJoven = fechaFinProgramaJoven;
	}

	public String getCodigoAgenda() {
		return codigoAgenda;
	}
	public void setCodigoAgenda(String codigoAgenda) {
		this.codigoAgenda = codigoAgenda;
	}

	public String getLugarAtencion() {
		return lugarAtencion;
	}
	public void setLugarAtencion(String lugarAtencion) {
		this.lugarAtencion = lugarAtencion;
	}

	public String getIdProfesional() {
		return idProfesional;
	}
	public void setIdProfesional(String idProfesional) {
		this.idProfesional = idProfesional;
	}
	public String getCodEmpleado() {
		return codEmpleado;
	}
	public void setCodEmpleado(String codEmpleado) {
		this.codEmpleado = codEmpleado;
	}

	public String getTipoPrestacion() {
		return tipoPrestacion;
	}
	public void setTipoPrestacion(String tipoPrestacion) {
		this.tipoPrestacion = tipoPrestacion;
	}

	public boolean isEquipoDeCalle() {
		return equipoDeCalle;
	}
	public void setEquipoDeCalle(boolean equipoDeCalle) {
		this.equipoDeCalle = equipoDeCalle;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public int getMotivoAlta() {
		return motivoAlta;
	}
	public void setMotivoAlta(int motivoAlta) {
		this.motivoAlta = motivoAlta;
	}

	public int getNumeroCita() {
		return numeroCita;
	}
	public void setNumeroCita(int numeroCita) {
		this.numeroCita = numeroCita;
	}

	public String getNumeroICU() {
		return numeroICU;
	}
	public void setNumeroICU(String numeroICU) {
		this.numeroICU = numeroICU;
	}

	public Date getFechaAltaEquipoCalle() {
		return fechaAltaEquipoCalle;
	}
	public void setFechaAltaEquipoCalle(Date fechaAltaEquipoCalle) {
		this.fechaAltaEquipoCalle = fechaAltaEquipoCalle;
	}
	public boolean isMedidaProteccion() {
		return this.medidaProteccion;
	}
	public void setMedidaProteccion(boolean medidaProteccion) {
		this.medidaProteccion = medidaProteccion;
	}
	public boolean isResidencia() {
		return this.residencia;
	}
	public void setResidencia(boolean residencia) {
		this.residencia = residencia;
	}

}
