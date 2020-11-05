package vn.emicode.ontology;

import com.github.owlcs.ontapi.OntFormat;
import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.OntologyManager;
import com.github.owlcs.ontapi.jena.OntModelFactory;
import com.github.owlcs.ontapi.jena.model.OntIndividual;
import com.github.owlcs.ontapi.jena.model.OntModel;

public class Test2 {
	public static void main(String[] args) {
		  // getting manager:
	    OntologyManager manager = OntManagers.createONT();

	    // creating a graph ontology:
	    OntModel model = manager.createGraphModel("http://example.com");
	    // add version iri:
	    model.getID().setVersionIRI("http://example.com/1.0");
	    // add some annotations to the ontology header
	    model.getID().addComment("this is an example ontology", "fr");
	    // add some ns-prefixes (including standard):
	    model.setNsPrefixes(OntModelFactory.STANDARD).setNsPrefix("test", "http://example.com#");

	    // adding several individuals (both named and anonymous):
	    OntIndividual named = model.getOWLThing().createIndividual("http://example.com#individual");
	    OntIndividual anonymous = model.getOWLNothing().createIndividual();

	    // add disjoint individuals axiom assertion:
	    model.createDifferentIndividuals(named, anonymous);

	    // add annotation to anonymous individual declaration:
	    anonymous.addComment("this is an anonymous individual");

	    // print result as turtle to stdout:
	    model.write(System.out, OntFormat.TURTLE.getID());
	}
}
