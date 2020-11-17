package beans;

import java.io.InputStream;

import org.apache.jena.riot.Lang;

import wiki2ont.Wiki2Ont;

public class Wiki2OntFactory {
	public static final String URI = "http://example.com";
	public static final String PATH_ONTO = "/WEB-INF/out/wiki2ont.owl";
	
	private static Wiki2Ont app;
	
	public static Wiki2Ont get() {
		if (app == null) {
			app = new Wiki2Ont(URI);
			
			InputStream is = Wiki2OntFactory.class.getResourceAsStream("wiki2ont.owl");
			
			// load ontology
			app.loadOntology(is, Lang.RDFXML);
		}
		
		return app;
	}
}
