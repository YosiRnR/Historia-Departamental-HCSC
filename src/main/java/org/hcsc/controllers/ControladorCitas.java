package org.hcsc.controllers;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.daos.DAOFactory;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.Cita;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class ControladorCitas {
	
	/**
	 * 
	 * @param userDni
	 * @return
	 * @throws HSCException
	 **/
	public String obtenerCodigoFacultativo(String userDni) throws HSCException {
		JsonObject result = null;
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			result = daoFactory.crearDAOCitas().obtenerCodigoFacultativo(userDni); 
			
			/** Cerrar conexion con la BDD **/
			daoFactory.close();
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
	 * @param idFacultativo
	 * @param fecha
	 * @return
	 * @throws HSCException
	 **/
	public JsonObject obtenerCitasPorFacultativo(int idFacultativo, int idFacultaAlt, String fecha) throws HSCException {
		JsonObject result = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			Date dateFecha = new Date(new SimpleDateFormat("dd/MM/yyyy").parse(fecha).getTime());
		
			ArrayList<Cita> citas = daoFactory.crearDAOCitas()
										.obtenerPorFacultativoDia(idFacultativo, idFacultaAlt, dateFecha);
			
			JsonObject nombreFac = daoFactory.crearDAOCitas()
									.obtenerNombreFacultativoPorDni(String.valueOf(idFacultativo));
			
			daoFactory.close();
			
			JsonArray array = new JsonArray();
			
			result.add("facultativo", nombreFac);
			
			for (Cita cita : citas) {
				JsonObject item = cita.toJson();
				
				array.add(item);
			}
			
			result.add("citas", array);
		}
		catch(ParseException ex) {
			Logger.getLogger(ControladorCitas.class).error("ParseException: StackTrace: ", ex);
			ex.printStackTrace();
		}
		catch(HSCException ex) {
			Logger.getLogger(ControladorCitas.class).error("HSCException: StackTrace: ", ex);
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result;
	}

	/**
	 * 
	 * @param codFacul
	 * @return
	 * @throws HSCException
	 **/
	public JsonObject obtenerNombreFacultativo(String codFacul) throws HSCException {
		JsonObject jsonResult = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			jsonResult = daoFactory.crearDAOCitas().obtenerNombreFacultativoPorDni(codFacul);
		
		} catch(HSCException ex) {
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return jsonResult;		
	}
	
	public JsonObject obtenerPorNumeroCita(int numeroCita) throws HSCException {
		JsonObject jsonResult = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			Cita cita = daoFactory.crearDAOCitas().obtenerPorNumeroCita(numeroCita);
			
			if (cita != null) {
				jsonResult.addProperty("descripcionPrestacion", cita.getDescripcionPrestacion());
				jsonResult.addProperty("descripcionCentro", cita.getDescripcionCentro());
				jsonResult.addProperty("codigoAgenda", cita.getIdAgenda());
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
	
	/**
	 * 
	 * @param numeroCita
	 * @return
	 * @throws HSCException
	 **/
	public JsonObject marcarCitaAtendida(int numeroCita) throws HSCException {
		JsonObject jsonResult = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			int result = daoFactory.crearDAOCitas().marcarAtendida(numeroCita);
			
			jsonResult.addProperty("numeroCita", numeroCita);
			jsonResult.addProperty("atendida", result > 0 ? true : false);
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
