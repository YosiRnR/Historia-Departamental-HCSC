package org.hcsc.models;


public class InputFields {
	private int idInput;
	private String seccion;
	private String label;
	private String tipoCampo;
	private int totales;
	private int min;
	private int max;
	private String opciones;
	private int posicion;
	private boolean checkfield;
	private boolean manual;
	private String urlManual;
	
	/** DEFAULT CONSTRUCTOR **/
	public InputFields() {
		idInput    = -1;
		seccion    = "";
		label      = "";
		tipoCampo  = "";
		totales    = -1;
		min        = -1;
		max        = -1;
		opciones   = "";
		posicion   = -1;
		checkfield = false;
		manual     = false;
		urlManual  = "";
	}

	/** PARAMETRIC CONSTRUCTOR **/
	public InputFields(int idInput, String seccion, String label, String tipoCampo, int totales, int min, int max,
			String opciones, int posicion, boolean checkfield, boolean manual, String urlManual) {
		super();
		this.idInput    = idInput;
		this.seccion    = seccion;
		this.label      = label;
		this.tipoCampo  = tipoCampo;
		this.totales    = totales;
		this.min        = min;
		this.max        = max;
		this.opciones   = opciones;
		this.posicion   = posicion;
		this.checkfield = checkfield;
		this.manual     = manual;
		this.urlManual  = urlManual;
	}
	
	

	/** GETTERS & SETTERS **/
	public int getIdInput() {
		return idInput;
	}
	public void setIdInput(int idInput) {
		this.idInput = idInput;
	}

	public String getSeccion() {
		return seccion;
	}
	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getTipoCampo() {
		return tipoCampo;
	}
	public void setTipoCampo(String tipoCampo) {
		this.tipoCampo = tipoCampo;
	}

	public int getTotales() {
		return totales;
	}
	public void setTotales(int totales) {
		this.totales = totales;
	}

	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}

	public String getOpciones() {
		return opciones;
	}
	public void setOpciones(String opciones) {
		this.opciones = opciones;
	}

	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

	public boolean isCheckfield() {
		return checkfield;
	}
	public void setCheckfield(boolean checkfield) {
		this.checkfield = checkfield;
	}

	public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public String getUrlManual() {
		return urlManual;
	}
	public void setUrlManual(String urlManual) {
		this.urlManual = urlManual;
	}

}
