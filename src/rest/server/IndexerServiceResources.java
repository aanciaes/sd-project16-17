/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.server;

import api.Document;
import api.IndexerServiceAPI;
import java.util.List;
import sys.storage.LocalVolatileStorage;

/**
 *
 * @author miguel
 */
public class IndexerServiceResources implements IndexerServiceAPI {

    private LocalVolatileStorage storage = new LocalVolatileStorage();

    @Override
    public List<String> search(String keywords) {
        String [] split = keywords.split(keywords);
        System.err.println(keywords);
        //return storage.search(keywords);
        return null;
    }

    @Override
    public void add(String id, Document doc) {
        boolean status = storage.store(id, doc);
        System.err.println(status ? "Document added successfully " : "An error occured. Document was not stored");
    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
