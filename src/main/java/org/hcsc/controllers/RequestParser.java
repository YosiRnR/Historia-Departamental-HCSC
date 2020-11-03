package org.hcsc.controllers;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.hcsc.models.Actuacion;
import org.hcsc.models.DatosClinicos;
import org.hcsc.models.Diagnostico;
import org.hcsc.models.Paciente;
import org.hcsc.models.ProgsUnidsEspProcs;
import org.hcsc.models.SitClinicaDAS;
import org.hcsc.models.Tratamiento;
import org.hcsc.models.TratamientosMup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class RequestParser {
		
	public final static short parseShortParam(String value) {
		short result = -1;
		
		try {
			result = Short.parseShort(value);
		}
		catch(NumberFormatException e) {
			result = -1;
		}
		
		return result;
	}

	
	public final static int parseIntParam(String value) {
		int result = 0;
		
		try {
			result = Integer.parseInt(value);
		}
		catch(NumberFormatException e) {
			result = 0;
		}
		
		return result;
	}
	
	
	public final static boolean parseBoolParam(String pParam) {
		if (pParam == null || pParam.isEmpty())
			return false;
		else
			return Boolean.parseBoolean(pParam);
	}
	

	public final static Date parseSqlDateParam(String value) {
		Date result = null;
		
		if (value == null) return result;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String dateTimeSplitted[] = value.split(":");
		
		try {
			if (dateTimeSplitted.length > 1) {
				result = new Date(sdf.parse(dateTimeSplitted[1]).getTime());
			}
			else {
				result = new Date(sdf.parse(value).getTime());
			}
		}
		catch(ParseException e) {
		}

		return result;
	}

	
	private static ArrayList<Diagnostico> parseDiagnosticos(HttpServletRequest request) throws ParseException {
		ArrayList<Diagnostico> diagnosticos = new ArrayList<Diagnostico>();
		
		JsonArray jsonDiagnosticos = JsonParser.parseString(request.getParameter("actuacion"))
									.getAsJsonObject().get("diagnosticos").getAsJsonArray();

		for (JsonElement item : jsonDiagnosticos) {
			Diagnostico diagnostico = new Diagnostico();
			String valor = item.getAsJsonObject().get("codigo").getAsString();
			int posicion = item.getAsJsonObject().get("posCombo").getAsInt();
			int tipoDiag = item.getAsJsonObject().get("tipoDiagnostico").getAsInt();
			
			diagnostico.setCieDiagnostico(valor);
			diagnostico.setPosCombo(posicion);
			diagnostico.setTipoDiagnostico(tipoDiag);
			
			diagnosticos.add(diagnostico);
		}
		
		return diagnosticos;
	}
	
	
	private static ArrayList<SitClinicaDAS> parseSituacionClinicaFuncional(HttpServletRequest request) throws ParseException {
		ArrayList<SitClinicaDAS> scfList = new ArrayList<SitClinicaDAS>();
		
		JsonArray jsonSitClinicaDAS = JsonParser.parseString(request.getParameter("actuacion"))
										.getAsJsonObject().get("scf").getAsJsonArray();
		
		for (JsonElement item : jsonSitClinicaDAS) {
			SitClinicaDAS sitClinicaDAS = new SitClinicaDAS();
			String valor   = item.getAsJsonObject().get("valor").getAsString();
			int posicion   = item.getAsJsonObject().get("posicion").getAsInt();
			int tipoSCFDAS = item.getAsJsonObject().get("tipo").getAsInt();
			
			sitClinicaDAS.setValor(valor);
			sitClinicaDAS.setPosicion(posicion);
			sitClinicaDAS.setTipoSCFDAS(tipoSCFDAS);
			
			scfList.add(sitClinicaDAS);
		}
		
		return scfList;
	}
	
	
	private static ArrayList<Tratamiento> parseTratamientos(HttpServletRequest request) throws ParseException {
		ArrayList<Tratamiento> tratamientos = new ArrayList<Tratamiento>();
		
		JsonArray jsonTratamientos = JsonParser.parseString(request.getParameter("actuacion"))
									.getAsJsonObject().get("tratamientos").getAsJsonArray();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		for (JsonElement item : jsonTratamientos) {
			Tratamiento tratamiento = new Tratamiento();
			String valor = item.getAsJsonObject().get("valor").getAsString();
			int posicion = item.getAsJsonObject().get("posicion").getAsInt();
			String fechaInicStr = item.getAsJsonObject().get("fechaInicio").getAsString();
			Date fecInic = fechaInicStr.isEmpty() ? null : new Date(sdf.parse(fechaInicStr).getTime());
			String fechaFinStr = item.getAsJsonObject().get("fechaFin").getAsString();
			Date fecFina = fechaFinStr.isEmpty() ? null : new Date(sdf.parse(fechaFinStr).getTime());
			
			tratamiento.setValor(valor);
			tratamiento.setPosicion(posicion);
			tratamiento.setFechaInicio(fecInic);
			tratamiento.setFechaFin(fecFina);
			
			tratamientos.add(tratamiento);
		}
		
		return tratamientos;
	}
	
	
	private static TratamientosMup parseTratamientosMup(HttpServletRequest request) throws ParseException {
		TratamientosMup mup = new TratamientosMup();
		
		JsonObject jsonMup = JsonParser.parseString(request.getParameter("actuacion"))
							.getAsJsonObject().get("tratamientos-mup").getAsJsonObject();
		
		mup.setTratamientoRecomendacion(jsonMup.get("trat-recom").getAsString());
		mup.setDescripcion(jsonMup.get("mup").getAsString());
		
		return mup;
	}
	
	
	private static ArrayList<ProgsUnidsEspProcs> parseProgramasUnidadesEspecialesProcesos(HttpServletRequest request) throws ParseException {
		ArrayList<ProgsUnidsEspProcs> puepList = new ArrayList<ProgsUnidsEspProcs>();
		
		JsonArray jsonPuep = JsonParser.parseString(request.getParameter("actuacion"))
											.getAsJsonObject().get("puep").getAsJsonArray();
		
		for (JsonElement item : jsonPuep) {
			ProgsUnidsEspProcs puep = new ProgsUnidsEspProcs();
			String valor = item.getAsJsonObject().get("valor").getAsString();
			int posicion = item.getAsJsonObject().get("posicion").getAsInt();
			
			puep.setValor(valor);
			puep.setPosicion(posicion);
			
			puepList.add(puep);
		}
		
		return puepList;
	}
	
	
	public static DatosClinicos parseDatosClinicos(HttpServletRequest request) throws ParseException {
		DatosClinicos datosClinicos = new DatosClinicos();
		
		JsonObject json = JsonParser.parseString(request.getParameter("datos-clinicos")).getAsJsonObject();
		
		datosClinicos.setAntecedentes(json.get("antecedentes").getAsString());
		datosClinicos.setEnfermedadActual(json.get("enfermedadActual").getAsString());
		datosClinicos.setEvolucionComentarios(json.get("evolucionComentarios").getAsString());
		datosClinicos.setIdPaciente(json.get("idPaciente").getAsInt());//, -1));
		datosClinicos.setIdActuacion(json.get("idActuacion").getAsInt());//, -1));
		
		return datosClinicos;
	}
	
	
	public static ParsedActuacion parseActuacion(HttpServletRequest request) throws ParseException {
		ParsedActuacion parsedActuacion = new ParsedActuacion();
		
		try {
			parsedActuacion.setPaciente(parsePacienteJson(request));
			
			JsonObject json = JsonParser.parseString(request.getParameter("actuacion")).getAsJsonObject();
			
			Actuacion actuacion = new Actuacion();
			actuacion.setIdActuacion(json.has("id-actuacion") ? json.get("id-actuacion").getAsInt() : -1);
			actuacion.setFecha(RequestParser.parseSqlDateParam(json.get("fecha-actuacion").getAsString()));
			actuacion.setIncapacidadTotal(json.get("incapacidad-total").getAsBoolean());
			actuacion.setCuratelaSalud(json.get("curatela-salud").getAsBoolean());
			actuacion.setCuratelaEconomica(json.get("curatela-economica").getAsBoolean());
			actuacion.setIdProfesional(json.get("id-profesional").getAsString());//, ""));
			actuacion.setCodigoAgenda(json.get("id-agenda").getAsString());//, ""));
			actuacion.setLugarAtencion(json.get("lugar-ate").getAsString());//, ""));
			actuacion.setTipoPrestacion(json.get("tipo-prest").getAsString());//, ""));
			actuacion.setEquipoDeCalle(json.get("equipo-calle").getAsBoolean());//, false));
			actuacion.setFechaAlta(RequestParser.parseSqlDateParam(json.get("fecha-alta").getAsString()));//, "")));
			actuacion.setMotivoAlta(RequestParser.parseIntParam(json.get("motivo-alta").getAsString()));//, "0")));
			actuacion.setProgramaContinuidadCuidados(json.get("prog-cont-cuidados").getAsBoolean());
			actuacion.setProgramaJoven(json.get("prog-joven").getAsBoolean());
			actuacion.setFechaInicioProgramaJoven(RequestParser.parseSqlDateParam(json.get("fec-ini-pj").getAsString()));
			actuacion.setFechaFinProgramaJoven(RequestParser.parseSqlDateParam(json.get("fec-fin-pj").getAsString()));
			actuacion.setMedidaProteccion(json.get("med-proteccion").getAsBoolean());
			actuacion.setResidencia(json.get("residencia").getAsBoolean());
			
			int numCita = 0;
			if (json.get("numeroCita") != JsonNull.INSTANCE) {
				numCita = json.get("numeroCita").getAsInt();
			}
			long numICU = 0;
			String icuStr = "";
			if (json.get("numeroICU") != JsonNull.INSTANCE) {
				try {
					icuStr = json.get("numeroICU").getAsString();//, "");
					numICU = Long.parseLong((icuStr == null || icuStr.isEmpty()) ? "0" : icuStr);
				}
				catch(UnsupportedOperationException e) {
					numICU = json.get("numeroICU").getAsInt();
				}
			}
			actuacion.setNumeroCita(numCita);
			actuacion.setNumeroICU(numICU == 0 ? "" : String.valueOf(numICU));
	
			actuacion.setIdPaciente(parsedActuacion.getPaciente().getIdPaciente());
			
			parsedActuacion.setActuacion(actuacion);
			
			parsedActuacion.setDiagnosticos(parseDiagnosticos(request));
			parsedActuacion.setSitClinicaDAS(parseSituacionClinicaFuncional(request));
			parsedActuacion.setTratamientos(parseTratamientos(request));
			parsedActuacion.setTratamientosMup(parseTratamientosMup(request));
			parsedActuacion.setProgsUnidsEspProcs(parseProgramasUnidadesEspecialesProcesos(request));
		}
		catch(ParseException ex) {
			throw ex;
		}
		
		return parsedActuacion;
	}
	
	
	private static Paciente parsePacienteJson(HttpServletRequest request) throws ParseException {
		Paciente paciente = new Paciente();
				
		JsonObject json = JsonParser.parseString(request.getParameter("actuacion"))
							.getAsJsonObject().get("paciente").getAsJsonObject();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		int idPaciente     = json.get("id-paciente").isJsonNull() ? 0 : json.get("id-paciente").getAsInt();//, -1);
		String nombre      = json.get("nombre").getAsString();//, "");
		String apellido1   = json.get("apellido1").getAsString();//, "");
		String apellido2   = json.get("apellido2").getAsString();//, "");
		short sexo         = Short.parseShort(json.get("sexo").getAsString());//, ""));
		String fechaNacStr = json.get("fec-nac").getAsString();//, "");
		Date fechaNac      = null;
		if (!fechaNacStr.equals("")) {
			fechaNac = new Date(sdf.parse(fechaNacStr).getTime());
		}
		String direccion = json.get("direccion").getAsString();//, "");
		String poblacion = json.get("poblacion").getAsString();//, "");
		String cpString = json.get("cod-postal").getAsString();//, "-1");
		int codigoPostal = Integer.parseInt(cpString.isEmpty() ? "-1" : cpString);;

		int tipoDoc = Integer.parseInt(json.get("tipo-doc").getAsString());//, "0"));
		String dni       = "";
		String pasaporte = "";
		String nie       = "";
		if (tipoDoc == 1) {
			dni       = json.get("num-doc").getAsString();//, "");
			pasaporte = "";
			nie       = "";
		}
		else if (tipoDoc == 2) {
			dni       = "";
			pasaporte = json.get("num-doc").getAsString();//, "");
			nie       = "";
		}
		else if (tipoDoc == 3) {
			dni       = "";
			pasaporte = "";
			nie       =  json.get("num-doc").getAsString();//, "");
		}
		
		String numHCStr = json.get("numeroHC").getAsString();//, "");
		int numeroHC    = 0;
		if (!numHCStr.isEmpty()) {
			numeroHC = Integer.parseInt(numHCStr);
		}
		String numeroSS    = json.get("numeroSS").getAsString();//, "");
		String numeroTS    = json.get("numeroTS").getAsString();//, "");
		String numCIPAStr  = json.get("numeroCIPA").getAsString();//, "");
		int numeroCIPA = -1;
		if (!numCIPAStr.isEmpty())
			numeroCIPA = Integer.parseInt(numCIPAStr);
		String telefono1   = json.get("telefono1").getAsString();//, "");
		String telefono2   = json.get("telefono2").getAsString();//, "");
		String familiar    = json.get("familiar").getAsString();//, "");
		String tlfFamiliar = json.get("telefono-familiar").getAsString();//, "");
		
		paciente.setIdPaciente(idPaciente);
		paciente.setNombre(nombre);
		paciente.setApellido1(apellido1);
		paciente.setApellido2(apellido2);
		paciente.setSexo(sexo);
		paciente.setFechaNacimiento(fechaNac);
		paciente.setDireccion(direccion);
		paciente.setPoblacion(poblacion);
		paciente.setCodigoPostal(codigoPostal);
		paciente.setDni(dni);
		paciente.setPasaporte(pasaporte);
		paciente.setNie(nie);
		paciente.setNumeroHistoriaClinica(numeroHC);
		paciente.setNumeroSeguridadSocial(numeroSS);
		paciente.setNumeroTarjetaSanitaria(numeroTS);
		paciente.setNumeroCIPA(numeroCIPA);
		paciente.setTelefono1(telefono1);
		paciente.setTelefono2(telefono2);
		paciente.setFamiliar(familiar);
		paciente.setTelefonoFamiliar(tlfFamiliar);
		
		return paciente;
	}
	
	
	public static Paciente parsePaciente(HttpServletRequest request) {
		
		Paciente paciente = new Paciente();
		
		int idPaciente     = RequestParser.parseIntParam(request.getParameter("idPaciente"));
		String nombre      = request.getParameter("nombre");
		String apellido1   = request.getParameter("apellido1");
		String apellido2   = request.getParameter("apellido2");
		short sexo         = RequestParser.parseShortParam(request.getParameter("sexo"));
		Date fechaNac      = RequestParser.parseSqlDateParam(request.getParameter("fechaNac"));
		String direccion   = request.getParameter("direccion");
		String poblacion   = request.getParameter("poblacion");
		int codigoPostal   = RequestParser.parseIntParam(request.getParameter("codigoPostal"));
		String dni         = request.getParameter("dni");
		String pasaporte   = request.getParameter("pasaporte");
		String nie         = request.getParameter("nie");
		int numeroHC       = RequestParser.parseIntParam(request.getParameter("numeroHC"));
		String numeroCIPA  = request.getParameter("numeroCIPA");
		String numeroTS    = request.getParameter("numeroTS");
		String telefono1   = request.getParameter("telefono1");
		String telefono2   = request.getParameter("telefono2");
		String familiar    = request.getParameter("familiar");
		String tlfFamiliar = request.getParameter("telefonoFamiliar");
		
		paciente.setIdPaciente(idPaciente);
		paciente.setNombre(nombre);
		paciente.setApellido1(apellido1);
		paciente.setApellido2(apellido2);
		paciente.setSexo(sexo);
		paciente.setFechaNacimiento(fechaNac);
		paciente.setDireccion(direccion);
		paciente.setPoblacion(poblacion);
		paciente.setCodigoPostal(codigoPostal);
		paciente.setDni(dni);
		paciente.setPasaporte(pasaporte);
		paciente.setNie(nie);
		paciente.setNumeroHistoriaClinica(numeroHC);
		paciente.setNumeroCIPA(numeroCIPA.isEmpty() ? -1 : Integer.parseInt(numeroCIPA));
		paciente.setNumeroTarjetaSanitaria(numeroTS);
		paciente.setTelefono1(telefono1);
		paciente.setTelefono2(telefono2);
		paciente.setFamiliar(familiar);
		paciente.setTelefonoFamiliar(tlfFamiliar);
		
		return paciente;
	}
	
}
