package org.hcsc.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.Cita;
//import org.hcsc.hl7.ClientHL7;
//import org.hcsc.models.Paciente;

import com.google.gson.JsonObject;


public class DAOCitas {
//	private static final String TABLA_CITAS = "CitasNuevo";	// DESARROLLO
	private static final String TABLA_CITAS = "CitasTest";	// PRODUCCION
	private static final String TABLA_USUARIOS = "usuarios_produccion";
	
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	// CONSTRUCTOR //
	public DAOCitas(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * 
	 * @param Ninguno
	 * @return Cierra los recursos abiertos por el Dao (PreparedStatement) 
	 * @throws SQLException
	 */
	private void closeResources() throws HSCException {
		try {
			stmt.close();
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOCitas.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException (DAOCitas): Error cerrando PreparedStatement()", ex.getCause());
		}
	}
	
	public JsonObject obtenerCodigoFacultativo(String userDni) throws HSCException {
		JsonObject result = new JsonObject();
		
		String query = "SELECT num_emp, num_emp_alt, categoria, cias FROM " + TABLA_USUARIOS + " WHERE dni = ?";
		
		String codigoFacultativo = "";
		String categoria         = "";
		String cias              = "";
		String codigoFacultaAlt  = "";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, userDni);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				codigoFacultativo = rs.getString("num_emp");
				categoria         = rs.getString("categoria");
				cias              = rs.getString("cias");
				codigoFacultaAlt  = rs.getString("num_emp_alt");
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOCitas.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerCodigoFacultativo()", ex);
		}
		finally {
			closeResources();
		}
		
		result.addProperty("codigoFacultativo", codigoFacultativo);
		result.addProperty("categoria", categoria);
		result.addProperty("cias", cias);
		result.addProperty("codigoFacultaAlt", codigoFacultaAlt);
		
