package com.example.backend.servlets;

import com.example.backend.DBConnection;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/api/health")
public class HealthServlet extends BaseServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (Connection c = DBConnection.getConnection()) {
            boolean ok = c != null && !c.isClosed();
            resp.getWriter().write(gson.toJson(java.util.Map.of("status", ok ? "ok" : "db-unavailable")));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(java.util.Map.of("status", "error", "message", e.getMessage())));
        }
    }
}
