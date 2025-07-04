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
		
		// Check for errors from addComment servlet
		if (request.getParameter("error") != null) {
			ctx.setVariable("error", true);
			ctx.setVariable("errorMessage", request.getParameter("error"));
		}
		
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
				return;
			}
		}
		// Set the album variable in order to know from
		// which album page the user is from
		if (album.isEmpty()) {
			ctx.setVariable("album", null);
		} else {
			ctx.setVariable("album", album.get());
		}

		Optional<Image> image;
		Optional<Person> author;
		
		try {
			image = imageDAO.get(imageId);
			// If the image does not exist
			if (image.isEmpty()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			// Handle deletion
			if (request.getParameter("delete") != null) {
				if (image.get().getUploaderId() == user.getId()) {
					// Delete the image
					imageDAO.delete(image.get());
					// Delete albums without images
					albumDAO.deleteEmptyAlbums();
					// Redirect the user to the home page
					response.sendRedirect("home");
					return;
				}
			}
			
			GeneralUtility.setCtxVariableIfPresent(ctx, response, "image", image);
			
			int uploderid = image.get().getUploaderId();

			// Get author info
			author = this.personDAO.get(uploderid);
			
			GeneralUtility.setCtxVariableIfPresent(ctx, response, "author", author);
			
			// Get comments
			LinkedHashMap<Comment, Person> comments = commentDAO.getAuthorsMapFromImage(image.get());
			
			ctx.setVariable("comments", comments.keySet());
			ctx.setVariable("commentsMap", comments);
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
		
		templateEngine.process("image", ctx, response.getWriter());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	public void destroy() {
		super.destroy();
		try {
			if (this.albumDAO != null) {
				this.albumDAO.close();
			}
			if (this.imageDAO != null) {
				this.imageDAO.close();
			}
			if (this.commentDAO != null) {
				this.commentDAO.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
