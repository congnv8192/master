package vn.emicode.ontology;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationImpl;

public class Demo {
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		// load owl
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology o;
		try {
			o = man.createOntology();
//			o = man.loadOntologyFromOntologyDocument(new File("/data/pizza.owl.xml"));
//			OWLOntology ontology = man.loadOntology(IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl"));
//			o.add(new OWLAxiomI)
			
//			ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
			
			OWLDataFactory df = OWLManager.getOWLDataFactory();
			
			
			System.out.println(o);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
}
