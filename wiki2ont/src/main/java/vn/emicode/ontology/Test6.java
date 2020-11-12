package vn.emicode.ontology;

import org.apache.jena.graph.Triple;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;

public class Test6 {
	public static void main(String[] args) {
		String uri = "http://ex.com";
	    // Get native OWL-API-impl manager through OWL-API-apibinding.
	    // Note that in ONT-API there is an alternative way: OntManagers.createOWL()
	    OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
	    // Get ONT-API manager:
	    OntologyManager ontManager = OntManagers.createONT();

	    // Create pure OWL-ontology (no graph inside):
	    OWLOntology owlOntology = owlManager.createOntology(IRI.create(uri));
	    // Add single axiom (class-assertion):
	    OWLDataFactory df = ontManager.getOWLDataFactory();
	    OWLAxiom classAssertion = df.getOWLClassAssertionAxiom(
	            df.getOWLClass(IRI.create(uri + "#", "clazz")),
	            df.getOWLNamedIndividual(IRI.create(uri + "#", "individual")));
	    owlOntology.add(classAssertion);
	    // Copy from OWL- to ONT-Manager.
	    // This will produce an OWL-ontology (Ontology) with a jena Graph inside:
	    Ontology ontOntology = ontManager.copyOntology(owlOntology, OntologyCopy.DEEP);
	    // Print all triples from the inner graph:
	    ontOntology.asGraphModel().getGraph().find(Triple.ANY).forEachRemaining(System.out::println);
	    // Print all axioms:
	    ontOntology.axioms().forEach(System.out::println);
	}
}
