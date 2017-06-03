package ch.hsr.isf.serepo.server;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class SeRepoServer {

    private Server server;

    public static SeRepoServer create(int port) throws IOException {
        SeRepoServer seRepoServer = new SeRepoServer();
        seRepoServer.server = createHttpServer(port);
        return seRepoServer;
    }

    public static Server createHttpServer(int port) throws IOException {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/serepo");
        ServletHolder h = context.addServlet(org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class, "/*");
        // We need to do the old way (see Section "3.6. Standalone Resteasy in Older Servlet Containers" in https://docs.jboss.org/resteasy/docs/3.0.6.Final/userguide/html_single/#Installation_Configuration),
        // because the scanning does not work in esotoeric folders - see https://stackoverflow.com/a/21251781/873282
        h.setInitParameter("javax.ws.rs.Application", "ch.hsr.isf.serepo.rest.SeRepoRestApplication");
        h.setInitParameter("resteasy.resources",
                "ch.hsr.isf.serepo.rest.resources.repos.ReposResource," +
                "ch.hsr.isf.serepo.rest.resources.commits.CommitsResource," +
                "ch.hsr.isf.serepo.rest.resources.consistencies.ConsistenciesResource," +
                "ch.hsr.isf.serepo.rest.resources.repository.RepositoryResource," +
                "ch.hsr.isf.serepo.rest.resources.search.SearchResource," +
                "ch.hsr.isf.serepo.rest.resources.seitems.SeItemsResource"
        );
        h.setInitOrder(1);
        Server server = new Server(port);
        server.setHandler(context);
        return server;
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

}