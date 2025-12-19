package com.example.backend;

import javax.servlet.annotation.WebServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.connector.Connector;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;

public class AppMain {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }

        System.out.println("Starting embedded Tomcat on port " + port);

        // Check DB connection early
        try (Connection c = DBConnection.getConnection()) {
            if (c != null && !c.isClosed()) System.out.println("Database: CONNECTED");
            else System.out.println("Database: NULL or CLOSED");
        } catch (Exception ex) {
            System.out.println("Database: ERROR -> " + ex.getMessage());
        }

        Tomcat tomcat = new Tomcat();

        // Create connector explicitly and bind to 0.0.0.0 to ensure accessibility
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        try {
            connector.setProperty("address", "0.0.0.0");
        } catch (Exception ignored) {}
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);

        // Select a sensible webapp directory. Prefer the development folder, but
        // fall back to a packaged target folder if present (useful when running
        // from the built artifacts).
        String[] candidates = new String[] {
            "src/main/webapp",
            "target/tienda-backend-1.0.0",
            "target/tienda-backend-1.0.0/"  // sometimes the browser files live here
        };
        File webapp = null;
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists()) { webapp = f; break; }
        }
        if (webapp == null) {
            // Ensure the dev webapp folder exists so Tomcat can serve something
            webapp = new File("src/main/webapp");
            webapp.mkdirs();
        }

        System.out.println("Using webapp directory: " + webapp.getAbsolutePath() + " (exists=" + webapp.exists() + ")");
        Context ctx = tomcat.addWebapp(DBConnection.APP_CONTEXT, webapp.getAbsolutePath());

        // Ensure the 'browser' folder (packaged SPA) is exposed as a web resource
        try {
            File browserDir = new File(webapp, "browser");
            if (browserDir.exists()) {
                WebResourceRoot resources = new StandardRoot(ctx);
                // Expose browser assets both under /browser and directly under root
                resources.addPreResources(new DirResourceSet(resources, "/browser", browserDir.getAbsolutePath(), "/"));
                resources.addPreResources(new DirResourceSet(resources, "/", browserDir.getAbsolutePath(), "/"));
                ctx.setResources(resources);
            }
        } catch (Throwable t) {
            System.out.println("Warning: could not add browser resource mapping: " + t.getMessage());
        }

        // Set a friendly welcome file so requesting the context root serves the SPA
        // If you build the frontend into `browser/index.html`, prefer that.
        try {
            // Prefer serving the packaged SPA index if present
            if (new File(webapp, "browser/index.html").exists()) {
                ctx.addWelcomeFile("browser/index.html");
            } else if (new File(webapp, "index.html").exists()) {
                ctx.addWelcomeFile("index.html");
            }
        } catch (Exception ignored) { }

        // Register servlets found in package com.example.backend.servlets using their @WebServlet mapping
        String[] servletClasses = new String[] {
            "AuthServlet",
            "RootServlet",
            "BannerServlet",
            "BaseServlet",
            "CategoriaServlet",
            "ComentarioServlet",
            "CompraServlet",
            "EmpresaServlet",
            "HealthServlet",
            "ReportServlet",
            "UploadServlet",
            "VideojuegoServlet",
            "AccountServlet",
            "AdminServlet"
        };

        String pkg = "com.example.backend.servlets.";
        for (String sc : servletClasses) {
            String fqcn = pkg + sc;
            try {
                Class<?> cls = Class.forName(fqcn);
                WebServlet ann = cls.getAnnotation(WebServlet.class);
                String name = sc;
                tomcat.addServlet(ctx, name, cls.getName());

                // Collect mappings and register them; print one clean line per servlet
                java.util.List<String> mapped = new java.util.ArrayList<>();
                if (ann != null) {
                    String[] mappings = ann.value();
                    if (mappings == null || mappings.length == 0) mappings = ann.urlPatterns();
                    if (mappings != null) {
                        for (String m : mappings) {
                            if (m == null) continue;
                            String mm = m.trim();
                            if (mm.isEmpty()) continue;
                            ctx.addServletMappingDecoded(mm, name);
                            mapped.add(mm);
                        }
                    }
                }

                if (mapped.isEmpty()) {
                    System.out.println("Registered servlet: " + fqcn + " (no mappings)");
                } else {
                    System.out.println("Mapped " + fqcn + " -> " + String.join(", ", mapped));
                }

            } catch (ClassNotFoundException cnf) {
                System.out.println("Servlet class not found (skipping): " + fqcn);
            } catch (Exception ex) {
                System.out.println("Error registering servlet " + fqcn + ": " + ex.getMessage());
            }
        }

        tomcat.start();
        String baseUrl = "http://localhost:" + port + DBConnection.APP_CONTEXT + "/";
        System.out.println("Server started: " + baseUrl);

        // Try to open the default browser to the app root (best-effort)
        try {
            java.awt.Desktop desktop = java.awt.Desktop.isDesktopSupported() ? java.awt.Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                final java.net.URI uri = new java.net.URI(baseUrl);
                new Thread(() -> {
                    try { desktop.browse(uri); } catch (Exception ignored) {}
                }).start();
            }
        } catch (Throwable t) {
            // ignore if running headless or unsupported
        }

        tomcat.getServer().await();
    }
}
