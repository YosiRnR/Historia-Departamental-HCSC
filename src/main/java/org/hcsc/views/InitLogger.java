package org.hcsc.views;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hcsc.daos.DAOFactory;
import org.hcsc.exceptions.HSCException;

/**
 * Servlet implementation class InitLogger
 */
public class InitLogger extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitLogger() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
    	ServletContext context = config.getServletContext();

    	String log4jFile = context.getInitParameter("log4j-config-location");
    	
    	log4jFile = context.getRealPath("") + File.separator + log4jFile;
    	
    	if (new File(log4jFile).isFile()) {
    		PropertyConfigurator.configure(log4jFile);
    	}
    	else {
    		BasicConfigurator.configure();
    	}
    	
    	try {
	    	DAOFactory initPool = new DAOFactory();
	    	initPool.close();
    	}
    	catch(HSCException ex) {
    		Logger.getLogger(InitLogger.class).warn("ERROR CREANDO POOL DE CONEXIONES: StackTrace:", ex);
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
