/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.soap;

import javax.xml.namespace.QName;

/**
 *
 * @author rmamaral
 */
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import api.soap.RendezVousAPI;

import static api.soap.RendezVousAPI.*;
import java.net.URL;


public class ListEndpoints {
	static final QName QNAME = new QName( NAMESPACE, NAME);

	public static void main(String[] args) throws Exception {

			URL wsURL = new URL("http://localhost:9090/contacts?wsdl");

			
			Service service = Service.create( wsURL, QNAME);
			
			RendezVousAPI contacts = service.getPort( RendezVousAPI.class );
			
			System.out.println( Arrays.asList(contacts.endpoints() ) );
	}
}
