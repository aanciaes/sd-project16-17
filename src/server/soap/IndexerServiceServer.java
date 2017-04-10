/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.soap;

import api.soap.RendezVousAPI;
import static api.soap.RendezVousAPI.NAME;
import static api.soap.RendezVousAPI.NAMESPACE;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import org.glassfish.jersey.client.ClientConfig;

/**
 *
 * @author rmamaral
 */
public class IndexerServiceServer {

    private static final String MESSAGE = "rendezvous";
    private static final String HEARTBEATMESSAGE = "IAmAlive";
    private static final int TIMEOUT = 1000;
    private static api.Endpoint endpoint;
    private static URI rendezVousAddr;

    public static void main(String[] args) throws Exception {

        int port = 8080;
        if (args.length > 0) {
            rendezVousAddr = UriBuilder.fromUri(args[0]).build();
        }

        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        String baseURI = String.format("http://0.0.0.0:%d/indexer", port);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", "soap");
        endpoint = new api.Endpoint(UriBuilder.fromUri(String.format("http://%s/indexer", hostAddress)).port(port).build().toString(),attributes);
        IndexerServiceServerImpl indexerServiceImpl = new IndexerServiceServerImpl();
        Endpoint.publish(baseURI, indexerServiceImpl);

        System.err.println("SOAP IndexerService Server ready @ " + baseURI);

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

                int status = registerRendezVous(rendezVousURL);
                if (status == 204) {
                    indexerServiceImpl.setUrl(rendezVousURL);
                    System.err.println("Service registered succesfully");
                    break;
                }
                System.err.println("An error occured while registering on the RendezVousServer. HTTP Error code: " + status);

            } catch (SocketTimeoutException e) {
                //No server responded within given time
            }
        }

        //Creating keepAlive thread
        Thread heartbeat = new Thread(new Runnable() {
            public void run() {

                while (true) {

                    try {
                        MulticastSocket socket = new MulticastSocket();

                        byte[] input = (HEARTBEATMESSAGE + "/" + endpoint.generateId()).getBytes();
                        DatagramPacket packet = new DatagramPacket(input, input.length);
                        packet.setAddress(InetAddress.getByName("238.69.69.69"));
                        packet.setPort(6969);

                        socket.send(packet);
                        Thread.sleep(3000);

                    } catch (IOException | InterruptedException ex) {

                    }
                }
            }

        });

        heartbeat.start();

    }

    private static int registerRendezVous(String rendezVousURL) {

        for (int retry = 0; retry < 3; retry++) {

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            rendezVousAddr = UriBuilder.fromUri(rendezVousURL).build();

            WebTarget target = client.target(rendezVousAddr);

            try {
                Response response = target.path("/" + endpoint.generateId())
                        .request()
                        .post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));
                return response.getStatus();
            } catch (ProcessingException ex) {
                //
            }
        }
        return 0;
    }

}
