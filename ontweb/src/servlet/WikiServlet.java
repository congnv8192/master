package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Wiki2OntFactory;
import info.bliki.wiki.dump.WikiArticle;
import wiki2ont.Wiki2Ont;

@WebServlet("/wiki")
public class WikiServlet extends Servlet {
	
	private Wiki2Ont app;
	
	@Override
	public void init() throws ServletException {
		this.app = Wiki2OntFactory.get();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String page = request.getParameter("page");
		
		WikiArticle article = app.addArticleByUrl(page);
		
		if (article == null) {
			json(response, "error !200");
		} else {
			if (article.isMain()) {
				app.processArticle(article);
				
				json(response, "success");
			} else {
				json(response, "not main");
			}
		}
	}
}
