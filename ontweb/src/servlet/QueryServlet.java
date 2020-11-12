package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import com.github.owlcs.ontapi.Ontology;

import vn.emicode.Wiki2Ont;

@WebServlet(urlPatterns = "query")
public class QueryServlet extends Servlet {
	
	private Wiki2Ont app;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		final String URI = "http://emicode.vn";
		String PATH_WIKI_DUMPS = "in/wikidumps";
		final String PATH_ONTO = "out/wiki2ont.owl";

		try {
			Wiki2Ont app = new Wiki2Ont(URI);
			app.processDumpFiles(PATH_WIKI_DUMPS);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String name = request.getParameter("name");
		
		ResultSet res = app.query("Nguyễn Thị Muôn");
		
		json(response, res);
		
	}
}
