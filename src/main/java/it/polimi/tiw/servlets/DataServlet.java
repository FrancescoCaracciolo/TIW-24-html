package it.polimi.tiw.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;


import it.polimi.tiw.dao.PersonDAO;

public abstract class DataServlet extends HttpServlet {
    private static final long serialVersionUID = -4286758107289165886L;
    
	protected Connection dbConnection;
	protected PersonDAO personDAO;
	
    public DataServlet() {
        super();
	}
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
	
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
