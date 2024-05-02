package it.polimi.tiw.utils;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SignUtility {
	private String template;
	private HttpServletResponse response;
	private WebContext ctx;
	private TemplateEngine templateEngine;
	
	public SignUtility(HttpServletResponse response, TemplateEngine templateEngine, WebContext ctx, String template) {
		this.response = response;
		this.templateEngine = templateEngine;
		this.ctx = ctx;
		this.template = template;
	}
	
	public void invalidFormData(String errorMessage) throws IOException {
		ctx.setVariable("error", true);
		ctx.setVariable("errorMessage", errorMessage);
		
		templateEngine.process(template, ctx, response.getWriter());
	}
	
	public static boolean isEmailValid(String email) {
		// RFC 5322 regular expression for email address validation
		String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	    
	    return Pattern.compile(regexPattern).matcher(email).matches();
	}
	
	public static boolean isNullOrBlank(String string) {
		return string == null || string.isBlank();
	}
	
	public static String hashAndSalt(String string) {
		return BCrypt.withDefaults().hashToString(12, string.toCharArray());
	}
	
	public static boolean verifyEqualityToHash(String unhashedString, String hashedString) {
		return BCrypt.verifyer().verify(unhashedString.toCharArray(), hashedString).verified;
	}
}
