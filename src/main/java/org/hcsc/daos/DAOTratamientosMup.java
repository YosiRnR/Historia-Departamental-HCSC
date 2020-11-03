package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.TratamientosMup;


public class DAOTratamientosMup {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAOTratamientosMup(Connection connection) {
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
			Logger.getLogger(DAOTratamientosMup.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}
	
	/**
	 * 
	 * @param idActuacion
	 * @return
	 **/
	public TratamientosMup obtenerPorIdActuacion(int idActuacion) throws HSCException {
		TratamientosMup tratamientosMup = new TratamientosMup();
		
		String query = "SELECT * FROM TratamientosMUP WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next())
			{
				tratamientosMup.setIdTratamientoMup(rs.getInt("IdTratamientoMUP"));
				tratamientosMup.setIdActuacion     (rs.getInt("IdActuacion"));
				tratamientosMup.setDescripcion     (rs.getString("Descripcion"));
				tratamientosMup.setTratamientoRecomendacion(rs.getString("TratamientoRecomendacion"));				
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientosMup.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdActuacion()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return tratamientosMup;
	}

	/**
	 * 
	 * @param tratamientosMup
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 **/
	public int insertar(TratamientosMup tratamientosMup, int idActuacion) throws HSCException {
		int result = 0;

		String query = "INSERT INTO TratamientosMup (IdActuacion, Descripcion,"
					+ " TratamientoRecomendacion) VALUES(?, ?, ?)";

		if (tratamientosMup != null) {
			try {
				stmt = connection.prepareStatement(query);
				
				stmt.setInt   (1, idActuacion);
				stmt.setString(2, tratamientosMup.getDescripcion());
				stmt.setString(3, tratamientosMup.getTratamientoRecomendacion());
	
				synchronized(this) {
					result = stmt.executeUpdate();
				}
			}
			catch(SQLException ex) {
				Logger.getLogger(DAOTratamientosMup.class).error("StackTrace: ", ex);
				throw new HSCException("SQLException: insertar()", ex.getCause());
			}
			finally {
				closeResources();
			}
		}

		return result;
	}

	/**
	 * 
	 * @param tratamientosMup
	 * @param idActuacion
	 * @return
	 */
	public int actualizar(TratamientosMup tratamientosMup, int idActuacion) throws HSCException {
		int result = -1;
		
		String query = "UPDATE TratamientosMUP SET Descripcion = ?,"
							+ " TratamientoRecomendacion = ?"
							+ " WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, tratamientosMup.getDescripcion());
			stmt.setString(2, tratamientosMup.getTratamientoRecomendacion());
			stmt.setInt   (3, idActuacion);
				
			synchronized (this) {
				result = stmt.executeUpdate();
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientosMup.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: actualizar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}	

	public ArrayList<TratamientosMup> obtenerTodo() throws HSCException {
		ArrayList<TratamientosMup> tratamientosMupList = new ArrayList<TratamientosMup>();
		
		String query = "SELECT * FROM TratamientosMUP";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next())
			{
				TratamientosMup tratamientosMup = new TratamientosMup();
				
				tratamientosMup.setIdTratamientoMup(rs.getInt("IdTratamientoMUP"));
				tratamientosMup.setIdActuacion     (rs.getInt("IdActuacion"));
				tratamientosMup.setDescripcion     (rs.getString("Descripcion"));
				tratamientosMup.setTratamientoRecomendacion(rs.getString("TratamientoRecomendacion"));
				
				tratamientosMupList.add(tratamientosMup);
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientosMup.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return tratamientosMupList;
	}
	
}
