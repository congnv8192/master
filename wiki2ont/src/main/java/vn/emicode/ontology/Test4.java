package vn.emicode.ontology;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.semanticweb.owlapi.model.AxiomType;

import com.github.owlcs.ontapi.internal.AxiomParserProvider;
import com.github.owlcs.ontapi.jena.OntModelFactory;

public class Test4 {
	public static void main(String[] args) throws IOException {
		// Create a standard Jena Model:
	    Model m = ModelFactory.createDefaultModel();
	    // Load RDF data:
	    try (InputStream in = Test4.class.getResourceAsStream("pizza.ttl")) {
	        RDFDataMgr.read(m, in, Lang.TURTLE);
	    }
	    // list all OWLAxioms from Jena Model:
	    AxiomType.AXIOM_TYPES.stream()
	            .map(AxiomParserProvider::get)
	            .forEach(t -> t.axioms(OntModelFactory.createModel(m.getGraph()))
	                    .forEach(System.out::println));       
	}
}
