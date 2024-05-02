package it.polimi.tiw.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

@WebServlet("/home")
public class HomeServlet extends ThymeleafServlet {
    private static final long serialVersionUID = -7297515526961117146L;

	public HomeServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		
		templateEngine.process("home", ctx, response.getWriter());
		
		Integer id = (Integer) request.getSession().getAttribute("Id");
		
		response.getWriter().print(id);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
