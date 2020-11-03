package org.hcsc.daos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hcsc.exceptions.FileIOException;
import org.hcsc.models.ComboOption;


public class DAOCiePsiquiatria {
	protected final String XML_PATH  = "org/hcsc/sql/";
	protected final int LIMIT_RESULT = 10;
	
	public DAOCiePsiquiatria() {
	}
	
	
	public ArrayList<ComboOption> metaBuscarCIE_XML(String pPattern) throws FileIOException {
		InputStream input = null;
		ArrayList<ComboOption> combos = new ArrayList<ComboOption>();
		
		try {		
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_PATH + "cie10-psiquiatria.properties");
			
			Properties props = new Properties();
			props.load(new InputStreamReader(input, "UTF-8"));

			Enumeration<?> enumKeys = props.propertyNames();
			
			Pattern pattern = Pattern.compile(pPattern, Pattern.CASE_INSENSITIVE);
			
			while (enumKeys.hasMoreElements()) {
				String key      = (String) enumKeys.nextElement();
				String valor    = props.getProperty(key);
				
				/** Buscar primero por codigo CIE10... **/
				Matcher matcher = pattern.matcher(key);

				if (matcher.find()) {
					ComboOption co = new ComboOption(key,  key + " " + valor);
					combos.add(co);
				}
				else {
					/** ...si no, buscar por codigo CIE10 + Descripcion **/
					matcher = pattern.matcher(key + " " + valor);
					if (matcher.find()) {
						ComboOption co = new ComboOption(key,  key + " " + valor);
						combos.add(co);
					}
				}
			}
			
			Collections.sort(combos);
		}
		catch(IOException e) {
			throw new FileIOException(e, "#ERROR BUSCANDO EL CODIGO CIE SEGUN EL PATRON: " + pPattern + " (IO Error)#");
		}
		finally {
			try {
				if (input != null) {
					input.close();
				}
			}
			catch (IOException e) {
				throw new FileIOException(e, "Error cerrando el fichero XML de codigos CIE Psiquiatricos");
			}
		}
		
		return combos;
	}
	
	
	public ComboOption getCIE_XML(String pPattern) throws FileIOException {
		InputStream inStream = null;
		ComboOption returnCombo  = null;
		try {
			inStream = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(XML_PATH + "cie10-psiquiatria.properties");
			
			Properties props = new Properties();
			props.load(new InputStreamReader(inStream, "UTF-8"));

			Enumeration<?> enumKeys = props.propertyNames();
			
			Pattern pattern = Pattern.compile(pPattern, Pattern.CASE_INSENSITIVE);
			
			while (enumKeys.hasMoreElements()) {
				String key      = (String) enumKeys.nextElement();
				String valor    = props.getProperty(key);
				
				/** Buscar primero por codigo CIE10... **/
				Matcher matcher = pattern.matcher(key);

				if (matcher.matches()) {
					returnCombo = new ComboOption(key,  key + " " + valor);
					break;
				}
			}
		}
		catch(IOException e) {
			throw new FileIOException(e, "#ERROR BUSCANDO EL CODIGO CIE SEGUN EL PATRON: " + pPattern + " (IO Error)#");
		}
		finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			}
			catch (IOException e) {
				throw new FileIOException(e, "Error cerrando el fichero XML de codigos CIE Psiquiatricos");
			}
		}
		
		return returnCombo;
	}
		
}
