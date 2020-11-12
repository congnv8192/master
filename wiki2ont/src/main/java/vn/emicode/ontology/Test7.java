package vn.emicode.ontology;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.model.parameters.Imports;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;

public class Test7 {
	public static void main(String[] args) {
		// Jena ONT-Model API
	    OntModelSpec spec = OntModelSpec.OWL_DL_MEM;
	    OntModel o1 = ModelFactory.createOntologyModel(spec);
	    OntModel o2 = ModelFactory.createOntologyModel(spec);
	    o1.createOntology("http://test.com/1/v1")
	            .addImport(o2.createOntology("http://test.com/2"));
	    o1.addSubModel(o2);
	    o1.createOntology("http://test.com/1/v2");
	    o1.createClass("http://test.com#clazz1");
	    o2.createClass("http://test.com#clazz2");

	    // Pass to ONT-API
	    OntologyManager manager = OntManagers.createONT();
	    Ontology ontology = manager.addOntology(o1.getGraph());

	    // expected: two class-declarations:
	    ontology.axioms(Imports.INCLUDED).forEach(System.out::println);
	    // expected: <http://test.com/1/v1>
	    System.out.println(ontology.getOntologyID());
	    // expected: 2
	    System.out.println(manager.ontologies().count());
	}
}
