package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.ProgsUnidsEspProcs;

public class DAOProgsUnidsEspProcs {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAOProgsUnidsEspProcs(Connection connection) {
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
			Logger.getLogger(DAOProgsUnidsEspProcs.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}

	/**
	 * 
	 * @param idActuacion
	 * @return
	 **/
	public ArrayList<ProgsUnidsEspProcs> obtenerPorIdActuacion(int idActuacion) throws HSCException {
		ArrayList<ProgsUnidsEspProcs> progUnidsProcsList = new ArrayList<ProgsUnidsEspProcs>();

		String query = "SELECT * FROM ProgsUnidsEspProcs WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				ProgsUnidsEspProcs puep = new ProgsUnidsEspProcs();
				
				puep.setIdProgramasUnidadesProcesos(rs.getInt   ("IdProgramasUnidadesProcesos"));
				puep.setIdActuacion                (rs.getInt   ("IdActuacion"));
				puep.setPosicion                   (rs.getInt   ("Posicion"));
				puep.setValor                      (rs.getString("Valor"));
				
				progUnidsProcsList.add(puep);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOProgsUnidsEspProcs.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdActuacion()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return progUnidsProcsList;
	}

	/**
	 * 
	 * @param puepList
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 */
	public int insertar(ArrayList<ProgsUnidsEspProcs> puepList, int idActuacion) throws HSCException {
		int result = 0;
		
		String query = "INSERT INTO ProgsUnidsEspProcs (IdActuacion,"
						+ " Posicion, Valor) VALUES(?, ?, ?)";
		
		try {
			stmt = connection.prepareStatement(query);
				
			for (ProgsUnidsEspProcs item : puepList) {
				stmt.setInt(1, idActuacion);
				stmt.setInt(2, item.getPosicion());
				stmt.setString(3, item.getValor());

				synchronized (this) {
					result += stmt.executeUpdate();
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOProgsUnidsEspProcs.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: insertar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}

	/**
	 * 
	 * @param puepList
	 * @param idActuacion
	 * @return
	 */
	public int actualizar(ArrayList<ProgsUnidsEspProcs> puepList, int idActuacion) throws HSCException {
		int result = 0;
		
		try {
			result = borrar(idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error eliminando ProgsUnidsEspProcs durante"
					+ " actualización de registro", ex.getCause());			
		}
		try {
			result = insertar(puepList, idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error insertando ProgsUnidsEspProcs durante"
					+ " actualización de registro", ex.getCause());						
		}
		
		return result;
	}

	/**
	 * 
	 * @param idActuacion
	 * @return
	 */
	public int borrar(int idActuacion) throws HSCException {
		int result = 0;
		
		String query = "DELETE FROM ProgsUnidsEspProcs WHERE IdActuacion = ?";
				
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			synchronized (this) {
				result = stmt.executeUpdate();				
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOProgsUnidsEspProcs.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: borrar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}

	public ArrayList<ProgsUnidsEspProcs> obtenerTodo() throws HSCException {
		ArrayList<ProgsUnidsEspProcs> progUnidsProcsList = new ArrayList<ProgsUnidsEspProcs>();
		
		String query = "SELECT * FROM ProgsUnidsEspProcs";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				ProgsUnidsEspProcs puep = new ProgsUnidsEspProcs();
				
				puep.setIdProgramasUnidadesProcesos(rs.getInt   ("IdProgramasUnidadesProcesos"));
				puep.setIdActuacion                (rs.getInt   ("IdActuacion"));
				puep.setPosicion                   (rs.getInt   ("Posicion"));
				puep.setValor                      (rs.getString("Valor"));
				
				progUnidsProcsList.add(puep);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOProgsUnidsEspProcs.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return progUnidsProcsList;
	}
	
}
