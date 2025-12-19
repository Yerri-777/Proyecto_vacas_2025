package com.example.backend.servlets;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RootServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Redirect to the packaged SPA if present
        String context = req.getContextPath();
        String target = context + "/browser/index.html";
        resp.sendRedirect(target);
    }
}
