package servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.Lang;

import info.bliki.wiki.dump.WikiArticle;
import wiki2ont.AppConfig;
import wiki2ont.Wiki2Ont;
import wiki2ont.Wiki2OntFactory;
import wiki2ont.wiki.Utils;

@WebServlet("/wiki")
public class WikiServlet extends Servlet {

	private Wiki2Ont app;

	@Override
	public void init() throws ServletException {
		this.app = Wiki2OntFactory.get();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// add page
		if (request.getParameter("page") != null) {
			// add page into ontology
			String page = Utils.paramToUTF8(request.getParameter("page"));

			WikiArticle article = app.addArticleByUrl(page);

			if (article == null) {
				json(response, "error !200");
			} else {
				json(response, "added");
			}
		}

		// save changes on ontology
		if ("save".equals(request.getParameter("action"))) {
			try {
				URL url = getClass().getClassLoader().getResource(AppConfig.WEB_PATH_ONTO);
				OutputStream os = new FileOutputStream(new File(url.toURI()));
				
				app.exportOntology(os, Lang.RDFXML);
				
				json(response, "saved");
				
			} catch (Exception e) {
				json(response, "save: failed");
			}
		}
	}
}