		return result;
	}
	
	public JsonObject obtenerNombreFacultativoPorDni(String userDni) throws HSCException {
		JsonObject result = null;
		
		String query = "SELECT nombre, apellido1, apellido2, dni, cias"
						+ " FROM " + TABLA_USUARIOS + " WHERE num_emp = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, userDni);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				String nombre = rs.getString("nombre");
				String ape1   = rs.getString("apellido1");
				String ape2   = rs.getString("apellido2");
				String dni    = rs.getString("dni");
				String cias   = rs.getString("cias");
				
				result = new JsonObject();
				result.addProperty("apellido1", ape1);
				result.addProperty("apellido2", ape2);
				result.addProperty("nombre", nombre);
				result.addProperty("dni", dni);
				result.addProperty("cias", cias);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerNombreFacultativoPorDni()", ex);
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	
	
	public ArrayList<Cita> obtenerPorFacultativoDia(int codigoFacultativo, int idFacultaAlt, Date fecha) throws HSCException {
		ArrayList<Cita> citas = new ArrayList<Cita>();
		
		String query = "SELECT * FROM " + TABLA_CITAS + " WHERE (CodigoFacultativo = ?"
						+ " OR CodigoFacultativo = ?)"
						+ " AND CAST(FechaInicioCita AS DATE) = CAST(? AS DATE)";// ORDER BY FechaInicioCita";

		idFacultaAlt = idFacultaAlt == -1 ? codigoFacultativo : idFacultaAlt;
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt      (1, codigoFacultativo);
			stmt.setInt      (2, idFacultaAlt);
			stmt.setTimestamp(3, new Timestamp(fecha.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			
//			ClientHL7 hl7Client = new ClientHL7();
			
			while (rs.next()) {
				Cita cita = new Cita();
				
				cita.setNumeroCita(rs.getInt("NumeroCita"));
				cita.setIdAgenda(rs.getString("IdAgenda"));
				cita.setDescripcionCentro(rs.getString("DescripcionCentro"));
				cita.setDescripcionPrestacion(rs.getString("DescripcionPrestacion"));
				cita.setFechaIniCita(rs.getTimestamp("FechaInicioCita"));
				cita.setFechaFinCita(rs.getTimestamp("FechaFinCita"));
				cita.setNumeroICU(rs.getString("NumICU"));
				cita.setCodigoFacultativo(rs.getInt("CodigoFacultativo"));
				cita.setApellidosFacultativo(rs.getString("ApellidosFacultativo"));
				cita.setNombreFacultativo(rs.getString("NombreFacultativo"));
				cita.setNumeroHC(rs.getInt("NumeroHCSCPaciente"));
				cita.setNumeroCIPA(rs.getString("NumeroCIPAPaciente"));
				cita.setNumeroSS(rs.getString("NumeroSSPaciente"));
				cita.setDNIPaciente(rs.getString("DNIPaciente"));
				cita.setAtendida(rs.getBoolean("Atendida"));
				
				cita.setNombrePaciente(rs.getString("NombrePaciente"));
				cita.setApellido1Paciente(rs.getString("Apellido1Paciente"));
				cita.setApellido2Paciente(rs.getString("Apellido2Paciente"));
				
//				Paciente pacienteABuscar = new Paciente();
//				
//				/** Establece el numero de historia clinica procedente de la Cita
//				 *  para buscar el paciente en el HPHIS
//				 **/
//				pacienteABuscar.setNumeroHistoriaClinica(cita.getNumeroHC());
//				
//				/** Busqueda HL7 de los demograficos del paciente en el HIS **/
//				Paciente paciente = null;
//				ArrayList<Paciente> pacientesEncontrados = hl7Client.sendQRY_A19(pacienteABuscar);
//				if (!pacientesEncontrados.isEmpty()) {
//					paciente = pacientesEncontrados.get(0);
//				}
//				else {
//					Logger.getLogger(DAOCitas.class).info("ATENCION! (DAOCitas): Paciente: " + pacienteABuscar.getNumeroHistoriaClinica() + " no encontrado en el HPHIS");
//					paciente = new Paciente();
//					// Para DEBUG en localhost que no se tiene acceso a HL7
////					paciente.setNombre("JUAN VICENTE");
////					paciente.setApellido1("FERNANDEZ");
////					paciente.setApellido2("RODRIGUEZ");
//				}
//				
//				/** Nos quedamos con los demograficos relativos al nombre para mostrarlos
//				 *  en la lista de citas
//				 **/
//				cita.setApellido1Paciente(paciente.getApellido1());
//				cita.setApellido2Paciente(paciente.getApellido2());
//				cita.setNombrePaciente(paciente.getNombre());
//				cita.setNumeroCIPA(String.valueOf(paciente.getNumeroCIPA()));
				
				citas.add(cita);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOCitas.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorFacultativoDia()", ex);
		}
		finally {
			closeResources();
		}
		
		return citas;
	}
	
	/**
	 * 
	 * @param numeroCita
	 * @return
	 * @throws HSCException
	 **/
	public Cita obtenerPorNumeroCita(int numeroCita) throws HSCException {
		Cita cita = null;
		
		String query = "SELECT * FROM " + TABLA_CITAS + " WHERE NumeroCita = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, numeroCita);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				cita = new Cita();
				
				cita.setNumeroCita(rs.getInt("NumeroCita"));
				cita.setIdAgenda(rs.getString("IdAgenda"));
				cita.setDescripcionCentro(rs.getString("DescripcionCentro"));
				cita.setDescripcionPrestacion(rs.getString("DescripcionPrestacion"));
				cita.setFechaIniCita(rs.getTimestamp("FechaInicioCita"));
				cita.setFechaFinCita(rs.getTimestamp("FechaFinCita"));
				cita.setNumeroICU(rs.getString("NumICU"));
				cita.setCodigoFacultativo(rs.getInt("CodigoFacultativo"));
				cita.setApellidosFacultativo(rs.getString("ApellidosFacultativo"));
				cita.setNombreFacultativo(rs.getString("NombreFacultativo"));
				cita.setNumeroHC(rs.getInt("NumeroHCSCPaciente"));
				cita.setNumeroCIPA(rs.getString("NumeroCIPAPaciente"));
				cita.setNumeroSS(rs.getString("NumeroSSPaciente"));
				cita.setDNIPaciente(rs.getString("DNIPaciente"));
				cita.setAtendida(rs.getBoolean("Atendida"));
				
				cita.setNombrePaciente(rs.getString("NombrePaciente"));
				cita.setApellido1Paciente(rs.getString("Apellido1Paciente"));
				cita.setApellido2Paciente(rs.getString("Apellido2Paciente"));
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOCitas.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorNumeroCita()", ex);
		}
		finally {
			closeResources();
		}
		
		return cita;
	}
	
	/**
	 * 
	 * @param numeroCita
	 * @return
	 * @throws HSCException
	 **/
	public int marcarAtendida(int numeroCita) throws HSCException {
		int opResult = 0;
		
		String query = "UPDATE " + TABLA_CITAS + " SET Atendida = ? WHERE NumeroCita = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, 1);
			stmt.setInt(2, numeroCita);
			
			synchronized (this) {
				opResult = stmt.executeUpdate();				
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOCitas.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: marcarAtendida()", ex);
		}
		finally {
			closeResources();
		}
		
		return opResult;
	}
				
}
