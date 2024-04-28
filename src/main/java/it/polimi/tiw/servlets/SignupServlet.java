package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

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

import at.favre.lib.crypto.bcrypt.BCrypt;
import it.polimi.tiw.dao.Person;
import it.polimi.tiw.dao.PersonDAO;

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
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	
		try {
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

		if (isNullOrBlank(username) || isNullOrBlank(email) || isNullOrBlank(password) || isNullOrBlank(repeatedPassword)) {
			invalidFormData("Some fields are empty", request, response, ctx);
		} else if (!password.equals(repeatedPassword)) {
			invalidFormData("The passwords are not equal", request, response, ctx);
		} else if (!isEmailValid(email)) {
			invalidFormData("The email is not valid", request, response, ctx);
		} else {
			try {
				Optional<Person> personFromEmail = personDAO.getFromEmail(email);
				Optional<Person> personFromUsername = personDAO.get(username);
				
				if (personFromUsername.isPresent()) {
					invalidFormData("The username is already taken", request, response, ctx);
				} else if (personFromEmail.isPresent()) {
					invalidFormData("The email is already taken", request, response, ctx);
				} else { 
					// Form valid
					
					// Hash and salt password
					String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
					
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
	
	// Utility methods
	private boolean isEmailValid(String email) {
		// RFC 5322 regular expression for email address validation
		String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	    
	    return Pattern.compile(regexPattern).matcher(email).matches();
	}
	private boolean isNullOrBlank(String string) {
		return string == null || string.isBlank();
	}
	
	private void invalidFormData(String errorMessage, HttpServletRequest request, HttpServletResponse response, WebContext ctx) throws IOException {
		ctx.setVariable("error", true);
		ctx.setVariable("errorMessage", errorMessage);
		
		templateEngine.process("signup", ctx, response.getWriter());
	}
}
