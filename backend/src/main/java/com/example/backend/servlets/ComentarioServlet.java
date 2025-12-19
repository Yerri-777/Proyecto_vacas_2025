package com.example.backend.servlets;

import com.example.backend.dao.ComentarioDAO;
import com.example.backend.models.Comentario;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/comentario")
public class ComentarioServlet extends BaseServlet {
    private final ComentarioDAO dao = new ComentarioDAO();
    private final Gson gson = new Gson();

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String vid = req.getParameter("videojuegoId");
        if (vid == null) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"videojuegoId es requerido\"}"); return; }
        try {
            List<Comentario> list = dao.listByVideojuego(Integer.parseInt(vid));
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(list));
        } catch (NumberFormatException nfe) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"videojuegoId inválido\"}"); }
        catch (Exception e) { resp.setStatus(500); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer actingUser = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId")!=null) actingUser = (Integer) session.getAttribute("userId");

        try (BufferedReader br = req.getReader()) {
            Comentario c;
            try { c = gson.fromJson(br, Comentario.class); }
            catch (JsonSyntaxException ex) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"JSON inválido\"}"); AuditLogger.log("comentario:create JSON inválido", actingUser, req.getRemoteAddr()); return; }

            if (c == null || c.getUsuarioId() <= 0 || c.getVideojuegoId() <= 0) {
                resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"usuarioId y videojuegoId son requeridos\"}"); AuditLogger.log("comentario:create datos incompletos", actingUser, req.getRemoteAddr()); return;
            }
            c.setTexto(InputSanitizer.sanitize(c.getTexto(), 1000));
            if (isBlank(c.getTexto())) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Texto requerido\"}"); AuditLogger.log("comentario:create texto vacío", actingUser, req.getRemoteAddr()); return; }
            if (c.getPuntuacion() < 1 || c.getPuntuacion() > 5) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Puntuación debe estar entre 1 y 5\"}"); AuditLogger.log("comentario:create puntuación inválida", actingUser, req.getRemoteAddr()); return; }
            if (c.getFecha() == null) c.setFecha(new java.util.Date());

            try {
                int id = dao.create(c);
                c.setId(id);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(c));
                AuditLogger.log("comentario:create éxito id=" + id, actingUser, req.getRemoteAddr());
            } catch (SQLException sqlEx) {
                resp.setStatus(409); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"No se pudo guardar el comentario\"}");
                AuditLogger.log("comentario:create fallo SQL", actingUser, req.getRemoteAddr());
            } catch (Exception ex) { resp.setStatus(500); AuditLogger.log("comentario:create error interno", actingUser, req.getRemoteAddr()); }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer actingUser = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId")!=null) actingUser = (Integer) session.getAttribute("userId");

        String id = req.getParameter("id");
        if (id == null) { resp.setStatus(400); return; }
        try {
            boolean ok = dao.delete(Integer.parseInt(id));
            resp.setStatus(ok ? 200 : 404);
            AuditLogger.log("comentario:delete id=" + id + " ok=" + ok, actingUser, req.getRemoteAddr());
        } catch (NumberFormatException nfe) { resp.setStatus(400); } catch (Exception e) { resp.setStatus(500); AuditLogger.log("comentario:delete error id=" + id, actingUser, req.getRemoteAddr()); }
    }
}
