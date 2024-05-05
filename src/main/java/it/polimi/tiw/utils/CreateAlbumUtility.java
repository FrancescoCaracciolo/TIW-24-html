package it.polimi.tiw.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import at.favre.lib.crypto.bcrypt.BCrypt;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.dao.ImageDAO;

public class CreateAlbumUtility {
	private String template;
	private HttpServletResponse response;
	private WebContext ctx;
	private TemplateEngine templateEngine;
	private ImageDAO imageDAO;
	private Person user;
	
	public CreateAlbumUtility(HttpServletResponse response, TemplateEngine templateEngine, WebContext ctx, String template, ImageDAO imageDAO, Person user ) {
		this.response = response;
		this.templateEngine = templateEngine;
		this.ctx = ctx;
		this.template = template;
		this.imageDAO = imageDAO;
		this.user = user;
	}
	
	public void invalidFormData(String errorMessage) throws IOException {
		ctx.setVariable("error", true);
		ctx.setVariable("errorMessage", errorMessage);
		try {
			this.addNormalVars();
		} catch (Exception e) {
			e.printStackTrace();
		}
		templateEngine.process(template, ctx, response.getWriter());
	}
	
	public void validFromatData() throws IOException, SQLException {
		this.addNormalVars();
		templateEngine.process(template, ctx, response.getWriter());
	}
	
	private void addNormalVars() throws SQLException {
		ctx.setVariable("user", user);
		List<Image> userPhotos = imageDAO.getPersonImages(user);
		ctx.setVariable("userPhotos", userPhotos);
	}
	
	/**
	 * @param targetPath path to check, ending with /
	 * @param extension file extension
	 * @return File name (ex. image.png)
	 */
	public static String getRandomFilePath(String targetPath, String extension) {
	    int targetStringLength = 10;
	    Random random = new Random();
	    boolean file_exists = true;
	    String generatedString = "";
	    while (file_exists) {
		    // Generate random alphanumeric string of 10 characters
		    generatedString = random.ints(48, 122 + 1)
		      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
		      .limit(targetStringLength)
		      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		      .toString();
		    // Check if path exists
		    File f = new File(targetPath + generatedString + "." + extension);
		    if (!f.exists() && !f.isDirectory()) {
		    	file_exists = false;
		    }
	    }
	    return generatedString + "." + extension;
	}
	
	public static boolean isNumeric(String s) {
		if (s==null) {
			return false;
		}
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
