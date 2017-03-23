package rest.server;

import api.Endpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import api.RendezVousAPI;
import javax.ws.rs.DELETE;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Implementacao do servidor de rendezvous em REST 
 */

public class RendezVousResources implements RendezVousAPI{

	private Map<String, Endpoint> db = new ConcurrentHashMap<>();
        
        @Override
	public Endpoint[] endpoints() {
		return db.values().toArray( new Endpoint[ db.size() ]);
	}

	
        @Override
	public void register( String id, Endpoint endpoint) {
		System.err.printf("register: %s <%s>\n", id, endpoint);
		
		if (db.containsKey(id))
			throw new WebApplicationException( CONFLICT );
		else
			db.put(id, endpoint);		
	}

	
        @Override
	public void update(@PathParam("id") String id, Endpoint endpoint) {
		System.err.printf("update: %s <%s>\n", id, endpoint);
		
		if ( ! db.containsKey(id))
			throw new WebApplicationException( NOT_FOUND );
		else
			db.put(id, endpoint);		
	}

        
        @Override
	public void unregister(@PathParam("id") String id) {
		System.err.printf("deleting: %s\n", id);
		
		if ( ! db.containsKey(id))
			throw new WebApplicationException( NOT_FOUND );
		else
			db.remove(id);
	}
}
