/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.server;

import api.Document;
import api.IndexerServiceAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import sys.storage.LocalVolatileStorage;

/**
 *
 * @author miguel
 */
public class IndexerServiceResources implements IndexerServiceAPI {

    private final LocalVolatileStorage storage = new LocalVolatileStorage();

    @Override
    public List<String> search(String keywords) {
        System.err.println(keywords);
        String[] split = keywords.split("\\+");
        System.err.println(split);
        for(int i = 0; i < split.length; i++){
            System.out.println(split[i]);
        }
        List<String> request = Arrays.asList(split);
        List<Document> Sresponse = storage.search(request);
        List<String> finalResponse = new ArrayList<>();
        
        for(int i = 0; i < Sresponse.size(); i++){
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
