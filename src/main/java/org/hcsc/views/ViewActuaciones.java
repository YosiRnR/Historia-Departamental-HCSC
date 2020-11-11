package org.hcsc.views;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hcsc.controllers.ControladorActuacion;
import org.hcsc.controllers.ControladorCIE;
import org.hcsc.controllers.ControladorCitas;
import org.hcsc.controllers.ControladorDatosClinicos;
import org.hcsc.controllers.ControladorPaciente;
import org.hcsc.controllers.ParsedActuacion;
import org.hcsc.controllers.RequestParser;
import org.hcsc.exceptions.FileIOException;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.DatosClinicos;
import org.hcsc.models.Paciente;
import org.hcsc.servlet.DALoginState;
import org.hcsc.servlet.LoginChecker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ViewActuaciones
 */
@MultipartConfig
public class ViewActuaciones extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Map<Integer, String> debugRequestStrings;
	static {
		debugRequestStrings = new HashMap<Integer, String>();
		debugRequestStrings.put(-1, "INVALID_REQUEST");
		debugRequestStrings.put(10, "RQ_LOGIN_USUARIO");
		debugRequestStrings.put(11, "RQ_OBTENER_CODIGO_FACULTATIVO");
		debugRequestStrings.put(12, "RQ_BUSCAR_PACIENTE_POR_PATRON");
		debugRequestStrings.put(13, "RQ_OBTENER_ACTUACION_POR_PACIENTE");
		debugRequestStrings.put(14, "RQ_OBTENER_ACTUACION_POR_REGISTRO");
		debugRequestStrings.put(15, "RQ_GUARDAR_REGISTRO");
		debugRequestStrings.put(16, "RQ_ACTUALIZAR_REGISTRO");
		debugRequestStrings.put(17, "RQ_METABUSCADOR_DIAGNOSTICOS");
		debugRequestStrings.put(18, "RQ_GUARDAR_DATOS_CLINICOS");
		debugRequestStrings.put(19, "RQ_OBTENER_NOMBRE_FACULTATIVO");
		debugRequestStrings.put(20, "RQ_CITAS_POR_FACULTATIVO");
		debugRequestStrings.put(21, "RQ_OBTENER_PACIENTE_DEL_HOSPITAL");
		debugRequestStrings.put(22, "RQ_OBTENER_DATOS_AGENDA");
		debugRequestStrings.put(23, "RQ_MARCAR_CITA_ATENDIDA");
		debugRequestStrings.put(24, "RQ_ENVIAR_INFORME_HL7");
		debugRequestStrings.put(25, "RQ_OBTENER_PACIENTE_POR_CIPA");
		debugRequestStrings.put(26, "RQ_EXPORTAR_A_CSV");
		debugRequestStrings.put(27, "RQ_CERRAR_SESION");
		debugRequestStrings.put(50, "RQ_DEBUG_MUP_LOCATION");
	}
	
	private final int INVALID_REQUEST               = -1;
	private final int RQ_LOGIN_USUARIO              = 10;
	private final int RQ_OBTENER_CODIGO_FACULTATIVO = 11;
	private final int RQ_BUSCAR_PACIENTE_POR_PATRON = 12;
	private final int RQ_OBTENER_ACTUACION_POR_PACIENTE = 13;
	private final int RQ_OBTENER_ACTUACION_POR_REGISTRO = 14;
	private final int RQ_GUARDAR_REGISTRO           = 15;
	private final int RQ_ACTUALIZAR_REGISTRO        = 16;
	private final int RQ_METABUSCADOR_DIAGNOSTICOS  = 17;
	private final int RQ_GUARDAR_DATOS_CLINICOS     = 18;
	private final int RQ_OBTENER_NOMBRE_FACULTATIVO = 19;
	private final int RQ_CITAS_POR_FACULTATIVO      = 20;
	private final int RQ_OBTENER_PACIENTE_DEL_HOSPITAL = 21;
	private final int RQ_OBTENER_DATOS_AGENDA       = 22;
	private final int RQ_MARCAR_CITA_ATENDIDA       = 23;
	private final int RQ_ENVIAR_INFORME_HL7         = 24;
	private final int RQ_OBTENER_PACIENTE_POR_CIPA  = 25;
	private final int RQ_EXPORTAR_A_CSV             = 26;

    private final int RQ_CERRAR_SESION              = 27; 
	private final int RQ_DEBUG_MUP_LOCATION         = 50;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewActuaciones() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-AllowHeaders", "x-requested-with");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		String peticion = request.getParameter("peticion");
		int code = (peticion == null || peticion.isEmpty()) ? INVALID_REQUEST : Integer.parseInt(peticion);
		Logger.getLogger(ViewActuaciones.class).info("PETICION RECIBIDA: " + debugRequestStrings.get(code));
		
