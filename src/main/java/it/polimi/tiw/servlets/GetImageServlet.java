package it.polimi.tiw.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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

@WebServlet("/images/*")
public class GetImageServlet extends DataServlet {
    private static final long serialVersionUID = -7297515526961117240L;
    
    private AlbumDAO albumDAO;
	private ImageDAO imageDAO;

	public GetImageServlet() {
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
		// Initialize variables
		String currentPath = request.getPathInfo();
		ServletContext context = request.getServletContext();

		String image_path = URLDecoder.decode(currentPath.replaceFirst("/", ""), "UTF-8");
		String full_path = context.getInitParameter("uploadDir") + image_path;
		String mime = getServletContext().getMimeType(full_path);
		File f = new File(full_path);
		if (!f.exists() || f.isDirectory()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		response.setHeader("Content-Type", mime);
		response.setHeader("Content-Length", String.valueOf(f.length()));
		Files.copy(f.toPath(), response.getOutputStream());
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
