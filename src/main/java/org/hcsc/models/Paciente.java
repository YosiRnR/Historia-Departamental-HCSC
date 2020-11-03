package org.hcsc.models;

import java.text.SimpleDateFormat;
import java.sql.Date;

import com.google.gson.JsonObject;


public class Paciente {
	
	private int idPaciente = -1;
	private String nombre = "";
	private String apellido1 = "";
	private String apellido2 = "";
	private short sexo;
	private Date fechaNacimiento;
	private String direccion;
	private String poblacion;
	private int codigoPostal;
	private String telefono1;
	private String telefono2;
	private String dni = "";
	private String pasaporte = "";
	private String nie = "";
	private int numeroHistoriaClinica = -1;
	private String numeroSeguridadSocial = "";
	private String numeroTarjetaSanitaria = "";
	private int numeroCIPA = -1;
	private String familiar;
	private String telefonoFamiliar;
	
	
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		result.addProperty("idPaciente", idPaciente);
		result.addProperty("nombre", nombre);
		result.addProperty("apellido1", apellido1);
		result.addProperty("apellido2", apellido2);
		result.addProperty("sexo", sexo);
		result.addProperty("fechaNac", fechaNacimiento == null ? "" : sdf.format(fechaNacimiento).toString());
		result.addProperty("direccion", direccion);
		result.addProperty("poblacion", poblacion);
		result.addProperty("codigoPostal", codigoPostal);
		result.addProperty("telefono1", telefono1);
		result.addProperty("telefono2", telefono2);
		result.addProperty("dni", dni);
		result.addProperty("pasaporte", pasaporte);
		result.addProperty("nie", nie);
		result.addProperty("numeroHistoriaClinica", numeroHistoriaClinica);
		result.addProperty("numeroSeguridadSocial", numeroSeguridadSocial);
		result.addProperty("numeroTarjetaSanitaria", numeroTarjetaSanitaria);
		result.addProperty("numeroCIPA", numeroCIPA);
		result.addProperty("familiar", familiar);
		result.addProperty("telefonoFamiliar", telefonoFamiliar);
		
		return result;
	}
	
	
	public java.util.List<String> toCSVStrings() {
		/**/
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		java.util.ArrayList<String> csvRow = new java.util.ArrayList<String>();
		
		csvRow.add(Integer.toString(idPaciente));
		csvRow.add(nombre);
		csvRow.add(apellido1);
		csvRow.add(apellido2);
		csvRow.add(Short.toString(sexo));
		String fecNacStr = fechaNacimiento == null ? "" : sdf.format(fechaNacimiento);
		csvRow.add(fecNacStr == null ? "": fecNacStr);
		csvRow.add(direccion);
		csvRow.add(poblacion);
		csvRow.add(Integer.toString(codigoPostal));
		csvRow.add(telefono1);
		csvRow.add(telefono2);
		csvRow.add(dni);
		csvRow.add(pasaporte);
		csvRow.add(nie);
		csvRow.add(Integer.toString(numeroHistoriaClinica));
		csvRow.add(numeroSeguridadSocial == null ? "" : numeroSeguridadSocial);
		csvRow.add(numeroTarjetaSanitaria == null ? "" : numeroTarjetaSanitaria);
		csvRow.add(numeroCIPA == -1 ? "" : Integer.toString(numeroCIPA));
		csvRow.add(familiar);
		csvRow.add(telefonoFamiliar);

		return csvRow;
	}
	

	public boolean isEqual(Paciente other) {
		boolean result = false;
		
		return result;
	}
	
	
	
	/**
	 ** GETTERS & SETTERS
	 **/

	public int getIdPaciente() {
		return idPaciente;
	}
	public void setIdPaciente(int idPaciente) {
		this.idPaciente = idPaciente;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellido1() {
		return apellido1;
	}
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
	
	public short getSexo() {
		return sexo;
	}
	public void setSexo(short sexo) {
		this.sexo = sexo;
	}
	
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public String getPoblacion() {
		return poblacion;
	}
	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}
	
	public int getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	
	public String getTelefono1() {
		return telefono1;
	}
	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}
	
	public String getTelefono2() {
		return telefono2;
	}
	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}
	
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		if (dni != null) this.dni = dni;
		else this.dni = "";
	}
	
	public String getPasaporte() {
		return pasaporte;
	}
	public void setPasaporte(String pasaporte) {
		if (pasaporte != null) this.pasaporte = pasaporte;
		else pasaporte = "";
	}
	
	public String getNie() {
		return nie;
	}
	public void setNie(String nie) {
		if (nie != null) this.nie = nie;
		else nie = "";
	}
	
	public int getNumeroHistoriaClinica() {
		return numeroHistoriaClinica;
	}
	public void setNumeroHistoriaClinica(int numeroHistoriaClinica) {
		this.numeroHistoriaClinica = numeroHistoriaClinica;
	}
	
	public String getNumeroSeguridadSocial() {
		return numeroSeguridadSocial;
	}
	public void setNumeroSeguridadSocial(String numeroSeguridadSocial) {
		this.numeroSeguridadSocial = numeroSeguridadSocial;
	}
	
	public String getNumeroTarjetaSanitaria() {
		return numeroTarjetaSanitaria;
	}
	public void setNumeroTarjetaSanitaria(String numeroTarjetaSanitaria) {
		this.numeroTarjetaSanitaria = numeroTarjetaSanitaria;
	}
	
	public int getNumeroCIPA() {
		return numeroCIPA;
	}
	public void setNumeroCIPA(int numeroCIPA) {
		this.numeroCIPA = numeroCIPA;
	}
	
	public String getFamiliar() {
		return familiar;
	}
	public void setFamiliar(String familiar) {
		this.familiar = familiar;
	}
	
	public String getTelefonoFamiliar() {
		return telefonoFamiliar;
	}
	public void setTelefonoFamiliar(String telefonoFamiliar) {
		this.telefonoFamiliar = telefonoFamiliar;
	}
	
}
