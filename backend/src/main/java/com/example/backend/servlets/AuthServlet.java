package com.example.backend.servlets;

import com.example.backend.dao.UsuarioDAO;
import com.example.backend.models.Usuario;
import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/auth")
public class AuthServlet extends BaseServlet {
    private static final Logger LOG = Logger.getLogger(AuthServlet.class.getName());
    private final UsuarioDAO dao = new UsuarioDAO();
    private final Gson gson = new Gson();

    private boolean estaVacio(String s) { return s == null || s.trim().isEmpty(); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer intentoUserId = null;
        resp.setContentType("application/json");

        try (BufferedReader br = req.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            String body = sb.toString().trim();
            Usuario cred;
            try {
                cred = gson.fromJson(body, Usuario.class);
            } catch (JsonSyntaxException ex) {
                AuditLogger.log("login: JSON inválido", null, req.getRemoteAddr());
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"JSON inválido\"}");
                return;
            }

            if (cred == null) {
                AuditLogger.log("login: cuerpo vacío", null, req.getRemoteAddr());
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"Cuerpo vacío\"}");
                return;
            }

            String correo = InputSanitizer.sanitize(cred.getCorreo(), 150);
            String password = InputSanitizer.sanitize(cred.getPassword(), 150);

            // Safe audit: log only the sanitized email (no passwords)
            AuditLogger.log("login: intento correo -> " + (correo == null ? "(null)" : correo), null, req.getRemoteAddr());

            if (estaVacio(correo) || estaVacio(password)) {
                AuditLogger.log("login: datos incompletos", null, req.getRemoteAddr());
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"Correo y contraseña son requeridos\"}");
                return;
            }
            if (!InputSanitizer.isValidEmail(correo)) {
                AuditLogger.log("login: correo inválido -> " + correo, null, req.getRemoteAddr());
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"Correo inválido\"}");
                return;
            }

            Usuario u = dao.findByEmail(correo);
            if (u == null) {
                AuditLogger.log("login: usuario no encontrado (" + correo + ")", null, req.getRemoteAddr());
                resp.setStatus(401);
                resp.getWriter().write("{\"error\":\"Credenciales inválidas\"}");
                return;
            }
            intentoUserId = u.getId();

            // Verify password using BCrypt (stored passwords are hashed).
            // Migration-friendly: if stored password is plain text (old users), accept and re-hash in DB.
            try {
                boolean ok = false;
                String stored = u.getPassword();
                if (stored != null && !stored.isEmpty()) {
                    try {
                        ok = org.mindrot.jbcrypt.BCrypt.checkpw(password, stored);
                    } catch (IllegalArgumentException iae) {
                        // stored value not a BCrypt hash; fall back to plain comparison
                        if (stored.equals(password)) ok = true;
                    }
                }

                if (!ok) {
                    AuditLogger.log("login: contraseña incorrecta userId=" + intentoUserId, intentoUserId, req.getRemoteAddr());
                    resp.setStatus(401);
                    resp.getWriter().write("{\"error\":\"Credenciales inválidas\"}");
                    return;
                }

                // If stored was plain (not BCrypt), re-hash and update the account record
                if (stored != null && !stored.startsWith("$2a$") && stored.equals(password)) {
                    try {
                        com.example.backend.dao.AccountDAO accountDao = new com.example.backend.dao.AccountDAO();
                        com.example.backend.models.Account a = new com.example.backend.models.Account();
                        a.setId(u.getId());
                        a.setCorreo(u.getCorreo());
                        a.setPassword(password); // AccountDAO.update will hash
                        a.setRol(u.getRole());
                        a.setEstado(u.getEstado());
                        accountDao.update(a);
                        AuditLogger.log("login: re-hashed password for userId=" + u.getId(), u.getId(), req.getRemoteAddr());
                    } catch (Exception ignore) { /* non-fatal */ }
                }

            } catch (Exception ex) {
                AuditLogger.log("login: error verificación password userId=" + intentoUserId, intentoUserId, req.getRemoteAddr());
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"Error interno\"}");
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("userId", u.getId());
            session.setAttribute("userRole", u.getRole());
            session.setMaxInactiveInterval(60 * 60);

            // Asegurar cookie de sesión: HttpOnly + SameSite=Lax (añadimos header para forzar atributos)
            boolean secure = req.isSecure();
            StringBuilder sc = new StringBuilder();
            sc.append("JSESSIONID=").append(session.getId()).append("; Path=/; HttpOnly; SameSite=Lax");
            if (secure) sc.append("; Secure");
            resp.setHeader("Set-Cookie", sc.toString());

            AuditLogger.log("login: éxito userId=" + u.getId(), u.getId(), req.getRemoteAddr());
            resp.getWriter().write(gson.toJson(u));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error en AuthServlet", e);
            AuditLogger.log("login: error interno userId=" + intentoUserId, intentoUserId, req.getRemoteAddr());
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Error interno del servidor\"}");
        }
    }
}
