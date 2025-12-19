<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Categorías</title>
    <style>body{font-family:Arial,Helvetica,sans-serif;margin:20px}table{border-collapse:collapse;width:100%}th,td{padding:8px;border:1px solid #ddd;text-align:left}</style>
</head>
<body>
<h1>Categorías</h1>
<%
    java.util.List categorias = (java.util.List) request.getAttribute("categorias");
    if (categorias != null && !categorias.isEmpty()) {
%>
    <table>
        <thead>
        <tr><th>Id</th><th>Nombre</th><th>Descripción</th></tr>
        </thead>
        <tbody>
        <%
            for (Object o : categorias) {
                com.example.backend.models.Categoria c = (com.example.backend.models.Categoria) o;
        %>
        <tr>
            <td><%= c.getId_categoria() %></td>
            <td><%= c.getNombre() %></td>
            <td><%= c.getDescripcion() %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
<% } else { %>
    <p>No hay categorías para mostrar.</p>
<% } %>
</body>
</html>
