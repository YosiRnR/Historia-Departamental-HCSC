package org.hcsc.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Node;
import org.apache.log4j.Logger;
import org.hcsc.exceptions.HSCException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;



public class LoginChecker {
	
	private static final String servletLDAPUser = "05667997S";
	private static final String servletLDAPPass = "Temporal05";
	
	private static final String SOAPServiceEndPointURL = "https://gestionai.salud.madrid.org/ServiciosGestionAI/ServicioDa.asmx";
	private static final String SOAPAction = "https://gestionai.salud.madrid.org/ValidarUsuario";
	
	
	public static DALoginState checkUserPassword(String user, String password) throws HSCException {
		DALoginState loginState     = new DALoginState();
		String validarUsuarioResult = null;
		
		String SOAPMessage =
				"<?xml version='1.0' encoding='utf-8'?>\n" +
				"<soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
				"xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
				"<soap:Body>\n" +
				"<ValidarUsuario xmlns='https://gestionai.salud.madrid.org/'>\n" +
				"<userLogin>" + servletLDAPUser + "</userLogin>\n" +
				"<userPassword>" + servletLDAPPass + "</userPassword>\n" +
				//"<userPassword>Temporal05</userPassword>\n" +
				"<login>" + user + "</login>\n" +
				"<password>" + password + "</password>\n" +
				"</ValidarUsuario>\n" +
				"</soap:Body>\n" +
				"</soap:Envelope>";
		
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			/** Crea una instancia de la implementacion por defecto para crear mensajes SOAP (SOAP 1.1)
			 **/
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapRequestMessage;
			
			try {
				soapRequestMessage = messageFactory.createMessage(new MimeHeaders(),
						new ByteArrayInputStream(SOAPMessage.getBytes(Charset.forName("UTF-8"))));
				
				MimeHeaders headers = soapRequestMessage.getMimeHeaders();
				headers.addHeader("SOAPAction", SOAPAction);
				
				soapRequestMessage.saveChanges();
				
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				soapRequestMessage.writeTo(output);
				
				SOAPMessage soapResponseMessage = soapConnection.call(soapRequestMessage, SOAPServiceEndPointURL);
				soapResponseMessage.writeTo(output);
				Logger.getLogger(LoginChecker.class).info("SOAP RESPONSE MESSAGE:\n" + output.toString());
				
				soapConnection.close();
				
				/** Parse XML response received from service **/
				Source xmlSource = soapResponseMessage.getSOAPPart().getContent();
				
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = null;
				
				try {
					transformer = tf.newTransformer();
				}
				catch(TransformerConfigurationException e) {
					Logger.getLogger(LoginChecker.class)
							.error("TransformerConfigurationException: StackTrace: ", e);
				}
				
				DOMResult domResult = new DOMResult();
				try {
					transformer.transform(xmlSource, domResult);
				}
				catch(TransformerException e) {
					Logger.getLogger(LoginChecker.class)
							.error("TransformerException: StackTrace: ", e);
				}
				
				Document document = (Document)domResult.getNode();
				document.getDocumentElement().normalize();
				
				NodeList nodeList = document.getElementsByTagName("ValidarUsuarioResult");
				
				for (int index = 0; index < nodeList.getLength(); index++) {
					Node node = nodeList.item(index);
					
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						validarUsuarioResult = node.getTextContent();
					}
				}
			}
			catch(IOException e) {
				Logger.getLogger(LoginChecker.class)
						.error("IOException: StackTrace: ", e);
			}
		}
		catch(UnsupportedOperationException e) {
			Logger.getLogger(LoginChecker.class)
						.error("UnsupportedOperationException: StackTrace: ", e);
		}
		catch(SOAPException e) {
//			Logger.getLogger(LoginChecker.class)
//						.error("SOAPException: StackTrace: ", e);
			throw new HSCException("SOAPException", e);
		}
		
		loginState.setState(validarUsuarioResult);
		loginState.setValidate(validarUsuarioResult.equalsIgnoreCase("LOGIN_OK") ? true : false);
//		loginState.setState("LOGIN_OK");
//		loginState.setValidate(true);
		
		return loginState;
	}	
}
