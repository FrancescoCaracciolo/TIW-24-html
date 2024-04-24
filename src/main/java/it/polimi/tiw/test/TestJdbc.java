package it.polimi.tiw.test;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/BasicQueryServlet")
public class TestJdbc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection" + e.getStackTrace());
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String query = "SELECT firstname, lastname, city FROM persons";
		ResultSet result = null;
		Statement statement = null;
		res.setContentType("text/plain");
		PrintWriter out = res.getWriter();
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			while (result.next()) {
				out.println("Firstname: " + result.getString("firstname") + " Lastname: " + result.getString("lastname")
						+ " City: " + result.getString("city"));
			}
		} catch (SQLException e) {
			out.append("SQL ERROR");
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				out.append("SQL RES ERROR");
			}
			try {
				statement.close();
			} catch (Exception e1) {
				out.append("SQL STMT ERROR");
			}
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}