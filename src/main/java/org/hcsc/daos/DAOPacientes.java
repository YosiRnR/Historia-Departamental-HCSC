package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.hl7.ClientHL7;
import org.hcsc.models.Paciente;


public class DAOPacientes {
	private Connection connection = null;
	private PreparedStatement stmt = null;
	
	
	/**
	 * CONSTRUCTOR 
	 * @param Connection connection
	 * @return Inyecta la conexion con BBDD creada por la factoria de Daos
	 * @throws ConnectException
	 */
	public DAOPacientes(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * 
	 * @param ResultSet proveniente de una consulta en la BBDD
	 * @return El objeto Paciente formado con los resultados de la consulta en BBDD 
	 * @throws SQLException
	 */
	private Paciente crearPacienteDeResultSet(ResultSet rs) throws SQLException {
		Paciente paciente = new Paciente();
		
		paciente.setIdPaciente(rs.getInt("IdPaciente"));
		paciente.setNombre(rs.getString("Nombre"));
		paciente.setApellido1(rs.getString("Apellido1"));
		paciente.setApellido2(rs.getString("Apellido2"));
		paciente.setSexo(rs.getShort("Sexo"));
		paciente.setFechaNacimiento(rs.getDate("FechaNacimiento"));
		paciente.setDireccion(rs.getString("Direccion"));
		paciente.setPoblacion(rs.getString("Poblacion"));
		paciente.setCodigoPostal(rs.getInt("CodPostal"));
		paciente.setDni(rs.getString("Dni"));
		paciente.setPasaporte(rs.getString("Pasaporte"));
		paciente.setNie(rs.getString("Nie"));
		paciente.setNumeroHistoriaClinica(rs.getInt("NumHistoriaClinica"));
		paciente.setNumeroSeguridadSocial(rs.getString("NumSeguridadSocial"));
		paciente.setNumeroTarjetaSanitaria(rs.getString("NumTarjetaSanitaria"));
		paciente.setNumeroCIPA(rs.getInt("NumCIPA"));
		paciente.setTelefono1(rs.getString("Telefono1"));
		paciente.setTelefono2(rs.getString("Telefono2"));
		paciente.setFamiliar(rs.getString("Familiar"));
		paciente.setTelefonoFamiliar(rs.getString("TelefonoFamiliar"));
		
		return paciente;
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
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}
	
	/**
	 * 
	 * @param int idPaciente
	 * @return Valor booleano que indica si el paciente esta o no en la BBDD
	 * @throws ConnectException
	 */
	public boolean comprobarIdPacienteEnBBDD(int idPaciente) throws HSCException {
		boolean result = false;
		
		String query = "SELECT COUNT(IdPaciente) FROM Pacientes WHERE IdPaciente = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idPaciente);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1) > 0 ? true : false;
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: comprobarIdPacienteEnBBDD()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param int idPaciente
	 * @return El Paciente coincidente con el IdPaciente especificado
	 * @throws ConnectException
	 */
	public Paciente obtenerPorID(int idPaciente) throws HSCException {
		Paciente paciente = null;
		
		String query = "SELECT * FROM Pacientes WHERE IdPaciente = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idPaciente);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				paciente = this.crearPacienteDeResultSet(rs);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorID()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return paciente;
	}
	
	/**
	 * 
	 * @param Paciente toFind
	 * @return La lista de Pacientes coincidentes con el patrón de búsqueda especificado
	 * @throws ConnectException
	 */
	public ArrayList<Paciente> obtenerPorPatron(Paciente toFind) throws HSCException {
		ArrayList<Paciente> result = new ArrayList<Paciente>();
		
		String tipoDoc = "(Dni LIKE ? OR Pasaporte LIKE ? OR Nie LIKE ?)";
		String numDocu = toFind.getDni();
		
		if (numDocu != null && !numDocu.isEmpty()) {
			tipoDoc = "Dni LIKE ?";
			numDocu = toFind.getDni();
		}
		else {
			numDocu = toFind.getPasaporte();
			if (numDocu == null || !numDocu.isEmpty()) {
				tipoDoc = "Pasaporte LIKE ?";
				numDocu = toFind.getPasaporte();
			}
			else {
				numDocu = toFind.getNie();
				if (numDocu == null || !numDocu.isEmpty()) {
					tipoDoc = "Nie LIKE ?";
					numDocu = toFind.getNie();
				}
			}
		}
		
		try {
			if (toFind.getFechaNacimiento() != null) {
				String query = "SELECT * FROM Pacientes WHERE Apellido1 LIKE ?"
								+ " AND Apellido2 LIKE ? AND Nombre LIKE ?"
								+ " AND FechaNacimiento = ? AND " + tipoDoc;
			
				stmt = connection.prepareStatement(query);
				
				stmt.setString(1, "%" + toFind.getApellido1().trim() + "%");
				stmt.setString(2, "%" + toFind.getApellido2().trim() + "%");
				stmt.setString(3, "%" + toFind.getNombre().trim() + "%");
				stmt.setDate  (4, toFind.getFechaNacimiento());
				if (tipoDoc.equalsIgnoreCase("(Dni LIKE ? OR Pasaporte LIKE ? OR Nie LIKE ?)")) {
					stmt.setString(5, "%" + numDocu.trim() + "%");
					stmt.setString(6, "%" + numDocu.trim() + "%");
					stmt.setString(7, "%" + numDocu.trim() + "%");
				}
				else {
					stmt.setString(5, "%" + numDocu.trim() + "%");
				}
			}
			else {
				String query = "SELECT * FROM Pacientes WHERE Apellido1 LIKE ?"
								+ " AND Apellido2 LIKE ? AND Nombre LIKE ? AND " + tipoDoc;
			
				stmt = connection.prepareStatement(query);
				
				stmt.setString(1, "%" + toFind.getApellido1().trim() + "%");
				stmt.setString(2, "%" + toFind.getApellido2().trim() + "%");
				stmt.setString(3, "%" + toFind.getNombre().trim() + "%");
				if (tipoDoc.equalsIgnoreCase("(Dni LIKE ? OR Pasaporte LIKE ? OR Nie LIKE ?)")) {
					stmt.setString(4, "%" + numDocu.trim() + "%");
					stmt.setString(5, "%" + numDocu.trim() + "%");
					stmt.setString(6, "%" + numDocu.trim() + "%");
				}
				else {
					stmt.setString(4, "%" + numDocu.trim() + "%");					
				}
			}
		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				Paciente paciente = this.crearPacienteDeResultSet(rs);
				result.add(paciente);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorPatron()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * ¿¿??
	 * @param Paciente paciente
	 * @return El Paciente coincidente con alguno de los identificadores sanitarios especificados
	 * @throws ConnectException
	 */
	public Paciente obtenerPorIdentificadorSanitario(Paciente paciente) throws HSCException {
		Paciente result = null;
		
		String query = "SELECT * FROM Pacientes WHERE NumHistoriaClinica = ?"
						+ " OR NumCIPA = ? OR NumTarjetaSanitaria = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt   (1, paciente.getNumeroHistoriaClinica());
			stmt.setInt   (2, paciente.getNumeroCIPA());
			stmt.setString(3, paciente.getNumeroTarjetaSanitaria());
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				result = this.crearPacienteDeResultSet(rs);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdentificadorSanitario()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param Paciente paciente
	 * @return int generado por la BBDD como nuevo IdPaciente
	 * @throws ConnectException
	 */
	public int guardar(Paciente paciente) throws HSCException {
		int generatedIdPaciente = -1;
		
		String query = "INSERT INTO Pacientes (Nombre, Apellido1, Apellido2, Sexo,"
				+ " Direccion, Poblacion, CodPostal, Dni, Pasaporte, Nie,"
				+ " FechaNacimiento, NumHistoriaClinica, NumSeguridadSocial,"
				+ " NumTarjetaSanitaria, NumCIPA, Telefono1, Telefono2, Familiar, TelefonoFamiliar)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, paciente.getNombre());
			stmt.setString(2, paciente.getApellido1());
			stmt.setString(3, paciente.getApellido2());
			stmt.setShort (4, paciente.getSexo());
			stmt.setString(5, paciente.getDireccion());
			stmt.setString(6, paciente.getPoblacion());
			if (paciente.getCodigoPostal() <= 0)
				stmt.setNull(7, Types.INTEGER);
			else
				stmt.setInt(7, paciente.getCodigoPostal());
			stmt.setString(8,  paciente.getDni());
			stmt.setString(9,  paciente.getPasaporte());
			stmt.setString(10, paciente.getNie());
			stmt.setDate  (11, paciente.getFechaNacimiento());
			if (paciente.getNumeroHistoriaClinica() <= 0)
				stmt.setNull(12, Types.INTEGER);
			else
				stmt.setInt(12, paciente.getNumeroHistoriaClinica());
			if (paciente.getNumeroSeguridadSocial().isEmpty())
				stmt.setNull(13, Types.VARCHAR);
			else
				stmt.setString(13, paciente.getNumeroSeguridadSocial());
			if (paciente.getNumeroTarjetaSanitaria().isEmpty())
				stmt.setNull(14, Types.VARCHAR);
			else
				stmt.setString(14, paciente.getNumeroTarjetaSanitaria());
			if (paciente.getNumeroCIPA() <= 0)
				stmt.setNull(15, Types.INTEGER);
			else
				stmt.setInt(15, paciente.getNumeroCIPA());
			stmt.setString(16, paciente.getTelefono1());
			stmt.setString(17, paciente.getTelefono2());
			stmt.setString(18, paciente.getFamiliar());
			stmt.setString(19, paciente.getTelefonoFamiliar());
			
			synchronized(this) {
				int opResult = stmt.executeUpdate();
				
				if (opResult > 0) {
					ResultSet generatedKeys = stmt.getGeneratedKeys();
					if (generatedKeys != null) {
						if (generatedKeys.next()) {
							generatedIdPaciente = generatedKeys.getInt(1);
						}
					}
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: guardar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return generatedIdPaciente;
	}
	
	/**
	 * 
	 * @param Paciente paciente (el IdPaciente a actualizar lo lleva dentro)
	 * @return Actualiza los datos del paciente
	 * @throws ConnectException
	 */
	public int actualizar(Paciente paciente) throws HSCException {
		int result = 0;
		
		String query = "UPDATE Pacientes SET Nombre = ?, Apellido1 = ?, Apellido2 = ?,"
				+ " Sexo = ?, Direccion = ?, Poblacion = ?, CodPostal = ?,"
				+ " Dni = ?, Pasaporte = ?, Nie = ?, FechaNacimiento = ?,"
				+ " NumHistoriaClinica = ?, NumSeguridadSocial = ?, NumTarjetaSanitaria = ?,"
				+ " NumCIPA = ?, Telefono1 = ?, Telefono2 = ?, Familiar = ?, TelefonoFamiliar = ?"
				+ " WHERE IdPaciente = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, paciente.getNombre());
			stmt.setString(2, paciente.getApellido1());
			stmt.setString(3, paciente.getApellido2());
			stmt.setShort (4, paciente.getSexo());
			stmt.setString(5, paciente.getDireccion());
			stmt.setString(6, paciente.getPoblacion());
			if (paciente.getCodigoPostal() <= 0)
				stmt.setNull(7, Types.INTEGER);
			else
				stmt.setInt(7, paciente.getCodigoPostal());
			if (paciente.getDni().isEmpty())
				//stmt.setNull(8, Types.VARCHAR);
				stmt.setString(8, "");
			else
				stmt.setString(8, paciente.getDni());
			if (paciente.getPasaporte().isEmpty())
				//stmt.setNull(9, Types.VARCHAR);
				stmt.setString(9, "");
			else
				stmt.setString(9, paciente.getPasaporte());
			if (paciente.getNie().isEmpty())
				//stmt.setNull(10, Types.VARCHAR);
				stmt.setString(10, "");
			else
				stmt.setString(10, paciente.getNie());
			stmt.setDate(11, paciente.getFechaNacimiento());
			if (paciente.getNumeroHistoriaClinica() <= 0)
				stmt.setNull(12, Types.INTEGER);
			else
				stmt.setInt(12, paciente.getNumeroHistoriaClinica());
			if (paciente.getNumeroSeguridadSocial().isEmpty())
				stmt.setNull(13, Types.VARCHAR);
			else
				stmt.setString(13, paciente.getNumeroSeguridadSocial());
			if (paciente.getNumeroTarjetaSanitaria().isEmpty())
				stmt.setNull(14, Types.VARCHAR);
			else
				stmt.setString(14, paciente.getNumeroTarjetaSanitaria());
			if (paciente.getNumeroCIPA() <= 0)
				stmt.setNull(15, Types.INTEGER);
			else
				stmt.setInt(15, paciente.getNumeroCIPA());
			stmt.setString(16, paciente.getTelefono1());
			stmt.setString(17, paciente.getTelefono2());
			stmt.setString(18, paciente.getFamiliar());
			stmt.setString(19, paciente.getTelefonoFamiliar());
			stmt.setInt   (20, paciente.getIdPaciente());
			
			synchronized(this) {
				result = stmt.executeUpdate();
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: actualizar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param String numeroCIPA (el campo en BBDD es de tipo VARCHAR)
	 * @return El paciente encontrado con el numero CIPA especificado
	 * @throws ConnectException
	 */
	public Paciente obtenerPorCIPA(int numeroCIPA) throws HSCException {
		Paciente paciente = null;
		
		String query = "SELECT * FROM Pacientes WHERE NumCIPA = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, numeroCIPA);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				paciente = this.crearPacienteDeResultSet(rs);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorCIPA()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return paciente;
	}
	
	/**
	 * 
	 * @param numeroHC
	 * @return
	 * @throws HSCException
	 **/
	public Paciente obtenerPorNumeroHC(int numeroHC) throws HSCException {
		Paciente paciente = null;
		
		String query = "SELECT * FROM Pacientes WHERE NumHistoriaClinica = ?";
		
		try {
			stmt = connection.prepareStatement(query);
		
			stmt.setInt(1, numeroHC);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				paciente = crearPacienteDeResultSet(rs);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).info("SQLException: StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorNumeroHC()", ex.getCause());
		}
		finally {
			closeResources();
		}
				
		return paciente;
	}	
	
	/**
	 * 
	 * @param String numeroHC (el campo en BBDD es de tipo VARCHAR)
	 * @return El paciente encontrado con el numero de historia clinica especificado
	 * @throws ConnectException
	 */
	public Paciente obtenerPorNumeroHistoriaClinica(int numeroHC) throws HSCException {
		Paciente paciente = null;
		
		String query = "SELECT * FROM Pacientes WHERE NumHistoriaClinica = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, numeroHC);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				paciente = this.crearPacienteDeResultSet(rs);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorNumeroHistoriaClinica()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return paciente;
	}
	
	/**
	 * 
	 * @param idPaciente
	 * @return Valor booleano que indica si se encontro FechaAltaEquipoDeCalle o no.
	 * @throws ConnectException
	 */
	public boolean buscarFechaAltaEquipoCalle(int idPaciente) throws HSCException {
		boolean result = true;
		
		String query = "SELECT FechaAltaEquipoCalle FROM Pacientes WHERE IdPaciente = ?";
		
		try {
			stmt = connection.prepareStatement(query);
		
			stmt.setInt(1, idPaciente);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				java.sql.Date fechaAlta = rs.getDate(1);
				if (fechaAlta == null) {
					result = false;
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: buscarFechaAltaEquipoCalle()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param paciente
	 * @return El IdPaciente encontrado, en caso de no encontrar paciente devuelve 0
	 * @throws ConnectException
	 */
	public int comprobarIdentificadoresUnicos(Paciente paciente) throws HSCException {
		int result = 0;
		
		String query = "SELECT IdPaciente FROM Pacientes WHERE" +
				" (Dni IS NOT NULL AND Dni = ? AND Dni <> '') OR" +
				" (Pasaporte IS NOT NULL AND Pasaporte = ? AND Pasaporte <> '') OR" +
				" (Nie IS NOT NULL AND Nie = ? AND Nie <> '') OR" +
				" (NumHistoriaClinica IS NOT NULL AND NumHistoriaClinica = ? AND NumHistoriaClinica <> '') OR" +
				" (NumSeguridadSocial IS NOT NULL AND NumSeguridadSocial = ? AND NumSeguridadSocial <> '') OR" +
				" (NumTarjetaSanitaria IS NOT NULL AND NumTarjetaSanitaria = ? AND NumTarjetaSanitaria <> '') OR" +
				" (NumCIPA IS NOT NULL AND NumCIPA = ? AND NumCIPA <> '')";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, paciente.getDni());
			stmt.setString(2, paciente.getPasaporte());
			stmt.setString(3, paciente.getNie());
			stmt.setInt   (4, paciente.getNumeroHistoriaClinica());
			stmt.setString(5, paciente.getNumeroSeguridadSocial());
			stmt.setString(6, paciente.getNumeroTarjetaSanitaria());
			stmt.setInt   (7, paciente.getNumeroCIPA());
			
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: comprobarIdentificadoresUnicos()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}

	/**
	 * 
	 * @param paciente
	 * @return
	 * @throws ConnectException
	 **/
	public int insertar(Paciente paciente) throws HSCException {
		
		int generatedIdPaciente = -1;
		
		String query = "INSERT INTO Pacientes (Nombre, Apellido1, Apellido2, Sexo, Direccion,"
				+ " Poblacion, CodPostal, Dni, Pasaporte, Nie, FechaNacimiento,"
				+ " NumHistoriaClinica, NumSeguridadSocial, NumTarjetaSanitaria,"
				+ " NumCIPA, Telefono1, Telefono2, Familiar, TelefonoFamiliar)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				
			stmt.setString(1, paciente.getNombre());
			stmt.setString(2, paciente.getApellido1());
			stmt.setString(3, paciente.getApellido2());
			stmt.setShort (4, paciente.getSexo());
			stmt.setString(5, paciente.getDireccion());
			stmt.setString(6, paciente.getPoblacion());
			if (paciente.getCodigoPostal() <= 0) {
				stmt.setNull(7, Types.INTEGER);
			}
			else {
				stmt.setInt(7, paciente.getCodigoPostal());
			}
			if (paciente.getDni().isEmpty()) {
				//stmt.setNull(8, Types.VARCHAR);
				stmt.setString(8, "");
			}
			else {
				stmt.setString(8, paciente.getDni());
			}
			if (paciente.getPasaporte().isEmpty()) {
				//stmt.setNull(9, Types.VARCHAR);
				stmt.setString(9, "");
			}
			else {
				stmt.setString(9, paciente.getPasaporte());
			}
			if (paciente.getNie().isEmpty()) {
				//stmt.setNull(10, Types.VARCHAR);
				stmt.setString(10, "");
			}
			else {
				stmt.setString(10, paciente.getNie());
			}
			stmt.setDate(11, paciente.getFechaNacimiento());
			if (paciente.getNumeroHistoriaClinica() <= 0) {
				stmt.setNull(12, Types.INTEGER);
			}
			else {
				stmt.setInt(12, paciente.getNumeroHistoriaClinica());
			}
			if (paciente.getNumeroSeguridadSocial().isEmpty()) {
				stmt.setNull(13, Types.VARCHAR);
			}
			else {
				stmt.setString(13, paciente.getNumeroSeguridadSocial());
			}
			if (paciente.getNumeroTarjetaSanitaria().isEmpty()) {
				stmt.setNull(14, Types.VARCHAR);
			}
			else {
				stmt.setString(14, paciente.getNumeroTarjetaSanitaria());
			}
			if (paciente.getNumeroCIPA() <= 0) {
				stmt.setNull(15, Types.INTEGER);
			}
			else {
				stmt.setInt(15, paciente.getNumeroCIPA());
			}
			stmt.setString(16, paciente.getTelefono1());
			stmt.setString(17, paciente.getTelefono2());
			stmt.setString(18, paciente.getFamiliar());
			stmt.setString(19, paciente.getTelefonoFamiliar());
				
			synchronized (this) {				
				int result = stmt.executeUpdate();
				
				ResultSet generatedKeys = stmt.getGeneratedKeys();
				
				if (generatedKeys != null && result > 0) {
					if (generatedKeys.next()) {
						generatedIdPaciente = generatedKeys.getInt(1);
					}
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: insertar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return generatedIdPaciente;
	}
	
	/**
	 * 
	 * @param numeroHC
	 * @return
	 **/
	public Paciente obtenerPacienteDelHospital(int numeroHC) {
		Paciente pacienteABuscar = new Paciente();
		
		pacienteABuscar.setNumeroHistoriaClinica(numeroHC);
		
		ClientHL7 hl7Client = new ClientHL7();
		Paciente paciente = null;
		ArrayList<Paciente> pacientesEncontrados = hl7Client.sendQRY_A19(pacienteABuscar);
		if (!pacientesEncontrados.isEmpty()) {
			paciente = pacientesEncontrados.get(0);
		}
		else {
			Logger.getLogger(DAOPacientes.class).info("ATENCION! (DAOPacientes): Paciente con Numero HC: " + numeroHC + " no encontrado en el HPHIS");
			paciente = new Paciente();
		}
		
		return paciente;
	}

	public ArrayList<Paciente> obtenerTodo() throws HSCException {
		ArrayList<Paciente> pacientes = new ArrayList<Paciente>();
		
		String query = "SELECT * FROM Pacientes";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while( rs.next() ) {
				Paciente paciente = crearPacienteDeResultSet(rs);
				
				pacientes.add(paciente);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOPacientes.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return pacientes;
	}

}
