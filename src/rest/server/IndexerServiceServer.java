/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.server;

import api.Endpoint;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author miguel
 */
public class IndexerServiceServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        String url = "http://" + InetAddress.getLocalHost().getHostAddress() + "/";
        System.err.println(url);
        URI baseUri = UriBuilder.fromUri(url).port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new RendezVousResources());

        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST IndexerService Server ready @ " + baseUri+ " : local IP = " + InetAddress.getLocalHost().getHostAddress());
        System.err.println(baseUri.toString());
        registerRendezVous(baseUri.toString());
    }

    private static void registerRendezVous(String url) {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        URI baseURI = UriBuilder.fromUri("http://172.17.0.2:8080/").build();

        WebTarget target = client.target(baseURI);
        
        Endpoint endpoint = new Endpoint(url, Collections.emptyMap());
        
        Response response = target.path("/contacts/" + endpoint.generateId())
                .request()
                .post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));

        System.out.println(response.getStatus());
    }

}
