package com.example.backend.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Lista de orígenes permitidos (dev y producción local)
        String[] allowed = new String[] { "http://localhost:4200", "http://localhost:8080" };
        String origin = req.getHeader("Origin");
        boolean originAllowed = false;
        if (origin != null) {
            for (String a : allowed) { if (a.equalsIgnoreCase(origin)) { originAllowed = true; break; } }
        }

        if (originAllowed) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            // Permitir envío de cookies de sesión desde el frontend cuando corresponda
            res.setHeader("Access-Control-Allow-Credentials", "true");
        }

        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
        res.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }
}
