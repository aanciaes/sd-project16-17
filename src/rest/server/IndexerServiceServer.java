/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.server;

import api.Endpoint;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
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

    private static final String MESSAGE = "RendezVousServer";
    private static final int TIMEOUT = 1000;

    private static URI baseUri;

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        //Set up server
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        baseUri = UriBuilder.fromUri("http://" + hostAddress + "/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new RendezVousResources());
        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST IndexerService Server ready @ " + baseUri);
        //

        //Discovering RendezVousServer
        //Setting up multicast request.
        final int portMulti = 6969;
        final InetAddress multiAddress = InetAddress.getByName("238.69.69.69");
        if (!multiAddress.isMulticastAddress()) {
            System.out.println("Use range : 224.0.0.0 -- 239.255.255.255");
        }

        MulticastSocket socket = new MulticastSocket();

        //Send multicast request with MESSAGE - Send up to three times
        for (int retry = 0; retry < 3; retry++) {
            byte[] input = (MESSAGE).getBytes();
            DatagramPacket packet = new DatagramPacket(input, input.length);

            packet.setAddress(multiAddress);
            packet.setPort(portMulti);

            socket.send(packet);

            byte[] buffer = new byte[65536];
            DatagramPacket url_packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(TIMEOUT);

            try {
                socket.receive(url_packet);
                String rendezVousURL = new String(url_packet.getData(), 0, url_packet.getLength());
                
                int status=registerRendezVous(rendezVousURL);
                if (status == 204) {
                    System.err.println("Service registered succesfully");
                    break;
                }
                System.err.println("An error occured while registering on the RendezVousServer. HTTP Error code: " + status);
            } catch (SocketTimeoutException e) {
                //No server responded within given time
            }
        }
    }

    private static int registerRendezVous(String url) {

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        URI rendezVousAddr = UriBuilder.fromUri(url).build();
       
        WebTarget target = client.target(rendezVousAddr);
   
        Endpoint endpoint = new Endpoint(baseUri.toString(), Collections.emptyMap());
        
        Response response = target.path("/contacts/" + endpoint.generateId())
                .request()
                .post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));
        
        return response.getStatus();
    }
}
