/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.soap;

/**
 *
 * @author rmamaral
 */
import static api.soap.RendezVousAPI.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import api.Endpoint;
import api.soap.RendezVousAPI;

public class RegisterEndpoint {

	public static void main(String[] args) throws IOException {

		try {
			URL wsURL = new URL(String.format("http://localhost:9090/contacts?wsdl"));

			QName qname = new QName( NAMESPACE, NAME);
			
			Service service = Service.create( wsURL, qname);
			
			RendezVousAPI contacts = service.getPort( RendezVousAPI.class );

			Endpoint endpoint = new Endpoint("http://some-server-endpoint-url", Collections.emptyMap());

			contacts.register(endpoint.generateId(), endpoint);

		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
		}

	}
}