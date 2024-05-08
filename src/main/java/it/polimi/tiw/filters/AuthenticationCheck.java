package it.polimi.tiw.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.utils.SessionUtility;

public class AuthenticationCheck implements Filter {

	public AuthenticationCheck() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();

		System.out.println("Filtering...");
		boolean sessionInvalid = SessionUtility.redirectOnInvalidSession("logout", req, res);
		if (!sessionInvalid) 
			chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}