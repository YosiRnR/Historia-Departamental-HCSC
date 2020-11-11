package org.hcsc.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.Actuacion;


public class DAOActuaciones {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAOActuaciones(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * 
	 * @param Ninguno
	 * @return Cierra los recursos abiertos por el Dao (PreparedStatement) 
	 * @throws SQLException
	 **/
	private void closeResources() {
		try {
			stmt.close();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param idPaciente
	 * @return
	 **/
	public int obtenerNumRegistros(int idPaciente) throws HSCException {
		int totalRegistros = -1;
		
		String query = "SELECT COUNT(IdPaciente) AS NumRegistros"
						+ " FROM Actuaciones WHERE IdPaciente = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idPaciente);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				totalRegistros = rs.getInt("NumRegistros");
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerNumRegistros()", ex);
		}
		finally {
			closeResources();
		}
		
		return totalRegistros;
	}
	
	/**
	 * 
	 * @param idPaciente
	 * @return
	 **/
	public ArrayList<String> obtenerTodasLasFechas(int idPaciente) throws HSCException {
		ArrayList<String> allDates = new ArrayList<String>();
		
		String query = "SELECT Fecha FROM Actuaciones"
					+ " WHERE IdPaciente = ? ORDER BY Fecha DESC, NumRegistro DESC";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idPaciente);
			
			ResultSet rs = stmt.executeQuery();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			while(rs.next()) {
				Date date = rs.getDate("Fecha");
				
				allDates.add(sdf.format(date));
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodasLasFechas()", ex);
		}
		finally {
			closeResources();
		}
		
		return allDates;
	}
	
	/**
	 * 
	 * @param idPaciente
	 * @return
	 */
	public Actuacion obtenerUltimaPorIdPaciente(int idPaciente) throws HSCException {
		Actuacion actuacion = null;
		
		String query = "SELECT TOP 1 * FROM Actuaciones"
					+ " WHERE IdPaciente = ? ORDER BY Fecha DESC, NumRegistro DESC";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idPaciente);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				actuacion = new Actuacion();
				actuacion.setIdActuacion(rs.getInt("IdActuacion"));
				actuacion.setIdPaciente(rs.getInt("IdPaciente"));
				actuacion.setNumRegistro(rs.getInt("NumRegistro"));
				actuacion.setFecha(rs.getDate("Fecha"));
				actuacion.setIncapacidadTotal(rs.getInt("IncapacidadTotal") > 0 ? true : false);
				actuacion.setCuratelaSalud(rs.getInt("CuratelaSalud") > 0 ? true : false);
				actuacion.setCuratelaEconomica(rs.getInt("CuratelaEconomica") > 0 ? true : false);
				actuacion.setProgramaContinuidadCuidados(rs.getInt("ProgramaContinuidadCuidados") > 0 ? true : false);
				actuacion.setProgramaJoven(rs.getInt("ProgramaJoven") > 0 ? true : false);
				actuacion.setFechaInicioProgramaJoven(rs.getDate("FechaInicioProJoven"));
				actuacion.setFechaFinProgramaJoven(rs.getDate("FechaFinProJoven"));
				
				actuacion.setCodigoAgenda(rs.getString("CodigoAgenda"));
				actuacion.setLugarAtencion(rs.getString("LugarAtencion"));
				actuacion.setIdProfesional(rs.getString("IdProfesional"));
				actuacion.setTipoPrestacion(rs.getString("TipoPrestacion"));
				
				actuacion.setEquipoDeCalle(rs.getInt("EquipoDeCalle") > 0 ? true : false);
				
				actuacion.setFechaAlta(rs.getDate("FechaAlta"));
				actuacion.setMotivoAlta(rs.getInt("MotivoAlta"));
				
				actuacion.setNumeroCita(rs.getInt("NumeroCita"));
				actuacion.setNumeroICU(rs.getString("NumICU"));
				
				actuacion.setFechaAltaEquipoCalle(rs.getDate("FechaAltaEquipoCalle"));
				
				actuacion.setMedidaProteccion(rs.getInt("MedidaProteccion") > 0 ? true : false);
				actuacion.setResidencia(rs.getInt("Residencia") > 0 ? true : false);				
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerUltimaPorIdPaciente()", ex);
		}
		finally {
			closeResources();
		}
		
