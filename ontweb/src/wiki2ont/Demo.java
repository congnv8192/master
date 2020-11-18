package wiki2ont;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.github.owlcs.ontapi.jena.model.OntIndividual;

import info.bliki.wiki.dump.WikiArticle;

public class Demo {
	public static void main(String[] args) {
		final String URI = "http://example.com";
		String PATH_WIKI_DUMPS = "in/wikidumps";
		final String PATH_ONTO = "out/wiki2ont.owl";

		try {
			Wiki2Ont app = new Wiki2Ont(URI);
			
			FileInputStream is = new FileInputStream(new File(PATH_ONTO));
//			InputStream is = Wiki2Ont.class.getResourceAsStream("wiki2ont.owl");

//			 load ontology
//			app.loadOntology(is, Lang.RDFXML);

			// load wiki dumps
//			app.processDumpFiles(PATH_WIKI_DUMPS);

			WikiArticle article = app.addArticleByUrl("Đại_học_Quốc_gia_Hà_Nội");
			System.out.println(article);

			List<OntIndividual> individuals = app.query("Đại học");

			for (OntIndividual individual : individuals) {
				System.out.println(individual);
			}

//			FileOutputStream os = new FileOutputStream(new File(PATH_ONTO));
//			app.exportOntology(os, "RDF/XML");

//			app.exportOntology(System.out, Lang.RDFXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
