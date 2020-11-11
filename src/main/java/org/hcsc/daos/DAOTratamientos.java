package org.hcsc.daos;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.Tratamiento;

public class DAOTratamientos {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAOTratamientos(Connection connection) {
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
			Logger.getLogger(DAOTratamientos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}
	
	/**
	 * 
	 * @param pIdActuacion
	 * @return
	 **/
	public ArrayList<Tratamiento> obtenerPorIdActuacion(int pIdActuacion) throws HSCException {		
		ArrayList<Tratamiento> tratamientosList = new ArrayList<Tratamiento>();
		
		String query = "SELECT * FROM TRATAMIENTOS WHERE IdActuacion = ?";

		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, pIdActuacion);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				Tratamiento tratamiento = new Tratamiento();
				
				tratamiento.setIdTratamiento(rs.getInt("IdTratamiento"));
				tratamiento.setIdActuacion  (rs.getInt("IdActuacion"));
				tratamiento.setPosicion     (rs.getInt("Posicion"));
				tratamiento.setValor        (rs.getString("Valor"));
				tratamiento.setFechaInicio  (rs.getDate("FechaInicio"));
				tratamiento.setFechaFin     (rs.getDate("FechaFin"));
				
				tratamientosList.add(tratamiento);
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdActuacion()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return tratamientosList;
	}

	/**
	 * 
	 * @param tratamientos
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 */
	public int insertar(ArrayList<Tratamiento> tratamientos, int idActuacion) throws HSCException {
		int result = 0;
		
		String query = "INSERT INTO Tratamientos ( IdActuacion, Posicion,"
					+ " Valor, FechaInicio, FechaFin) VALUES(?, ?, ?, ?, ?)";
		
		try {
			stmt = connection.prepareStatement(query);
			
			for (Tratamiento item : tratamientos) {
				stmt.setInt   (1,  idActuacion);
				stmt.setInt   (2, item.getPosicion());
				stmt.setString(3, item.getValor());
				stmt.setDate  (4, item.getFechaInicio());
				stmt.setDate  (5, item.getFechaFin());
				
				synchronized (this) {					
					result += stmt.executeUpdate();
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: insertar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}

	/**
	 * 
	 * @param tratamientos
	 * @param idActuacion
	 * @return
	 */
	public int actualizar(ArrayList<Tratamiento> tratamientos, int idActuacion) throws HSCException {
		int result = 0;
		
		try {
			result = borrar(idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error eliminando Tratamientos durante"
					+ " actualización de registro", ex.getCause());			
		}
		try {
			result = insertar(tratamientos, idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error insertando Tratamientos durante"
					+ " actualización de registro", ex.getCause());			
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param idActuacion
	 * @return
	 */
	public int borrar(int idActuacion)  throws HSCException {
		int result = 0;
		
		String query = "DELETE FROM Tratamientos WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
				
			synchronized (this) {
				result = stmt.executeUpdate();			
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: borrar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	public ArrayList<Tratamiento> obtenerTodo() throws HSCException {
		ArrayList<Tratamiento> tratamientosList = new ArrayList<Tratamiento>();
		
		String query = "SELECT * FROM Tratamientos";

		String queryIF = "SELECT Label, Opciones, Posicion FROM InputFields WHERE Seccion = 'TRAT'";

		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			stmt = connection.prepareStatement(queryIF);
			
			ResultSet rsIF = stmt.executeQuery();
			
			Map<Integer, String[]> tratsValues = new HashMap<Integer, String[]>();
			
			while(rsIF.next()) {
				tratsValues.put(rsIF.getInt("Posicion"), rsIF.getString("Opciones").split(";"));
			}
			
			while(rs.next())
			{
				Tratamiento tratamiento = new Tratamiento();
				
				tratamiento.setIdTratamiento(rs.getInt("IdTratamiento"));
				tratamiento.setIdActuacion  (rs.getInt("IdActuacion"));
				tratamiento.setPosicion     (rs.getInt("Posicion"));
//				tratamiento.setValor        (rs.getString("Valor"));
				
				String[] opciones = tratsValues.get(rs.getInt("Posicion"));
				tratamiento.setValor(rs.getString("Valor").isEmpty() ? "" : opciones[Integer.parseInt(rs.getString("Valor")) - 1]);
				
				tratamiento.setFechaInicio  (rs.getDate("FechaInicio"));
				tratamiento.setFechaFin     (rs.getDate("FechaFin"));
				
				tratamientosList.add(tratamiento);
			}			
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOTratamientos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return tratamientosList;
	}	
	
}
