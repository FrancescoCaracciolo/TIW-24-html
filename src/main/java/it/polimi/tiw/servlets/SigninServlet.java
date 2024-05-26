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

import it.polimi.tiw.beans.Person;
import it.polimi.tiw.utils.SessionUtility;
import it.polimi.tiw.utils.SignUtility;

@WebServlet("/signin")
public class SigninServlet extends ThymeleafServlet {
    private static final long serialVersionUID = 7259568503736798305L;

	public SigninServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If a session already exists, redirect to homepage
		boolean isSessionValid = SessionUtility.redirectOnValidSession("home", request, response);
		if (isSessionValid) 
			return;
		
		// Otherwise, load the signin.html page
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		ctx.setVariable("error", false);
		
		templateEngine.process("sign", ctx, response.getWriter());	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String emailOrUsername = request.getParameter("signin-email-username");
		String password = request.getParameter("signin-password");
		
		// Create a new session
		HttpSession session = request.getSession(true);
		
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		SignUtility util = new SignUtility(response, templateEngine, ctx, "sign");

		if (SignUtility.isNullOrBlank(emailOrUsername) || emailOrUsername.length() > 255 || password.length() > 40 || SignUtility.isNullOrBlank(password)) {
			util.invalidFormData("Some fields are empty or invalid");
		} else {
			try {
				Optional<Person> person = personDAO.getFromEmailOrUsername(emailOrUsername);
				
				// If the user exists and the password is right
				if (person.isPresent() && SignUtility.verifyEqualityToHash(password, person.get().getPasswordHash())) {
					session.setAttribute("user", person.get());
					response.sendRedirect("home");
				} else {
					util.invalidFormData("Wrong username/email or password");
				}
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}
}
