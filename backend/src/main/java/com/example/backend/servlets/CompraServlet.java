package com.example.backend.servlets;

import com.example.backend.dao.CompraDAO;
import com.example.backend.models.Compra;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebServlet("/api/compra")
public class CompraServlet extends BaseServlet {
    private final CompraDAO dao = new CompraDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String usuarioParam = req.getParameter("usuarioId");
        if (usuarioParam == null) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"usuarioId es requerido\"}"); return; }
        try {
            int uid = Integer.parseInt(usuarioParam);
            List<Compra> list = dao.listByUsuario(uid);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(list));
        } catch (NumberFormatException nfe) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"usuarioId inválido\"}"); }
        catch (Exception e) { resp.setStatus(500); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer actingUser = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId")!=null) actingUser = (Integer) session.getAttribute("userId");

        try (BufferedReader br = req.getReader()) {
            Compra c;
            try { c = gson.fromJson(br, Compra.class); }
            catch (JsonSyntaxException ex) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"JSON inválido\"}"); AuditLogger.log("compra:create JSON inválido", actingUser, req.getRemoteAddr()); return; }

            if (c == null || c.getUsuarioId() <= 0 || c.getVideojuegoId() <= 0) {
                resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"usuarioId y videojuegoId son requeridos\"}"); AuditLogger.log("compra:create datos incompletos", actingUser, req.getRemoteAddr()); return;
            }
            if (c.getTotal() < 0) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Total inválido\"}"); AuditLogger.log("compra:create total inválido", actingUser, req.getRemoteAddr()); return; }
            if (c.getFecha() == null) c.setFecha(new Date());

            try {
                int id = dao.create(c);
                c.setId(id);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(c));
                AuditLogger.log("compra:create éxito id=" + id, actingUser, req.getRemoteAddr());
            } catch (SQLException sqlEx) {
                resp.setStatus(409); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"No se pudo registrar la compra\"}");
                AuditLogger.log("compra:create fallo SQL", actingUser, req.getRemoteAddr());
            } catch (Exception ex) { resp.setStatus(500); AuditLogger.log("compra:create error interno", actingUser, req.getRemoteAddr()); }
        }
    }
}
