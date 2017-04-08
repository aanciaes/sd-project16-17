/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.rest;

import api.Document;
import api.Endpoint;
import api.rest.IndexerServiceAPI;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import org.glassfish.jersey.client.ClientConfig;
import sys.storage.LocalVolatileStorage;

/**
 *
 * @author miguel
 */
public class IndexerServiceResources implements IndexerServiceAPI {

    private final LocalVolatileStorage storage = new LocalVolatileStorage();

    @Override
    public List<String> search(String keywords) {

        String[] split = keywords.split("\\+");

        List<String> request = Arrays.asList(split);
        List<Document> Sresponse = storage.search(request);
        List<String> finalResponse = new ArrayList<>();

        for (int i = 0; i < Sresponse.size(); i++) {
            String url = Sresponse.get(i).getUrl();
            finalResponse.add(i, url);
        }

        return finalResponse;
    }

    @Override
    public void add(String id, Document doc) {
        boolean status = storage.store(id, doc);
        if (!status) {
            throw new WebApplicationException(CONFLICT);
        }
        System.err.println(status ? "Document added successfully " : "An error occured. Document was not stored");
    }

    @Override
    public void remove(String id) {

        try {
            //Getting rendezVous url
            MulticastSocket socket = new MulticastSocket();

            byte[] input = ("rendezvous").getBytes();
            DatagramPacket packet = new DatagramPacket(input, input.length);

            packet.setAddress(InetAddress.getByName("238.69.69.69"));
            packet.setPort(6969);

            socket.send(packet);

            byte[] buffer = new byte[65536];
            DatagramPacket url_packet = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(url_packet);
                String rendezVousURL = new String(url_packet.getData(), 0, url_packet.getLength());
                
                //Creating a client to ask for Endpoints[] on rendezVous
                ClientConfig config = new ClientConfig();
                Client client = ClientBuilder.newClient(config);

                WebTarget target = client.target(rendezVousURL);
                Endpoint[] endpoints = target.path("/contacts")
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get(Endpoint[].class);
                
                boolean removed = false;
                //Removing the asked document from all indexers
                for (int i = 0; i < endpoints.length; i++) {
                    WebTarget newTarget = client.target(endpoints[i].getUrl());
                    Response response = newTarget.path("/remove/" + id).request().delete();
                    System.err.println(endpoints[i].getUrl() + "returned: " + response.getStatus());
                    if(response.getStatus()==204){
                        removed = true;
                    }
                }
                
                if(!removed)
                   throw new WebApplicationException(NOT_FOUND);
               
           
            } catch (SocketTimeoutException e) {
              
            }
        } catch (IOException ex) {

        }
    }

    @Override
    public void removeDoc(String id) {

        boolean status = storage.remove(id);
        if(!status)
            throw new WebApplicationException(NOT_FOUND);
     
        System.out.println(status ? "Document removed." : "Document doesn't exist.");
    }

}
