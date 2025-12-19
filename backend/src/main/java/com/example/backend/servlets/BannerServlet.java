package com.example.backend.servlets;

import com.example.backend.dao.BannerDAO;
import com.example.backend.models.Banner;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/banner")
public class BannerServlet extends BaseServlet {
    private final BannerDAO dao = new BannerDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{ List<Banner> list = dao.listAll(); resp.setContentType("application/json"); resp.getWriter().write(gson.toJson(list)); } catch(Exception e){ resp.setStatus(500); }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader br = req.getReader()) {
            Banner b = gson.fromJson(br, Banner.class);
            if (b == null) { resp.setStatus(400); resp.setContentType("application/json"); resp.getWriter().write("{\"error\":\"Cuerpo vacío\"}"); AuditLogger.log("banner:create cuerpo vacío", null, req.getRemoteAddr()); return; }
            b.setUrlImagen(InputSanitizer.sanitize(b.getUrlImagen(), 255));
            try {
                int id = dao.create(b);
                b.setId(id);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(b));
                AuditLogger.log("banner:create id=" + id, null, req.getRemoteAddr());
            } catch (Exception e) { resp.setStatus(500); AuditLogger.log("banner:create error", null, req.getRemoteAddr()); }
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id"); if (id == null) { resp.setStatus(400); return; }
        try { boolean ok = dao.delete(Integer.parseInt(id)); resp.setStatus(ok?200:404); AuditLogger.log("banner:delete id=" + id + " ok=" + ok, null, req.getRemoteAddr()); } catch (Exception e) { resp.setStatus(500); AuditLogger.log("banner:delete error id=" + id, null, req.getRemoteAddr()); }
    }
}
