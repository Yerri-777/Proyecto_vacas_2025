package com.example.backend.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class GlobalErrorFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            // Log server-side
            System.err.println("Unhandled exception for " + req.getMethod() + " " + req.getRequestURI() + ": " + t.getMessage());
            t.printStackTrace(System.err);

            // Return sanitized JSON error
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json");
            String msg = t.getMessage() == null ? "internal error" : t.getMessage().replace("\"","'");
            res.getWriter().write("{\"error\":\"Internal server error\",\"message\":\"" + msg + "\"}");
        }
    }

    @Override
    public void destroy() { }
}
