package org.hcsc.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.hcsc.daos.DAOCieNoPsiquiatria;
import org.hcsc.daos.DAOCiePsiquiatria;
import org.hcsc.daos.DAOFactory;
import org.hcsc.exceptions.FileIOException;
import org.hcsc.exceptions.HSCException;
import org.hcsc.hl7.ClientHL7;
import org.hcsc.models.Actuacion;
import org.hcsc.models.ComboOption;
import org.hcsc.models.DatosClinicos;
import org.hcsc.models.Diagnostico;
import org.hcsc.models.InputFields;
import org.hcsc.models.Paciente;
import org.hcsc.models.ProgsUnidsEspProcs;
import org.hcsc.models.SitClinicaDAS;
import org.hcsc.models.Tratamiento;
import org.hcsc.models.TratamientosMup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ControladorActuacion {

	public String cargarCamposVista() throws HSCException {
		JsonObject jsonResponse = new JsonObject();
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			ArrayList<InputFields> inputFields = daoFactory.crearDAOInputFields().getInputFields();
			
			/** Cerrar conexion con la BDD **/
			daoFactory.close();
		
			JsonArray jsonArray = new JsonArray();
			
			for (InputFields input : inputFields) {
				JsonObject item = new JsonObject();
				item.addProperty("Seccion", input.getSeccion());
				item.addProperty("Label", input.getLabel());
				item.addProperty("TipoCampo" , input.getTipoCampo());
				item.addProperty("Totales"   , input.getTotales());
				item.addProperty("Min"       , input.getMin());
				item.addProperty("Max"       , input.getMax());
				item.addProperty("Opciones"  , input.getOpciones());
				item.addProperty("Posicion"  , input.getPosicion());
				item.addProperty("CheckField", input.isCheckfield());
				item.addProperty("Manual"    , input.isManual());
				item.addProperty("UrlManual" , input.getUrlManual());
	
				jsonArray.add(item);
			}
			
			jsonResponse.add("Inputs", jsonArray);
		}
		catch(HSCException ex) {
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
 		return jsonResponse.toString();
	}
	
	public JsonObject obtenerActuacionPorIdPaciente(int idPaciente, int userId) throws HSCException {
		JsonObject result = null;
		
		DAOFactory daoFactory = new DAOFactory();
		daoFactory.setAutoCommit(false);
		
		try {
			Paciente paciente = daoFactory.crearDAOPacientes().obtenerPorID(idPaciente);
			
			if (paciente.getNumeroHistoriaClinica() > 0 ||
					paciente.getNumeroCIPA() > 0 &&
					paciente.getNumeroTarjetaSanitaria() != null &&
					paciente.getNumeroTarjetaSanitaria().equals("") == false) {
				
				ClientHL7 hl7Client = new ClientHL7();
				ArrayList<Paciente> pacientesHL7 = hl7Client.sendQRY_A19(paciente);
				
				if (!pacientesHL7.isEmpty()) {
					paciente = pacientesHL7.get(0);
					paciente.setIdPaciente(idPaciente);
					
					daoFactory.crearDAOPacientes().actualizar(paciente);
				}
				
				daoFactory.commit();
			}
			
			int numRegistros         = daoFactory.crearDAOActuaciones()
											.obtenerNumRegistros(idPaciente);
			ArrayList<String> fechas = daoFactory.crearDAOActuaciones()
											.obtenerTodasLasFechas(idPaciente);
			Actuacion actuacion      = daoFactory.crearDAOActuaciones()
											.obtenerUltimaPorIdPaciente(idPaciente);
			
			DatosClinicos datosClinicos = daoFactory.crearDAODatosClinicos()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<Diagnostico> diagnosticos = daoFactory.crearDAODiagnosticos()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<Tratamiento> tratamientos = daoFactory.crearDAOTratamientos()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			TratamientosMup mup = daoFactory.crearDAOTratamientosMup()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<SitClinicaDAS> sitClinicaDAS = daoFactory.crearDAOSitClinicaDAS()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<ProgsUnidsEspProcs> puep = daoFactory.crearDAOProgsUnidsEspProcs()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			JsonObject jsonFacultativo = daoFactory.crearDAOCitas()
									.obtenerNombreFacultativoPorDni(actuacion.getIdProfesional());
			
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(jsonFacultativo.get("nombre").getAsString());
			strBuilder.append(" " + jsonFacultativo.get("apellido1").getAsString());
			strBuilder.append(" " +jsonFacultativo.get("apellido2").getAsString());
			
			String nombreFacultativo = strBuilder.toString();/*jsonFacultativo.get("nombre").getAsString();			
			nombreFacultativo += " " + jsonFacultativo.get("apellido1").getAsString();
			nombreFacultativo += " " + jsonFacultativo.get("apellido2").getAsString();*/
			actuacion.setCodEmpleado(actuacion.getIdProfesional());
			actuacion.setIdProfesional(nombreFacultativo);
			
			/** Almacenar el control del acceso al registro **/
			daoFactory.crearDAOAccesos().grabarAcceso
			(
					actuacion.getIdActuacion(),
					userId, actuacion.getIdPaciente(),
					paciente.getNumeroHistoriaClinica(), actuacion.getNumRegistro(),
					new java.sql.Date(actuacion.getFecha().getTime())
			);
			
			/** Cerrar conexion con BBDD **/
			daoFactory.close();
			
			result = actuacionToJson(actuacion, datosClinicos, diagnosticos, tratamientos,
												mup, sitClinicaDAS, puep);
			
			result.add("Paciente", paciente.toJson());
			result.addProperty("totalRegistros", numRegistros);
			JsonArray jsonFechas = new JsonArray();
			for (String fecha : fechas) {
				jsonFechas.add(fecha);
			}
			result.add("fechasActuaciones", jsonFechas);
		}
		catch(HSCException ex) {
			daoFactory.rollback();
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result;
	}
	
	public JsonObject obtenerActuacionPorNumRegistro(int idPaciente, int numRegistro, int userId) throws HSCException {
		JsonObject result = null;
		
		DAOFactory daoFactory = new DAOFactory();
		
		try {
			Paciente paciente = daoFactory.crearDAOPacientes().obtenerPorID(idPaciente);
			
			Actuacion actuacion = daoFactory.crearDAOActuaciones()
									.obtenerPorIdPacienteNumRegistro(idPaciente, numRegistro);
			
			DatosClinicos datosClinicos = daoFactory.crearDAODatosClinicos()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<Diagnostico> diagnosticos = daoFactory.crearDAODiagnosticos()
												.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<Tratamiento> tratamientos = daoFactory.crearDAOTratamientos()
												.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			TratamientosMup mup = daoFactory.crearDAOTratamientosMup()
								.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<SitClinicaDAS> scf = daoFactory.crearDAOSitClinicaDAS()
										.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<ProgsUnidsEspProcs> puep = daoFactory.crearDAOProgsUnidsEspProcs()
											.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			JsonObject jsonFacultativo = daoFactory.crearDAOCitas()
									.obtenerNombreFacultativoPorDni(actuacion.getIdProfesional());
			
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(jsonFacultativo.get("nombre").getAsString());
			strBuilder.append(" " + jsonFacultativo.get("apellido1").getAsString());
			strBuilder.append(" " + jsonFacultativo.get("apellido2").getAsString());
			String nombreFacultativo = strBuilder.toString();/*jsonFacultativo.get("nombre").getAsString();
			nombreFacultativo += " " + jsonFacultativo.get("apellido1").getAsString();
			nombreFacultativo += " " + jsonFacultativo.get("apellido2").getAsString();*/
			actuacion.setCodEmpleado(actuacion.getIdProfesional());
			actuacion.setIdProfesional(nombreFacultativo);
			
			/** Almacenar el control del acceso al registro **/
			daoFactory.crearDAOAccesos().grabarAcceso
			(
					actuacion.getIdActuacion(),
					userId, actuacion.getIdPaciente(),
					paciente.getNumeroHistoriaClinica(), actuacion.getNumRegistro(),
					new java.sql.Date(actuacion.getFecha().getTime())
			);
			
			/** Cerrar conexion con BBDD **/
			daoFactory.close();
			
			result = actuacionToJson(actuacion, datosClinicos, diagnosticos,
													tratamientos, mup, scf, puep);
			result.add("Paciente",  paciente.toJson());
		}
		catch(HSCException ex) {
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result;
	}

	/**
	 * 
	 * @param parsedActuacion
	 **/
	public JsonObject insertar(ParsedActuacion parsedActuacion) throws HSCException {
		JsonObject result = new JsonObject();
		
//		boolean ops        = true;
		int idPaciente     = -1;
		int idActuacion    = -1;
		
		Actuacion actuacion = parsedActuacion.getActuacion();
		
		DAOFactory daoFactory = new DAOFactory();
		daoFactory.setAutoCommit(false);
		
		try {
			idPaciente = parsedActuacion.getPaciente().getIdPaciente();
			
			/** Si el ID del paciente recogido en la actuacion procesada es menor o igual a cero,
			 ** buscamos el paciente por identificador único en la BBDD por si acaso estuviera ya 
			 **/
			int idPacienteEncontrado = 0;
			if (idPaciente <= 0) {
				idPacienteEncontrado = daoFactory.crearDAOPacientes()
								.comprobarIdentificadoresUnicos(parsedActuacion.getPaciente());
			}
			/** Si no, el idPaciente debe ser correcto pues se establecio anteriormente
			 ** navegando por los registros del paciente en la pantalla de registro
			 **/
			else {
				idPacienteEncontrado = idPaciente;
			}
						
			/** Si encontramos el paciente ya existente en la BBDD **/
			if (idPacienteEncontrado > 0) {
				idPaciente = idPacienteEncontrado;
				Paciente paciente = daoFactory.crearDAOPacientes().obtenerPorID(idPacienteEncontrado);
				
				/** Si el usuario introdujo un CIPA o Numero de Historia Clinica,
				 ** el paciente debe existir en el HPHIS, tomar los datos del HPHIS.
				 **/
				if (parsedActuacion.getPaciente().getNumeroHistoriaClinica() > 0 ||
						parsedActuacion.getPaciente().getNumeroCIPA() > 0) {
					ClientHL7 hl7Client = new ClientHL7();
					ArrayList<Paciente> pacienteHL7 = hl7Client.sendQRY_A19(parsedActuacion.getPaciente());
					if (!pacienteHL7.isEmpty())
						paciente = pacienteHL7.get(0);
					paciente.setIdPaciente(idPaciente);
					
					if (!daoFactory.crearDAOPacientes().buscarFechaAltaEquipoCalle(idPaciente)) {
						java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
						actuacion.setFechaAltaEquipoCalle(today);
						
						/** Se esta produciendo el 'alta' del Equipo de Calle del paciente
						 ** en esta intervención con lo que lo marcamos a false.
						 **/
						actuacion.setEquipoDeCalle(false);
					}
					/** Actualizar los datos del paciente con los datos mas nuevos recibidos del HIS
					 **/
					daoFactory.crearDAOPacientes().actualizar(paciente);				
				}
				else {
					/** Actualizar el paciente een BBDD local, por si el usuario añadió algún dato más **/
					daoFactory.crearDAOPacientes().actualizar(parsedActuacion.getPaciente());
				}
			}
			else {
				/** El paciente no se encontró en la BD Local, y se recibió
				 ** un idPaciente menor o igual a 0, guardar como nuevo paciente.
				 **/
				idPaciente = daoFactory.crearDAOPacientes().insertar(parsedActuacion.getPaciente());			
			}
			
			actuacion.setIdPaciente(idPaciente);
			
			idActuacion = daoFactory.crearDAOActuaciones()
										.insertar(actuacion);
			daoFactory.crearDAODiagnosticos()
							.insertar(parsedActuacion.getDiagnosticos(), idActuacion);
			daoFactory.crearDAOTratamientos()
							.insertar(parsedActuacion.getTratamientos(), idActuacion);
			daoFactory.crearDAOTratamientosMup()
							.insertar(parsedActuacion.getTratamientosMup(), idActuacion);
			daoFactory.crearDAOSitClinicaDAS()
							.insertar(parsedActuacion.getSitClinicaDAS(), idActuacion);
			daoFactory.crearDAOProgsUnidsEspProcs()
							.insertar(parsedActuacion.getProgsUnidsEspProcs(), idActuacion);
			
			daoFactory.commit();
			
			result.addProperty("status", 1);
			result.addProperty("message", "REGISTRO GUARDADO CON ÉXITO");
			result.addProperty("idPaciente", idPaciente);
			result.addProperty("idActuacion", idActuacion);
			
//			if (!ops) {
//				result.addProperty("status", 0);
//				result.addProperty("message", "ERROR GUARDANDO...");
//			}
//			else {
//				result.addProperty("status", 1);
//				result.addProperty("message", "REGISTRO GUARDADO CON ÉXITO");
//				result.addProperty("idPaciente", idPaciente);
//				result.addProperty("idActuacion", idActuacion);
//			}
		}
		catch(HSCException ex) {
			daoFactory.rollback();
			result.addProperty("status", 0);
			result.addProperty("message", "ERROR GUARDANDO...");
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result;
	}

	/**
	 * 
	 * @param parsedActuacion
	 * @return
	 * @throws HSCException
	 * @throws SQLException
	 */
	public JsonObject actualizar(ParsedActuacion parsedActuacion) throws HSCException {
		JsonObject jsonResult = new JsonObject();
		
		int result = 0;
		int idPaciente  = parsedActuacion.getPaciente().getIdPaciente();		
		int idActuacion = parsedActuacion.getActuacion().getIdActuacion();
		
		DAOFactory daoFactory = new DAOFactory();
		daoFactory.setAutoCommit(false);
		
		try {
			synchronized(this) {
				daoFactory.crearDAOPacientes().actualizar(parsedActuacion.getPaciente());

				Actuacion actuacion = parsedActuacion.getActuacion();
				daoFactory.crearDAOActuaciones().actualizar(actuacion);
				
				daoFactory.crearDAODiagnosticos()
								.actualizar(parsedActuacion.getDiagnosticos(), idActuacion);
				
				daoFactory.crearDAOSitClinicaDAS()
								.actualizar(parsedActuacion.getSitClinicaDAS(), idActuacion);
				
				daoFactory.crearDAOTratamientos()
								.actualizar(parsedActuacion.getTratamientos(), idActuacion);
				
				daoFactory.crearDAOTratamientosMup()
								.actualizar(parsedActuacion.getTratamientosMup(), idActuacion);
				
				daoFactory.crearDAOProgsUnidsEspProcs()
								.actualizar(parsedActuacion.getProgsUnidsEspProcs(), idActuacion);			
	
				daoFactory.commit();
			}
		
			if (result < 0) {
				jsonResult.addProperty("status", 0);
				jsonResult.addProperty("message", "ERROR ACTUALIZANDO...");
			}
			else {
				jsonResult.addProperty("status", 1);
				jsonResult.addProperty("message", "REGISTRO ACTUALIZADO CON ÉXITO");
				jsonResult.addProperty("idPaciente", idPaciente);
				jsonResult.addProperty("idActuacion", idActuacion);
			}
		}
		catch(HSCException ex) {
			daoFactory.rollback();
			result = -1;
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return jsonResult;		
	}
	

	/**
	 * 
	 * @param actuacion
	 * @param datosClinicos
	 * @param diagnosticos
	 * @param tratamientos
	 * @param mup
	 * @param sitClinicaDAS
	 * @param progsUnidsEspProcs
	 * @return
	 * @throws FileIOException
	 **/
	private JsonObject actuacionToJson(Actuacion actuacion, DatosClinicos datosClinicos,
			ArrayList<Diagnostico> diagnosticos, ArrayList<Tratamiento> tratamientos,
			TratamientosMup mup, ArrayList<SitClinicaDAS> sitClinicaDAS,
			ArrayList<ProgsUnidsEspProcs> progsUnidsEspProcs) throws FileIOException { 

		JsonObject result = new JsonObject();

		/** ACTUACION **/
		result.add("Actuacion", actuacion == null ? new JsonObject() : actuacion.toJson());

		
		/** DATOS CLINICOS **/
		result.add("DatosClinicos", datosClinicos == null ? new JsonObject() : datosClinicos.toJson());


		/** DIAGNOSTICOS **/
		JsonArray jsonDiagnosticos = new JsonArray();
		for (Diagnostico diagnostico : diagnosticos) {
			JsonObject item = diagnostico.toJson();

			/** Buscar la descripcion para el codigo CIE del diagnostico y añadirlo al JSON **/
			ComboOption diagCombo = null;
			if (diagnostico.getTipoDiagnostico() < 3) {
				DAOCiePsiquiatria daoCiePsiquiatria = new DAOCiePsiquiatria();
				diagCombo = daoCiePsiquiatria.getCIE_XML(diagnostico.getCieDiagnostico());

				diagCombo.setValue(diagCombo.getValue().split(" ", 2)[1]);
			}
			else if (diagnostico.getTipoDiagnostico() == 3) {
				DAOCieNoPsiquiatria daoCieNoPsiquiatria = new DAOCieNoPsiquiatria();
				diagCombo = daoCieNoPsiquiatria.getCIE_XML(diagnostico.getCieDiagnostico());

				diagCombo.setValue(diagCombo.getValue().split(" ", 2)[1]);				
			}
			else {
				diagCombo = new ComboOption(diagnostico.getCieDiagnostico(), diagnostico.getCieDiagnostico());
			}

			item.addProperty("descripcion", diagCombo.getValue());

			jsonDiagnosticos.add(item);
		}
		result.add("Diagnosticos", jsonDiagnosticos);


		/** TRATAMIENTOS **/
		JsonArray jsonTratamientos = new JsonArray();
		for (Tratamiento item : tratamientos) {
			jsonTratamientos.add(item.toJson());
		}
		result.add("Tratamientos", jsonTratamientos);


		/** TRATAMIENTOS MUP **/
		result.add("TratamientosMUP", mup.toJson());


		/** SITUACION CLINICA Y FUNCIONAL **/
		JsonArray jsonSitClinicaDAS = new JsonArray();
		for (SitClinicaDAS item : sitClinicaDAS) {
			jsonSitClinicaDAS.add(item.toJson());
		}
		result.add("SituacionClinicaFuncional", jsonSitClinicaDAS);


		/** PROGRAMAS / UNIDADES ESPECIALES / PROCESOS **/
		JsonArray jsonPUEP = new JsonArray();
		for (ProgsUnidsEspProcs item : progsUnidsEspProcs) {
			jsonPUEP.add(item.toJson());
		}
		result.add("ProgramasUnidadesEspecialesProcesos", jsonPUEP);


		return result;
	}
	
	/**
	 * 
	 * @param dni
	 * @return
	 **/
	private JsonObject obtenerNombreFacultativoPorDni(String dni) throws HSCException {
		DAOFactory daoFactory = new DAOFactory();
		
		JsonObject result = null;
		
		try {
			result = daoFactory.crearDAOCitas().obtenerNombreFacultativoPorDni(dni);
		}
		catch(HSCException ex) {
			Logger.getLogger(ControladorActuacion.class).info("obtenerNombreFacultativoPorDni()", ex);
			throw ex;
		}
		finally {
			daoFactory.close();
		}
		
		return result;
	}
	
	public ByteArrayOutputStream enviarInforme(String data, javax.servlet.ServletContext context) throws HSCException {
		
		JsonObject jsonData   = JsonParser.parseString(data).getAsJsonObject();
		
		JsonObject result = obtenerNombreFacultativoPorDni(jsonData.get("codfacul").getAsString());

		int codigoFacultativo       = Integer.parseInt(jsonData.get("codfacul").getAsString());
		String nombreFacultativo    = result.get("nombre").getAsString();
		String apellido1Facultativo = result.get("apellido1").getAsString();
		String apellido2Facultativo = result.get("apellido2").getAsString();
		
		jsonData.addProperty("nombreFacultativo", nombreFacultativo + " " + apellido1Facultativo + " " + apellido2Facultativo);
		
		/** Generar stream con el contenido del PDF **/
		ByteArrayOutputStream pdfStream = null;
		try { 
			org.hcsc.hl7.GeneradorInformePDF pdfGen = new org.hcsc.hl7.GeneradorInformePDF();
			pdfStream = pdfGen.generarInformePDF(jsonData, context);
			Logger.getLogger(ControladorActuacion.class).info("INFORME GENERADO usuario: "
					+ nombreFacultativo + " " + apellido1Facultativo + " " + apellido2Facultativo
					+ " (" + codigoFacultativo + ")");
		}
		catch(IOException ex)
		{
			Logger.getLogger(ControladorActuacion.class).error("IOException: StackTrace: ", ex);
		}
		
		boolean enviarAlHPHIS = jsonData.get("enviarHIS").getAsBoolean();
		Logger.getLogger(ControladorActuacion.class).info("¿Enviar al HPHIS?: " + (enviarAlHPHIS ? "Si": "No"));
		if (enviarAlHPHIS) {
			Logger.getLogger(ControladorActuacion.class).info("Tratando de enviar informe...");
			org.hcsc.hl7.ClientHL7 hl7Client = new org.hcsc.hl7.ClientHL7();
			hl7Client.sendMDM_T02(pdfStream, jsonData, codigoFacultativo,
							apellido1Facultativo + " " + apellido2Facultativo, nombreFacultativo);
		}
		
		return pdfStream;
	}
	
	public byte[] exportToCSVRelacionado() throws IOException, HSCException {
		String output = "";
		
		DAOFactory daoFactory = new DAOFactory();
		
		StringWriter writer        = new StringWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos        = new ZipOutputStream(baos);
		int gIndex = 0;
		
		try {
		java.util.List<String> exportHeaders = java.util.Arrays.asList(
			"Numero Historia Clinica",
			"Numero CIPA",
			"Nombre", "Apellido1", "Apellido2",
			"Sexo",
			"Fecha Nacimiento",
			"Fecha Actuacion",
			"Codigo Agenda",
			"Lugar Atencion",
			"Id Profesional",
			"Tipo Prestacion",
			"Numero Cita",
			"Numero ICU",
			"Diagnostico Psiquiatrico Principal 1", "Diagnostico Psiquiatrico Principal 2", "Diagnostico Psiquiatrico Principal 3",
			"Diagnostico Psiquiatrico Secundario 1", "Diagnostico Psiquiatrico Secundario 2", "Diagnostico Psiquiatrico Secundario 3",
			"Diagnostico Psiquiatrico Secundario 4", "Diagnostico Psiquiatrico Secundario 5", "Diagnostico Psiquiatrico Secundario 6",
			"Diagnostico No Psiquiatrico 1", "Diagnostico No Psiquiatrico 2", "Diagnostico No Psiquiatrico 3",
			"Diagnostico No Psiquiatrico 4", "Diagnostico No Psiquiatrico 5", "Diagnostico No Psiquiatrico 6",
			"Estadiaje Depresión", "Estadiaje Psicosis",
			"Escala CGI",
			"GAF",
			"CGAF",
			"Cuidado Personal",
			"Funcionamiento Ocupacional",
			"Funcionamiento Familiar",
			"Funcionamiento Social Amplio",
			"Psicoterapia Individual",
			"Fecha Inicio", "Fecha Fin",
			"Psicoterapia Grupal",
			"Fecha Inicio", "Fecha Fin",
			"Psicoeducación Grupal",
			"Fecha Inicio", "Fecha Fin",
			"Grupo Multifamiliar",
			"Fecha Inicio", "Fecha Fin",
			"Terapia Familiar",
			"Fecha Inicio", "Fecha Fin",
			"Programa Continuidad Cuidados",
			"Proceso Psicosis (No intervención precoz)",
			"Programa Intervención Precoz Psicosis",
			"Proceso Depresión",
			"Proceso TCA",
			"Unidad de Trastorno de Personalidad",
			"Unidad de Psicogeriatría",
			"Tratamiento ambulatorio intensivo SMNyA",
			"Programa Transición",
			"Programa C",
			"Programa Joven",
			"Fecha Fin"
		);
		
		char separator = ';';
		
		CSVExport.writeLine(writer, exportHeaders, separator);
		
		//int baseIdActuacion = 320;
		//ArrayList<Integer> idsActuaciones = daoFactory.crearDAOActuaciones().obtenerTodosIdActuaciones();
		
		for (int index = 320; index < 370/*idsActuaciones.size()*/; index++) {
			gIndex = index;
			Actuacion actuacion = daoFactory.crearDAOActuaciones().obtenerPorIdActuacion(index/*idsActuaciones.get(index)*/);
			if (actuacion != null) {
			
			Paciente paciente = daoFactory.crearDAOPacientes().obtenerPorID(actuacion.getIdPaciente());
			if (paciente == null) { System.out.println("ID ACTUACION: " + index); }
			
			ArrayList<Diagnostico> diagnosticos = daoFactory.crearDAODiagnosticos()
					.obtenerPorIdActuacion(actuacion.getIdActuacion());
			ArrayList<SitClinicaDAS> sitClinicaDAS = daoFactory.crearDAOSitClinicaDAS()
					.obtenerPorIdActuacion(actuacion.getIdActuacion());
			ArrayList<Tratamiento> tratamientos = daoFactory.crearDAOTratamientos()
					.obtenerPorIdActuacion(actuacion.getIdActuacion());
			ArrayList<ProgsUnidsEspProcs> puep = daoFactory.crearDAOProgsUnidsEspProcs()
					.obtenerPorIdActuacion(actuacion.getIdActuacion());
			
			ArrayList<String> rowLine = new ArrayList<String>();
			
			rowLine.add(Integer.toString(paciente.getNumeroHistoriaClinica()));
			rowLine.add(Integer.toString(paciente.getNumeroCIPA()));
			rowLine.add(String.valueOf(paciente.getNombre().isEmpty() ? "" : paciente.getNombre().charAt(0)));
			rowLine.add(paciente.getApellido1().isEmpty() ? "" : String.valueOf(paciente.getApellido1().charAt(0)));
			rowLine.add(paciente.getApellido2().isEmpty() ? "" : String.valueOf(paciente.getApellido2().charAt(0)));
			rowLine.add(Short.toString(paciente.getSexo()));
			rowLine.add(paciente.getFechaNacimiento() == null ? "" : paciente.getFechaNacimiento().toString());
			rowLine.add(actuacion.getFecha().toString());
			rowLine.add(actuacion.getCodigoAgenda());
			rowLine.add(actuacion.getLugarAtencion());
			rowLine.add(actuacion.getCodEmpleado());
			rowLine.add(actuacion.getTipoPrestacion());
			rowLine.add(Integer.toString(actuacion.getNumeroCita()));
			rowLine.add(actuacion.getNumeroICU());
			
			String diagP[] = {"", "", ""};
			String diagS[] = {"", "", "", "", "", ""};
			String diagN[] = {"", "", "", "", "", ""};
			String estadiajes[] = {"", ""};
			String estadiajesValores[] = { "1a", "1b", "2", "3a", "3b", "3c", "4" };
			
			for (Diagnostico diagnostico : diagnosticos) {
				if (diagnostico.getTipoDiagnostico() == 1) {
					diagP[diagnostico.getPosCombo() - 1] = diagnostico.getCieDiagnostico();
				}
				else if (diagnostico.getTipoDiagnostico() == 2) {
					diagS[diagnostico.getPosCombo() - 1] = diagnostico.getCieDiagnostico();
				}
				else if (diagnostico.getTipoDiagnostico() == 3) {
					diagN[diagnostico.getPosCombo() - 1] = diagnostico.getCieDiagnostico();
				}			
				else if (diagnostico.getTipoDiagnostico() == 4) {
					estadiajes[diagnostico.getPosCombo() - 1] = estadiajesValores[Integer.parseInt(diagnostico.getCieDiagnostico()) - 1];
				}			
			}
			
			for (int i = 0; i < diagP.length; i++) {
				rowLine.add(diagP[i]);
			}
			for (int i = 0; i < diagS.length; i++) {
				rowLine.add(diagS[i]);
			}
			for (int i = 0; i < diagN.length; i++) {
				rowLine.add(diagN[i]);
			}
			for (int i = 0; i < estadiajes.length; i++) {
				rowLine.add(estadiajes[i]);
			}
			
			
			String stringSCF[] = {"", "", ""};
			String stringDAS[] = {"", "", "", ""};
			
			for (SitClinicaDAS sitClinDAS : sitClinicaDAS) {
				if (sitClinDAS.getTipoSCFDAS() == 0) {
					stringSCF[sitClinDAS.getPosicion() - 1] = sitClinDAS.getValor();
				}
				else if (sitClinDAS.getTipoSCFDAS() == 1) {
					stringDAS[sitClinDAS.getPosicion() - 1] = sitClinDAS.getValor();
				}
			}
			
			for (int i = 0; i < stringSCF.length; i++) {
				rowLine.add(stringSCF[i]);
			}
			for (int i = 0; i < stringDAS.length; i++) {
				rowLine.add(stringDAS[i]);
			}
			
			
			String stringTrats[] = {"", "", "", "", ""};
			String stringFechI[] = {"", "", "", "", ""};
			String stringFechF[] = {"", "", "", "", ""};
			String tratamientosValores[] = { "0-5 sesiones", "6-10 sesiones", "11-15 sesiones", "16-20 sesiones" };
			
			for (Tratamiento tratamiento : tratamientos) {
				stringTrats[tratamiento.getPosicion() - 1] = tratamientosValores[Integer.parseInt(tratamiento.getValor()) - 1];
				stringFechI[tratamiento.getPosicion() - 1] = tratamiento.getFechaInicio().toString();
				stringFechF[tratamiento.getPosicion() - 1] = tratamiento.getFechaFin().toString();
			}
			
			for (int i = 0; i < stringTrats.length; i++) {
				rowLine.add(stringTrats[i]);
				rowLine.add(stringFechI[i]);
				rowLine.add(stringFechF[i]);
			}
			
			
			rowLine.add(actuacion.isProgramaContinuidadCuidados() ? "true" : "false");
			
			
			String stringPuep[] = {"", "", "", "", "", "", "", "", ""};
			
			for (ProgsUnidsEspProcs puepItem : puep) {
				stringPuep[puepItem.getPosicion() - 1] = puepItem.getValor();
			}
			for (int i = 0; i < stringPuep.length; i++) {
				rowLine.add(stringPuep[i]);
			}
			
			rowLine.add(actuacion.isProgramaJoven() ?
					(actuacion.getFechaInicioProgramaJoven() == null ? "" : actuacion.getFechaInicioProgramaJoven().toString()) : "");
			rowLine.add(actuacion.isProgramaJoven() ? 
					(actuacion.getFechaFinProgramaJoven() == null ? "" : actuacion.getFechaFinProgramaJoven().toString()) : "");
			
			CSVExport.writeLine(writer, rowLine, separator);
			}
		}
		
		output = writer.toString();
		
		zos.putNextEntry(new ZipEntry("export_relacional.csv"));
		zos.write(output.getBytes(Charset.forName("UTF-8")));
		zos.closeEntry();
		writer.getBuffer().setLength(0);
		}
		catch(HSCException ex) {
			Logger.getLogger(ControladorActuacion.class).error("HSCException: StackTrace: ", ex);
			throw ex;
		}
		catch(IOException ex) {
			Logger.getLogger(ControladorActuacion.class).error("IOException: StackTrace: ", ex);
			throw ex;
		}
		catch(RuntimeException ex) {
			System.out.println("ID ACTUACION: " + gIndex);
			ex.printStackTrace();
		}
		finally {
			daoFactory.close();
			writer.flush();
			zos.flush();
			baos.flush();
			writer.close();
			zos.close();
			baos.close();
		}
		
		return baos.toByteArray();
	}

	public byte[] exportToCSV() throws IOException, HSCException {
		String output = "";

		DAOFactory daoFactory = new DAOFactory();
		
		StringWriter writer        = new StringWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos        = new ZipOutputStream(baos);
		
		char separator = ';';
		
		try {
			/** PACIENTES a CSV
			 **/
			java.util.List<String> pacientesHeader = java.util.Arrays.asList("IdPaciente", "Nombre", "Apellido1", "Apellido2",
					"Sexo", "FechaNacimiento", "Direccion",	"Poblacion", "CodigoPostal", "Telefono1", "Telefono2",
					"Dni", "Pasaporte", "Nie", "NumeroHistoriaClinica",	"NumeroSeguridadSocial",
					"NumeroTarjetaSanitaria", "NumeroCIPA", "Familiar", "TelefonoFamiliar");
			
			CSVExport.writeLine(writer, pacientesHeader, separator);
						
			ArrayList<Paciente> pacientes = daoFactory.crearDAOPacientes().obtenerTodo();
			
			for (Paciente paciente : pacientes) { 
				CSVExport.writeLine(writer, paciente.toCSVStrings(), separator);
			}
			
			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("pacientes.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
			
			
			/** ACTUACIONES a CSV
			 **/
			java.util.List<String> actuacionHeader = java.util.Arrays.asList("idActuacion", "idPaciente", "numRegistro",
					"fecha", "incapacidadTotal", "curatelaSalud", "curatelaEconomica", "programaContinuidadCuidados",
					"programaJoven", "fechaInicioProgramaJoven", "fechaFinProgramaJoven",
					"codigoAgenda", "lugarAtencion", "idProfesional", "tipoPrestacion",
					"equipoDeCalle", "fechaAlta", "motivoAlta", "numeroCita", "numeroICU", "fechaAltaEquipoCalle",
					"medidaProteccion", "residencia");
			
			CSVExport.writeLine(writer, actuacionHeader, separator);
			
			ArrayList<Actuacion> actuaciones = daoFactory.crearDAOActuaciones().obtenerTodo();
			
			for (Actuacion actuacion : actuaciones) {
				CSVExport.writeLine(writer, actuacion.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("actuaciones.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
			
			
			/** DIAGNOSTICOS a CSV
			 **/
			java.util.List<String> diagnosticoHeader = java.util.Arrays.asList("idDiagnostico", "idActuacion",
					"tipoDiagnostico", "cieDiagnostico", "posCombo", "TipoDiagnostico");
			
			CSVExport.writeLine(writer, diagnosticoHeader, separator);
			
			ArrayList<Diagnostico> diagnosticos = daoFactory.crearDAODiagnosticos().obtenerTodo();
			
			for (Diagnostico diagnostico : diagnosticos) {
				CSVExport.writeLine(writer, diagnostico.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("diagnosticos.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
			
			
			/** TRATAMIENTOS a CSV
			 **/
//			java.util.List<String> tratamientosHeader = java.util.Arrays.asList("idTratamiento", "idActuacion",
//					"Posicion", "Valor", "FechaInicio", "FechaFin");
			java.util.List<String> tratamientosHeader = java.util.Arrays.asList(
					"idTratamiento", "idActuacion",
					"Psicoterapia Individual", "Psicoterapia Grupal", "Psicoeducación Grupal", "Grupo Multifamiliar", "Terapia Familiar",
					"FechaInicio", "FechaFin");
			
			CSVExport.writeLine(writer, tratamientosHeader, separator);
			
			ArrayList<Tratamiento> tratamientos = daoFactory.crearDAOTratamientos().obtenerTodo();
			
			for (Tratamiento tratamiento : tratamientos) {
				CSVExport.writeLine(writer, tratamiento.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("tratamientos.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
						
			
			/** TRATAMIENTOS MUP/RECOMENDACIONES a CSV
			 **/
			java.util.List<String> tratamientosMUPHeader = java.util.Arrays.asList("idTratamientoMup", "idActuacion",
					"Descripcion", "tratamientoRecomendacion");
			
			CSVExport.writeLine(writer, tratamientosMUPHeader, separator);
			
			ArrayList<TratamientosMup> tratamientosMup = daoFactory.crearDAOTratamientosMup().obtenerTodo();
			
			for (TratamientosMup tratamientoMup : tratamientosMup) {
				CSVExport.writeLine(writer, tratamientoMup.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("tratamientosMup.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
						
			
			/** SITUACIÓN CLÍNICA FUNCIONAL a CSV
			 **/
//			java.util.List<String> sitClinicaDASHeader = java.util.Arrays.asList("idSitClinicaDAS", "idActuacion",
//					"Posicion", "Valor", "TipoSCFDAS", "NombreCampoSCFDAS");
			java.util.List<String> sitClinicaDASHeader = java.util.Arrays.asList(
					"idSitClinicaDAS", "idActuacion",
					"Escala CGI", "GAF (en mayores de 18 años)", "CGAF (entre 0 y 17 años)",
					"Cuidado Personal", "Funcionamiento Ocupacional", "Funcionamiento Familiar", "Funcionamiento Social Amplio");
					
			
			CSVExport.writeLine(writer, sitClinicaDASHeader, separator);
			
			ArrayList<SitClinicaDAS> sitClinicaDASList = daoFactory.crearDAOSitClinicaDAS().obtenerTodo();
			
			for (SitClinicaDAS sitClinicaDAS : sitClinicaDASList) {
				CSVExport.writeLine(writer, sitClinicaDAS.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("sitClinicaDAS.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
			
			
			/** PROGRAMAS, UNIDADES ESPECIALES y PROCESOS a CSV
			 **/
//			java.util.List<String> progsUnidsEspProcsHeader = java.util.Arrays.asList("idDProgramasUnidadesProcesos", "idActuacion",
//					"Posicion", "Valor", "NombrePrograma");
			java.util.List<String> progsUnidsEspProcsHeader = java.util.Arrays.asList(
					"idDProgramasUnidadesProcesos", "idActuacion",
					"Proceso Psicosis (No intervención precoz)", "Programa Intervención Precoz Psicosis",
					"Proceso Depresión", "Proceso TCA", "Unidad de Trastorno de Personalidad",
					"Unidad de Psicogeriatría", "Tratamiento ambulatorio intensivo SMNyA",
					"Programa Transición", "Programa C");
			
			CSVExport.writeLine(writer, progsUnidsEspProcsHeader, separator);
			
			ArrayList<ProgsUnidsEspProcs> progsUnidsEspProcsList = daoFactory.crearDAOProgsUnidsEspProcs().obtenerTodo();
			
			for (ProgsUnidsEspProcs progUnidsEspProcs : progsUnidsEspProcsList) {
				CSVExport.writeLine(writer, progUnidsEspProcs.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("progsUnidsEspProcs.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
			
			
			/** DATOS CLINICOS a CSV
			 **/
			java.util.List<String> datosClinicosHeader = java.util.Arrays.asList("idActuacion", "idPaciente");/*,
					"antecedentes", "enfermedadActual", "evolucionComentarios");*/
			
			CSVExport.writeLine(writer, datosClinicosHeader, separator);
			
			ArrayList<DatosClinicos> datosClinicosList = daoFactory.crearDAODatosClinicos().obtenerTodo();
			
			for (DatosClinicos datosClinicos : datosClinicosList) {
				CSVExport.writeLine(writer, datosClinicos.toCSVStrings(), separator);
			}	

			output = writer.toString();
			
			zos.putNextEntry(new ZipEntry("datosClinicos.csv"));
			zos.write(output.getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
			writer.getBuffer().setLength(0);
		}
		catch(HSCException ex) {
			Logger.getLogger(ControladorActuacion.class).error("HSCException: StackTrace: ", ex);
			throw ex;
		}
		catch(IOException ex) {
			Logger.getLogger(ControladorActuacion.class).error("IOException: StackTrace: ", ex);
			throw ex;
		}
		finally {
			daoFactory.close();
			writer.flush();
			zos.flush();
			baos.flush();
			writer.close();
			zos.close();
			baos.close();
		}
		
		return baos.toByteArray();
	}

}
