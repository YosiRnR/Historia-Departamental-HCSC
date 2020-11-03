package org.hcsc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.hcsc.models.InputFields;


public class DAOInputFields {
	private Connection connection = null;
	private PreparedStatement stmt = null;
	
	// CONSTRUCTOR //
	public DAOInputFields(Connection connection) {
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
			Logger.getLogger(DAOInputFields.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cerrando PreparedStatement()", ex.getCause());
		}
	}
	
	public ArrayList<InputFields> getInputFields() throws HSCException {
		ArrayList<InputFields> inputFields = new ArrayList<InputFields>();
		
		String query = "SELECT * FROM InputFields";

		try {
			stmt = connection.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				InputFields inFields = new InputFields();
				inFields.setSeccion   (rs.getString("Seccion"));
				inFields.setLabel     (rs.getString("Label"));
				inFields.setTipoCampo (rs.getString("TipoCampo"));
				inFields.setTotales   (rs.getInt("Totales"));
				inFields.setMin       (rs.getInt("Min"));
				inFields.setMax       (rs.getInt("Max"));
				inFields.setOpciones  (rs.getString("Opciones"));
				inFields.setPosicion  (rs.getInt("Posicion"));
				inFields.setCheckfield(rs.getInt("CheckField") == 0 ? false : true);
				inFields.setManual    (rs.getInt("Manual") == 0 ? false : true);
				inFields.setUrlManual (rs.getString("UrlManual"));
				
				inputFields.add(inFields);
			}
		}
		catch(SQLException ex) {
			Logger.getLogger(DAOInputFields.class).error("StackTrace: ", ex);
			throw new HSCException("SQLException: Error cargando los campos de la vista", ex);
		}
		finally {
			closeResources();
		}
		
		return inputFields;
	}

}
