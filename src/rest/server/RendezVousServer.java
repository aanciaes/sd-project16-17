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

    private static String hostname;
    private static int port;
	public static void main(String[] args) throws Exception {
		port = 8080;
		if( args.length > 0)
			port = Integer.parseInt(args[0]);
                
		hostname = "http://" + InetAddress.getLocalHost().getHostAddress();
                
		URI baseUri = UriBuilder.fromUri(hostname + "/").port(port).build();

		ResourceConfig config = new ResourceConfig();
		config.register( new RendezVousResources() );
		
		JdkHttpServerFactory.createHttpServer(baseUri, config);

		System.err.println("REST RendezVous Server ready @ " + baseUri + " : local IP = " + InetAddress.getLocalHost().getHostAddress());
                
                //Creating Multicast Socket
                
		final InetAddress address_multi = InetAddress.getByName("238.69.69.69");
                
		if(!address_multi.isMulticastAddress()) {
			System.out.println( "Use range : 224.0.0.0 -- 239.255.255.255");
			System.exit(1);
		}

		MulticastSocket socket = new MulticastSocket(6969);
		socket.joinGroup(address_multi);

		//Waiting for a client request
		while(true) {
			byte[] buffer = new byte[65536];
			DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
			socket.receive(packet);
			processMessage (packet, socket);
		}
	}

    private static void processMessage(DatagramPacket packet, MulticastSocket socket) throws IOException {
      
        if(new String (packet.getData(), 0, packet.getLength()).equals("RendezVousServer")){
              
			String address = String.format("%s:%d/contacts",hostname,port);

			byte[] input = new String (address).getBytes();
			DatagramPacket reply = new DatagramPacket( input, input.length );
			reply.setAddress(packet.getAddress());
			reply.setPort(packet.getPort());
			socket.send(reply);
		}
	}
}
