package it.polimi.tiw.servlets;


import javax.servlet.ServletException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public abstract class ThymeleafServlet extends DataServlet {
    private static final long serialVersionUID = -4286758107289165886L;
    
	protected TemplateEngine templateEngine;
	
    public ThymeleafServlet() {
        super();
	}
    
    public void init() throws ServletException {
    	super.init();		
		
		// Initialize and setup the TemplateEngine
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	
	}
}
