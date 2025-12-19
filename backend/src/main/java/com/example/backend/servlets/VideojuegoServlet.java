package com.example.backend.servlets;

import com.example.backend.dao.VideojuegoDAO;
import com.example.backend.models.Videojuego;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.example.backend.enums.Role;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/videojuego")
public class VideojuegoServlet extends BaseServlet {
    private final VideojuegoDAO dao = new VideojuegoDAO();
    private final Gson gson = new Gson();

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String empresaParam = req.getParameter("empresaId");
        try {
            if (empresaParam != null) {
                int emp = Integer.parseInt(empresaParam);
                List<Videojuego> list = dao.listByEmpresa(emp);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(list));
            } else {
                resp.setStatus(400);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":\"empresaId es requerido\"}");
            }
        } catch (NumberFormatException nfe) {
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"empresaId inválido\"}");
        } catch (Exception e) { resp.setStatus(500); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer actingUser = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId")!=null) actingUser = (Integer) session.getAttribute("userId");

        try (BufferedReader br = req.getReader()) {
            Videojuego v;
            try { v = gson.fromJson(br, Videojuego.class); }
            catch (JsonSyntaxException ex) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"JSON inválido\"}"); AuditLogger.log("videojuego:create JSON inválido", actingUser, req.getRemoteAddr()); return; }

            if (v == null) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Cuerpo vacío\"}"); AuditLogger.log("videojuego:create cuerpo vacío", actingUser, req.getRemoteAddr()); return; }

            v.setNombre(InputSanitizer.sanitize(v.getNombre(), 150));
            v.setDescripcion(InputSanitizer.sanitize(v.getDescripcion(), 1000));

            if (v.getEmpresaId() <= 0 || v.getNombre()==null || v.getNombre().isEmpty()) {
                resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Nombre y empresaId son requeridos\"}"); AuditLogger.log("videojuego:create datos incompletos", actingUser, req.getRemoteAddr()); return;
            }
            if (v.getPrecio() < 0) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Precio inválido\"}"); AuditLogger.log("videojuego:create precio inválido", actingUser, req.getRemoteAddr()); return; }

            try {
                int id = dao.create(v);
                v.setId(id);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(v));
                AuditLogger.log("videojuego:create éxito id=" + id, actingUser, req.getRemoteAddr());
            } catch (SQLException sqlEx) {
                resp.setStatus(409); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"No se pudo crear videojuego\"}");
                AuditLogger.log("videojuego:create fallo SQL", actingUser, req.getRemoteAddr());
            } catch (Exception ex) { resp.setStatus(500); AuditLogger.log("videojuego:create error interno", actingUser, req.getRemoteAddr()); }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer actingUser = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId")!=null) actingUser = (Integer) session.getAttribute("userId");

        try (BufferedReader br = req.getReader()) {
            Videojuego v;
            try { v = gson.fromJson(br, Videojuego.class); }
            catch (JsonSyntaxException ex) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"JSON inválido\"}"); AuditLogger.log("videojuego:update JSON inválido", actingUser, req.getRemoteAddr()); return; }

            if (v == null || v.getId() <= 0) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Id requerido\"}"); AuditLogger.log("videojuego:update id faltante", actingUser, req.getRemoteAddr()); return; }

            v.setNombre(InputSanitizer.sanitize(v.getNombre(), 150));
            v.setDescripcion(InputSanitizer.sanitize(v.getDescripcion(), 1000));

            try {
                boolean ok = dao.update(v);
                resp.setStatus(ok ? 200 : 404);
                AuditLogger.log("videojuego:update id=" + v.getId() + " ok=" + ok, actingUser, req.getRemoteAddr());
            } catch (SQLException sqlEx) {
                resp.setStatus(409); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Error de integridad\"}");
                AuditLogger.log("videojuego:update fallo SQL id=" + v.getId(), actingUser, req.getRemoteAddr());
            } catch (Exception ex) { resp.setStatus(500); AuditLogger.log("videojuego:update error interno id=" + v.getId(), actingUser, req.getRemoteAddr()); }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer actingUser = null;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId")!=null) actingUser = (Integer) session.getAttribute("userId");

        String idParam = req.getParameter("id");
        if (idParam == null) { resp.setStatus(400); return; }
        try {
            int id = Integer.parseInt(idParam);
            boolean ok = dao.delete(id);
            resp.setStatus(ok ? 200 : 404);
            AuditLogger.log("videojuego:delete id=" + id + " ok=" + ok, actingUser, req.getRemoteAddr());
        } catch (NumberFormatException nfe) { resp.setStatus(400); } catch (Exception e) { resp.setStatus(500); AuditLogger.log("videojuego:delete error id=" + idParam, actingUser, req.getRemoteAddr()); }
    }
}
