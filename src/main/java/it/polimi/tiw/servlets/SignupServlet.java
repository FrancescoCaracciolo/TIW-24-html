package it.polimi.tiw.servlets;

import java.io.IOException;
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If a session already exists, redirect to homepage
		SessionUtility.redirectOnValidSession("home", request, response);
		
		// Otherwise, load the signup.html page
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		ctx.setVariable("error", false);
		
		templateEngine.process("signup", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String repeatedPassword = request.getParameter("repeat-password");
		
		// Create a new session
		HttpSession session = request.getSession(true);
			
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		SignUtility util = new SignUtility(response, templateEngine, ctx, "signup");

		// If some fields haven't been filled
		if (SignUtility.isNullOrBlank(username) || SignUtility.isNullOrBlank(email) || SignUtility.isNullOrBlank(password) || SignUtility.isNullOrBlank(repeatedPassword)) {
			util.invalidFormData("Some fields are empty");
		
		// If the given passwords are not equal
		} else if (!password.equals(repeatedPassword)) {
			util.invalidFormData("The passwords are not equal");
		
		// If the email doesn't have a vaild format
		} else if (!SignUtility.isEmailValid(email)) {
			util.invalidFormData("The email is not valid");
		
		// If the form is valid
		} else {
			try {
				Optional<Person> personFromEmail = personDAO.getFromEmail(email);
				Optional<Person> personFromUsername = personDAO.getFromUsername(username);
				
				if (personFromUsername.isPresent()) {
					util.invalidFormData("The username is already taken");
				} else if (personFromEmail.isPresent()) {
					util.invalidFormData("The email is already taken");
				
				// If the user can be created (i.e. the chosen email and username are unique)
				} else { 
					// Save the new user on the db
					personDAO.save(username, email, password);
					
					Optional<Person> createdPerson = personDAO.getFromUsername(username);
					
					// If the user has actually been added to the db
					if(createdPerson.isPresent()) {
						session.setAttribute("person", createdPerson.get());
						response.sendRedirect("home");
					} else {
						util.invalidFormData("The account cannot be created, try later");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
}
