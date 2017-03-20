package api;

import api.Document;

/*
 * 
 */
public interface IndexerService {

	void add( String documentId, Document doc );

	void remove( String documentId );
	
}
