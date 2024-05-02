package it.polimi.tiw.servlets;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.dao.Person;
import it.polimi.tiw.utils.SignUtility;

@WebServlet("/signup")
public class SignupServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;
       
    public SignupServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		ctx.setVariable("error", false);
		
		templateEngine.process("signup", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String repeatedPassword = request.getParameter("repeat-password");
		
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		
		SignUtility util = new SignUtility(response, templateEngine, ctx, "signup");

		if (SignUtility.isNullOrBlank(username) || SignUtility.isNullOrBlank(email) || SignUtility.isNullOrBlank(password) || SignUtility.isNullOrBlank(repeatedPassword)) {
			util.invalidFormData("Some fields are empty");
		} else if (!password.equals(repeatedPassword)) {
			util.invalidFormData("The passwords are not equal");
		} else if (!SignUtility.isEmailValid(email)) {
			util.invalidFormData("The email is not valid");
		} else {
			try {
				Optional<Person> personFromEmail = personDAO.getFromEmail(email);
				Optional<Person> personFromUsername = personDAO.getFromUsername(username);
				
				if (personFromUsername.isPresent()) {
					util.invalidFormData("The username is already taken");
				} else if (personFromEmail.isPresent()) {
					util.invalidFormData("The email is already taken");
				} else { 
					// Form valid
					
					// Hash and salt password
					String hashedPassword = SignUtility.hashAndSalt(password);
					
					// Create and save the new user
					String[] newPerson = {username, email, hashedPassword};
					personDAO.save(newPerson);
					
					// Redirect to homepage
					response.sendRedirect("home");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
}
