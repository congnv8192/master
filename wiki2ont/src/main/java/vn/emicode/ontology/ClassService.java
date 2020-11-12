package vn.emicode.ontology;


import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLClass;

import com.github.owlcs.ontapi.Ontology;

public class ClassService {
	private Ontology ontology;
	
	public ClassService(Ontology ontology) {
		this.ontology = ontology;
	}
	
	public Stream<OWLClass> all() {
		return ontology.classesInSignature();
	}
	
	void find() {
		
	}
	
	void add() {
		
	}
	
	void update() {
		
	}
	
	void remove() {
		
	}
}
