package it.polimi.tiw.utils;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

public class GeneralUtility {
	public static <T> void setCtxVariableIfPresent(WebContext ctx, HttpServletResponse response, String name, Optional<T> value) throws IOException {
		if (value.isPresent()) {
			ctx.setVariable(name, value.get());
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	public static boolean isValidNumericParameter(String param) {
		if (param == null || param.equals("") || !isNumeric(param) ) {
			return false;
		}
		return true;
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
