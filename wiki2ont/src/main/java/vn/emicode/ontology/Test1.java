package vn.emicode.ontology;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.jena.OntVocabulary.Factory;

public class Test1 {

	public static void main(String[] args) throws OWLOntologyCreationException {
		// Getting manager:
		OWLOntologyManager manager = OntManagers.createONT();

		// ====================================
		// Interacting using OWL-API interface:
		// ====================================
		// Related data-factory:
		OWLDataFactory factory = manager.getOWLDataFactory();
		// Creating an ontology:
		OWLOntology ontology = manager.createOntology(IRI.create("first-class"));
		// Adding sub-class-of axiom:

	    ontology.addAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLClass("first-class"), factory.getOWLThing()));

	    // =====================================
	    // Interacting using jena-API interface:
	    // =====================================
	    Model model = ((Ontology)ontology).asGraphModel();
	    model.createResource("second-class").addProperty(RDFS.subClassOf, model.createResource("first-class"));
		
	    // =================
	    // Printing results:
	    // =================
		
	    System.out.println("Ontology as Turtle:");
		
	    System.out.println("\nList of owl-axioms:");
	    ontology.axioms().forEach(System.out::println);
	    
	    System.out.println("\nAll jena-statements:");
	    model.listStatements().forEachRemaining(System.out::println);
	}

}
