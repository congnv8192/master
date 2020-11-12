package vn.emicode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.xml.sax.SAXException;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.InfoBox;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import vn.emicode.ontology.ClassService;
import vn.emicode.wiki.PatternMatcher;
import vn.emicode.wiki.Utils;

public class Wiki2Ont implements IArticleFilter {
	private String uri;
	private String ns;

	private Ontology ontology;
	private OWLDataFactory df;

	public Wiki2Ont(String uri) {
		this.uri = uri;
		this.ns = uri + "#";

		OntologyManager manager = OntManagers.createONT();
		this.df = manager.getOWLDataFactory();
		this.ontology = manager.createOntology(IRI.create(uri));
	}

	public void processDumpFile(File file) throws IOException, SAXException {
		new WikiXMLParser(file, this).parse();
	}

	public void processDumpFiles(String path) throws IOException, SAXException {
		File directory = new File(path);
		File files[] = directory.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				processDumpFile(file);
			}
		}
	}

	/**
	 * map wiki page -> onto
	 * 
	 * @effects
	 * 
	 *          <pre>
	 * 	if redirect 
	 * 		sameIndividual
	 * 	else 
	 * 		infobox template = class (entity)
	 *  	article.title = object (individual)
	 * 		infobox attributes = data/object properties
	 *          </pre>
	 */
	@Override
	public void process(WikiArticle article, Siteinfo siteinfo) throws IOException {
		if (article.isMain()) {
			PatternMatcher parser = new PatternMatcher(article.getText());

			// redirect
			if (parser.isRedirect()) {
				OWLNamedIndividual target = df.getOWLNamedIndividual(IRI.create(ns, parser.getRedirectText()));
				OWLNamedIndividual from = df.getOWLNamedIndividual(IRI.create(ns, article.getText()));
				ontology.add(df.getOWLSameIndividualAxiom(from, target));
			} else {
				InfoBox infoBox = parser.getInfoBox();

				if (infoBox != null) {

					// infobox.template = class (entity)
					OWLClass entity = df.getOWLClass(IRI.create(ns, parser.getInfoBoxTemplate()));
					
					ontology.add(df.getOWLDeclarationAxiom(entity));

					// article.title = object (individual)
					OWLNamedIndividual individual = df.getOWLNamedIndividual(IRI.create(ns, article.getTitle()));
					OWLAnnotation anno = df.getRDFSLabel(article.getTitle());
					
//					ontology.add(df.getOWLAnnotationAssertionAxiom(OWLAnnotationSubject));
//					ontology.add(df.getOWLClassAssertionAxiom(entity, individual));

					// infobox.attributes = data properties
					Map<String, String> attributes = parser.getInfoBoxAttributes();
					
					for (Entry<String, String> entry : attributes.entrySet()) {
						OWLDataProperty prop = df.getOWLDataProperty(IRI.create(ns, entry.getKey()));
						ontology.add(df.getOWLDataPropertyAssertionAxiom(prop, individual, entry.getValue()));
					}

					// TODO: ref = object properties [[abc]]
				}
			}
		}
	}
	
	public IRI createIRI(String name) {
		return IRI.create(ns, Utils.toWikiUrl(name));
	}
	
	public ResultSet query(String name) {
		QueryExecution qexec = QueryExecutionFactory
				.create(QueryFactory.create("PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
						+ "SELECT ?s ?o WHERE { ?s a ?o }"), ontology.asGraphModel());
		ResultSet res = qexec.execSelect();
		return res;
	}

	public void exportOntology(String filename) throws OWLOntologyStorageException, FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(new File(filename));
		this.ontology.saveOntology(new RDFXMLDocumentFormat(), fos);
		System.out.println("Exported to " + filename);
	}

	public static void main(String[] args) {
		final String URI = "http://emicode.vn";
		String PATH_WIKI_DUMPS = "in/wikidumps";
		final String PATH_ONTO = "out/wiki2ont.owl";

		try {
			Wiki2Ont app = new Wiki2Ont(URI);
			app.processDumpFiles(PATH_WIKI_DUMPS);
			
			ResultSet res = app.query("Nguyễn Thị Muôn");
			
//			while (res.hasNext()) {
//				System.out.println(res.next());
//				System.out.println("----");
//			}
			
//			app.ontology.saveOntology(new RDFXMLDocumentFormat(), System.out);
			
			ClassService service = new ClassService(app.ontology);
			
			service.all().forEach(cls -> {
				System.out.println(cls);
			});
			
			
//			app.exportOntology(PATH_ONTO, OntFormat.OWL_XML.getID());

//			OntFormat.TURTLE.getID()
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
