/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.soap;

import api.Document;
import api.soap.IndexerServiceAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import sys.storage.LocalVolatileStorage;

/**
 *
 * @author rmamaral
 */
@WebService(
        serviceName = IndexerServiceAPI.NAME,
        targetNamespace = IndexerServiceAPI.NAMESPACE,
        endpointInterface = IndexerServiceAPI.INTERFACE)

public class IndexerServiceServerImpl implements IndexerServiceAPI {

    private final LocalVolatileStorage storage = new LocalVolatileStorage();

    @Override
    public List<String> search(String keywords) throws InvalidArgumentException {
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
    public boolean add(Document doc) throws InvalidArgumentException {
        boolean status = storage.store(doc.id(), doc);
        System.err.println(status ? "Document added successfully " : "An error occured. Document was not stored");
        return status;
    }

    @Override
    public boolean remove(String id) throws InvalidArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
