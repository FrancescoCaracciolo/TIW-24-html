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
}
