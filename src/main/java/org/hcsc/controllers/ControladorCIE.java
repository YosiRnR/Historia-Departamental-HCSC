package org.hcsc.controllers;

import java.util.ArrayList;

import org.hcsc.daos.DAOCieNoPsiquiatria;
import org.hcsc.daos.DAOCiePsiquiatria;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.ComboOption;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class ControladorCIE {

	public JsonObject metaBuscarCIE(String pattern, char tipoDiag) throws HSCException {
		
		DAOCiePsiquiatria   daoCiePsiquiatria   = new DAOCiePsiquiatria();
		DAOCieNoPsiquiatria daoCieNoPsiquiatria = new DAOCieNoPsiquiatria();

		pattern = pattern.toUpperCase();
		pattern = pattern.replace("Á", "A");
		pattern = pattern.replace("É", "E");
		pattern = pattern.replace("Í", "I");
		pattern = pattern.replace("Ó", "O");
		pattern = pattern.replace("Ú", "U");

		ArrayList<ComboOption> combo = null;
		
		/** Buscar diagnosticos psiquiatricos o no psiquiatricos segun el tipo de diagnostico **/
		if (tipoDiag != 'n')
			combo = daoCiePsiquiatria.metaBuscarCIE_XML(pattern);
		else
			combo = daoCieNoPsiquiatria.metaBuscarCIE_XML(pattern);

		/** Serializar resultados a JSON **/
		JsonObject jsonResultados = new JsonObject();

		JsonArray jsonOptionsArray = new JsonArray();

		for (ComboOption c : combo) {
			JsonObject jsonArrayElement = new JsonObject();
			
			jsonArrayElement.addProperty("cie", c.getKey());
			jsonArrayElement.addProperty("descripcion", c.getValue());

			jsonOptionsArray.add(jsonArrayElement);
		}

		jsonResultados.add("Diagnosticos", jsonOptionsArray);
		
		return jsonResultados;
	}
	
}
