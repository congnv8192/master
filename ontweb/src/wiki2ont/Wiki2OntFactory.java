package wiki2ont;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.riot.Lang;

public class Wiki2OntFactory {
	public static final String URI = "http://example.com";
	public static final String PATH_ONTO = "wiki2ont.owl";
	
	private static Wiki2Ont app;
	
	public static Wiki2Ont get() {
		
		if (app == null) {
			app = new Wiki2Ont(URI);
//			try (FileInputStream is = new FileInputStream(new File(PATH_ONTO))) {
			try (InputStream is = Wiki2OntFactory.class.getResourceAsStream(PATH_ONTO)) {
				// load ontology
				app.loadOntology(is, Lang.RDFXML);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return app;
	}
}
