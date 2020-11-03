package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.SitClinicaDAS;


public class DAOSitClinicaDAS {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAOSitClinicaDAS(Connection connection) {
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
			Logger.getLogger(DAOSitClinicaDAS.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}

	/**
	 * 
	 * @param idActuacion
	 * @return
	 **/
	public ArrayList<SitClinicaDAS> obtenerPorIdActuacion(int idActuacion) throws HSCException {
		ArrayList<SitClinicaDAS> sitClinFunc = new ArrayList<SitClinicaDAS>();
				
		String query = "SELECT * FROM SitClinicaDAS WHERE IdActuacion = ?";

		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				SitClinicaDAS scf = new SitClinicaDAS();
				
				scf.setIdSituacionClinicaFuncional(rs.getInt("IdSituacionClinicaFuncional"));
				scf.setPosicion                   (rs.getInt("Posicion"));
				scf.setValor                      (rs.getString("Valor"));
				scf.setTipoSCFDAS                 (rs.getInt("TipoSCFDAS"));
				
				sitClinFunc.add(scf);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOSitClinicaDAS.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdActuacion()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return sitClinFunc;
	}

	/**
	 * 
	 * @param scfList
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 **/
	public int insertar(ArrayList<SitClinicaDAS> scfList, int idActuacion) throws HSCException {
		int result = 0;
		
		String query = "INSERT INTO SitClinicaDAS (IdActuacion, Posicion,"
						+ " Valor, TipoSCFDAS) VALUES(?, ?, ?, ?)";
		
		try {
			stmt = connection.prepareStatement(query);
			
			for (SitClinicaDAS item : scfList) {
				stmt.setInt   (1, idActuacion);
				stmt.setInt   (2, item.getPosicion());
				stmt.setString(3, item.getValor());
				stmt.setInt   (4, item.getTipoSCFDAS());
					
				synchronized (this) {				
					result += stmt.executeUpdate();
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOSitClinicaDAS.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: insertar()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param scfList
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 */
	public int actualizar(ArrayList<SitClinicaDAS> scfList, int idActuacion) throws HSCException {
		int result = 0;
		
		try {
			result = borrar(idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error eliminando SitClinicaDAS durante"
					+ " actualización de registro", ex.getCause());			
		}
		try {
			result = insertar(scfList, idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error insertando SitClinicaDAS durante"
					+ " actualización de registro", ex.getCause());			
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 */
	public int borrar(int idActuacion) throws HSCException {
		int result = 0;
		
		String query = "DELETE FROM SitClinicaDAS WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
				
			synchronized (this) {
				result = stmt.executeUpdate();
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOSitClinicaDAS.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: borrar()", ex.getCause());
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
	public ArrayList<SitClinicaDAS> obtenerTodo() throws HSCException {
		ArrayList<SitClinicaDAS> sitClinFunc = new ArrayList<SitClinicaDAS>();
	
		String query = "SELECT * FROM SitClinicaDAS";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				SitClinicaDAS scf = new SitClinicaDAS();
				
				scf.setIdSituacionClinicaFuncional(rs.getInt("IdSituacionClinicaFuncional"));
				scf.setPosicion                   (rs.getInt("Posicion"));
				scf.setValor                      (rs.getString("Valor"));
				scf.setTipoSCFDAS                 (rs.getInt("TipoSCFDAS"));
				
				sitClinFunc.add(scf);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOSitClinicaDAS.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex.getCause());
		}
		finally {
			closeResources();
		}
		
		return sitClinFunc;
	}	
	
}
