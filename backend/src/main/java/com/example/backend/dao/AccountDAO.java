package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Account;
import com.example.backend.enums.Role;
import com.example.backend.enums.EstadoCuenta;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class AccountDAO {
    public int create(Account a) throws SQLException {
        String sql = "INSERT INTO Usuario(correo,password,role,estado) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getCorreo());
            String hashed = a.getPassword() != null ? BCrypt.hashpw(a.getPassword(), BCrypt.gensalt(12)) : null;
            ps.setString(2, hashed);
            ps.setString(3, a.getRol() != null ? a.getRol().name() : Role.USUARIO.name());
            ps.setString(4, a.getEstado() != null ? a.getEstado().name() : EstadoCuenta.ACTIVA.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public Account findByEmail(String correo) throws SQLException {
        String sql = "SELECT id, correo, password, role, estado FROM Usuario WHERE correo=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account a = new Account();
                    a.setId(rs.getInt("id"));
                    a.setCorreo(rs.getString("correo"));
                    a.setPassword(rs.getString("password"));
                    a.setRol(Role.valueOf(rs.getString("role")));
                    a.setEstado(EstadoCuenta.valueOf(rs.getString("estado")));
                    return a;
                }
            }
        }
        return null;
    }

    public Account findById(int id) throws SQLException {
        String sql = "SELECT id, correo, password, role, estado FROM Usuario WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account a = new Account();
                    a.setId(rs.getInt("id"));
                    a.setCorreo(rs.getString("correo"));
                    a.setPassword(rs.getString("password"));
                    a.setRol(Role.valueOf(rs.getString("role")));
                    a.setEstado(EstadoCuenta.valueOf(rs.getString("estado")));
                    return a;
                }
            }
        }
        return null;
    }

    public boolean update(Account a) throws SQLException {
        String sql = "UPDATE Usuario SET correo=?, password=?, role=?, estado=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getCorreo());
            String hashed = a.getPassword() != null ? BCrypt.hashpw(a.getPassword(), BCrypt.gensalt(12)) : null;
            ps.setString(2, hashed);
            ps.setString(3, a.getRol().name()); ps.setString(4, a.getEstado().name()); ps.setInt(5, a.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
}
