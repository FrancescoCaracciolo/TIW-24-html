package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.SessionUtility;

@WebServlet("/home")
public class HomeServlet extends ThymeleafServlet {
    private static final long serialVersionUID = -7297515526961117146L;
    
    private AlbumDAO albumDAO;
	private ImageDAO imageDAO;

	public HomeServlet() {
        super();
    }
	
	public void init() throws ServletException {
		super.init();
		
		try {
			albumDAO = new AlbumDAO(this.dbConnection);
			imageDAO = new ImageDAO(this.dbConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		
		// Get the logged user from the session and export it to the web context
		Person user = (Person) request.getSession().getAttribute("user");
		ctx.setVariable("user", user);
		
		// Get the logger user's albums and export them to the web context
		try {
			List<Album> userAlbums = albumDAO.getFromCreator(user);
			LinkedHashMap<Album, Image> albumThumbnail = albumDAO.getAlbumThumbnailMap();
			LinkedHashMap<Album, Person> albumAuthor = albumDAO.getAlbumAuthorMap();
			
			ctx.setVariable("userAlbums", userAlbums);
			ctx.setVariable("allAlbums", albumAuthor.keySet());
			ctx.setVariable("albumThumbnail", albumThumbnail);
			ctx.setVariable("albumAuthor", albumAuthor);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		templateEngine.process("home", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
