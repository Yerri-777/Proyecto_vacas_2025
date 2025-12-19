package com.example.backend.servlets;

import com.example.backend.dao.EmpresaDAO;
import com.example.backend.models.Empresa;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/empresa")
public class EmpresaServlet extends BaseServlet {
    private final EmpresaDAO dao = new EmpresaDAO();
    private final Gson gson = new Gson();

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Empresa> list = dao.listAll();
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(list));
        } catch (Exception e) {
            resp.setStatus(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader br = req.getReader()) {
            Empresa e;
            try { e = gson.fromJson(br, Empresa.class); }
            catch (JsonSyntaxException ex) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"JSON inválido\"}"); AuditLogger.log("empresa:create JSON inválido", null, req.getRemoteAddr()); return; }

            if (e == null) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Cuerpo vacío\"}"); AuditLogger.log("empresa:create cuerpo vacío", null, req.getRemoteAddr()); return; }

            // Sanitizar
            e.setNombre(InputSanitizer.sanitize(e.getNombre(), 150));
            e.setCorreo(InputSanitizer.sanitize(e.getCorreo(), 150));
            e.setTelefono(InputSanitizer.sanitize(e.getTelefono(), 50));

            if (e.getNombre()==null || e.getNombre().isEmpty() || e.getCorreo()==null || e.getCorreo().isEmpty()) {
                resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Nombre y correo son requeridos\"}");
                AuditLogger.log("empresa:create datos incompletos", null, req.getRemoteAddr());
                return;
            }

            try {
                int id = dao.create(e);
                e.setId(id);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(e));
                AuditLogger.log("empresa:create éxito id=" + id, null, req.getRemoteAddr());
            } catch (SQLException sqlEx) {
                resp.setStatus(409); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"No se pudo crear la empresa\"}");
                AuditLogger.log("empresa:create fallo SQL", null, req.getRemoteAddr());
            } catch (Exception ex) { resp.setStatus(500); AuditLogger.log("empresa:create error interno", null, req.getRemoteAddr()); }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader br = req.getReader()) {
            Empresa e;
            try { e = gson.fromJson(br, Empresa.class); }
            catch (JsonSyntaxException ex) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"JSON inválido\"}"); AuditLogger.log("empresa:update JSON inválido", null, req.getRemoteAddr()); return; }

            if (e == null || e.getId() <= 0) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Id requerido\"}"); AuditLogger.log("empresa:update id faltante", null, req.getRemoteAddr()); return; }

            e.setNombre(InputSanitizer.sanitize(e.getNombre(), 150));
            e.setCorreo(InputSanitizer.sanitize(e.getCorreo(), 150));
            e.setTelefono(InputSanitizer.sanitize(e.getTelefono(), 50));

            try {
                boolean ok = dao.update(e);
                resp.setStatus(ok ? 200 : 404);
                AuditLogger.log("empresa:update id=" + e.getId() + " ok=" + ok, e.getId(), req.getRemoteAddr());
            } catch (SQLException sqlEx) {
                resp.setStatus(409); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Error de integridad\"}");
                AuditLogger.log("empresa:update fallo SQL id=" + e.getId(), e.getId(), req.getRemoteAddr());
            } catch (Exception ex) { resp.setStatus(500); AuditLogger.log("empresa:update error interno id=" + e.getId(), e.getId(), req.getRemoteAddr()); }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) { resp.setStatus(400); return; }
        try {
            int id = Integer.parseInt(idParam);
            try {
                if (dao.hasVideojuegos(id)) {
                    resp.setStatus(409);
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"error\":\"La empresa tiene videojuegos, no puede eliminarse\"}");
                    return;
                }
            } catch (SQLException ex) { resp.setStatus(500); return; }
            boolean ok = dao.delete(id);
            resp.setStatus(ok ? 200 : 404);
        } catch (NumberFormatException nfe) { resp.setStatus(400); } catch (Exception e) { resp.setStatus(500); }
    }
}
