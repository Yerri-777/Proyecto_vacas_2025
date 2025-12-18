package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Usuario;
import com.example.backend.enums.Role;
import com.example.backend.enums.EstadoCuenta;

import java.sql.*;

public class UsuarioDAO {
    public int create(Usuario u) throws SQLException {
        String sql = "INSERT INTO Usuario(correo,password,role,estado) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getCorreo());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRole().name());
            ps.setString(4, u.getEstado().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public Usuario findByEmail(String correo) throws SQLException {
        String sql = "SELECT id,correo,password,role,estado FROM Usuario WHERE correo=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Usuario(rs.getInt("id"), rs.getString("correo"), rs.getString("password"),
                        Role.valueOf(rs.getString("role")), EstadoCuenta.valueOf(rs.getString("estado")));
            }
        }
        return null;
    }

    public Usuario findById(int id) throws SQLException {
        String sql = "SELECT id,correo,password,role,estado FROM Usuario WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Usuario(rs.getInt("id"), rs.getString("correo"), rs.getString("password"),
                        Role.valueOf(rs.getString("role")), EstadoCuenta.valueOf(rs.getString("estado")));
            }
        }
        return null;
    }
}
