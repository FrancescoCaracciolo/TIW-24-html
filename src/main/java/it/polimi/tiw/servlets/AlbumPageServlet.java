package it.polimi.tiw.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.CreateAlbumUtility;
import it.polimi.tiw.utils.SessionUtility;

@WebServlet("/album")

public class AlbumPageServlet extends ThymeleafServlet {
    private static final long serialVersionUID = -7297515526961117240L;
    
    private AlbumDAO albumDAO;
	private ImageDAO imageDAO;

	public AlbumPageServlet() {
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
		CreateAlbumUtility albumUtil = new CreateAlbumUtility(response, templateEngine, ctx, getServletInfo(), imageDAO, null);
		
		// Get the logged user from the session and export it to the web context
		Person user = (Person) request.getSession().getAttribute("user");
		ctx.setVariable("user", user);
		// Get parameters
		String pageParameter = request.getParameter("page");
		String albumParameter = request.getParameter("albumId");
		// Get page number, default 0
		Integer page = 0;
		if (pageParameter != null && CreateAlbumUtility.isNumeric(pageParameter)) {
			page = Integer.parseInt(request.getParameter("page"));
		}
		
		// Get album ID from parameter
		if (albumParameter == null || !CreateAlbumUtility.isNumeric(albumParameter)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Integer albumId = Integer.parseInt(request.getParameter("albumId"));
		
		try {
			// Get album if available
			Optional<Album> requestedAlbum = albumDAO.get(albumId);
			if (requestedAlbum.isEmpty()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			Album album = requestedAlbum.get();
			List<Image> images = imageDAO.getAlbumImages(album);
			
			// Calculate max page
			int maxPage = 0;
			if (images.size() > 5) {
				maxPage = images.size()/5-1;
				if (images.size() % 5 != 0) {
					maxPage++;
				}
			}
			// In case there are not enough images for the requested page
			// go to the first page
			if (images.size() < page*5) {
				page = 0;
			}
			
			// Create image array
			Optional<Person> albumAuthor = personDAO.get(album.getCreatorId());
			Image[] imageArray = {null, null, null, null, null};
			int currentIndex = 0;
			for (int i = page*5; i < (page+1)*5 && i < images.size(); i++) {
				imageArray[currentIndex] = images.get(i);
				currentIndex++;
			}
			
			// Set template variables
			ctx.setVariable("images", imageArray);
			ctx.setVariable("album", album);
			ctx.setVariable("albumAuthor", albumAuthor.get());
			ctx.setVariable("page", page);
			ctx.setVariable("maxPage", maxPage);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		templateEngine.process("album", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	public void destory() {
		super.destroy();
		try {
			if (this.albumDAO != null) {
				this.albumDAO.close();
			}
			if (this.imageDAO != null) {
				this.imageDAO.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
