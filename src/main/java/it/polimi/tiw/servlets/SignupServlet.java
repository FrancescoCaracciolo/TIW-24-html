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

@WebServlet("/signup")
public class SignupServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	public SignupServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If a session already exists, redirect to homepage
		boolean isSessionValid = SessionUtility.redirectOnValidSession("home", request, response);
		if (isSessionValid)
			return;

		// Otherwise, load the signup.html page
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		ctx.setVariable("error", false);

		templateEngine.process("signup", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String repeatedPassword = request.getParameter("repeat-password");

		// Create a new session
		HttpSession session = request.getSession(true);

		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		SignUtility util = new SignUtility(response, templateEngine, ctx, "signup");

		// If some fields haven't been filled
		if (email.length() > 255 || SignUtility.isNullOrBlank(username) || SignUtility.isNullOrBlank(email)
				|| SignUtility.isNullOrBlank(password) || SignUtility.isNullOrBlank(repeatedPassword)) {
			util.invalidFormData("Some fields are empty or invalid");

			// If the given passwords are not equal
		} else if (password.length() > 40 || password.length() < 8) {
			util.invalidFormData("The password has to be at least 8 characters and must not exceed 40 characters");
		} else if (username.length() > 20) {
			util.invalidFormData("The username must not exceed 20 characters");
		} else if (!password.equals(repeatedPassword)) {
			util.invalidFormData("The passwords are not equal");

			// If the email doesn't have a vaild format
		} else if (!SignUtility.isEmailValid(email)) {
			util.invalidFormData("The email is not valid");

			// If the form is valid
		} else {
				// Try saving the new user on the db
			Optional<Person> createdPerson;
			try {
				createdPerson = personDAO.save(username, email, password);
			} catch(SQLException e) {
				createdPerson = Optional.empty();
			}
			// If the user has actually been added to the db
			if (createdPerson.isPresent()) {
				session.setAttribute("user", createdPerson.get());
				response.sendRedirect("home");
			} else {
				try {
					Optional<Person> personFromEmail = personDAO.getFromEmail(email);
					Optional<Person> personFromUsername = personDAO.getFromUsername(username);
					if (personFromUsername.isPresent()) {
						util.invalidFormData("The username is already taken");
					} else if (personFromEmail.isPresent()) {
						util.invalidFormData("The email is already taken");
					} else {
						util.invalidFormData("The account cannot be created, try later");
					}
				} catch (Exception e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}
	}
}
