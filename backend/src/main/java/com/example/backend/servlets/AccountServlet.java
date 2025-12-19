package com.example.backend.servlets;

import com.example.backend.dao.AccountDAO;
import com.example.backend.models.Account;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

@WebServlet("/api/account")
public class AccountServlet extends BaseServlet {
    private final AccountDAO dao = new AccountDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) { resp.setStatus(400); resp.getWriter().write("{\"error\":\"id requerido\"}"); return; }
        try {
            int id = Integer.parseInt(idParam);
            Account a = dao.findById(id);
            if (a == null) { resp.setStatus(404); return; }
            resp.setContentType("application/json"); resp.getWriter().write(gson.toJson(a));
        } catch (Exception e) { resp.setStatus(500); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader br = req.getReader()) {
            Account a = gson.fromJson(br, Account.class);
            // validations
            if (a == null || a.getCorreo() == null || a.getCorreo().trim().isEmpty()) {
                resp.setStatus(400);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":\"correo es requerido\"}");
                return;
            }
            if (a.getPassword() == null || a.getPassword().trim().isEmpty()) {
                resp.setStatus(400);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":\"password es requerido\"}");
                return;
            }

            // uniqueness check
            try {
                if (dao.findByEmail(a.getCorreo()) != null) {
                    resp.setStatus(409);
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"error\":\"correo ya registrado\"}");
                    return;
                }
            } catch (SQLException ex) {
                resp.setStatus(500); return;
            }

            int id = dao.create(a);
            a.setId(id);
            resp.setContentType("application/json"); resp.getWriter().write(gson.toJson(a));
        } catch (Exception e) { resp.setStatus(500); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader br = req.getReader()) {
            Account a = gson.fromJson(br, Account.class);
            if (a == null || a.getId() == 0) { resp.setStatus(400); resp.getWriter().write("{\"error\":\"id es requerido\"}"); return; }
            boolean ok = dao.update(a);
            resp.setStatus(ok ? 200 : 404);
        } catch (Exception e) { resp.setStatus(500); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id"); if (idParam == null) { resp.setStatus(400); return; }
        try { int id = Integer.parseInt(idParam); boolean ok = dao.delete(id); resp.setStatus(ok ? 200 : 404); } catch (Exception e) { resp.setStatus(500); }
    }
}
