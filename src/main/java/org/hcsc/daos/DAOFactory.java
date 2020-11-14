package org.hcsc.daos;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hcsc.db.DataSource;
import org.hcsc.exceptions.HSCException;


public class DAOFactory {	
	private Connection connection = null;
	

	// CONSTRUCTOR NUEVO CON POOL DE CONEXIONES USANDO HIKARICP //
	public DAOFactory() throws HSCException {
		try {
			connection = DataSource.getConnection();
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOFactory.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error obteniendo conexión", ex.getCause());
		}
	}
// CONSTRUCTOR ANTIGUO SIN POOL DE CONEXIONES //
//	public DAOFactory() {
//		try {
//			Class.forName("net.sourceforge.jtds.jdbc.Driver");
//			connection = DriverManager.getConnection(connectionUrl);
//		}
//		catch(ClassNotFoundException ex) {
//			ex.printStackTrace();
//		}
//		catch(SQLException ex) {
//			ex.printStackTrace();
//			try {
//				if (connection != null) {
//					connection.close();
//					connection = null;
//				}
//			}
//			catch(SQLException ex2) {
//				ex2.printStackTrace();
//			}
//		}
//	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOFactory.class).error("SQLException: Error cerrando conexión", ex);
//			ex.printStackTrace();
		}		
	}
	
	public void setAutoCommit(boolean autoCommit) {
		try {
			connection.setAutoCommit(autoCommit);
		} catch (SQLException ex) {
			Logger.getLogger(DAOFactory.class).error("SQLException: Error estableciendo AutoCommit", ex);
//			ex.printStackTrace();
		}
	}
	
	public void commit() {
		try {
			connection.commit();
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOFactory.class).error("SQLException: Error haciendo commit", ex);
//			ex.printStackTrace();
			this.close();
		}
	}
	
	public void rollback() {
		try {
			connection.rollback();
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOFactory.class).error("SQLException: Error cerrando rollback", ex);
//			ex.printStackTrace();
			this.close();
		}
	}
	
	public DAOPacientes crearDAOPacientes() {
		return new DAOPacientes(connection);
	}
	
	public DAOInputFields crearDAOInputFields() {
		return new DAOInputFields(connection);
	}

	public DAOCitas crearDAOCitas() {
		return new DAOCitas(connection);
	}

	public DAOActuaciones crearDAOActuaciones() {
		return new DAOActuaciones(connection);
	}
	
	public DAODatosClinicos crearDAODatosClinicos() {
		return new DAODatosClinicos(connection);
	}
	
	public DAODiagnosticos crearDAODiagnosticos() {
		return new DAODiagnosticos(connection);
	}
	
	public DAOTratamientos crearDAOTratamientos() {
		return new DAOTratamientos(connection);
	}
	
	public DAOTratamientosMup crearDAOTratamientosMup() {
		return new DAOTratamientosMup(connection);
	}
	
	public DAOSitClinicaDAS crearDAOSitClinicaDAS() {
		return new DAOSitClinicaDAS(connection);
	}
	
	public DAOProgsUnidsEspProcs crearDAOProgsUnidsEspProcs() {
		return new DAOProgsUnidsEspProcs(connection);
	}
	
	public DAOAccesos crearDAOAccesos() {
		return new DAOAccesos(connection);
	}
}
