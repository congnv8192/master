package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void json(HttpServletResponse response, Object data) throws IOException {
		String json = new Gson().toJson(data);
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		out.print(json);
		out.flush();
	}
	
	protected void html(HttpServletResponse response, String html) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		out.print(html);
		out.flush();
	}
}
