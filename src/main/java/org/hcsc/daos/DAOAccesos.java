package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	public void grabarAcceso(int codigoFacultativo, int idPacienteAccedido) throws HSCException {
		String query = "INSERT INTO AccesosUsuarios (CodigoFacultativo, IdPacienteAccedido)"
						+ " VALUES (?, ?)";
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, codigoFacultativo);
			stmt.setInt(2, idPacienteAccedido);
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
