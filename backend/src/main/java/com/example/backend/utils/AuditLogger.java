package com.example.backend.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuditLogger {
	private static final String LOGFILE = System.getProperty("user.dir") + File.separator + "auth_audit.log";

	public static synchronized void log(String event, Integer userId, String remoteAddr) {
		String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String line = ts + " | userId:" + (userId == null ? "null" : userId) + " | remote:" + (remoteAddr == null ? "-" : remoteAddr) + " | " + event;
		try (FileWriter fw = new FileWriter(LOGFILE, true); BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(line);
			bw.newLine();
		} catch (IOException ignored) { /* no fallar la app por el log */ }
	}
}
