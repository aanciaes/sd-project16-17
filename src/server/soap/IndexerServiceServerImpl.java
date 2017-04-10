/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.soap;

import api.Document;
import api.Endpoint;
import api.soap.IndexerAPI;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.glassfish.jersey.client.ClientConfig;
import sys.storage.LocalVolatileStorage;

@WebService(
        serviceName = IndexerAPI.NAME,
        targetNamespace = IndexerAPI.NAMESPACE,
        endpointInterface = IndexerAPI.INTERFACE)

public class IndexerServiceServerImpl implements IndexerAPI {

    private final LocalVolatileStorage storage = new LocalVolatileStorage(); //Documents "database"
    private String rendezUrl; //RendezVous location

    @Override
    public List<String> search(String keywords) throws InvalidArgumentException {
        try {
            //Split query words
            String[] split = keywords.split("\\+");

            //Convert to list
            List<String> query = Arrays.asList(split);
            List<Document> documents = storage.search(query);
            List<String> response = new ArrayList<>();

            for (int i = 0; i < documents.size(); i++) {
                String url = documents.get(i).getUrl();
                response.add(i, url);
            }

            return response;
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }
    }

    @Override
    public boolean add(Document doc) throws InvalidArgumentException {
        try {
            boolean status = storage.store(doc.id(), doc);
            System.err.println(status ? "Document added successfully " : "An error occured. Document was not stored");
            return status;
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }
    }

    @Override
    public boolean remove(String id) throws InvalidArgumentException {
        try {

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            Endpoint[] endpoints = null;
            for (int retry = 0; retry < 3; retry++) {
                try {
                    WebTarget target = client.target(rendezUrl);
                    endpoints = target.path("/")
                            .request()
                            .accept(MediaType.APPLICATION_JSON)
                            .get(Endpoint[].class);
                    if (endpoints != null) {
                        break;
                    }
                } catch (ProcessingException ex) {
                    //retry up to three times
                }
            }

            boolean removed = false;
            //Removing the asked document from all indexers
            for (int i = 0; i < endpoints.length; i++) {

                Endpoint endpoint = endpoints[i];
                String url = endpoint.getUrl();
                Map<String, Object> map = endpoint.getAttributes();

                //Defensive progamming checks if server is soap or rest and ignores other types
                if (map.containsKey("type")) {
                    if (map.get("type").equals("soap")) {
                        if (removeSoap(id, url)) {
                            removed = true;
                        }
                    }
                    if (map.get("type").equals("rest")) {
                        if (removeRest(id, url)) {
                            removed = true;
                        }
                    }
                } else { //if no type tag exists - treat as rest server
                    if (removeRest(id, url)) {
                        removed = true;
                    }
                }
            }
            return removed;
        } catch (Exception e) {
            throw new InvalidArgumentException();

        }
    }

    public void setUrl(String rendezVousURL) {
        this.rendezUrl = rendezVousURL;
    }

    @Override
    public boolean removeDoc(String id) {
        return storage.remove(id);
    }

    public boolean removeSoap(String id, String url) {

        try {
            URL wsURL = new URL(url);
            QName QNAME = new QName(NAMESPACE, NAME);
            Service service = Service.create(wsURL, QNAME);
            IndexerAPI indexer = service.getPort(IndexerAPI.class);
            return indexer.removeDoc(id);
        } catch (MalformedURLException | InvalidArgumentException ex) {
            return false;
        }
    }

    private boolean removeRest(String id, String url) throws WebApplicationException {
        for (int retry = 0; retry < 3; retry++) {
            try {
                ClientConfig config = new ClientConfig();
                Client client = ClientBuilder.newClient(config);
                WebTarget target = client.target(url);
                Response response = target.path("/remove/" + id).request().delete();

                //return response.getStatus();
                if (response.getStatus() == 204) {
                    return true;
                }
            } catch (ProcessingException x) {
                //retry method up to three times
            }
        }
        return false;
    }

}
