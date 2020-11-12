package vn.emicode.ontology;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateAction;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;

public class Test8 {
	public static void main(String[] args) throws OWLOntologyStorageException {
		String uri = "http://ex.com/test";
	    String ns = uri + "#";

	    OntologyManager m = OntManagers.createONT();
	    OWLDataFactory df = m.getOWLDataFactory();

	    // Interacting with OWL-API interfaces: assembly the ontology:
	    Ontology o = m.createOntology(IRI.create(uri));
	    OWLAnnotationProperty a1 = df.getOWLAnnotationProperty(IRI.create(ns, "prop1"));
	    OWLAnnotationProperty a2 = df.getOWLAnnotationProperty(IRI.create(ns, "prop2"));
	    
	    OWLClass c1 = df.getOWLClass(IRI.create(ns, "class1"));
	    
	    OWLNamedIndividual i1 = df.getOWLNamedIndividual(IRI.create(ns, "indi1"));
	    
	    o.add(df.getOWLDeclarationAxiom(a1));
	    o.add(df.getOWLDeclarationAxiom(a2));
	    o.add(df.getOWLDeclarationAxiom(c1));
	    
	    
	    o.add(df.getOWLAnnotationPropertyDomainAxiom(a1, c1.getIRI()));
	    o.add(df.getOWLClassAssertionAxiom(c1, i1));
	    o.add(df.getOWLSubAnnotationPropertyOfAxiom(a2, a1));
	    
	    o.add(df.getOWLAnnotationAssertionAxiom(a1, df.getOWLAnonymousIndividual(), i1.getIRI()));

	    // Print ontology before updating:
	    o.axioms().forEach(System.out::println);
	    o.saveOntology(System.out);
	    
	    System.out.println("-------");

	    // SPARQL UPDATE: replace owl:AnnotationProperty -> owl:ObjectProperty
	    UpdateAction.parseExecute("PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
	                    "PREFIX owl:     <http://www.w3.org/2002/07/owl#>\n" +
	                    "DELETE { ?x rdf:type owl:AnnotationProperty } \n" +
	                    "INSERT { ?x rdf:type owl:ObjectProperty } \n" +
	                    "WHERE { ?x rdf:type owl:AnnotationProperty }",
	            o.asGraphModel());

	    // Print result:
	    o.axioms().forEach(System.out::println);
	    o.saveOntology(System.out);

	    // SPARQL SELECT: just print all declarations:
	    try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory
	            .create("PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
	                    "SELECT ?s ?o WHERE { ?s a ?o }"), o.asGraphModel())) {
	        ResultSet res = qexec.execSelect();
	        while (res.hasNext()) {
	            System.out.println(res.next());
	        }
	    }
	}
}
