package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.Diagnostico;


public class DAODiagnosticos {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAODiagnosticos(Connection connection) {
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
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}

	/**
	 * 
	 * @param idActuacion
	 * @return
	 **/
	public ArrayList<Diagnostico> obtenerPorIdActuacion(int idActuacion) throws HSCException {
		ArrayList<Diagnostico> diagnosticos = new ArrayList<Diagnostico>();
		
		String query = "SELECT * FROM Diagnosticos WHERE IdActuacion = ?";
				
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Diagnostico diagnostico = new Diagnostico();
				
				diagnostico.setIdDiagnostico(rs.getInt("IdDiagnostico"));
				diagnostico.setIdActuacion(rs.getInt("IdActuacion"));
				diagnostico.setTipoDiagnostico(rs.getInt("TipoDiagnostico"));
				diagnostico.setCieDiagnostico(rs.getString("cieDiagnostico"));
				diagnostico.setPosCombo(rs.getInt("Posicion"));
				
				diagnosticos.add(diagnostico);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdActuacion()", ex);
		}
		finally {
			closeResources();
		}
		
		return diagnosticos;		
	}

	/**
	 * 
	 * @param diagnosticos
	 * @param idActuacion
	 * @return
	 * @throws ConnectException
	 * @throws InsertDiagnosticosException
	 **/
	public int insertar(ArrayList<Diagnostico> diagnosticos, int idActuacion) throws HSCException {
		int result = 0;
		
		String query = "INSERT INTO Diagnosticos ( IdActuacion, TipoDiagnostico,"
						+ " CieDiagnostico, Posicion ) VALUES(?, ?, ?, ?)";
				
		try {
			stmt = connection.prepareStatement(query);
			
			for (Diagnostico item : diagnosticos) {
				stmt.setInt   (1, idActuacion);
				stmt.setInt   (2, item.getTipoDiagnostico());
				stmt.setString(3, item.getCieDiagnostico());
				stmt.setInt   (4, item.getPosCombo());
				
				synchronized (this) {				
					result += stmt.executeUpdate();
				}
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: insertar()", ex);
		}
		finally {
			closeResources();
		}
		
		return result;
	}

	/**
	 * 
	 * @param diagnosticos
	 * @param idActuacion
	 * @return
	 */
	public int actualizar(ArrayList<Diagnostico> diagnosticos, int idActuacion) throws HSCException {
		int result = 0;

		try {
			result = borrar(idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error eliminando diagnósticos durante"
									+ " actualización de registro", ex.getCause());
		}
		try {
			result = insertar(diagnosticos, idActuacion);
		}
		catch(HSCException ex) {
			throw new HSCException("SQLException: Error insertando diagnósticos durante"
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
		
		String query = "DELETE FROM Diagnosticos WHERE IdActuacion = ?";

		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			synchronized (this) {
				result = stmt.executeUpdate();
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: borrar()", ex);
		}
		finally {
			closeResources();
		}

		return result;
	}
	
	public ArrayList<Diagnostico> obtenerTodo() throws HSCException {
		
		ArrayList<Diagnostico> diagnosticos = new ArrayList<Diagnostico>();
		
		String query = "SELECT * FROM Diagnosticos";
		
		String queryIF = "SELECT Opciones, Label FROM InputFields WHERE Seccion = 'ESTA'";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			stmt = connection.prepareStatement(queryIF);
			
			ResultSet rsIF = stmt.executeQuery();
			
			String[] estadiajesDepresion = null;
			String[] estadiajesPsicosis  = null;
			
			while (rsIF.next()) {
				if (rsIF.getString("Label").equalsIgnoreCase("Estadiaje Depresión"))
					estadiajesDepresion = rsIF.getString("Opciones").split(";");
				
				else if (rsIF.getString("Label").equalsIgnoreCase("Estadiaje Psicosis"))
					estadiajesPsicosis = rsIF.getString("Opciones").split(";");
			}
			
			String[][] csvNames =
				{
					{ "", "Diagnóstico Principal", "Diagnóstico Secundario", "Diagnóstico No Psiquiatrico"
					}
					,
					{ "", "Estadiaje Depresión", "Estadiaje Psicosis"
					}
				};
			
			while (rs.next()) {
				Diagnostico diagnostico = new Diagnostico();
				
				diagnostico.setIdDiagnostico  (rs.getInt("IdDiagnostico"));
				diagnostico.setIdActuacion    (rs.getInt("IdActuacion"));
				diagnostico.setTipoDiagnostico(rs.getInt("TipoDiagnostico"));
				diagnostico.setPosCombo       (rs.getInt("Posicion"));
				
				if (rs.getInt("TipoDiagnostico") == 4 && rs.getInt("Posicion") == 1) {
					diagnostico.setCieDiagnostico(estadiajesDepresion[Integer.parseInt(rs.getString("cieDiagnostico")) - 1]);
				}
				else if (rs.getInt("TipoDiagnostico") == 4 && rs.getInt("Posicion") == 2) {
					diagnostico.setCieDiagnostico(estadiajesPsicosis[Integer.parseInt(rs.getString("cieDiagnostico")) - 1]);
				}
				else {
					diagnostico.setCieDiagnostico(rs.getString("cieDiagnostico"));					
				}
				
				if (diagnostico.getTipoDiagnostico() > 3) {
					diagnostico.setCsvDescripcion(csvNames[1][diagnostico.getPosCombo()]);
				}
				else {
					diagnostico.setCsvDescripcion(csvNames[0][diagnostico.getTipoDiagnostico()]);
				}
				
				diagnosticos.add(diagnostico);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerTodo()", ex);
		}
		finally {
			closeResources();
		}
		
		return diagnosticos;
	}
		
}