//		if (!comprobarSesionDeUsuario(request, response)) {
//			response.getWriter().print("{ state: SESSION CLOSED }");
//			return;
//		}
		
		try {
		switch(code) {
		case RQ_CERRAR_SESION: {
			String user = request.getParameter("id-facultativo");
			deleteSessionCookie(response);
			Logger.getLogger(ViewActuaciones.class)
							.info("Usuario " + user + " ha finalizado la sesion" );
			response.getWriter().print("{ state: SESSION CLOSED }");
			break;
		}
		case RQ_DEBUG_MUP_LOCATION: {
			String mupString = request.getParameter("mupString");
			Logger.getLogger(ViewActuaciones.class).info("MUPLocation: " + mupString);		
			response.getWriter().print("MUPLocation: " + mupString);
			break;
		}
		case RQ_LOGIN_USUARIO: {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			DALoginState state = LoginChecker.checkUserPassword(username, password);
			Logger.getLogger(ViewActuaciones.class).info("Procesando " + debugRequestStrings.get(code));
			Logger.getLogger(ViewActuaciones.class).info(state.getState());
			
			JsonObject jsonResponse = new JsonObject();
			if (!state.isValidate()) {
				jsonResponse.addProperty("error", 0);
				jsonResponse.addProperty("message", state.getState());
				// Login fallido, eliminar posibles cookies de sesion //
				deleteSessionCookie(response);
			}
			else {
				jsonResponse.addProperty("error", 1);
				jsonResponse.addProperty("message", state.getState());
			}
			
			response.getWriter().print(jsonResponse.toString());
			break;
		}
		
		case RQ_OBTENER_CODIGO_FACULTATIVO: {
			ControladorCitas citasCtrl = new ControladorCitas();
			
			String userDni = request.getParameter("userDni");
			Logger.getLogger(ViewActuaciones.class)
				.info("Procesando " + debugRequestStrings.get(code));
			String result  = citasCtrl.obtenerCodigoFacultativo(userDni);
			Logger.getLogger(ViewActuaciones.class)
				.info(debugRequestStrings.get(code) + " resultado: " + result);
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_BUSCAR_PACIENTE_POR_PATRON: {
			ControladorPaciente pacienteCtrl = new ControladorPaciente();

			int codUser          = RequestParser.parseIntParam(request.getParameter("codUser"));
			Logger.getLogger(ViewActuaciones.class)
				.info("Procesando " + debugRequestStrings.get(code) + " Usuario: " + codUser); 			
			Paciente paciente    = RequestParser.parsePaciente(request);
			Logger.getLogger(ViewActuaciones.class).info("Buscando paciente: " + paciente.toJson().toString());
			boolean modoConsulta = Boolean.parseBoolean(request.getParameter("modoConsulta"));
			String result = pacienteCtrl.buscarPorPatron(paciente, modoConsulta);
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_OBTENER_ACTUACION_POR_PACIENTE: {
			int idProfesional = RequestParser.parseIntParam(request.getParameter("idProfesional"));
			Logger.getLogger(ViewActuaciones.class)
				.info("Procesando " + debugRequestStrings.get(code) + " Usuario: " + idProfesional);
			int idPaciente = RequestParser.parseIntParam(request.getParameter("idPaciente"));
			Logger.getLogger(ViewActuaciones.class)
				.info("Buscando actuaciones para el IdPaciente: " + idPaciente);
			ControladorActuacion actuacionCtrl = new ControladorActuacion();
			String result = "";
			try {
				result = actuacionCtrl.obtenerActuacionPorIdPaciente(idPaciente).toString();
			} catch (FileIOException e) {
				e.printStackTrace();
			}
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_OBTENER_ACTUACION_POR_REGISTRO: {
			int idProfesional = RequestParser.parseIntParam(request.getParameter("idProfesional"));
			Logger.getLogger(ViewActuaciones.class)
				.info("Procesando " + debugRequestStrings.get(code) + " Usuario: " + idProfesional);
			int idPaciente    = RequestParser.parseIntParam(request.getParameter("idPaciente"));
			int numRegistro   = RequestParser.parseIntParam(request.getParameter("registro"));
			Logger.getLogger(ViewActuaciones.class)
				.info("Buscando actuacion para el IdPaciente: " + idPaciente + " registro: "  + numRegistro);
			ControladorActuacion actuacionCtrl = new ControladorActuacion();
			String result = "";
			
			result = actuacionCtrl.obtenerActuacionPorNumRegistro(idPaciente, numRegistro).toString();
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_GUARDAR_REGISTRO: {
			ParsedActuacion parsedActuacion = RequestParser.parseActuacion(request);
			
			ControladorActuacion actuacionCtrl = new ControladorActuacion();
			Logger.getLogger(ViewActuaciones.class)
				.info("Procesando " + debugRequestStrings.get(code));
			Logger.getLogger(ViewActuaciones.class)
				.info("Guardando Actuacion: " + new Gson().toJson(parsedActuacion));
			String result = actuacionCtrl.insertar(parsedActuacion).toString();
			Logger.getLogger(ViewActuaciones.class)
				.info(debugRequestStrings.get(code) + " resultado: " + result);
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_ACTUALIZAR_REGISTRO: {
			String result = "";
			ParsedActuacion parsedActuacion = RequestParser.parseActuacion(request);
			ControladorActuacion actuacionCtrl = new ControladorActuacion();
			
			/** Actualizar registro solo esta permitido si no ha pasado más de un día **/
			Date today = new Date();
			Date fecha = new Date(parsedActuacion.getActuacion().getFecha().getTime());
			if (isSameDay(today, fecha)) {
				result = actuacionCtrl.actualizar(parsedActuacion).toString();
			}
			else {
				/** Informar al usuario que no puede actualizar por no ser el mismo día **/
				JsonObject json = new JsonObject();
				json.addProperty("error", true);
				json.addProperty("message", "<span style='font-weight:bold;'>AVISO: </span>SOLO SE PUEDEN ACTUALIZAR REGISTROS DURANTE EL MISMO D&Iacute;A");
				json.addProperty("show", true);
				result = json.toString();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_METABUSCADOR_DIAGNOSTICOS: {
			ControladorCIE cieCtrl = new ControladorCIE();
			String pattern       = request.getParameter("patron");
			char tipoDiagnostico = request.getParameter("psiquiatricos").charAt(0);
			
			JsonObject result = cieCtrl.metaBuscarCIE(pattern, tipoDiagnostico);
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_GUARDAR_DATOS_CLINICOS: {
			ControladorDatosClinicos datosClinicosCtrl = new ControladorDatosClinicos();
			boolean actualizar = RequestParser.parseBoolParam(request.getParameter("actualizar"));
			JsonObject json;
			DatosClinicos datosClinicos = RequestParser.parseDatosClinicos(request);
			if (actualizar) {
				json = datosClinicosCtrl.actualizar(datosClinicos);
			}
			else {
				json = datosClinicosCtrl.insertar(datosClinicos);
			}
			
			response.getWriter().print(json.toString());
			break;
		}
		
		case RQ_OBTENER_NOMBRE_FACULTATIVO: {
			String codigoFacultativo = request.getParameter("codFacultativo");
			
			ControladorCitas citasCtrl = new ControladorCitas();			
			JsonObject result = citasCtrl.obtenerNombreFacultativo(codigoFacultativo);
			
			response.getWriter().print(result.toString());
			break;
		}
		
		case RQ_CITAS_POR_FACULTATIVO: {
			ControladorCitas citasCtrl = new ControladorCitas();
			int idFacultativo = Integer.parseInt(request.getParameter("id-facultativo"));
			String facultaAltStr = request.getParameter("id-faculta-alt");
			int idFacultaAlt  = Integer.parseInt((facultaAltStr == null || facultaAltStr == "" ||
					facultaAltStr.equalsIgnoreCase("null")) ? "-1" : request.getParameter("id-faculta-alt")
			);
			String fecha      = request.getParameter("fecha");
			
			Logger.getLogger(ViewActuaciones.class).info("Procesando " + debugRequestStrings.get(code)
												+ " Usuario: " + idFacultativo + " (" + fecha + ")");
			String result = citasCtrl.obtenerCitasPorFacultativo(idFacultativo, idFacultaAlt, fecha).toString();
//			Logger.getLogger(ViewActuaciones.class)
//				.info(debugRequestStrings.get(code) + " resultado: " + result);
			
			response.getWriter().print(result);
			break;
		}
		
		case RQ_OBTENER_PACIENTE_DEL_HOSPITAL: {
			int numeroHC = Integer.parseInt(request.getParameter("numeroHC"));

			ControladorPaciente pacienteCtrl = new ControladorPaciente();
			JsonObject jsonResult = pacienteCtrl.obtenerPacienteDelHospital(numeroHC);
			
			response.getWriter().print(jsonResult.toString());
			break;
		}
		
		case RQ_OBTENER_DATOS_AGENDA: {
			int numeroCita = Integer.parseInt(request.getParameter("numeroCita"));
			
			ControladorCitas citasCtrl = new ControladorCitas();
			JsonObject jsonResult = citasCtrl.obtenerPorNumeroCita(numeroCita);
			
			response.getWriter().print(jsonResult.toString());
			break;			
		}
		
		case RQ_MARCAR_CITA_ATENDIDA: {
			int numeroCita = Integer.parseInt(request.getParameter("numeroCita"));

			ControladorCitas citasCtrl = new ControladorCitas();
			JsonObject jsonResult = citasCtrl.marcarCitaAtendida(numeroCita);
			
			response.getWriter().print(jsonResult.toString());
			break;
		}
		
		case RQ_ENVIAR_INFORME_HL7: {
			ControladorActuacion actuacionCtrl = new ControladorActuacion();
			
			if (request.getParameter("data") == null) {
				Logger.getLogger(ViewActuaciones.class).info("ERROR POST. No se han recibido datos via POST");
			}
			String dataTmp = request.getParameter("data");
			// Convierte la cadena ISO-8859-1 a UTF-8 para que pueda ser convertida a PDF //
			String data = new String(dataTmp.getBytes("iso-8859-1"), "utf-8");
			java.io.ByteArrayOutputStream pdf = actuacionCtrl.enviarInforme(data, request.getServletContext());
			
			/** Respond PDF file to client download **/
			response.setContentType("application/octet-stream; charset=UTF-8");
			response.addHeader("Content-Type", "application/octet-stream"); 
	        response.addHeader("Content-Disposition", "attachment; filename=\"informe.pdf\"");
	        response.setContentLength(pdf.size());
        	response.getOutputStream().write(pdf.toByteArray());
	        response.getOutputStream().flush();
			break;
		}
		
		case RQ_OBTENER_PACIENTE_POR_CIPA: {
			int numeroCIPA = Integer.parseInt(request.getParameter("numeroCIPA"));
			
			ControladorPaciente pacienteCtrl = new ControladorPaciente();
			JsonObject jsonResult = pacienteCtrl.obtenerPacientePorCIPA(numeroCIPA);
			
			response.getWriter().print(jsonResult.toString());
			break;
		}
		
		case RQ_EXPORTAR_A_CSV: {
			//int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
			ControladorActuacion actuacionCtrl = new ControladorActuacion();
			byte[] csvZip = actuacionCtrl.exportToCSV();
			
			response.setContentType("application/zip");				
			response.getOutputStream().write(csvZip);
			response.getOutputStream().flush();
			break;
		}		
				
		default:
			break;
		}
		}
		catch(ParseException ex) {
			Logger.getLogger(ViewActuaciones.class).error("ParseException: StackTrace: ", ex);
		}
		catch(HSCException ex) {
			Logger.getLogger(ViewActuaciones.class).error("Exception message: " + ex.getMessage());
			Logger.getLogger(ViewActuaciones.class).error("Exception cause: " + ex.getCause().getMessage());			
			Logger.getLogger(ViewActuaciones.class).error("HSCException: StackTrace: ", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print("EXCEPTION: " + ex.getCause());
		}
		catch(RuntimeException ex) {
			Logger.getLogger(ViewActuaciones.class).error("RuntimeException message: " + ex.getMessage());
			Logger.getLogger(ViewActuaciones.class).error("Exception cause: " + ex.getCause().getMessage());						
			Logger.getLogger(ViewActuaciones.class).error("RuntimeException: StackTrace: ", ex);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private boolean isSameDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
//	private boolean comprobarSesionDeUsuario(HttpServletRequest request, HttpServletResponse response) {
//		boolean result = false;
//		
//		Cookie cookies[] = request.getCookies();
//		
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				String cookieName = cookie.getName();
//				if (cookieName.equals("hcscpsiqapp")) {
//					result = true;
//					break;
//				}
//			}
//		}
//		
//		if (!result) {
//			String peticion = request.getParameter("peticion");
//			if (Integer.parseInt(peticion) == RQ_LOGIN_USUARIO) {
//				Cookie nuevaCookie = new Cookie("hcscpsiqapp", "true");
//				nuevaCookie.setMaxAge(60 * 60);
//				response.addCookie(nuevaCookie);
//				result = true;
//			}
//		}
//		else {
//			String peticion = request.getParameter("peticion");
//			if (Integer.parseInt(peticion) != RQ_CERRAR_SESION) {
//				Cookie nuevaCookie = new Cookie("hcscpsiqapp", "true");
//				nuevaCookie.setMaxAge(60 * 60);
//				response.addCookie(nuevaCookie);
//			}
//		}
//		
//		return result;
//	}
	
	/**
	 * 
	 * @param response
	 */
	private void deleteSessionCookie(HttpServletResponse response) {
		/**
		 ** Eliminar la cookie setando un nueva cookie con tiempo de vida a cero
		 **/
		Cookie newCookie = new Cookie("hscscpsiapp", "logged");
		/**
		 ** Duracion de la cookie en segundos
		 **/
		newCookie.setMaxAge(0);
		response.addCookie(newCookie);
	}

}
