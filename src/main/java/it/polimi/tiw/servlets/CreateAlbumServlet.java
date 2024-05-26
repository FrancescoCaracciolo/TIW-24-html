package it.polimi.tiw.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Optional;
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

@WebServlet("/createAlbum")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
		maxFileSize = 1024 * 1024 * 10, // 10 MB
		maxRequestSize = 1024 * 1024 * 100 // 100MB
)
public class CreateAlbumServlet extends ThymeleafServlet {
	private static final long serialVersionUID = -7297515526961117240L;

	private AlbumDAO albumDAO;
	private ImageDAO imageDAO;

	public CreateAlbumServlet() {
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
		// If a session doesn't exist or it's invalid, redirect to signin page
		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		// Get the logged user from the session and export it to the web context
		Person user = (Person) request.getSession().getAttribute("user");

		CreateAlbumUtility albumUtil = new CreateAlbumUtility(response, templateEngine, ctx, "create_album", imageDAO, user);

		// Get the logger user's albums and export them to the web context
		try {
			albumUtil.validFromatData();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check session
		boolean isSessionInvalid = SessionUtility.redirectOnInvalidSession("logout", request, response);
		if (isSessionInvalid)
			return;
		if (request.getParameter("image-title") != null) {
			// Do image upload
			this.saveImage(request, response);
		} else if (request.getParameter("album-title") != null) {
			// Create album
			this.createAlbum(request, response);
		} else {
			this.doGet(request, response);
		}
	}

	private void createAlbum(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Person user = (Person) request.getSession().getAttribute("user");

		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		CreateAlbumUtility albumUtil = new CreateAlbumUtility(response, templateEngine, ctx, "create_album", imageDAO, user);
		ServletContext context = request.getServletContext();

		// Check title param
		String title = request.getParameter("album-title");
		if (title == "" || title.length() > 30) {
			albumUtil.invalidFormData("Wrong title given, the title must be at least one character and maximum 30 characters");
			return;
		}

		try {
			// Get the list of the given parameters
			Set<String> parameters = request.getParameterMap().keySet();
			int[] ids = parameters.stream().filter(CreateAlbumUtility::isNumeric).mapToInt(Integer::parseInt).toArray();
			// Check that every id is an image from this user
			for (int id : ids) {
				Optional<Image> img = imageDAO.get(id);
				if (img.isEmpty() || img.get().getUploaderId() != user.getId()) {
					albumUtil.invalidFormData("Wrong image selected");
					return;
				}
			}
			if (ids.length < 1) {
				albumUtil.invalidFormData("You have to choose at least one image to create an album");
				return;
			}

			// Save the album
			Optional<Album> album = albumDAO.save(title, String.valueOf(user.getId()));
			// Save the images
			for (int id : ids) {
				if (request.getParameter(String.valueOf(id)).equals("on"))
					albumDAO.addImage(album.get().getId(), id);
			}
			// Send redirect to home
			response.sendRedirect("home");
		} catch (SQLException e) {
			albumUtil.invalidFormData("Could not connect to the database");
			return;
		}
	}

	private void saveImage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Person user = (Person) request.getSession().getAttribute("user");

		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		CreateAlbumUtility albumUtil = new CreateAlbumUtility(response, templateEngine, ctx, "create_album", imageDAO,
				user);
		ServletContext context = request.getServletContext();

		// The user is trying to upload an image
		String title = request.getParameter("image-title");
		String description = request.getParameter("image-description");
		if (title == null || title.isEmpty() || title.length() > 30) {
			albumUtil.invalidFormData("Wrong title given, the title must be at least one character and maximum 30 characters");
			return;
		}
		if (description == null || description.isEmpty() || description.length() > 4096) {
			albumUtil.invalidFormData("Wrong description given, the description must be at least one character and maximum 4096 characters");
			return;	
		}

		// Receive the file
		Part imagePart = request.getPart("image");
		if (imagePart.getContentType() != null && !imagePart.getContentType().split("/")[0].equals("image")) {
			albumUtil.invalidFormData("The file given was not an image");
			return;
		}
		InputStream imageContent = imagePart.getInputStream();
		String submitted = imagePart.getSubmittedFileName();
		String extension = submitted.substring(submitted.lastIndexOf("."));
		imagePart.getSubmittedFileName().lastIndexOf(".");
		String image_path = CreateAlbumUtility.getRandomFilePath(context.getInitParameter("uploadDir"), extension);
		String full_path = context.getInitParameter("uploadDir") + image_path;

		try {
			// Try saving the file
			Files.copy(imageContent, new File(full_path).toPath());
		} catch (Exception e) {
			e.printStackTrace();
			albumUtil.invalidFormData("Error saving the file");
			return;
		}
		// Try saving the image in the database
		try {
			imageDAO.save(image_path, title, description, String.valueOf(user.getId()));
		} catch (SQLException e) {
			albumUtil.invalidFormData("Error connecting to the database");
			return;
		}
		response.sendRedirect("createAlbum");
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
