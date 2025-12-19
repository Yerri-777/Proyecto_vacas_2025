package com.example.backend.servlets;

import com.example.backend.utils.InputSanitizer;
import com.example.backend.utils.AuditLogger;
import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.*;

public abstract class BaseServlet extends HttpServlet {
	private final Gson gson = new Gson();

	protected <T> T readJson(HttpServletRequest req, Class<T> clazz) {
		try (BufferedReader br = req.getReader()) {
			return gson.fromJson(br, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	protected void writeJson(HttpServletResponse resp, Object obj) throws IOException {
		resp.setContentType("application/json");
		resp.getWriter().write(gson.toJson(obj));
	}

	protected void writeError(HttpServletResponse resp, int status, String message) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.getWriter().write("{\"error\":\"" + message.replace("\"", "'") + "\"}");
	}

	protected String sanitize(String s, int maxLen) {
		return InputSanitizer.sanitize(s, maxLen);
	}

	protected Integer sessionUserId(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) return null;
		Object v = session.getAttribute("userId");
		if (v instanceof Integer) return (Integer)v;
		if (v instanceof Long) return ((Long)v).intValue();
		if (v instanceof String) {
			try { return Integer.parseInt((String)v); } catch (Exception ex) { return null; }
		}
		return null;
	}

	protected void audit(String event, Integer userId, HttpServletRequest req) {
		AuditLogger.log(event, userId, req.getRemoteAddr());
	}
}
