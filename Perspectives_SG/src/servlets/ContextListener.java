package servlets;

import java.util.Hashtable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import perspectives.Environment;

public class ContextListener implements ServletContextListener {
    
    Hashtable<VizOnlineServlet, Environment> envs = null;

    public void contextInitialized(ServletContextEvent sce) {
    	System.out.println("creating the vizonline context");
    	envs = new Hashtable<VizOnlineServlet, Environment>();
    	
    	sce.getServletContext().setAttribute("envs", envs);
    	
    }

    public void contextDestroyed(ServletContextEvent sce){
    	System.out.println("clearing the vizonline context");
    	((Hashtable<VizOnlineServlet, Environment>)sce.getServletContext().getAttribute("envs")).clear();
    }

}
