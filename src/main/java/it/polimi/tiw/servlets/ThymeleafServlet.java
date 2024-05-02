package it.polimi.tiw.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.PersonDAO;

public abstract class ThymeleafServlet extends HttpServlet {
    private static final long serialVersionUID = -4286758107289165886L;
    
	protected Connection dbConnection;
	protected TemplateEngine templateEngine;
	protected PersonDAO personDAO;
	
    public ThymeleafServlet() {
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
			throw new UnavailableException("Cannot load database driver" + e.getStackTrace());
		} catch (SQLException e) {
			throw new UnavailableException("Cannot get database connection" + e.getStackTrace());
		}
	}
}
