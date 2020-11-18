package wiki2ont;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.xml.sax.SAXException;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.OntologyManager;
import com.github.owlcs.ontapi.jena.OntModelFactory;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntDataProperty;
import com.github.owlcs.ontapi.jena.model.OntIndividual;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import wiki2ont.wiki.InfoBox;
import wiki2ont.wiki.PatternMatcher;
import wiki2ont.wiki.Utils;

public class Wiki2Ont implements IArticleFilter {
	private String uri;
	private String ns;

	private OntModel model;

	private OntClass clArticle;
	private OntClass clCategory;
	private OntClass clTemplate;
	private OntDataProperty propSummary;
	private OntDataProperty propInfobox;
	private Siteinfo siteinfo;

	public Wiki2Ont(String uri) {
		this.uri = uri;
		this.ns = uri + "#";

		// init ontology
		OntologyManager manager = OntManagers.createONT();
		model = manager.createGraphModel(uri);

		model.getID().setVersionIRI(uri + "/1.0");
		model.getID().addComment("wiki 2 ontology", "vi");
		model.setNsPrefixes(OntModelFactory.STANDARD);
		model.setNsPrefix("wo", ns);

		// shared
		clArticle = model.createOntClass(ns + "Article");
		clCategory = model.createOntClass(ns + "Category");
		clTemplate = model.createOntClass(ns + "Template");
		propSummary = model.createDataProperty(ns + "summary");
		propInfobox = model.createDataProperty(ns + "infobox");
		siteinfo = new Siteinfo();
		siteinfo.setSitename("Wikipedia");
	}

	public OntDataProperty getPropSummary() {
		return propSummary;
	}

	public OntDataProperty getPropInfobox() {
		return propInfobox;
	}

	public void loadOntology(InputStream is, Lang lang) {
		RDFDataMgr.read(model, is, lang);
	}

	public void processDumpFiles(String path) throws IOException, SAXException, URISyntaxException {
		File directory = new File(path);
		File files[] = directory.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				processDumpFile(file);
			}
		}
	}

	public void processDumpFile(File file) throws IOException, SAXException {
		new WikiXMLParser(file, this).parse();
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
	public void processArticle(WikiArticle article) {
		PatternMatcher matcher = new PatternMatcher(article.getText());

		OntIndividual inArticle = clArticle.createIndividual(getURI(article.getTitle()));
		inArticle.addLabel(article.getTitle());

		// redirect
		if (matcher.isRedirect()) {
			String redirectText = matcher.getRedirectText();

			// inner link = IGNORE
			if (redirectText.contains("#")) {
				model.removeOntObject(inArticle);

				return;
			}

			String uriRedirect = getURI(redirectText);
			OntIndividual target = model.getIndividual(uriRedirect);
			if (target == null) {
				target = clArticle.createIndividual(uriRedirect);
				target.addLabel(redirectText);
			}

			inArticle.addSameIndividual(target);

			return;
		}

		// prop
		inArticle.addProperty(propSummary, matcher.getSummary());

		// infobox
		InfoBox infoBox = matcher.getInfoBox();
		if (infoBox != null) {
			String infoBoxTemplate = matcher.getInfoBoxTemplate();
			String uriInfoBox = getURI(infoBoxTemplate);
			OntClass clInfoBox = model.getOntClass(uriInfoBox);

			if (clInfoBox == null) {
				clInfoBox = model.createOntClass(uriInfoBox);
				clInfoBox.addSuperClass(clTemplate);

				clInfoBox.addLabel(infoBoxTemplate);
			}

			inArticle.addClassAssertion(clInfoBox);

			// prop
			inArticle.addProperty(propInfobox, matcher.getInfoBox().getText());
		}

		// categories
		List<String> categories = matcher.getCategories();

		for (String category : categories) {
			category = category.trim();

			String uriCategory = getURI(category);
			OntClass clCategory = model.getOntClass(uriCategory);
			if (clCategory == null) {
				clCategory = model.createOntClass(uriCategory);
				clCategory.addSuperClass(this.clCategory);
				clCategory.addLabel(category);
			}

			inArticle.addClassAssertion(clCategory);
		}
	}

	/**
	 * @requires text != null
	 */
	private String getURI(String text) {
		// handle ns prob
		if (Character.isDigit(text.charAt(0))) {
			text = '_' + text;
		}

		return ns + Utils.toWikiUrl(text);
	}

	@Override
	public void process(WikiArticle article, Siteinfo siteinfo) throws IOException {

		if (article.isMain()) {
			processArticle(article);
		}
	}

	public WikiArticle addArticleByUrl(String page) {
		try {
			page = URLEncoder.encode(page, "UTF-8");

			String url = "https://vi.wikipedia.org/w/api.php?action=parse&prop=wikitext&format=json&page=" + page;

			String json = sendGet(url);

			JsonObject result = JsonParser.parseString(json).getAsJsonObject();
			JsonObject parse = result.getAsJsonObject("parse");

			WikiArticle article = new WikiArticle();
			article.setTitle(parse.get("title").getAsString(), siteinfo);
			article.setId(parse.get("pageid").getAsString());
			article.setText(parse.getAsJsonObject("wikitext").get("*").getAsString());

			//
			processArticle(article);

			return article;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String sendGet(String href) {
		try {
			URL url = new URL(href);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				return response.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<OntIndividual> query(String name) {
		QueryExecution qexec = QueryExecutionFactory
				.create(QueryFactory.create("PREFIX rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
						+ "PREFIX owl:     <http://www.w3.org/2002/07/owl#> \n"
						+ "PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#> \n"
						+ "PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#> \n"
						+ "PREFIX wo:     <http://example.com#> \n" +

						"SELECT * WHERE { \n" + "?x rdfs:label ?label . FILTER regex(?label, \"" + name
						+ "\", \"i\") \n" + "?x rdf:type wo:Article ." + "?x wo:summary ?summary ."
						+ "?x wo:infobox ?infobox ."
//						+ "?x ?p ?o"
						+ "}"), model);

		ResultSet res = qexec.execSelect();

		List<OntIndividual> individuals = new ArrayList<OntIndividual>();
		while (res.hasNext()) {
			System.out.println(res.next());
//			QuerySolution querySolution = res.next();
//
//			RDFNode node = querySolution.get("x");
//			OntIndividual individual = model.getIndividual(node.toString());
//
//			individuals.add(individual);
		}

		return individuals;
	}

	public void exportOntology(OutputStream os, Lang lang) {
		model.write(os, lang.getName());
	}
}
