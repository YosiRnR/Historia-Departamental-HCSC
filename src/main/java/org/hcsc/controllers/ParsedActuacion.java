package org.hcsc.controllers;

import java.util.ArrayList;

import org.hcsc.models.Actuacion;
import org.hcsc.models.Diagnostico;
import org.hcsc.models.Paciente;
import org.hcsc.models.ProgsUnidsEspProcs;
import org.hcsc.models.SitClinicaDAS;
import org.hcsc.models.Tratamiento;
import org.hcsc.models.TratamientosMup;


public class ParsedActuacion {
	
	private Actuacion actuacion;
	private Paciente paciente;
	private ArrayList<Diagnostico> diagnosticos;
	private ArrayList<SitClinicaDAS> sitClinicaDAS;
	private ArrayList<Tratamiento> tratamientos;
	private TratamientosMup tratamientosMup;
	private ArrayList<ProgsUnidsEspProcs> progsUnidsEspProcs;
	
	
	
	public Actuacion getActuacion() {
		return actuacion;
	}
	public void setActuacion(Actuacion actuacion) {
		this.actuacion = actuacion;
	}
	
	public Paciente getPaciente() {
		return paciente;
	}
	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	
	public ArrayList<Diagnostico> getDiagnosticos() {
		return diagnosticos;
	}
	public void setDiagnosticos(ArrayList<Diagnostico> diagnosticos) {
		this.diagnosticos = diagnosticos;
	}
	
	public ArrayList<SitClinicaDAS> getSitClinicaDAS() {
		return sitClinicaDAS;
	}
	public void setSitClinicaDAS(ArrayList<SitClinicaDAS> sitClinicaDAS) {
		this.sitClinicaDAS = sitClinicaDAS;
	}
	
	public ArrayList<Tratamiento> getTratamientos() {
		return tratamientos;
	}
	public void setTratamientos(ArrayList<Tratamiento> tratamientos) {
		this.tratamientos = tratamientos;
	}
	
	public TratamientosMup getTratamientosMup() {
		return tratamientosMup;
	}
	public void setTratamientosMup(TratamientosMup tratamientosMup) {
		this.tratamientosMup = tratamientosMup;
	}

	public ArrayList<ProgsUnidsEspProcs> getProgsUnidsEspProcs() {
		return progsUnidsEspProcs;
	}
	public void setProgsUnidsEspProcs(ArrayList<ProgsUnidsEspProcs> puep) {
		this.progsUnidsEspProcs = puep;
	}
	
}
