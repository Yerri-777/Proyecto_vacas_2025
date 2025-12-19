package com.example.backend.utils;

public class InputSanitizer {
	// sanitize: trim, cut to maxLen, remove angle brackets
	public static String sanitize(String s, int maxLen) {
		if (s == null) return null;
		String t = s.trim();
		if (t.length() > maxLen) t = t.substring(0, maxLen);
		// quitar caracteres potencialmente peligrosos de forma simple
		t = t.replaceAll("<", "").replaceAll(">", "");
		return t;
	}

	// validación simple de correo (no exhaustiva)
	public static boolean isValidEmail(String email) {
		if (email == null) return false;
		return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	}
}
