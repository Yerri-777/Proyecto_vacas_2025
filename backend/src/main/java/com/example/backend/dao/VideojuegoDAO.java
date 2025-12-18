package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Videojuego;
import com.example.backend.enums.EstadoVideojuego;

import java.sql.*;
import java.util.*;

public class VideojuegoDAO {
    public int create(Videojuego v) throws SQLException {
        String sql = "INSERT INTO Videojuego(nombre,descripcion,empresa_id,precio,estado) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getNombre()); ps.setString(2, v.getDescripcion()); ps.setInt(3, v.getEmpresaId()); ps.setDouble(4, v.getPrecio()); ps.setString(5, v.getEstado().name());
            ps.executeUpdate(); try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public Videojuego findById(int id) throws SQLException {
        String sql = "SELECT id,nombre,descripcion,empresa_id,precio,estado FROM Videojuego WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Videojuego(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"), rs.getInt("empresa_id"), rs.getDouble("precio"), EstadoVideojuego.valueOf(rs.getString("estado")));
            }
        }
        return null;
    }

    public List<Videojuego> listByEmpresa(int empresaId) throws SQLException {
        List<Videojuego> list = new ArrayList<>();
        String sql = "SELECT id,nombre,descripcion,empresa_id,precio,estado FROM Videojuego WHERE empresa_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, empresaId); try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Videojuego(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"), rs.getInt("empresa_id"), rs.getDouble("precio"), EstadoVideojuego.valueOf(rs.getString("estado"))));
            }
        }
        return list;
    }

    public boolean update(Videojuego v) throws SQLException {
        String sql = "UPDATE Videojuego SET nombre=?,descripcion=?,precio=?,estado=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getNombre()); ps.setString(2, v.getDescripcion()); ps.setDouble(3, v.getPrecio()); ps.setString(4, v.getEstado().name()); ps.setInt(5, v.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Videojuego WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
}
