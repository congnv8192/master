package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Statement;

import com.github.owlcs.ontapi.jena.model.OntIndividual;

import wiki2ont.Wiki2Ont;
import wiki2ont.Wiki2OntFactory;
import wiki2ont.wiki.Utils;

@WebServlet("/query")
public class QueryServlet extends Servlet {
	private Wiki2Ont app;

	@Override
	public void init() throws ServletException {
		app = Wiki2OntFactory.get();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");

		String q = Utils.paramToUTF8(request.getParameter("q"));
		
		List<OntIndividual> individuals = app.query(q);
		
		
		List<Map<String, Object>> results = new ArrayList<>();
		for(OntIndividual individual : individuals) {
			Map<String, Object> result = new HashMap<>();
			
			result.put("label", individual.getLabel());
			result.put("uri", individual.getURI());
			
			// summary
			Statement propSummary = individual.getProperty(app.getPropSummary());
			if (propSummary != null) {
				result.put("summary", propSummary.getString());
			}
			
			// infobox
			Statement propInfobox = individual.getProperty(app.getPropInfobox());
			if (propInfobox != null) {
				result.put("infobox", propInfobox.getString());
			}

			// same as
			List<Map<String, String>> sameIndividuals = new ArrayList<>();
			individual.sameIndividuals().forEach(in -> {
				Map<String, String> props = new HashMap<>();
				
				props.put("label", in.getLabel());
				props.put("uri", in.getURI());
				
				sameIndividuals.add(props);
			});
			
			// types
			List<String> types = new ArrayList<>();
			individual.types().forEach((type) -> {
				types.add(type.getURI());
			});
			result.put("types", types);
			
			results.add(result);
		}
		
		json(response, results);
	}
}
