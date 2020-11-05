package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Employee;

@WebServlet(urlPatterns = "/query")
public class QueryServlet extends Servlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String name = request.getParameter("name");
		
		Employee employee = new Employee(1, name, "IT", 5000);
		
		json(response, employee);
	}
}
