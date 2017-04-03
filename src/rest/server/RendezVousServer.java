package rest.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RendezVousServer {

    private static final int TIMEOUT = 5000;

    //base url of this server - contains "http", ip address, port and base path
    private static URI baseUri;
    private static MulticastSocket socket;

    private static Map<String, Long> servers;

    public static void main(String[] args) throws Exception {
        servers = new ConcurrentHashMap<>();

        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        //Setting server up
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        baseUri = UriBuilder.fromUri(String.format("http://%s/", hostAddress)).port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new RendezVousResources());
        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST RendezVous Server ready @ " + baseUri);
        //

        //Creating Multicast Socket
        final InetAddress address_multi = InetAddress.getByName("238.69.69.69");

        if (!address_multi.isMulticastAddress()) {
            System.out.println("Use range : 224.0.0.0 -- 239.255.255.255");
            System.exit(1);
        }

        socket = new MulticastSocket(6969);
        socket.joinGroup(address_multi);

        //Creating keepAlive thread
        Thread cleanUp = new Thread(new Runnable() {
            public void run() {

                while (true) {
                    for (String key : servers.keySet()) {
                        if (System.currentTimeMillis() - servers.get(key) > TIMEOUT) {
                            deleteServer(key);
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {

                    }
                }
            }

        });

        cleanUp.start();

        //Waiting for a client request
        while (true) {
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            processMessages(packet, socket);
        }
    }

    /**
     * Processes the messages received in the multicast socket Checks whether or
     * not the message is meant to this server and if so, it replies with this
     * server location - ip address and port
     *
     * @param packet Datagram Packet send by someone and received in the
     * multicast socket
     * @param socket Multicast Socket
     */
    private static void register(DatagramPacket packet, MulticastSocket socket) {

        try {
            //check if multicast message is meant for this server 

            byte[] input = baseUri.toString().getBytes();
            DatagramPacket reply = new DatagramPacket(input, input.length);

            //set reply packet destination
            reply.setAddress(packet.getAddress());
            reply.setPort(packet.getPort());

            socket.send(reply);
            //else ignore message
        } catch (IOException ex) {
            System.err.println("Error processing message from client. No reply was sent");
        }
    }

    private static void processMessages(DatagramPacket response, MulticastSocket socket) {
        String request = new String(response.getData(), 0, response.getLength());
        String[] split = request.split("/");

        switch (split[0]) {
            case "RendezVousServer":
                register(response, socket);
                break;
            case "IAmAlive":
                processKeepAliveMessage(split[1]);
                break;

            default:
                break;
        }
    }

    private static void processKeepAliveMessage(String id) {
        servers.put(id, System.currentTimeMillis());
    }

    private static void deleteServer(String key) {
        servers.remove(key);

        for (int retry = 0; retry < 3; retry++) {

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            URI rendezVousAddr = UriBuilder.fromUri(baseUri).build();

            WebTarget target = client.target(rendezVousAddr);

            try {
                Response response = target.path("/contacts/" + key)
                        .request()
                        .delete();
                if (response.getStatus() == 204) {
                    break;
                }

            } catch (ProcessingException ex) {
                //
            }
        }

    }
}
