package org.unikn.eurasim.servlets;

import org.locationtech.jts.geom.GeometryFactory;
import org.unikn.eurasim.util.PostgresLoader;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebListener
public class EvacuationPlannerStartupLoader implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Configuration.load("file.config");
        EvacuationPlanner.g =  PostgresLoader.loadGraph();

        //System.out.println("[LOG] Graph loaded: nodes = "+EvacuationPlanner.g.vertexSet().size()+ ", edges = "+EvacuationPlanner.g.edgeSet().size());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is added to a session. */
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is removed from a session. */
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is replaced in a session. */
    }
}
