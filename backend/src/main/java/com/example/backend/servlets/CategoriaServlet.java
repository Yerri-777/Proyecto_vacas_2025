package com.example.backend.servlets;

import com.example.backend.dao.CategoriaDAO;
import com.example.backend.models.Categoria;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/categoria")
public class CategoriaServlet extends BaseServlet {
    private final CategoriaDAO dao = new CategoriaDAO();

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        try {
            java.util.List<Categoria> list = dao.listAll();
            String accept = req.getHeader("Accept");
            if (accept != null && accept.contains("text/html")) {
                req.setAttribute("categorias", list);
                req.getRequestDispatcher("/WEB-INF/views/categoria.jsp").forward(req, resp);
                return;
            }
            writeJson(resp, list);
        } catch (Exception e) { writeError(resp, 500, "Error interno"); }
    }

    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        Categoria cat = readJson(req, Categoria.class);
        if (cat == null) { audit("categoria:create JSON inválido", null, req); writeError(resp, 400, "JSON inválido o cuerpo vacío"); return; }

        cat.setNombre(sanitize(cat.getNombre(), 100));
        cat.setDescripcion(sanitize(cat.getDescripcion(), 500));
        if (cat.getNombre() == null || cat.getNombre().isEmpty()) { audit("categoria:create nombre vacío", null, req); writeError(resp, 400, "Nombre requerido"); return; }

        try {
            if (dao.existsByName(cat.getNombre())) { audit("categoria:create duplicado", null, req); writeError(resp, 409, "Nombre ya existe"); return; }
            int id = dao.create(cat);
            cat.setId_categoria(id);
            writeJson(resp, cat);
            audit("categoria:create éxito id=" + id, null, req);
        } catch (Exception e) { audit("categoria:create error interno", null, req); writeError(resp, 500, "Error interno"); }
    }

    @Override
    protected void doPut(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        Categoria cat = readJson(req, Categoria.class);
        if (cat == null || cat.getId_categoria() <= 0) { audit("categoria:update id faltante", null, req); writeError(resp, 400, "Id requerido"); return; }

        cat.setNombre(sanitize(cat.getNombre(), 100));
        cat.setDescripcion(sanitize(cat.getDescripcion(), 500));
        try {
            boolean ok = dao.update(cat);
            if (ok) resp.setStatus(200); else resp.setStatus(404);
            audit("categoria:update id=" + cat.getId_categoria() + " ok=" + ok, null, req);
        } catch (Exception e) { audit("categoria:update error interno id=" + cat.getId_categoria(), null, req); writeError(resp, 500, "Error interno"); }
    }

    @Override
    protected void doDelete(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) { writeError(resp, 400, "id es requerido"); return; }
        try {
            int id = Integer.parseInt(idParam);
            try {
                if (dao.isInUse(id)) { audit("categoria:delete en uso id=" + id, null, req); writeError(resp, 409, "Categoría en uso"); return; }
            } catch (java.sql.SQLException ex) { audit("categoria:delete SQL err id=" + id, null, req); writeError(resp, 500, "Error interno"); return; }
            boolean ok = dao.delete(id);
            resp.setStatus(ok ? 200 : 404);
            audit("categoria:delete id=" + id + " ok=" + ok, null, req);
        } catch (NumberFormatException e) { writeError(resp, 400, "id inválido"); } catch (Exception e) { audit("categoria:delete error id=" + idParam, null, req); writeError(resp, 500, "Error interno"); }
    }
}
