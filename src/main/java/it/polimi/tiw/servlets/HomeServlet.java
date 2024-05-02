package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.dao.Album;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.Person;

@WebServlet("/home")
public class HomeServlet extends ThymeleafServlet {
    private static final long serialVersionUID = -7297515526961117146L;
    
    private Person loggedPerson;

	public HomeServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		
		// Get the logged user from the session and export it to the web context
		loggedPerson = (Person) request.getSession().getAttribute("person");
		ctx.setVariable("user", loggedPerson);
		
		// Get the logger user's albums and export them to the web context
		try {
			AlbumDAO albumDAO = new AlbumDAO(this.dbConnection);
			List<Album> albums = albumDAO.getFromCreator(loggedPerson);
			
			ctx.setVariable("userAlbums", albums);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		templateEngine.process("home", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