		return actuacion;
	}

	public Actuacion obtenerPorIdPacienteNumRegistro(int idPaciente, int numRegistro) throws HSCException {
		Actuacion actuacion = null;
		
		String query = "SELECT * FROM ( SELECT *, ROW_NUMBER() OVER (ORDER BY Fecha ASC,"
					+ " NumRegistro ASC) AS row FROM Actuaciones WHERE idPaciente = ? )"
					+ " a WHERE row = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idPaciente);
			stmt.setInt(2, numRegistro);

			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				actuacion = new Actuacion();
				actuacion.setIdActuacion(rs.getInt("IdActuacion"));
				actuacion.setIdPaciente(rs.getInt("IdPaciente"));
				actuacion.setNumRegistro(rs.getInt("NumRegistro"));
				actuacion.setFecha(rs.getDate("Fecha"));
				actuacion.setIncapacidadTotal(rs.getInt("IncapacidadTotal") > 0 ? true : false);
				actuacion.setCuratelaSalud(rs.getInt("CuratelaSalud") > 0 ? true : false);
				actuacion.setCuratelaEconomica(rs.getInt("CuratelaEconomica") > 0 ? true : false);
				actuacion.setProgramaContinuidadCuidados(rs.getInt("ProgramaContinuidadCuidados") > 0 ? true : false);
				actuacion.setProgramaJoven(rs.getInt("ProgramaJoven") > 0 ? true : false);
				actuacion.setFechaInicioProgramaJoven(rs.getDate("FechaInicioProJoven"));
				actuacion.setFechaFinProgramaJoven(rs.getDate("FechaFinProJoven"));
				
				actuacion.setCodigoAgenda(rs.getString("CodigoAgenda"));
				actuacion.setLugarAtencion(rs.getString("LugarAtencion"));
				actuacion.setIdProfesional(rs.getString("IdProfesional"));
				actuacion.setTipoPrestacion(rs.getString("TipoPrestacion"));
				
				actuacion.setEquipoDeCalle(rs.getInt("EquipoDeCalle") > 0 ? true : false);
				
				actuacion.setFechaAlta(rs.getDate("FechaAlta"));
				actuacion.setMotivoAlta(rs.getInt("MotivoAlta"));
				
				actuacion.setNumeroCita(rs.getInt("NumeroCita"));
				actuacion.setNumeroICU(rs.getString("NumICU"));
				
				actuacion.setFechaAltaEquipoCalle(rs.getDate("FechaAltaEquipoCalle"));
				
				actuacion.setMedidaProteccion(rs.getInt("MedidaProteccion") > 0 ? true : false);
				actuacion.setResidencia(rs.getInt("Residencia") > 0 ? true : false);				
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerUltimaPorIdPaciente()", ex);
		}
		finally {
			closeResources();
		}
		
		return actuacion;
	}
	
	/**
	 * 
	 * @param actuacion
	 * @return
	 **/
	public int insertar(Actuacion actuacion) throws HSCException {
		
		int generatedId = 0;
		
		String query = "INSERT INTO Actuaciones (IdPaciente, NumRegistro, Fecha,"
				+ " IncapacidadTotal, CuratelaSalud, CuratelaEconomica,"
				+ " ProgramaContinuidadCuidados, ProgramaJoven, FechaInicioProJoven,"
				+ " FechaFinProJoven, CodigoAgenda, LugarAtencion, IdProfesional,"
				+ " TipoPrestacion, EquipoDeCalle, FechaAlta, MotivoAlta, NumeroCita,"
				+ " NumICU, FechaAltaEquipoCalle, MedidaProteccion, Residencia)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		int numRegistro = siguienteNumeroRegistro(actuacion.getIdPaciente());
		
		try {
			stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setInt (1, actuacion.getIdPaciente());
			stmt.setInt (2, numRegistro);
			stmt.setDate(3, actuacion.getFecha());
			stmt.setInt (4, actuacion.isIncapacidadTotal() ? 1 : 0);
			stmt.setInt (5, actuacion.isCuratelaSalud() ? 1 : 0);
			stmt.setInt (6, actuacion.isCuratelaEconomica() ? 1 : 0);
			stmt.setInt (7, actuacion.isProgramaContinuidadCuidados() ? 1 : 0);
			stmt.setInt (8, actuacion.isProgramaJoven() ? 1 : 0);
			if (actuacion.getFechaInicioProgramaJoven() != null) {
				stmt.setDate(9, actuacion.getFechaInicioProgramaJoven());
			}
			else {
				stmt.setNull(9, Types.DATE);
			}
			if (actuacion.getFechaFinProgramaJoven() != null) {
				stmt.setDate(10, actuacion.getFechaFinProgramaJoven());
			}
			else {
				stmt.setNull(10, Types.DATE);
			}
			
			stmt.setString(11, actuacion.getCodigoAgenda());
			stmt.setString(12, actuacion.getLugarAtencion());
			stmt.setString(13, actuacion.getIdProfesional());
			stmt.setString(14, actuacion.getTipoPrestacion());
			
			stmt.setInt(15, actuacion.isEquipoDeCalle() ? 1 : 0);
			
			if (actuacion.getFechaAlta() != null)
				stmt.setDate(16, actuacion.getFechaAlta());
			else
				stmt.setNull(16, Types.DATE);
			stmt.setInt (17, actuacion.getMotivoAlta());
			
			stmt.setInt   (18, actuacion.getNumeroCita());
			stmt.setString(19, actuacion.getNumeroICU() == null ? "" : actuacion.getNumeroICU());
			
			stmt.setDate(20, actuacion.getFechaAltaEquipoCalle());
			
			stmt.setInt(21, actuacion.isMedidaProteccion() ? 1 : 0);
			stmt.setInt(22, actuacion.isResidencia() ? 1 : 0);
				
			synchronized(this) {
				stmt.executeUpdate();
				
				ResultSet generatedKeys = stmt.getGeneratedKeys();
				
				if (generatedKeys.next()) {
					generatedId = (int)generatedKeys.getLong(1);
				}
				else {
					throw new HSCException("HSCException: insertar(): No se generó ID"
											+ " para la actuación insertada", null);
				}
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: insertar()", ex);
		}
		finally {
			closeResources();
		}
		
		return generatedId;
	}

	/**
	 * 
	 * @param actuacion
	 * @return
	 * @throws ConnectException
	 * @throws InsertActuacionException
	 */
	public int actualizar(Actuacion actuacion) throws HSCException {
		int result = 0;
		
		String query = "UPDATE Actuaciones SET IdPaciente = ?, Fecha = ?,"
				+ " IncapacidadTotal = ?, CuratelaSalud = ?, CuratelaEconomica = ?,"
				+ " ProgramaContinuidadCuidados = ?, ProgramaJoven = ?, FechaInicioProJoven = ?,"
				+ " FechaFinProJoven = ?, CodigoAgenda = ?, LugarAtencion = ?, IdProfesional = ?,"
				+ " TipoPrestacion = ?, EquipoDeCalle = ?, FechaAlta = ?, MotivoAlta = ?,"
				+ " NumeroCita = ?, NumICU = ?, FechaAltaEquipoCalle = ?,"
				+ " MedidaProteccion = ?, Residencia = ?"
				+ " WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt (1, actuacion.getIdPaciente());
			stmt.setDate(2, actuacion.getFecha());
			stmt.setInt (3, actuacion.isIncapacidadTotal() ? 1 : 0);
			stmt.setInt (4, actuacion.isCuratelaSalud() ? 1 : 0);
			stmt.setInt (5, actuacion.isCuratelaEconomica() ? 1 : 0);
			stmt.setInt (6, actuacion.isProgramaContinuidadCuidados() ? 1 : 0);
			stmt.setInt (7, actuacion.isProgramaJoven() ? 1 : 0);
			stmt.setDate(8, actuacion.getFechaInicioProgramaJoven());
			stmt.setDate(9, actuacion.getFechaFinProgramaJoven());
			
			stmt.setString(10, actuacion.getCodigoAgenda());
			stmt.setString(11, actuacion.getLugarAtencion());
			stmt.setString(12, actuacion.getIdProfesional());
			stmt.setString(13, actuacion.getTipoPrestacion());
			
			stmt.setInt(14, actuacion.isEquipoDeCalle() ? 1 : 0);
			
			stmt.setDate(15, actuacion.getFechaAlta());
			stmt.setInt (16, actuacion.getMotivoAlta());
			
			stmt.setInt   (17, actuacion.getNumeroCita());
			stmt.setString(18, actuacion.getNumeroICU());
			
			stmt.setDate(19, actuacion.getFechaAltaEquipoCalle());
			
			stmt.setInt(20, actuacion.isMedidaProteccion() ? 1 : 0);
			stmt.setInt(21, actuacion.isResidencia() ? 1 : 0);
			
			stmt.setInt(22, actuacion.getIdActuacion());
				
			synchronized (this) {
				result = stmt.executeUpdate();				
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: actualizar()", ex);
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param idPaciente
	 * @return
	 **/
	private int siguienteNumeroRegistro(int idPaciente) throws HSCException {
		int result = 1;

		String query = "SELECT TOP 1 NumRegistro FROM Actuaciones WHERE idPaciente = ?"
						+ " ORDER BY Fecha DESC, NumRegistro DESC";
		
		try {
			stmt = connection.prepareStatement(query);
		
			this.stmt.setInt(1, idPaciente);
						
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt("NumRegistro") + 1;				
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: siguienteNumeroRegistro()", ex);
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 * @throws HSCException
	 **/
	public ArrayList<Actuacion> obtenerTodo() throws HSCException {
		ArrayList<Actuacion> actuaciones = new ArrayList<Actuacion>();
		
		String query = "SELECT * FROM Actuaciones";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				Actuacion actuacion = new Actuacion();
				actuacion.setIdActuacion(rs.getInt("IdActuacion"));
				actuacion.setIdPaciente(rs.getInt("IdPaciente"));
				actuacion.setNumRegistro(rs.getInt("NumRegistro"));
				actuacion.setFecha(rs.getDate("Fecha"));
				actuacion.setIncapacidadTotal(rs.getInt("IncapacidadTotal") > 0 ? true : false);
				actuacion.setCuratelaSalud(rs.getInt("CuratelaSalud") > 0 ? true : false);
				actuacion.setCuratelaEconomica(rs.getInt("CuratelaEconomica") > 0 ? true : false);
				
				actuacion.setCodigoAgenda(rs.getString("CodigoAgenda"));
				actuacion.setLugarAtencion(rs.getString("LugarAtencion"));
				actuacion.setIdProfesional(rs.getString("IdProfesional"));
				actuacion.setTipoPrestacion(rs.getString("TipoPrestacion"));
				
				actuacion.setEquipoDeCalle(rs.getInt("EquipoDeCalle") > 0 ? true : false);

				actuacion.setFechaAlta(rs.getDate("FechaAlta"));
				actuacion.setMotivoAlta(rs.getInt("MotivoAlta"));
				
				actuacion.setNumeroCita(rs.getInt("NumeroCita"));
				actuacion.setNumeroICU(rs.getString("NumICU"));
				
				actuacion.setFechaAltaEquipoCalle(rs.getDate("FechaAltaEquipoCalle"));
				
				actuacion.setMedidaProteccion(rs.getInt("MedidaProteccion") > 0 ? true : false);
				actuacion.setResidencia(rs.getInt("Residencia") > 0 ? true : false);
				
				actuaciones.add(actuacion);
			}
			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOActuaciones.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex);			
		}
		finally {
			closeResources();
		}
		
		return actuaciones;
	}
	
}
