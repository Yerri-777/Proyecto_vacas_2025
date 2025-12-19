package com.example.backend.servlets;

import com.example.backend.DBConnection;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/api/report/sales")
public class ReportServlet extends BaseServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String empresaParam = req.getParameter("empresaId");
        String fromParam = req.getParameter("from");
        String toParam = req.getParameter("to");

        String sql = "SELECT COUNT(*) as count, COALESCE(SUM(total),0) as total FROM Compra WHERE 1=1";
        List<Object> params = new ArrayList<>();
        if (empresaParam != null) { sql += " AND usuario_id = ?"; params.add(Integer.parseInt(empresaParam)); } // note: if you store empresa sales differently adjust
        if (fromParam != null) { sql += " AND fecha >= ?"; params.add(Timestamp.valueOf(fromParam)); }
        if (toParam != null) { sql += " AND fecha <= ?"; params.add(Timestamp.valueOf(toParam)); }

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> out = new HashMap<>();
                    out.put("count", rs.getInt("count"));
                    out.put("total", rs.getDouble("total"));
                    resp.setContentType("application/json");
                    resp.getWriter().write(gson.toJson(out));
                    return;
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
        }
        resp.setStatus(404);
    }
}
