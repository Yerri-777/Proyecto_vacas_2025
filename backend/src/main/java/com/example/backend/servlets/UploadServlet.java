package com.example.backend.servlets;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/api/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024)
public class UploadServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Part filePart;
        try {
            filePart = req.getPart("file");
            if (filePart == null) { resp.setStatus(400); resp.getWriter().write("{\"error\":\"file es requerido\"}"); return; }
        } catch (Exception e) { resp.setStatus(400); resp.getWriter().write("{\"error\":\"No se recibió file\"}"); return; }

        String uploadsDir = req.getServletContext().getRealPath("/") + "uploads";
        Files.createDirectories(Paths.get(uploadsDir));

        String submitted = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String ext = "";
        int i = submitted.lastIndexOf('.');
        if (i > 0) ext = submitted.substring(i);
        String name = UUID.randomUUID().toString() + ext;
        String full = uploadsDir + File.separator + name;
        try (InputStream in = filePart.getInputStream(); OutputStream out = new FileOutputStream(full)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);
        } catch (Exception e) { resp.setStatus(500); return; }

        String url = req.getContextPath() + "/uploads/" + name;
        resp.setContentType("application/json");
        resp.getWriter().write("{\"url\":\"" + url + "\"}");
    }
}
