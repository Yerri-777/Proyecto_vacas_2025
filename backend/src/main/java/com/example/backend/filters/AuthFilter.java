package com.example.backend.filters;

import com.example.backend.enums.Role;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) { /* no-op */ }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String method = req.getMethod();

        // Allow static resources and UI
        if (path.startsWith("/assets/") || path.startsWith("/uploads/") || path.equals("/") || path.startsWith("/index.html")) {
            chain.doFilter(request, response);
            return;
        }

        // API rules
        if (path.startsWith("/api/")) {
            // Login always allowed
            if (path.equals("/api/auth")) { chain.doFilter(request, response); return; }

            // Public GET endpoints allowed
            if ("GET".equalsIgnoreCase(method)) { chain.doFilter(request, response); return; }

            // For mutating methods require authentication
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"No autenticado\"}");
                return;
            }

            // Role-based checks (simple)
            Object r = session.getAttribute("userRole");
            Role role = null;
            if (r instanceof Role) role = (Role) r;
            else if (r instanceof String) {
                try { role = Role.valueOf((String) r); } catch (Exception ignored) {}
            }

            // Admin endpoints: categoria, empresa, banner management
            if (path.startsWith("/api/categoria") || path.startsWith("/api/empresa") || path.startsWith("/api/banner") || path.startsWith("/api/report")) {
                if (role != Role.ADMIN) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Requiere rol ADMIN\"}");
                    return;
                }
                chain.doFilter(request, response);
                return;
            }

            // Videojuego creation/modification: EMPRESA or ADMIN (further check in servlet for owner)
            if (path.startsWith("/api/videojuego")) {
                if (role == Role.ADMIN || role == Role.EMPRESA) { chain.doFilter(request, response); return; }
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Requiere rol EMPRESA o ADMIN\"}");
                return;
            }

            // Compra: only USUARIO can create
            if (path.startsWith("/api/compra")) {
                if ("POST".equalsIgnoreCase(method) && role != Role.USUARIO) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Requiere rol USUARIO\"}");
                    return;
                }
                chain.doFilter(request, response);
                return;
            }

            // Comentarios: USUARIO required to post
            if (path.startsWith("/api/comentario")) {
                if ("POST".equalsIgnoreCase(method) && role != Role.USUARIO) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Requiere rol USUARIO\"}");
                    return;
                }
                chain.doFilter(request, response);
                return;
            }

            // Default allow for authenticated
            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { /* no-op */ }
}
