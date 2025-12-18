package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Categoria;
import java.sql.*;
import java.util.*;

public class CategoriaDAO {
    public boolean existsByName(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM Categoria WHERE nombre = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    public int create(Categoria cat) throws SQLException {
        String sql = "INSERT INTO Categoria(nombre, descripcion) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getNombre()); ps.setString(2, cat.getDescripcion());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }
    public boolean update(Categoria cat) throws SQLException {
        String sql = "UPDATE Categoria SET nombre=?, descripcion=? WHERE id_categoria=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cat.getNombre()); ps.setString(2, cat.getDescripcion());
            ps.setInt(3, cat.getId_categoria());
            return ps.executeUpdate() > 0;
        }
    }
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Categoria WHERE id_categoria=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }
    public List<Categoria> listAll() throws SQLException {
        List<Categoria> list = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre, descripcion FROM Categoria";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        }
        return list;
    }
}
