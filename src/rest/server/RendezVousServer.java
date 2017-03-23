package rest.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RendezVousServer {

    //base url of this server - contains "http", ip address, port and base path
    private static URI baseUri;

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        //Setting server
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

        MulticastSocket socket = new MulticastSocket(6969);
        socket.joinGroup(address_multi);

        //Waiting for a client request
        while (true) {
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            processMessage(packet, socket);
        }
    }

    /**
     * Processes the messages received in the multicast socket
     * Checks whether or not the message is meant to this server and if so, it replies with this server location - ip address and port
     *
     * @param packet Datagram Packet send by someone and received in the multicast socket
     * @param socket Multicast Socket
     */
    private static void processMessage(DatagramPacket packet, MulticastSocket socket) {

        try {
            if (new String(packet.getData(), 0, packet.getLength()).equals("RendezVousServer")) { //check if multicast message is meant for this server 

                byte[] input = baseUri.toString().getBytes();
                DatagramPacket reply = new DatagramPacket(input, input.length);

                //set reply packet destination
                reply.setAddress(packet.getAddress());
                reply.setPort(packet.getPort());

                socket.send(reply);
            }//else ignore message
        } catch (IOException ex) {
            System.err.println("Error processing message from client. No reply was sent");
        }
    }
}
