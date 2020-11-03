package org.hcsc.exceptions;


public class FileIOException extends HSCException {

	private static final long serialVersionUID = 1L;
	private static final String exName  = "FileIOException";
	private static final String message = "Error de entrada/salida de ficheros";

	public FileIOException(Throwable cause, String info) {
		super(message + " " + info, exName, cause);
	}

	public FileIOException(Throwable cause) {
		super(message, exName, cause);
	}		
}
