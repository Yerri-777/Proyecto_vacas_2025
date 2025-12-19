package com.example.backend.servlets;

import com.example.backend.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/admin")
public class AdminServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><meta charset=\"utf-8\"><title>Admin - Estado</title></head><body>");
        out.println("<h1>Estado del backend</h1>");

        // Check DB connection
        out.println("<h2>Base de datos</h2>");
        try (Connection c = DBConnection.getConnection()) {
            if (c != null && !c.isClosed()) {
                out.println("<p style='color:green'>Conectado a la base de datos.</p>");
            } else {
                out.println("<p style='color:orange'>Conexión devuelta nula o cerrada.</p>");
            }
        } catch (SQLException ex) {
            out.println("<p style='color:red'>No se pudo conectar a la base de datos: " + escapeHtml(ex.getMessage()) + "</p>");
        } catch (Exception ex) {
            out.println("<p style='color:red'>Error al comprobar la base de datos: " + escapeHtml(ex.toString()) + "</p>");
        }

        // List registered servlets and their mappings
        out.println("<h2>Servlets registrados</h2>");
        out.println("<ul>");
        try {
            Map<String, ? extends ServletRegistration> regs = req.getServletContext().getServletRegistrations();
            for (Map.Entry<String, ? extends ServletRegistration> e : regs.entrySet()) {
                ServletRegistration reg = e.getValue();
                out.print("<li><strong>" + escapeHtml(e.getKey()) + "</strong> - class: " + escapeHtml(reg.getClassName()));
                if (reg.getMappings() != null && !reg.getMappings().isEmpty()) {
                    out.print(" - mappings: ");
                    boolean first = true;
                    for (String m : reg.getMappings()) {
                        if (!first) out.print(", ");
                        out.print("<a href='" + escapeHtml(m) + "'>" + escapeHtml(m) + "</a>");
                        first = false;
                    }
                }
                out.println("</li>");
            }
        } catch (Exception ex) {
            out.println("<li>Error listando servlets: " + escapeHtml(ex.toString()) + "</li>");
        }
        out.println("</ul>");

        out.println("</body></html>");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
