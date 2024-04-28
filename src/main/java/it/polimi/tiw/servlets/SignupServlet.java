package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.Person;
import it.polimi.tiw.dao.PersonDAO;
import it.polimi.tiw.utils.SignUtility;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConnection;
	private TemplateEngine templateEngine;
	private PersonDAO personDAO;
       
    public SignupServlet() {
        super();
    }
    

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		
		// Initialize and setup the TemplateEngine
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	
		try {
			// Retrieve database parameters and connect
			String driver = servletContext.getInitParameter("dbDriver");
			String url = servletContext.getInitParameter("dbUrl");
			String user = servletContext.getInitParameter("dbUser");
			String password = servletContext.getInitParameter("dbPassword");
			
			Class.forName(driver);
			dbConnection = DriverManager.getConnection(url, user, password);
			
			personDAO = new PersonDAO(dbConnection);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection" + e.getStackTrace());
		}
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
				Optional<Person> personFromUsername = personDAO.get(username);
				
				if (personFromUsername.isPresent()) {
					util.invalidFormData("The username is already taken");
				} else if (personFromEmail.isPresent()) {
					util.invalidFormData("The email is already taken");
				} else { 
					// Form valid
					
					// Hash and salt password
					String hashedPassword = SignUtility.hashAndSalt(password);
					
					// Create and save the new user
					Person newPerson = new Person(username, email, hashedPassword);
					personDAO.save(newPerson);
					
					// Redirect to signin page
					response.sendRedirect("signin");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
}
