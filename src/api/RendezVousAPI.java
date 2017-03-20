package api;

import api.Endpoint;

/**
 * Interface do servidor que mantem lista de servidores.
 */
public interface RendezVousAPI {

	/**
	 * Devolve array com a lista de servidores registados.
	 */
	Endpoint[] endpoints();

	/**
	 * Regista novo servidor.
	 */
	void register(String id, Endpoint endpoint);
        
        /**
         * Updates an existing server
         */
        public void update(String id, Endpoint endpoint);

	/**
	 * De-regista servidor, dado o seu id.
	 */
	void unregister(String id);
}
