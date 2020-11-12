package vn.emicode.ontology;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.Model;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;

public class Test5 {
	// not work
	public static void main(String[] args) throws OWLOntologyCreationException {
		String functionalSyntaxString = "Ontology(<http://test.org/simple>"
				+ "SubClassOf(<http://test.org/simple#class2> <http://test.org/simple#class1>)"
				+ "ClassAssertion(<http://test.org/simple#class1> <http://test.org/simple#individual>))";
		InputStream in = new ByteArrayInputStream(functionalSyntaxString.getBytes(StandardCharsets.UTF_8));
		OntologyManager manager = OntManagers.createONT();
		Ontology ontology = manager.loadOntologyFromOntologyDocument(in);
		Model model = ontology.asGraphModel();
		model.write(System.out, "ttl");
	}
}
