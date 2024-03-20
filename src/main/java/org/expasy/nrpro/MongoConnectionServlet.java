package org.expasy.nrpro;

import db.MongoConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MongoConnectionServlet implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Init");
        System.out.println("mongodb has been initialized");
      //  ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
        MongoConnection.getInstance();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        MongoConnection mongoConnection=MongoConnection.getInstance();
        mongoConnection.close();
        System.out.println("Finished");
    }

}
