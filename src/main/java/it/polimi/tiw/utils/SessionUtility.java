package it.polimi.tiw.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionUtility {
	
	public static boolean redirectOnValidSession(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		
		boolean sessionValid = session != null && request.isRequestedSessionIdValid() && session.getAttribute("user") != null;
		
		if (sessionValid) {
			response.sendRedirect(redirectPath);
		}
		
		return sessionValid;
	}
	
	public static boolean redirectOnInvalidSession(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		
		boolean sessionValid = session != null && request.isRequestedSessionIdValid() && session.getAttribute("user") != null;
		
		if (!sessionValid) {
			response.sendRedirect(redirectPath);
		}
		
		return !sessionValid;
	}
	
}
