package org.hcsc.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;


public class DAOAccesos {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	// CONSTRUCTOR //
	public DAOAccesos(Connection connection) {
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
			Logger.getLogger(DAOAccesos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException (DAOAccesos): Error cerrando PreparedStatement()", ex.getCause());
		}
	}
	
	/**
	 * @throws HSCException 
	 * 
	 */
	public void grabarAcceso(int idActuacion, int codigoFacultativo, int idPacienteAccedido,
			int numeroHCPacienteAccedido, int numRegistroPacienteAccedido, Date fechaRegistroPacienteAccedido) throws HSCException {
		String query = "INSERT INTO AccesosUsuarios (IdActuacion, CodigoFacultativo, IdPacienteAccedido,"
				+ " NumeroHCPacienteAccedido, NumeroRegistroPacienteAccedido, FechaHoraAcceso,"
				+ " FechaRegistroPacienteAccedido)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		Timestamp fechaHoraAcceso = new Timestamp(Calendar.getInstance().getTime().getTime());
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			stmt.setInt(2, codigoFacultativo);
			stmt.setInt(3, idPacienteAccedido);
			stmt.setInt(4, numeroHCPacienteAccedido);
			stmt.setInt(5, numRegistroPacienteAccedido);
			stmt.setTimestamp(6, fechaHoraAcceso);
			stmt.setDate(7, fechaRegistroPacienteAccedido);
			
			synchronized(this) {
				stmt.executeUpdate();
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOAccesos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: grabarAcceso()", ex);			
		}
		finally {
			closeResources();
		}
	}
}
