package vn.emicode.ontology;

import com.github.owlcs.ontapi.jena.OntModelFactory;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.model.OntObjectProperty;

public class Test3 {
	public static void main(String[] args) {
	    OntModel model = OntModelFactory.createModel().setNsPrefixes(OntModelFactory.STANDARD);
	    OntObjectProperty property = model.createObjectProperty("http://test#hasUnit");
	    OntClass clazz = model.createOntClass("http://test#Numeric");
	    clazz.addSuperClass(model.createOntClass("http://test#Characteristic"))
	            .addSuperClass(model.createObjectMaxCardinality(property, 1,
	                    model.createOntClass("http://test#Scale")));
	    model.write(System.out, "ttl");
	}
}
