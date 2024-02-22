package com.studyverse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Studyverse {
    public static void main(String[] args) {
        Server server = new Server(8080);
        WebAppContext webapp = new WebAppContext("src/main/webapp", "");
        server.setHandler(webapp);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
