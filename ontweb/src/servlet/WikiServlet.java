package servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import info.bliki.wiki.dump.WikiArticle;
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String page = Utils.paramToUTF8(request.getParameter("page"));
		
		WikiArticle article = app.addArticleByUrl(page);
		
		if (article == null) {
			json(response, "error !200");
		} else {
			json(response, "success");
		}
	}
}
