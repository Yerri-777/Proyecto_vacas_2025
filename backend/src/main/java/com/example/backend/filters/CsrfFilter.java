package com.example.backend.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebFilter("/api/*")
public class CsrfFilter implements Filter {
    public static final String CSRF_ATTR = "CSRF_TOKEN";
    public static final String CSRF_HEADER = "X-CSRF-Token";

    @Override public void init(FilterConfig filterConfig) { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(true);

        String method = req.getMethod();
        // Ensure token in session for GET (and any safe method)
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            Object t = session.getAttribute(CSRF_ATTR);
            if (t == null) {
                String token = UUID.randomUUID().toString();
                session.setAttribute(CSRF_ATTR, token);
                res.setHeader(CSRF_HEADER, token);
            } else {
                res.setHeader(CSRF_HEADER, t.toString());
            }
            chain.doFilter(request, response);
            return;
        }

      
        String header = req.getHeader(CSRF_HEADER);
        Object sessionToken = session.getAttribute(CSRF_ATTR);
        if (sessionToken == null || header == null || !sessionToken.toString().equals(header)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"CSRF token inválido o ausente\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
