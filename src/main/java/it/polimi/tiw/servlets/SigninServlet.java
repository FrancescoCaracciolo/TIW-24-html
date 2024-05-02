package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;
import it.polimi.tiw.dao.Person;
import it.polimi.tiw.utils.SignUtility;

@WebServlet("/signin")
public class SigninServlet extends ThymeleafServlet {
    private static final long serialVersionUID = 7259568503736798305L;

	public SigninServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		ctx.setVariable("error", false);
		
		templateEngine.process("signin", ctx, response.getWriter());	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String emailOrUsername = request.getParameter("email-username");
		String password = request.getParameter("password");
		HttpSession session = request.getSession();
		
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());

		SignUtility util = new SignUtility(response, templateEngine, ctx, "signin");

		if (SignUtility.isNullOrBlank(emailOrUsername) || SignUtility.isNullOrBlank(password)) {
			util.invalidFormData("Some fields are empty");
		} else {
			try {
				Optional<Person> person = personDAO.getFromEmailOrUsername(emailOrUsername);
				
				if (person.isPresent() && SignUtility.verifyEqualityToHash(password, person.get().getPasswordHash())) {
					Person loggedPerson = person.get();
					session.setAttribute("Id", loggedPerson.getId());
					
					response.sendRedirect("home");
				} else {
					util.invalidFormData("Wrong username/email or password");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
