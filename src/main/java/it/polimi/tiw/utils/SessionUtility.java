package it.polimi.tiw.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionUtility {
	
	public static void redirectOnValidSession(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		
		if (session != null && request.isRequestedSessionIdValid()) {
			response.sendRedirect(redirectPath);        
		}
	}
	
}
