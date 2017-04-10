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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.glassfish.jersey.client.ClientConfig;
import sys.storage.LocalVolatileStorage;

/**
 *
 * @author rmamaral
 */
@WebService(
        serviceName = IndexerAPI.NAME,
        targetNamespace = IndexerAPI.NAMESPACE,
        endpointInterface = IndexerAPI.INTERFACE)

public class IndexerServiceServerImpl implements IndexerAPI {

    private final LocalVolatileStorage storage = new LocalVolatileStorage();
    private String rendezUrl;

    @Override
    public List<String> search(String keywords) throws InvalidArgumentException {
        try {

            String[] split = keywords.split("\\+");

            List<String> request = Arrays.asList(split);
            List<Document> Sresponse = storage.search(request);
            List<String> finalResponse = new ArrayList<>();

            for (int i = 0; i < Sresponse.size(); i++) {
                String url = Sresponse.get(i).getUrl();
                finalResponse.add(i, url);
            }

            return finalResponse;
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

            WebTarget target = client.target(rendezUrl);
            Endpoint[] endpoints = target.path("/")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Endpoint[].class);

            boolean removed = false;
            for (int i = 0; i < endpoints.length; i++) {
                Endpoint endpoint = endpoints[i];
                String url = endpoint.getUrl();
                Map<String, Object> map = endpoint.getAttributes();

                if (map.containsKey("type")) {
                    if (map.get("type").equals("soap")) {
                        if (removeSoap(id, url)) {
                            removed = true;
                        }

                    } else if (map.get("type").equals("rest")) {
                        if (removeRest(id, url)) {
                            removed = true;
                        }

                    }

                } else {
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

    void setUrl(String rendezVousURL
    ) {
        rendezUrl = rendezVousURL;
    }

    @Override
    public boolean removeDoc(String id
    ) {
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

    private boolean removeRest(String id, String url) {

        try {
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            WebTarget newTarget = client.target(url);
            Response response = newTarget.path("/remove/" + id).request().delete();

            if (response.getStatus() == 204) {
                return true;
            }
        } catch (WebApplicationException e) {
            return false;
        }
        return false;
    }

}
