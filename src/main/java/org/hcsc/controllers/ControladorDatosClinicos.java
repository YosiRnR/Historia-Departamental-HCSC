package org.hcsc.controllers;

import org.hcsc.daos.DAOFactory;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.DatosClinicos;

import com.google.gson.JsonObject;


public class ControladorDatosClinicos {

	/**
	 * 
	 * @param datosClinicos
	 * @return
	 * @throws HSCException
	 */
	public JsonObject insertar(DatosClinicos datosClinicos) throws HSCException {
		int result = 0;
		JsonObject jsonResult = new JsonObject();
				
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			result = daoFactory.crearDAODatosClinicos().insertar(datosClinicos);
			
			if (result <= 0) {
				jsonResult.addProperty("status", 0);
				jsonResult.addProperty("message", "ERROR GUARDANDO DATOS CLINICOS...");
			}
			else {
				jsonResult.addProperty("status", 1);
				jsonResult.addProperty("message", "DATOS CLINICOS DEL REGISTRO GUARDADOS CON ÉXITO");
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
	 * @param datosClinicos
	 * @return
	 * @throws ConnectException
	 */
	public JsonObject actualizar(DatosClinicos datosClinicos) throws HSCException {
		int opResult = 0;
		JsonObject jsonResult = new JsonObject();
				
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			opResult = daoFactory.crearDAODatosClinicos().actualizar(datosClinicos);
			
			if (opResult <= 0) {
				jsonResult.addProperty("status", 0);
				jsonResult.addProperty("message", "ERROR ACTUALIZANDO DATOS CLINICOS...");
			}
			else {
				jsonResult.addProperty("status", 1);
				jsonResult.addProperty("message", "DATOS CLINICOS DEL REGISTRO ACTUALIZADOS CON ÉXITO");
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
