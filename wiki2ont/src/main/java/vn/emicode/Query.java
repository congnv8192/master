package vn.emicode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;

public class Query {
	public static void main(String[] args) {
		OntologyManager manager = OntManagers.createONT();
		
		try {
			Ontology ontology = manager.loadOntologyFromOntologyDocument(new FileInputStream(new File("o/wiki2ont.ttl")));
			Model model = ontology.asGraphModel();
			
			// SPARQL SELECT: just print all declarations:
		    try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory
		            .create("PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
		                    "SELECT ?s ?o WHERE { ?s a ?o }"), model)) {
		        ResultSet res = qexec.execSelect();
		        while (res.hasNext()) {
		            System.out.println(res.next());
		        }
		    }
		} catch (OWLOntologyCreationException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
