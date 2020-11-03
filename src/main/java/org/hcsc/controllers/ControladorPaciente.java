package org.hcsc.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.hcsc.daos.DAOFactory;
import org.hcsc.exceptions.HSCException;
import org.hcsc.hl7.ClientHL7;
import org.hcsc.models.Paciente;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class ControladorPaciente {
	
	public String buscarPorPatron(Paciente toFind, boolean modoConsulta) throws HSCException {
		JsonObject result = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			ArrayList<Paciente> pacientes = new ArrayList<Paciente>();			
			
			if (toFind.getNumeroHistoriaClinica() <= 0 &&
					toFind.getNumeroCIPA() <= 0 &&
					toFind.getNumeroTarjetaSanitaria().isEmpty()) {
				ArrayList<Paciente> encontrados = daoFactory.crearDAOPacientes().obtenerPorPatron(toFind);
				
				if (!encontrados.isEmpty()) {
					pacientes.addAll(encontrados);
				}
			}
			
			/** Si se especifico algún identificador sanitario, buscar por el especificado **/
			else {
				Paciente encontrado = daoFactory.crearDAOPacientes().obtenerPorIdentificadorSanitario(toFind);
				if (encontrado != null)
					pacientes.add(encontrado);
			}
			
			/** Cerrar conexion con la BBDD **/
			daoFactory.close();
			
			/** Si estamos en modo consulta, no buscamos en el HPHIS, ya que buscamos un paciente con registros **/
			if (!modoConsulta) {
				/** Como modoConsulta es false, buscamos en la BBDD del HPHIS via HL7 **/
				ArrayList<Paciente> pacientesHL7 = null;
				ClientHL7 hl7Client = new ClientHL7();
				
				/** Si no hay ningún identificador único en los parámetros, busca por todos los campos **/
				if (toFind.getNumeroHistoriaClinica() <= 0 &&
						toFind.getNumeroCIPA() <= 0 &&
						toFind.getNumeroTarjetaSanitaria().isEmpty()) {
					
					pacientesHL7 = hl7Client.sendQRY_A19(toFind);
				}
				else {
					Paciente buscarPorIdSanitario = new Paciente();
					buscarPorIdSanitario.setNumeroHistoriaClinica(toFind.getNumeroHistoriaClinica());
					buscarPorIdSanitario.setNumeroCIPA(toFind.getNumeroCIPA());
					buscarPorIdSanitario.setNumeroTarjetaSanitaria(toFind.getNumeroTarjetaSanitaria());
					
					pacientesHL7 = hl7Client.sendQRY_A19(buscarPorIdSanitario);
				}
			
				Set<String> set = new HashSet<String>();
				
				for (Paciente item : pacientes) {
					String idDocument = "";
					if (item.getDni() != null && !item.getDni().isEmpty())
						idDocument = item.getDni();
					else if (item.getPasaporte() != null && !item.getPasaporte().isEmpty())
						idDocument = item.getPasaporte();
					else if (item.getNie() != null && item.getNie().isEmpty())
						idDocument = item.getNie();
					
					set.add(idDocument);
				}
				
				for (Paciente item : pacientesHL7) {
					String idDocument = "";
					if (item.getDni() != null && !item.getDni().isEmpty())
						idDocument = item.getDni();
					else if (item.getPasaporte() != null && !item.getPasaporte().isEmpty())
						idDocument = item.getPasaporte();
					else if (item.getNie() != null && item.getNie().isEmpty())
						idDocument = item.getNie();
					
					set.add(idDocument);
					pacientes.add(item);
				}
			}
			
			/** Crear array JSON para devolver resultados al cliente **/
			JsonArray jsonArray = new JsonArray();
			
			for (Paciente item : pacientes) {
				jsonArray.add(item.toJson());
			}
			
			result.add("Pacientes", jsonArray);
		}
		catch(HSCException ex) {
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result.toString();
	}
	
	/**
	 * 
	 * @param numeroHC
	 * @return
	 * @throws HSCException
	 **/
	public JsonObject obtenerPacienteDelHospital(int numeroHC) throws HSCException {
		JsonObject result = new JsonObject();

		DAOFactory daoFactory = new DAOFactory();
		
		try {
			Paciente paciente = daoFactory.crearDAOPacientes().obtenerPacienteDelHospital(numeroHC);
			Paciente estaEnBDLocal = null;
			if (paciente.getNumeroCIPA() > 0) {
				estaEnBDLocal = daoFactory.crearDAOPacientes().obtenerPorCIPA(paciente.getNumeroCIPA());
			}
			else {
				estaEnBDLocal = daoFactory.crearDAOPacientes().obtenerPorNumeroHC(numeroHC);
			}
			if (estaEnBDLocal != null)
			{
				result = estaEnBDLocal.toJson();
			}
			else if (paciente != null) {
				result = paciente.toJson();
			}
		}
		catch(HSCException ex) {
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result;
	}
	
	public JsonObject obtenerPacientePorCIPA(int numeroCIPA) throws HSCException {
		JsonObject jsonResult = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			Paciente paciente = daoFactory.crearDAOPacientes().obtenerPorCIPA(numeroCIPA);
			
			if (paciente != null) {
				jsonResult.add("Paciente", paciente.toJson());
			}
		}
		catch(HSCException ex) {
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return jsonResult;
	}
	
}
