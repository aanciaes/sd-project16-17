/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soap.server;


/**
 *
 * @author rmamaral
 */

import javax.xml.ws.Endpoint;

public class RendezVousServer {
	public static void main(String[] args) throws Exception {

		int port = 9090;
		if (args.length > 0)
			port = Integer.parseInt(args[0]);

		String baseURI = String.format("http://0.0.0.0:%d/contacts", port);
		
		Endpoint.publish(baseURI, new RendezVousServiceImpl());

		System.err.println("SOAP RendezVous Server ready @ " + baseURI);
	}
}
