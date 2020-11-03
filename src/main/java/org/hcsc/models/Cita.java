package org.hcsc.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.gson.JsonObject;


public class Cita {
	private int numeroCita;
	private String idAgenda;
	private String descripcionCentro;
	private String descripcionPrestacion;
	private Timestamp fechaIniCita;
	private Timestamp fechaFinCita;
	private String numeroICU;
	private int codigoFacultativo;
	private String apellidosFacultativo;
	private String nombreFacultativo;
	
	private boolean atendida;
	
	private String apellido1Paciente;
	private String apellido2Paciente;
	private String nombrePaciente;
	private int numeroHC;
	private String numeroCIPA;
	private String numeroSS;
	private String DNIPaciente;
	
	
	public JsonObject toJson() {
		JsonObject jsonResult = new JsonObject();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		long duracion = fechaFinCita.getTime() - fechaIniCita.getTime();
		jsonResult.addProperty("duracionCita", duracion);
		
		jsonResult.addProperty("numeroCita", numeroCita);
		jsonResult.addProperty("idAgenda", idAgenda);
		jsonResult.addProperty("descripcionCentro", descripcionCentro);
		jsonResult.addProperty("descripcionPrestacion", descripcionPrestacion);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(fechaIniCita.getTime());
		int hora = calendar.get(Calendar.HOUR_OF_DAY);
		int minuto = calendar.get(Calendar.MINUTE);
		String horaString = "";
		if (hora < 10) horaString = "0" + Integer.toString(hora);
		else horaString = Integer.toString(hora);
		String minutoString = "";
		if (minuto < 10) minutoString = "0" + Integer.toString(minuto);
		else minutoString = Integer.toString(minuto);
		jsonResult.addProperty("horaCita", horaString + ":" + minutoString);
		
		jsonResult.addProperty("fechaInicioCita", sdf.format(fechaIniCita.getTime()));
		jsonResult.addProperty("fechaFinCita", sdf.format(fechaFinCita.getTime()));
		jsonResult.addProperty("numeroICU", numeroICU);
		jsonResult.addProperty("codigoFacultativo", codigoFacultativo);
		jsonResult.addProperty("apellidosFacultativo", apellidosFacultativo);
		jsonResult.addProperty("nombreFacultativo", nombreFacultativo);
		jsonResult.addProperty("numeroHC", numeroHC);
		jsonResult.addProperty("numeroCIPA", numeroCIPA);
		jsonResult.addProperty("numeroSS", numeroSS);
		jsonResult.addProperty("DNIPaciente", DNIPaciente);
		jsonResult.addProperty("atendida", atendida);
		
		jsonResult.addProperty("apellido1Paciente", apellido1Paciente);
		jsonResult.addProperty("apellido2Paciente", apellido2Paciente);
		jsonResult.addProperty("nombrePaciente", nombrePaciente);
		
		return jsonResult;
	}

	
	
	
	public int getNumeroCita() {
		return numeroCita;
	}
	public void setNumeroCita(int numeroCita) {
		this.numeroCita = numeroCita;
	}
	
	public String getIdAgenda() {
		return idAgenda;
	}
	public void setIdAgenda(String idAgenda) {
		this.idAgenda = idAgenda;
	}
	
	public String getDescripcionCentro() {
		return descripcionCentro;
	}
	public void setDescripcionCentro(String descripcionCentro) {
		this.descripcionCentro = descripcionCentro;
	}
	
	public String getDescripcionPrestacion() {
		return descripcionPrestacion;
	}
	public void setDescripcionPrestacion(String descripcionPrestacion) {
		this.descripcionPrestacion = descripcionPrestacion;
	}
	
	public Timestamp getFechaIniCita() {
		return fechaIniCita;
	}
	public void setFechaIniCita(Timestamp fechaIniCita) {
		this.fechaIniCita = fechaIniCita;
	}
	
	public Timestamp getFechaFinCita() {
		return fechaFinCita;
	}
	public void setFechaFinCita(Timestamp fechaFinCita) {
		this.fechaFinCita = fechaFinCita;
	}
	
	public String getNumeroICU() {
		return numeroICU;
	}
	public void setNumeroICU(String numeroICU) {
		this.numeroICU = numeroICU;
	}
	
	public int getCodigoFacultativo() {
		return codigoFacultativo;
	}
	public void setCodigoFacultativo(int codigoFacultativo) {
		this.codigoFacultativo = codigoFacultativo;
	}
	
	public String getApellidosFacultativo() {
		return apellidosFacultativo;
	}
	public void setApellidosFacultativo(String apellidosFacultativo) {
		this.apellidosFacultativo = apellidosFacultativo;
	}
	
	public String getNombreFacultativo() {
		return nombreFacultativo;
	}
	public void setNombreFacultativo(String nombreFacultativo) {
		this.nombreFacultativo = nombreFacultativo;
	}
	
	public boolean isAtendida() {
		return atendida;
	}
	public void setAtendida(boolean atendida) {
		this.atendida = atendida;
	}
	
	public String getApellido1Paciente() {
		return apellido1Paciente;
	}
	public void setApellido1Paciente(String apellido1Paciente) {
		this.apellido1Paciente = apellido1Paciente;
	}
	
	public String getApellido2Paciente() {
		return apellido2Paciente;
	}
	public void setApellido2Paciente(String apellido2Paciente) {
		this.apellido2Paciente = apellido2Paciente;
	}
	
	public String getNombrePaciente() {
		return nombrePaciente;
	}
	public void setNombrePaciente(String nombrePaciente) {
		this.nombrePaciente = nombrePaciente;
	}
	
	public int getNumeroHC() {
		return numeroHC;
	}
	public void setNumeroHC(int numeroHC) {
		this.numeroHC = numeroHC;
	}
	
	public String getNumeroCIPA() {
		return numeroCIPA;
	}
	public void setNumeroCIPA(String numeroCIPA) {
		this.numeroCIPA = numeroCIPA;
	}
	
	public String getNumeroSS() {
		return numeroSS;
	}
	public void setNumeroSS(String numeroSS) {
		this.numeroSS = numeroSS;
	}
	
	public String getDNIPaciente() {
		return DNIPaciente;
	}
	public void setDNIPaciente(String dNIPaciente) {
		DNIPaciente = dNIPaciente;
	}
	
}
