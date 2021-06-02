package org.hcsc.hl7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;

import org.apache.log4j.Logger;
import org.hcsc.models.Paciente;


public class ClientHL7 {
	private final int port = 6670;
	private final String domain = "10.196.14.88";
	private final boolean useTLS = false;

	public ClientHL7() {
	}
/*	public Pacientes HL7(int)
		HSCLogger.logInfo("Testing " + this.domain + " (" + Integer.toString(this.port) + ")...");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String fechaCreacion = sdf.format(date);-*/
	
//	private String validarNumeroTelefono(String tlfno) {
//		String result = "";
//		
//		/** Si el valor pasado es la cadena no es convertible a valor numerico se devuelve cadena vacia **/
//		try {
//			int number = Integer.parseInt(tlfno);
//			result = Integer.toString(number);
//		}
//		catch(NumberFormatException e) {
//			return result;
//		}
//		
//		return result;
//	}

	
//	public ArrayList<Pacientes> sendQRY_A19(int numeroHCSH, int type/*, String CIPA, String numeroSS*/) {	
	public ArrayList<Paciente> sendQRY_A19(Paciente pacienteToFind) {	
		/** HL7 MSH Segment descriptor
		 * MSH.1 Field Separator
		 * MSH.2 Encoding Characters
		 * MSH.3 Sending Application
		 * MSH.4 Sending Facility
		 * MSH.5 Receiving Application
		 * MSH.6 Receiving Facility
		 * MSH.7 Date/Time of Message
		 * MSH.8 Security
		 * MSH.9 Message Type
		 * MSH.10 Message Control ID
		 * MSH.11 Processing ID
		 * MSH.12 Version ID
		 * MSH.13 Sequence Number
		 * MSH.14 Continuation Pointer
		 * MSH.15 Accept Acknowledgement Type
		 * MSH.16 Application Acknowledgement Type
		 * MSH.17 Country Code = > (Posibles valores - TABLA 0399)
		 * MSH.18 Character Set => (Posibles valores - TABLA 0211)
		 * MSH.19 Principal Language of Message
		 * MSH.20 Alternate Charecter Set Handling Scheme
		 * MSH.21 Message Profile Identifier
		 **/		
		/** Example of Query for Patient Demographics (QRY^A19)
		 * QRD.1 Query Date/Time
		 * QRD.2 Query Format Code (D -> Response is in display format / R -> Response is in record-oriented format)
		 * QRD.3 Query Priority (D -> Deferred / I -> Inmediate)
		 * QRD.4 Query ID
		 * QRD.5 Deferred Response Type (SIN UTILIDAD EN ESTE CASO)
		 * QRD.6 Deferred Response Date / Time (SIN UTILIDAD EN ESTE CASO)
		 * QRD.7 Quantity Limited Request (Cantidad maxima de registros recibidos (en el ejemplo 1 solo valor en formato registro))
		 * QRD.8 Who Subject Filter (En este caso preguntamos por el paciente con el ID especificado aqui)
		 * QRD.9 What Subject Filter => (Posibles valores - TABLA 0048)
		 * QRD.10 What Department Data Code (SIN UTILIDAD EN ESTE CASO)
		 * QRD.11 What Data Code Value Qualifier (SIN UTILIDAD EN ESTE CASO)
		 * QRD.12 Query Result Level (SIN UTILIDAD EN ESTE CASO)
		 **/
		/** NHC = 18 (Paciente de pruebas)
		 **/
		String numId = "";
		String typeStr = "";
		if (pacienteToFind.getNumeroHistoriaClinica() > 0) {
			typeStr = "NHC";
			numId = Integer.toString(pacienteToFind.getNumeroHistoriaClinica());
		}
		else if (pacienteToFind.getNumeroCIPA() > 0) {
			typeStr = "CIPA";
			numId = String.valueOf(pacienteToFind.getNumeroCIPA());
		}
		else if (!pacienteToFind.getNumeroTarjetaSanitaria().isEmpty()) {
			typeStr = "TIS";
			numId = pacienteToFind.getNumeroTarjetaSanitaria();
		}
		else if (pacienteToFind.getDni() != null && !pacienteToFind.getDni().isEmpty()) {
			typeStr = "DNI";
			numId = pacienteToFind.getDni();
		}
		else if (pacienteToFind.getPasaporte() != null && !pacienteToFind.getPasaporte().isEmpty()) {
			typeStr = "PASAPORTE";
			numId = pacienteToFind.getPasaporte();
		}
		else if (pacienteToFind.getNie() != null && !pacienteToFind.getNie().isEmpty()) {
			typeStr = "NIE";
			numId = pacienteToFind.getNie();
		}
//		if (type == 1 && numeroHCSH > 0) {
//			typeStr = "NHC";
//		}
//		else if (type == 2 && numeroHCSH > 0) {
//			typeStr = "CIPA";
//		}
//		else if (type == 3 && numeroHCSH > 0) {
//			typeStr = "SS";
//		}
//		String qryString = "MSH|^~\\&|DELTANET-PSIQ|HCSC|HPHIS|HCSC|20191128140303||QRY^A19^QRY_A19|QRYPAT1000|T|2.5|||||ESP|UNICODE UTF-8\r"
//				+ "QRD|20191128140303|R|I|Q1004|1^RD|||" + Integer.toString(numeroHCSH) + "^^^^" + typeStr + "|DEM|HPHIS";
		
//		String qryString = "MSH|^~\\&|DELTANET-PSIQ|HCSC|HPHIS|HCSC|20191128140303||QRY^A19^QRY_A19|QRYPAT1000|T|2.5|||||ESP|UNICODE UTF-8\r"
//				+ "QRD|20191128140303|R|I|Q1004|||50^RD|2233019^^^^NHC|DEM|HPHIS";
		
		String apellido1 = pacienteToFind.getApellido1();
		String apellido2 = pacienteToFind.getApellido2();//""; // NO FUNCIONA
		String nombre = pacienteToFind.getNombre();
		
		String qryString = "MSH|^~\\&|DELTANET-PSIQ|HCSC|HPHIS|HCSC|20191128140303||QRY^A19^QRY_A19|QRYPAT1000|P|2.5|||||ESP|UNICODE UTF-8\r"
				+ "QRD|20191128140303|R|I|Q1004|||50^RD|" +
				numId + "^" + apellido1 + "^" + nombre + "^" + apellido2 + "^" + typeStr + "|DEM|HPHIS";
		
		
		HapiContext context = new DefaultHapiContext();
		
		MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
		mllp.setCharset("ISO-8859-1");
		context.setLowerLayerProtocol(mllp);
		//System.setProperty(MllpConstants.CHARSET_KEY, "UTF-8");
		
		Parser parser = context.getPipeParser();
		
		Connection connection = null;
		
		ArrayList<Paciente> pacientes = new ArrayList<Paciente>();
		
		try {
			Message qryMessage = parser.parse(qryString);
			
			if (connection == null) {
				connection = context.newClient(this.domain, this.port, this.useTLS);
			}
			Initiator initiator = connection.getInitiator();
//			initiator.setTimeout(30, TimeUnit.SECONDS);
			
			/** Send QRY^A19 message for Patient Demographics and wait for response.
			 **/
			Message responseMessage = initiator.sendAndReceive(qryMessage);
			//responseMessage.generateACK();
			
			pacientes = getPatientsResultFromQRYA19(parser.encode(responseMessage));
		}
		catch (LLPException e) {
			Logger.getLogger(ClientHL7.class).error("LLPException: StackTrace: ", e);
		}
		catch (IOException e) {
			Logger.getLogger(ClientHL7.class).error("IOException: StackTrace: ", e);
		}
		catch (HL7Exception e) {
			Logger.getLogger(ClientHL7.class).error("HL7Exception: StackTrace: ", e);
		}
		finally {
			if (connection != null && connection.isOpen()) {
				connection.close();
				connection = null;
			}
			try {
				context.close();
				context = null;
			}
			catch (IOException e) {
				Logger.getLogger(ClientHL7.class).error("IOExceptio: StackTrace: ", e);
			}
		}
		
		return pacientes;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 * @throws ParseException
	 */
	private ArrayList<Paciente> getPatientsResultFromQRYA19(String msg) {
//		HSCLogger.logger().info("\r\nADR_A19 RESPONSE: " + msg + "\r\n");
//		Logger.getLogger(ClientHL7.class).info("ADR_A19 RESPONSE:"  + msg);
		ArrayList<Paciente> pacientes = new ArrayList<Paciente>();
		
		Pattern patternFieldSeparator    = Pattern.compile("\\|*?\\r|\\|");//Pattern.compile("\\|");
		Pattern patternSubFieldSeparator = Pattern.compile("\\^");
		Pattern repetitionsSeparator     = Pattern.compile("\\~");

		
		String[] pids = msg.split("PID");
		
		String checkIfResults = patternFieldSeparator.split(msg)[15];
		//HSCLogger.logger().info(checkIfResults);
		if (checkIfResults.equalsIgnoreCase("NO HAY PARAMETROS DE BUSQUEDA")) {
			//HSCLogger.logger().info("MESSAGE RECEIVED: " + msg);
			return pacientes;
		}
		
		try {
			for (int i = 1; i < pids.length; i++) {
				String item = pids[i];
				
				String pid_5 = patternFieldSeparator.split(item)[5];
				String nombre = patternSubFieldSeparator.split(pid_5)[1];
				String apellido1 = patternSubFieldSeparator.split(pid_5)[0];
				String apellido2 = patternFieldSeparator.split(item)[6];
				String pid_4 = patternFieldSeparator.split(item)[4];			
//				Logger.getLogger(ClientHL7.class).info("PID_4 SPLITTER:"  + pid_4);
				String dni = "";
				String pasaporte = "";
				String nie = "";
				
				try {
					if (!pid_4.isEmpty()) {
						if (patternSubFieldSeparator.split(pid_4)[3].equalsIgnoreCase("DNI")) {
							dni = patternSubFieldSeparator.split(pid_4)[0];
						}
						else if (patternSubFieldSeparator.split(pid_4)[3].equalsIgnoreCase("PASAPORTE")) {
							pasaporte = patternSubFieldSeparator.split(pid_4)[0].replace("~", "");
						}
						else if (patternSubFieldSeparator.split(pid_4)[3].equalsIgnoreCase("NIE")) {
							nie = patternSubFieldSeparator.split(pid_4)[0].replace("~", "");
						}
					}
				}
				catch(IndexOutOfBoundsException e) {
					Logger.getLogger(ClientHL7.class).info("IndexOutOfBoundException: StackTrace: ", e);
					dni = "";
					pasaporte = "";
					nie = "";
				}
				short sexo = patternFieldSeparator.split(item)[8].equalsIgnoreCase("F") ? (short)2 : (short)1;
				String pid_11 = patternFieldSeparator.split(item)[11];
				String direccion = patternSubFieldSeparator.split(pid_11)[0];
				String poblacion = patternSubFieldSeparator.split(pid_11)[2];
				String codPostal = patternSubFieldSeparator.split(pid_11)[4];
				String telefono1 = patternFieldSeparator.split(item)[13];
				String telefono2 = patternFieldSeparator.split(item)[14];
				String fecnac = patternFieldSeparator.split(item)[7];
				String pid_3 = patternFieldSeparator.split(item)[3];
				String[] pid_3_reps = repetitionsSeparator.split(pid_3);
				
				String numhc = "";
				String cipa = "";
				String numss = "";
				String numts = "";
				for (String repItem : pid_3_reps) {
					if (patternSubFieldSeparator.split(repItem)[3].equalsIgnoreCase("HCSC")) {
						numhc = patternSubFieldSeparator.split(repItem)[0];
					}
					else if (patternSubFieldSeparator.split(repItem)[3].equalsIgnoreCase("CIPA")) {
						cipa = patternSubFieldSeparator.split(repItem)[0];
					}
					else if (patternSubFieldSeparator.split(repItem)[3].equalsIgnoreCase("SS")) {
						numss = patternSubFieldSeparator.split(repItem)[0];
					}
					else if (patternSubFieldSeparator.split(repItem)[3].equalsIgnoreCase("TIS")) {
						numts = patternSubFieldSeparator.split(repItem)[0];
					}
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				String[] NK1 = item.split("NK1");
				String nk1_2 = patternFieldSeparator.split(NK1[1])[2].replaceAll("\\r", "^");
				String familiar = patternSubFieldSeparator.split(nk1_2)[1] + " " + patternSubFieldSeparator.split(nk1_2)[0];
				String tlfFamiliar = "";
				try {
					tlfFamiliar = patternFieldSeparator.split(NK1[1])[5].split("\r")[0];
				}
				catch(IndexOutOfBoundsException e) {
					Logger.getLogger(ClientHL7.class).info("IndexOutOfBoundException: StackTrace: ", e);
					tlfFamiliar = "";
				}
				
				Paciente paciente = new Paciente();
				paciente.setApellido1(apellido1);
				paciente.setApellido2(apellido2);
				paciente.setNombre(nombre);
				paciente.setDni(dni);
				paciente.setPasaporte(pasaporte);
				paciente.setNie(nie);
				paciente.setSexo(sexo);
				try {
					if (!fecnac.isEmpty() && !fecnac.equals("")) {
						paciente.setFechaNacimiento(new java.sql.Date(sdf.parse(fecnac).getTime()));
					}
				}
				catch(ParseException ex) {
					Logger.getLogger(ClientHL7.class).info("ParseException: StackTrace", ex);
					ex.printStackTrace();
				}
				paciente.setDireccion(direccion);
				paciente.setPoblacion(poblacion);
				paciente.setCodigoPostal((codPostal.isEmpty() || codPostal.equalsIgnoreCase("null")) ? -1 : Integer.parseInt(codPostal));
				paciente.setTelefono1(telefono1);
				paciente.setTelefono2(telefono2);
				paciente.setFamiliar(familiar);
				paciente.setTelefonoFamiliar(tlfFamiliar);
				paciente.setNumeroHistoriaClinica(numhc.isEmpty() ? -1 : Integer.parseInt(numhc));
				paciente.setNumeroCIPA((cipa == null || cipa.isEmpty()) ? -1 : Integer.parseInt(cipa));
				paciente.setNumeroSeguridadSocial(numss);
				paciente.setNumeroTarjetaSanitaria(numts);
				
				//HSCLogger.logger().info(paciente.toJson().toString());
				
				pacientes.add(paciente);
			}		
		}
		catch(Exception ex) {
			Logger.getLogger(ClientHL7.class).info("Exception: StackTrace: ", ex);
			ex.printStackTrace();
		}
		
		return pacientes;
	}
	
	
	/**
	 * 
	 * @param pdfFilename
	 * @param data
	 * @param codigoFacultativo
	 * @param apellidosFacultativo
	 * @param nombreFacultativo
	 */
	public void sendMDM_T02(ByteArrayOutputStream pdfStream, JsonObject jsonData, int codigoFacultativo,
								String apellidosFacultativo, String nombreFacultativo) {
		
		HapiContext context   = null;
		Connection connection = null;
		
		Logger.getLogger(ClientHL7.class).info("Preparando informe para enviar vía HL7...");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentDate   = sdf.format(new java.util.Date());
		
		Logger.getLogger(ClientHL7.class).info("Formateando fechas para el mensaje MDM_T02 HL7...");
		SimpleDateFormat sdfFechaNac = new SimpleDateFormat("yyyyMMdd");
		
		String fecnacStr       = jsonData.get("fecnac").getAsString();
		String fechaNacimiento = "";
		String fechaInformeStr = jsonData.get("fecha").getAsString();
		String fechaInforme    = "";
		try {
			fechaNacimiento = sdfFechaNac.format(new SimpleDateFormat("dd/MM/yyyy").parse(fecnacStr));
			fechaInforme    = sdfFechaNac.format(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInformeStr));
		}
		catch (ParseException e1) {
			Logger.getLogger(ClientHL7.class).info("ParseException: StackTrace: ", e1);
		}
		Logger.getLogger(ClientHL7.class).info("Fechas formateadas para el mensaje MDM_T02 HL7...");
		
		String base64Pdf = "";
		try {			
			base64Pdf = Base64.encode(pdfStream.toByteArray(), 0);
			
			long numICU = -1;
			if (jsonData.has("numICU") && !jsonData.get("numICU").isJsonNull()) {
				Logger.getLogger(ClientHL7.class).info(jsonData.toString());
				try {
					String ICUString = jsonData.get("numICU").getAsString();//, "0");
					if (ICUString.isEmpty()) ICUString = "-1";
					try {
						numICU = Long.parseLong(ICUString);
					}
					catch(NumberFormatException nfEx) {
						Logger.getLogger(ClientHL7.class).info("NumberFormatException: StackTrace: ", nfEx);
					}
				}
				catch(UnsupportedOperationException uoEx) {
					Logger.getLogger(ClientHL7.class).info("UnsupportedOperationException: StackTrace: ", uoEx);
				}
			}
			String numICUString = (numICU == -1) ? "" : Long.toString(numICU);
			String prestacion = "";
			if (jsonData.get("prestacion") != null) {
				prestacion = jsonData.get("prestacion").getAsString();
				if (prestacion.isEmpty()) prestacion = "HCSC";
			}
			
			String citaStr = "-1";
			if (jsonData.has("numeroCita") && !jsonData.get("numeroCita").isJsonNull()) {
				citaStr = jsonData.get("numeroCita").getAsString();
				if (citaStr.isEmpty() || citaStr.equals("0")) citaStr = "";//"-1";
			}
			
			/** |T|2.5 Indica 'Training' tal vez por eso el HPHIS este ignorando los envios de informes **/
			String msgString = "MSH|^~\\&|DELTANET-PSIQ|HCSC|REPOSITORIO INFORMES|HCSC|" + currentDate
					+ "||MDM^T02^MDM_T02|REPORT" + numICU + "|P|2.5\r"
					+ "EVN|T02|" + currentDate + "\r"
					+ "PID|1||" + jsonData.get("numerohc").getAsString() + "^^^HCSC^PI~" + jsonData.get("numerocipa").getAsString()
					+ "^^^CIPA^PI~" + jsonData.get("numeross").getAsString() + "^^^SS^SS|"
					+ jsonData.get("dni").getAsString() + "^^^DNI^NI|" + jsonData.get("apellido1").getAsString() + " "
					+ jsonData.get("apellido2").getAsString() + "^" + jsonData.get("nombre").getAsString() + "^^^^||"
					+ fechaNacimiento + "|" + jsonData.get("sexo").getAsString() + "|||"
					+ jsonData.get("domicilio").getAsString() + "^^" + jsonData.get("poblacion").getAsString() + "^^"
					+ jsonData.get("codpostal").getAsString() + "^^^^||" + jsonData.get("numtlf1").getAsString() + "|"
					+ jsonData.get("numtlf2").getAsString() + "|\r"
					+ "PV1|1|O|PSQG^^^^^^^^^HOSPITAL CLINICO SAN CARLOS|||^^|" + codigoFacultativo + "^"
					+ apellidosFacultativo + "^" + nombreFacultativo
					+ "||||||||||||" + /*numICU*/numICUString + "|||||||||||||||||||||||||"
					+ fechaInforme + "||||||" + citaStr/*jsonData.get("numeroCita").getAsInt()*/ + "^^^NCITA^VN|||\r"
					+ "TXA|1|INFORME " + prestacion + "|PDF||||||||||||||AU\r"
					+ "OBX|1|ED|^^^^||^^PDF^BASE64^" + base64Pdf;
						
//			Logger.getLogger(ClientHL7.class).info("Mensaje HL7 para enviar: ");
//			Logger.getLogger(ClientHL7.class).info(msgString);
			
			int count    = 0;
			int maxTries = 5;
			while(count < maxTries) {
				try {
					Parser parser   = null;
					Message reportMessage = null;
					if (context == null) {
						context = new DefaultHapiContext();
						
						MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
						mllp.setCharset("ISO-8859-1");
						context.setLowerLayerProtocol(mllp);
						
						parser        = context.getPipeParser();
						reportMessage = parser.parse(msgString);
					}
					if (connection == null) {
						connection = context.newClient(this.domain, this.port, this.useTLS);
					}
					
					Initiator initiator = connection.getInitiator();
//					initiator.setTimeout(30, TimeUnit.SECONDS);
		
					Logger.getLogger(ClientHL7.class)
						.warn("ENVIANDO INFORME HL7 (usuario: " + codigoFacultativo + ")... INTENTO " + count);
					Message response = initiator.sendAndReceive(reportMessage);
					count = maxTries;
					//response.generateACK();
					Logger.getLogger(ClientHL7.class).warn("ENVIO INFORME HL7 ACK:\n " + parser.encode(response));
				}
				catch(HL7Exception e) {
					Logger.getLogger(ClientHL7.class).error("MDM_T02 TIMEOUT!!\n ", e);
					if (++count >= maxTries) throw e;
					if (connection != null) {
						connection.close();
						connection = null;
					}
					if (context != null) {
						context.close();
						context = null;
					}
				}
			}
		}
		catch(IOException e) {
			Logger.getLogger(ClientHL7.class).error("IOException: StackTrace: ", e);
		}
		catch(HL7Exception e) {
			Logger.getLogger(ClientHL7.class).error("HL7Exception: StackTrace: ", e);
		}
		catch(LLPException e) {
			Logger.getLogger(ClientHL7.class).error("LLPException: StackTrace: ", e);
		}
		catch(RuntimeException e) {
			Logger.getLogger(ClientHL7.class).error("RuntimeException: StackTrace: ", e);			
		}
		catch(Exception e) {
			Logger.getLogger(ClientHL7.class).error("Exception: StackTrace: ", e);			
		}
		finally {
			if (connection != null && connection.isOpen())
				Logger.getLogger(ClientHL7.class).warn("Closing ClientHL7 connection...");
				connection.close();
				connection = null;
				Logger.getLogger(ClientHL7.class).warn("ClientHL7 connection closed!");
			try {
				Logger.getLogger(ClientHL7.class).warn("Closing HAPI context...");
				context.close();
				context = null;
				Logger.getLogger(ClientHL7.class).warn("HAPI context closed!");
			}
			catch (IOException e) {
				Logger.getLogger(ClientHL7.class).warn("IOException: StackTrace: ", e);
			}
		}
	}
	
}


/** TABLA 0104 - Version ID
 * 2.0		Release 2.0		September 1988
 * 2.0D		Demo 2.0		October 1988
 * 2.1		Release 2. 1	March 1990
 * 2.2		Release 2.2		December 1994
 * 2.3		Release 2.3		March 1997
 * 2.3.1	Release 2.3.1	May 1999
 * 2.4		Release 2.4		November 2000
 * 2.5		Release 2.5
**/


/** TABLA 0103 - Processing ID
 * D	Debugging
 * P	Production
 * T	Training
 **/

/** TABLA 0207 - Processing Mode
 * A			Archive
 * I			Initial load
 * Not present	Not present (the default, meaning current processing)
 * R			Restore from archive
 * T			Current processing, transmitted at intervals (scheduled or on demand)
 **/


/** TABLA 0211 - Alternate Character Sets
 * 8859/1 -> The printable characters from the ISO 8859/1 Character set
 * 8859/2 -> The printable characters from the ISO 8859/2 Character set
 * 8859/3 -> The printable characters from the ISO 8859/3 Character set
 * 8859/4 -> The printable characters from the ISO 8859/4 Character set
 * 8859/5 -> The printable characters from the ISO 8859/5 Character set
 * 8859/6 -> The printable characters from the ISO 8859/6 Character set
 * 8859/7 -> The printable characters from the ISO 8859/7 Character set
 * 8859/8 -> The printable characters from the ISO 8859/8 Character set
 * 8859/9 -> The printable characters from the ISO 8859/9 Character set
 * ASCII  -> The printable 7-bit ASCII character set. (This is the default if this field is omitted)
 * BIG-5  -> Code for Taiwanese Character Set (BIG-5) Does not need an escape sequence. BIG-5 does not need an escape sequence.
 * 		ASCII is a 7 bit character set, which means that the top bit of the byte is "0". The parser knows that when the top bit of the
 * 		byte is "0", the character set is ASCII. When it is "1", the following bytes should be handled as 2 bytes (or more). No escape
 * 		technique is needed. However, since some servers do not correctly interpret when they receive a top bit "1", it is advised, in
 * 		internet RFC, to not use these kind of non-safe non-escape extension.
 * CNS 11643-1992 -> Code for Taiwanese Character Set (CNS 11643-1992)	Does not need an escape sequence.
 * GB 18030-2000 ->Code for Chinese Character Set (GB 18030-2000)	Does not need an escape sequence.
 * ISO IR14 -> Code for Information Exchange (one byte)(JIS X 0201-1976).	Note that the code contains a space, i.e. "ISO IR14".
 * ISO IR159 -> Code of the supplementary Japanese Graphic Character set for information interchange (JIS X 0212-1990).
 * 		Note that the code contains a space, i.e. "ISO IR159".
 * ISO IR87 -> Code for the Japanese Graphic Character set for information interchange (JIS X 0208-1990), Note that the code contains a space,
 * 		i.e. "ISO IR87". The JIS X 0208 needs an escape sequence. In Japan, the escape technique is ISO 2022. From basic ASCII, escape sequence
 * 		"escape" $ B (in HEX, 1B 24 42) lets the parser know that following bytes should be handled 2-byte wise. Back to ASCII is 1B 28 42.
 * KS X 1001 -> Code for Korean Character Set (KS X 1001)
 * UNICODE -> The world wide character standard from ISO/IEC 10646-1-1993[6]. Deprecated. Retained for backward compatibility only as v 2.5.
 * 		Replaced by specific Unicode encoding codes.
 * UNICODE UTF-16 -> UCS Transformation Format, 16-bit form. UTF-16 is identical to ISO/IEC 10646 UCS-2. Note that the code contains a space
 * 		before UTF but not before and after the hyphen.
 * UNICODE UTF-32 -> UCS Transformation Format, 32-bit form. UTF-32 is defined by Unicode Technical Report #19, and is an officially recognized
 * 		encoding as of Unicode Version 3.1. UTF-32 is a proper subset of ISO/IEC 10646 UCS-4. Note that the code contains a space before UTF
 * 		but not before and after the hyphen.
 * UNICODE UTF-8 -> UCS Transformation Format, 8-bit form	UTF-8 is a variable-length encoding, each code value is represented by 1,2 or 3 bytes,
 * 		depending on the code value. 7 bit ASCII is a proper subset of UTF-8. Note that the code contains a space before UTF but not before
 * 		and after the hyphen.
 **/


/** TABLA 0048 - What Subject Filter
 * ADV -> Advice/diagnosis
 * ANU -> Nursing unit lookup (return patients in beds, excluding empty beds)
 * APA -> Account number query, return matching visit
 * APM -> Medical record number query, return visits for a medical record number
 * APN -> Patient name lookup
 * APP -> Physical lookup
 * ARN -> Nursing unit lookup (return patient in beds, including empty beds)
 * CAN -> Cancel (used to cancel query)
 *** DEM -> DEMOGRAPHICS
 * FIN -> Financial
 * GID -> Generate new identifier
 * GOL -> Goals
 * MFQ -> Master file query
 * MRI -> Most recent inpatient
 * MRO -> Most recent outpatient
 * NCK -> Network clock
 * NSC -> Network status change
 * NST -> Network statics
 * ORD -> Order
 * OTH -> Other
 * PRB -> Problems
 * PRO -> Procedure
 * RAR -> Pharmacy administration information
 * RDR -> Pharmacy dispense information
 * RER -> Pharmacy encoded order information
 * RES -> Result
 * RGR -> Pharmacy give information
 * ROR -> Pharmacy prescription information
 *** SAL -> All schedule relate information, including open slots, booked slots, blocked slots
 * SBK -> Booked slots on the identified schedule
 * SBL -> Blocked slots on the identified schedule
 * SOF -> First open slot on the identified shcedule after the star date/time
 * SOP -> Open slots on the identified schedule between the begin and end of the start date/time range
 * SSA -> Time slots available for a single appointment
 * SSR -> Time slots available for a recurring appointment
 * STA -> Status
 * VXI -> Vaccine information
 * XID -> Get cross-referenced identifiers
**/


/** TABLA 0399 - Country Code
 * ABW	Aruba
 * AFG	Afghanistan
 * AGO	Angola
 * AIA	Anguilla
 * ALA	Åland Islands
 * ALB	Albania
 * AND	Andorra
 * ARE	United Arab Emirates
 * ARG	Argentina
 * ARM	Armenia
 * ASM	American Samoa
 * ATA	Antarctica
 * ATF	French Southern Territories
 * ATG	Antigua and Barbuda
 * AUS	Australia
 * AUT	Austria
 * AZE	Azerbaijan
 * BDI	Burundi
 * BEL	Belgium
 * BEN	Benin
 * BES	Bonaire, Saint Eustatius and Saba
 * BFA	Burkina Faso
 * BGD	Bangladesh
 * BGR	Bulgaria
 * BHR	Bahrain
 * BHS	Bahamas
 * BIH	Bosnia and Herzegovina
 * BLM	Saint Barthélemy
 * BLR	Belarus
 * BLZ	Belize
 * BMU	Bermuda
 * BOL	Bolivia, Plurinational State of
 * BRA	Brazil
 * BRB	Barbados
 * BRN	Brunei Darussalam
 * BTN	Bhutan
 * BVT	Bouvet Island
 * BWA	Botswana
 * CAF	Central African Republic
 * CAN	Canada
 * CCK	Cocos (Keeling) Islands
 * CHE	Switzerland
 * CHL	Chile
 * CHN	China
 * CIV	Côte d'Ivoire
 * CMR	Cameroon
 * COD	Congo, the Democratic Republic of the
 * COG	Congo
 * COK	Cook Islands
 * COL	Colombia
 * COM	Comoros
 * CPV	Cape Verde
 * CRI	Costa Rica
 * CUB	Cuba
 * CUW	Curaçao
 * CXR	Christmas Island
 * CYM	Cayman Islands
 * CYP	Cyprus
 * CZE	Czech Republic
 * DEU	Germany
 * DJI	Djibouti
 * DMA	Dominica
 * DNK	Denmark
 * DOM	Dominican Republic
 * DZA	Algeria
 * ECU	Ecuador
 * EGY	Egypt
 * ERI	Eritrea
 * ESH	Western Sahara
 * ESP	Spain
 * EST	Estonia
 * ETH	Ethiopia
 * FIN	Finland
 * FJI	Fiji
 * FLK	Falkland Islands (Malvinas)
 * FRA	France
 * FRO	Faroe Islands
 * FSM	Micronesia, Federated States of
 * GAB	Gabon
 * GBR	United Kingdom
 * GEO	Georgia
 * GGY	Guernsey
 * GHA	Ghana
 * GIB	Gibraltar
 * GIN	Guinea
 * GLP	Guadeloupe
 * GMB	Gambia
 * GNB	Guinea-Bissau
 * GNQ	Equatorial Guinea
 * GRC	Greece
 * GRD	Grenada
 * GRL	Greenland
 * GTM	Guatemala
 * GUF	French Guiana
 * GUM	Guam
 * GUY	Guyana
 * HKG	Hong Kong
 * HMD	Heard Island and McDonald Islands
 * HND	Honduras
 * HRV	Croatia
 * HTI	Haiti
 * HUN	Hungary
 * IDN	Indonesia
 * IMN	Isle of Man
 * IND	India
 * IOT	British Indian Ocean Territory
 * IRL	Ireland
 * IRN	Iran, Islamic Republic of
 * IRQ	Iraq
 * ISL	Iceland
 * ISR	Israel
 * ITA	Italy
 * JAM	Jamaica
 * JEY	Jersey
 * JOR	Jordan
 * JPN	Japan
 * KAZ	Kazakhstan
 * KEN	Kenya
 * KGZ	Kyrgyzstan
 * KHM	Cambodia
 * KIR	Kiribati
 * KNA	Saint Kitts and Nevis
 * KOR	Korea, Republic of
 * KWT	Kuwait
 * LAO	Lao People's Democratic Republic
 * LBN	Lebanon
 * LBR	Liberia
 * LBY	Libyan Arab Jamahiriya
 * LCA	Saint Lucia
 * LIE	Liechtenstein
 * LKA	Sri Lanka
 * LSO	Lesotho
 * LTU	Lithuania
 * LUX	Luxembourg
 * LVA	Latvia
 * MAC	Macao
 * MAF	Saint Martin (French part)
 * MAR	Morocco
 * MCO	Monaco
 * MDA	Moldova, Republic of
 * MDG	Madagascar
 * MDV	Maldives
 * MEX	Mexico
 * MHL	Marshall Islands
 * MKD	Macedonia, the former Yugoslav Republic of
 * MLI	Mali
 * MLT	Malta
 * MMR	Myanmar
 * MNE	Montenegro
 * MNG	Mongolia
 * MNP	Northern Mariana Islands
 * MOZ	Mozambique
 * MRT	Mauritania
 * MSR	Montserrat
 * MTQ	Martinique
 * MUS	Mauritius
 * MWI	Malawi
 * MYS	Malaysia
 * MYT	Mayotte
 * NAM	Namibia
 * NCL	New Caledonia
 * NER	Niger
 * NFK	Norfolk Island
 * NGA	Nigeria
 * NIC	Nicaragua
 * NIU	Niue
 * NLD	Netherlands
 * NOR	Norway
 * NPL	Nepal
 * NRU	Nauru
 * NZL	New Zealand
 * OMN	Oman
 * PAK	Pakistan
 * PAN	Panama
 * PCN	Pitcairn
 * PER	Peru
 * PHL	Philippines
 * PLW	Palau
 * PNG	Papua New Guinea
 * POL	Poland
 * PRI	Puerto Rico
 * PRK	Korea, Democratic People's Republic of
 * PRT	Portugal
 * PRY	Paraguay
 * PSE	Palestinian Territory, Occupied
 * PYF	French Polynesia
 * QAT	Qatar
 * REU	Réunion
 * ROU	Romania
 * RUS	Russian Federation
 * RWA	Rwanda
 * SAU	Saudi Arabia
 * SDN	Sudan
 * SEN	Senegal
 * SGP	Singapore
 * SGS	South Georgia and the South Sandwich Islands
 * SHN	Saint Helena, Ascension and Tristan da Cunha
 * SJM	Svalbard and Jan Mayen
 * SLB	Solomon Islands
 * SLE	Sierra Leone
 * SLV	El Salvador
 * SMR	San Marino
 * SOM	Somalia
 * SPM	Saint Pierre and Miquelon
 * SRB	Serbia
 * STP	Sao Tome and Principe
 * SUR	Suriname
 * SVK	Slovakia
 * SVN	Slovenia
 * SWE	Sweden
 * SWZ	Swaziland
 * SXM	Sint Maarten (Dutch part)
 * SYC	Seychelles
 * SYR	Syrian Arab Republic
 * TCA	Turks and Caicos Islands
 * TCD	Chad
 * TGO	Togo
 * THA	Thailand
 * TJK	Tajikistan
 * TKL	Tokelau
 * TKM	Turkmenistan
 * TLS	Timor-Leste
 * TON	Tonga
 * TTO	Trinidad and Tobago
 * TUN	Tunisia
 * TUR	Turkey
 * TUV	Tuvalu
 * TWN	Taiwan, Province of China
 * TZA	Tanzania, United Republic of
 * UGA	Uganda
 * UKR	Ukraine
 * UMI	United States Minor Outlying Islands
 * URY	Uruguay
 * USA	United States
 * UZB	Uzbekistan
 * VAT	Holy See (Vatican City State)
 * VCT	Saint Vincent and the Grenadines
 * VEN	Venezuela, Bolivarian Republic of
 * VGB	Virgin Islands, British
 * VIR	Virgin Islands, U.S.
 * VNM	Viet Nam
 * VUT	Vanuatu
 * WLF	Wallis and Futuna
 * WSM	Samoa
 * YEM	Yemen
 * ZAF	South Africa
 * ZMB	Zambia
 * ZWE	Zimbabwe
 **/

/** Exmaple of Notification of opened ("un-blocked") schedule time slot(s)
 * FILLER: Persona o servicio que produce las observaciones (llena el pedido) solicitado por el solicitante. La palabra es
 *  sinónimo con "productor" e incluye servicios de diagnóstico y clínica servicios y proveedores de atención que informan
 *  observaciones sobre sus pacientes El laboratorio clínico es un productor de resultados de pruebas de laboratorio.
 *  (relleno de una orden de laboratorio), el servicio de enfermería es el productor de vital observaciones de signos
 *  (el relleno de órdenes para medir signos vitales), etc. La aplicación responde a, es decir, realiza, un solicitud de
 *  servicios (pedidos) o producción de una observación. Otra definición para el relleno es que el relleno también puede
 *  originarse solicitudes de servicios (nuevos pedidos), agregue servicios adicionales a pedidos existentes, reemplazar
 *  pedidos existentes, poner un pedido en espera, descontinuar un pedido, liberar un pedido retenido o cancelar el existente
 *  pedidos. Referido como Productor en la terminología de ASTM.
 *  
 * PLACER: Persona o servicio que solicita (hace un pedido) una observación batería, por ejemplo, el médico, el consultorio,
 *  la clínica o el servicio de sala, que ordena una prueba de laboratorio, rayos X, signos vitales, etc. El significado es
 *  sinónimo y utilizado de manera intercambiable con el solicitante. Placer también se considera la aplicación (sistema o individual) 
 *  originando una solicitud de servicios (pedido).
 *  
 * SCH.1 Placer Appointment ID
 * SCH.2 Filler Appointment ID
 * SCH.3 Occurrence Number
 * SCH.4 Place Group Number
 * SCH.5 Schedule ID
 * SCH.6 Event Reason
 * SCH.7 Appointment Reason
 * SCH.8 Appointment Type
 * SCH.9 Appointment Duration
 * SCH.10 Appointment Duration Units
 * SCH.11 Appointment Timing Quantity
 * SCH.12 Placer Contact Person
 * SCH.13 Placer Contact Phone Number
 * SCH.14 Placer Contact Address
 * SCH.15 Placer Contact Location
 * SCH.16 Filler Contact Person
 * SCH.17 Filler Contact Phone Number
 * SCH.18 Filler Contact Address
 * SCH.19 Filler Contact Location
 * SCH.20 Entered By Person
 * SCH.21 Entered By Phone Number
 * SCH.22 Entered By Location
 * SCH.23 Parent Placer Appointment ID
 * SCH.24 Parent Filler Appointment ID
 * SCH.25 Filler Status Code
 **/
