package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.DatosClinicos;


public class DAODatosClinicos {
	private Connection connection  = null;
	private PreparedStatement stmt = null;
	
	public DAODatosClinicos(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * 
	 * @param Ninguno
	 * @return Cierra los recursos abiertos por el Dao (PreparedStatement) 
	 * @throws SQLException
	 */
	private void closeResources() {
		try {
			stmt.close();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}

	
	public DatosClinicos obtenerPorIdActuacion(int idActuacion) {
		DatosClinicos datosClinicos = null;
		
		String query = "SELECT * FROM DatosClinicos WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt(1, idActuacion);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				datosClinicos = new DatosClinicos();
				datosClinicos.setIdActuacion(rs.getInt("IdActuacion"));
				datosClinicos.setIdPaciente(rs.getInt("IdPaciente"));
				datosClinicos.setAntecedentes(rs.getString("Antecedentes"));
				datosClinicos.setEnfermedadActual(rs.getString("HistoriaActual"));
				datosClinicos.setEvolucionComentarios(rs.getString("EvolucionComentarios"));
			}
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		finally {
			closeResources();
		}
		
		return datosClinicos;
	}

	/**
	 * 
	 * @param datosClinicos
	 * @return
	 * @throws HSCException
	 **/
	public int insertar(DatosClinicos datosClinicos) throws HSCException {
		int opResult = 0;

		String query = "INSERT INTO DatosClinicos (IdActuacion, IdPaciente, Antecedentes,"
					+ " HistoriaActual, EvolucionComentarios) VALUES(?, ?, ?, ?, ?)";
		
			
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setInt   (1, datosClinicos.getIdActuacion());
			stmt.setInt   (2, datosClinicos.getIdPaciente());
			stmt.setString(3, datosClinicos.getAntecedentes());
			stmt.setString(4, datosClinicos.getEnfermedadActual());
			stmt.setString(5, datosClinicos.getEvolucionComentarios());
			
			synchronized(this) {
				opResult = stmt.executeUpdate();				
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAODiagnosticos.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: obtenerPorIdActuacion()", ex.getCause());
		}
		finally {
			closeResources();
		}			
		
		return opResult;
	}
	
	/**
	 * 
	 * @param datosClinicos
	 * @return
	 * @throws HSCException
	 **/
	public int actualizar(DatosClinicos datosClinicos) throws HSCException {
		int opResult = 0;
		
		String query = "UPDATE DatosClinicos SET Antecedentes = ?, HistoriaActual = ?,"
						+ " EvolucionComentarios = ? WHERE IdActuacion = ?";
		
		try {
			stmt = connection.prepareStatement(query);
			
			stmt.setString(1, datosClinicos.getAntecedentes());
			stmt.setString(2, datosClinicos.getEnfermedadActual());
			stmt.setString(3, datosClinicos.getEvolucionComentarios());
			stmt.setInt   (4, datosClinicos.getIdActuacion());
			
			synchronized(this) {					
				opResult = stmt.executeUpdate();
			}				
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			closeResources();
		}
			
		return opResult;
	}
	
	/**
	 * 
	 * @return
	 * @throws HSCException
	 **/
	public ArrayList<DatosClinicos> obtenerTodo() throws HSCException {
		ArrayList<DatosClinicos> datosClinicosList = new ArrayList<DatosClinicos>();
		
		String query = "SELECT IdActuacion, IdPaciente FROM DatosClinicos";
		
		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				DatosClinicos datosClinicos = new DatosClinicos();
				
				datosClinicos.setIdActuacion(rs.getInt("IdActuacion"));
				datosClinicos.setIdPaciente(rs.getInt("IdPaciente"));
//				datosClinicos.setAntecedentes(rs.getString("Antecedentes"));
//				datosClinicos.setEnfermedadActual(rs.getString("HistoriaActual"));
//				datosClinicos.setEvolucionComentarios(rs.getString("EvolucionComentarios"));
				
				datosClinicosList.add(datosClinicos);
			}
		}
		catch(SQLException e) {
		}
		finally {
			closeResources();
		}
		
		return datosClinicosList;
	}	
	
}
