package it.polimi.tiw.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.GeneralUtility;

@WebServlet("/addComment")
public class AddCommentServlet extends ThymeleafServlet {
    private static final long serialVersionUID = 7259568501736798325L;
    private ImageDAO imageDAO;
    private CommentDAO commentDAO;

	public AddCommentServlet() {
        super();
    }
	
	public void init() throws ServletException {
		super.init();
		try {
			imageDAO = new ImageDAO(this.dbConnection);
			commentDAO = new CommentDAO(this.dbConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		
		// Get the logged user from the session and export it to the web context
		Person user = (Person) request.getSession().getAttribute("user");
		String imgParameter = request.getParameter("imgId");
		String text = request.getParameter("text");
		
		// Check validity of the parameters
		if (!GeneralUtility.isValidNumericParameter(imgParameter) || text == null || text.equals("") ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		// Check if the text is not too long
		Integer imgId = Integer.parseInt(imgParameter);
		if (text.length() > 4096) {
			response.sendRedirect("image?imgId=" + String.valueOf(imgId) + "&albumId=" + request.getParameter("albumId") + "&error=" + URLEncoder.encode("The comment is too long", "UTF-8"));
			return;
		}
		try {
			Optional<Image> img = imageDAO.get(imgId);
			// If the image exists, save the comment
			if (img.isPresent()) {
				commentDAO.save(text, String.valueOf(imgId), String.valueOf(user.getId()));
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;			
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		response.sendRedirect("image?imgId=" + String.valueOf(imgId) + "&albumId=" + request.getParameter("albumId"));
	}
	
	public void destroy() {
		super.destroy();
		try {
			if (this.commentDAO != null) {
				this.commentDAO.close();
			}
			if (this.imageDAO != null) {
				this.imageDAO.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}