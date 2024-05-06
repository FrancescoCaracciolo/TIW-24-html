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
import it.polimi.tiw.utils.AlbumUtility;
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If a session doesn't exist or it's invalid, redirect to signin page
		boolean isSessionInvalid = SessionUtility.redirectOnInvalidSession("logout", request, response);
		if (isSessionInvalid)
			return;

		WebContext ctx = new WebContext(request, response, getServletContext(), response.getLocale());
		// Get the logged user from the session and export it to the web context
		Person user = (Person) request.getSession().getAttribute("user");

		CreateAlbumUtility albumUtil = new CreateAlbumUtility(response, templateEngine, ctx, "create_album", imageDAO,
				user);

		// Get the logger user's albums and export them to the web context
		try {
			albumUtil.validFromatData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		CreateAlbumUtility albumUtil = new CreateAlbumUtility(response, templateEngine, ctx, "create_album", imageDAO,
				user);
		ServletContext context = request.getServletContext();

		// Check title param
		String title = request.getParameter("album-title");
		if (title == "") {
			albumUtil.invalidFormData("Wrong title given");
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
		if (title.equals("")) {
			albumUtil.invalidFormData("No title provided");
			return;
		}

		// Receive the file
		Part imagePart = request.getPart("image");
		if (!imagePart.getContentType().split("/")[0].equals("image")) {
			albumUtil.invalidFormData("The file given was not an image");
			return;
		}
		InputStream imageContent = imagePart.getInputStream();
		String submitted = imagePart.getSubmittedFileName();
		String extension = submitted.substring(submitted.lastIndexOf("."));
		imagePart.getSubmittedFileName().lastIndexOf(".");
		String image_path = albumUtil.getRandomFilePath(context.getInitParameter("uploadDir"), extension);
		String full_path = context.getInitParameter("uploadDir") + image_path;

		try {
			// Try saving the file
			Files.copy(imageContent, new File(full_path).toPath());
		} catch (Exception e) {
			System.out.println(new File(full_path).toPath());
			e.printStackTrace();
			albumUtil.invalidFormData("Error saving the file");
			return;
		}
		// Try saving the image in the database
		try {
			imageDAO.save(image_path, title, String.valueOf(user.getId()));
		} catch (SQLException e) {
			albumUtil.invalidFormData("Error connecting to the database");
			return;
		}
		response.sendRedirect("createAlbum");
	}
}
