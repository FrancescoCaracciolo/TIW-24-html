package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Person;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.GeneralUtility;
import it.polimi.tiw.utils.SessionUtility;

@WebServlet("/image")
public class ImageServlet extends ThymeleafServlet {
    private static final long serialVersionUID = 4158541425697275291L;
    
    private ImageDAO imageDAO;
    private CommentDAO commentDAO;
    private AlbumDAO albumDAO;
    
	public ImageServlet() {
        super();
    }
	
	public void init() throws ServletException {
		super.init();
		
		try {
			imageDAO = new ImageDAO(this.dbConnection);
			commentDAO = new CommentDAO(this.dbConnection);
			albumDAO = new AlbumDAO(this.dbConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		
		// Get the logged user from the session and export it to the web context
		Person user = (Person) request.getSession().getAttribute("user");
		ctx.setVariable("user", user);
		
		// Parse and get image ID
		if (!GeneralUtility.isValidNumericParameter(request.getParameter("imgId"))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		int imageId = Integer.parseInt(request.getParameter("imgId"));

		// Parse and set album parameter
		String albumParam = request.getParameter("albumId");
		Optional<Album> album = Optional.empty();
		if (albumParam != null && GeneralUtility.isValidNumericParameter(albumParam)) {
			try {
				album = albumDAO.get(Integer.parseInt(albumParam));
			} catch (NumberFormatException | SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		if (album.isEmpty()) {
			ctx.setVariable("album", null);
		} else {
			ctx.setVariable("album", album.get());
		}

		Optional<Image> image;
		Optional<Person> author;
		
		try {
			image = imageDAO.get(imageId);
			
			GeneralUtility.setCtxVariableIfPresent(ctx, response, "image", image);
			
			int uploderid = image.get().getUploaderId();

			author = this.personDAO.get(uploderid);
			
			GeneralUtility.setCtxVariableIfPresent(ctx, response, "author", author);
			
			LinkedHashMap<Comment, Person> comments = commentDAO.getAuthorsMapFromImage(image.get());
			
			ctx.setVariable("comments", comments.keySet());
			ctx.setVariable("commentsMap", comments);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		templateEngine.process("image", ctx, response.getWriter());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
