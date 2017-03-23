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

    private static String hostname;
    private static int port;

    private static String message = "RendezVousServer";
    private static int TIMEOUT = 1000;

    public static void main(String[] args) throws Exception {
        port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        hostname = "http://" + InetAddress.getLocalHost().getHostAddress();

        URI baseUri = UriBuilder.fromUri(hostname + "/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new RendezVousResources());

        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST IndexerService Server ready @ " + baseUri + " : local IP = " + InetAddress.getLocalHost().getHostAddress());

        final int portMulti = 6969;
        final InetAddress address = InetAddress.getByName("238.69.69.69");
        if (!address.isMulticastAddress()) {
            System.out.println("Use range : 224.0.0.0 -- 239.255.255.255");
        }

        MulticastSocket socket = new MulticastSocket();

        byte[] input = (message).getBytes();
        DatagramPacket packet = new DatagramPacket(input, input.length);
        packet.setAddress(address);
        packet.setPort(portMulti);
        socket.send(packet);

        byte[] buffer = new byte[65536];
        DatagramPacket url_packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(TIMEOUT);

        while (true) {
            try {
                socket.receive(url_packet);
                String urlRegister = new String(url_packet.getData(), 0, url_packet.getLength());
                registerRendezVous(urlRegister);
            } catch (SocketTimeoutException e) {
                //No more servers respond to client request
                break;
            }
        }

    }

    private static void registerRendezVous(String url) {

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        URI baseURI = UriBuilder.fromUri(url).build();

        WebTarget target = client.target(baseURI);

        Endpoint endpoint = new Endpoint(hostname + ":" + port + "/", Collections.emptyMap());

        Response response = target.path("/" + endpoint.generateId())
                .request()
                .post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));

        System.out.println(response.getStatus());
    }

}
