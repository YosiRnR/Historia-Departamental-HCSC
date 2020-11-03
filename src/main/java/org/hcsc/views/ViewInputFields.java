package org.hcsc.views;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hcsc.controllers.ControladorActuacion;
import org.hcsc.exceptions.HSCException;

import com.google.gson.JsonObject;


/**
 * Servlet implementation class ViewInputFields
 */
public class ViewInputFields extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewInputFields() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ControladorActuacion controladorActuacion = new ControladorActuacion();
		
		response.setContentType("application/json" );
		response.setCharacterEncoding("utf-8");
		response.addHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.addHeader("Access-Control-Allow-Origin", "*");

		try {
			response.getWriter().print(controladorActuacion.cargarCamposVista());
		}
		catch(HSCException ex) {
			JsonObject error = new JsonObject();
			
			error.addProperty("status", 1);
			error.addProperty("message", ex.getMessage());
			error.addProperty("cause", ex.getCause().getMessage());
			
			Logger.getLogger(ViewInputFields.class).error("Exception message: " + ex.getMessage());
			Logger.getLogger(ViewInputFields.class).error("Exception cause: " + ex.getCause().getMessage());
			
			response.getWriter().print(error.toString());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
