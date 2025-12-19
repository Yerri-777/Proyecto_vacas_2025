package com.example.backend.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class HttpsEnforceFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // Allow bypass for local development or explicit disable flag
        String disable = System.getProperty("disable.https.enforce");
        String serverName = req.getServerName();
        boolean isLocal = "localhost".equalsIgnoreCase(serverName) || "127.0.0.1".equals(serverName) || req.getLocalAddr().startsWith("127.");
        if (Boolean.parseBoolean(disable) || isLocal) {
            chain.doFilter(request, response);
            return;
        }

        // If already secure, continue
        if (req.isSecure() || "https".equalsIgnoreCase(req.getHeader("X-Forwarded-Proto"))) {
            chain.doFilter(request, response);
            return;
        }

        // Redirect to HTTPS in non-local environments
        String query = req.getQueryString();
        String url = "https://" + req.getServerName() + (query != null ? req.getRequestURI() + "?" + query : req.getRequestURI());
        res.sendRedirect(url);
    }
}
