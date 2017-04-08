/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.soap;

/**
 *
 * @author rmamaral
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebService;

import api.Endpoint;
import api.soap.RendezVousAPI;

@WebService(
        serviceName = RendezVousAPI.NAME,
        targetNamespace = RendezVousAPI.NAMESPACE,
        endpointInterface = RendezVousAPI.INTERFACE)

public class RendezVousServiceImpl implements RendezVousAPI {

	private Map<String, Endpoint> db = new ConcurrentHashMap<>();

	@Override
	public Endpoint[] endpoints() {
		return db.values().toArray( new Endpoint[ db.size()]);
	}

	@Override
	public void register(String id, Endpoint endpoint) {
		System.err.printf("register: %s <%s>\n", id, endpoint);
		db.put(id, endpoint);
	}

	@Override
	public void unregister(String id) {
	}
}
