package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Empresa;
import com.example.backend.enums.EstadoCuenta;

import java.sql.*;
import java.util.*;

public class EmpresaDAO {
    public int create(Empresa e) throws SQLException {
        String sql = "INSERT INTO Empresa(nombre,correo,telefono,estado) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre()); ps.setString(2, e.getCorreo()); ps.setString(3, e.getTelefono()); ps.setString(4, e.getEstado().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public Empresa findById(int id) throws SQLException {
        String sql = "SELECT id,nombre,correo,telefono,estado FROM Empresa WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Empresa(rs.getInt("id"), rs.getString("nombre"), rs.getString("correo"), rs.getString("telefono"), EstadoCuenta.valueOf(rs.getString("estado")));
            }
        }
        return null;
    }

    public List<Empresa> listAll() throws SQLException {
        List<Empresa> list = new ArrayList<>();
        String sql = "SELECT id,nombre,correo,telefono,estado FROM Empresa";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Empresa(rs.getInt("id"), rs.getString("nombre"), rs.getString("correo"), rs.getString("telefono"), EstadoCuenta.valueOf(rs.getString("estado"))));
        }
        return list;
    }

    public boolean update(Empresa e) throws SQLException {
        String sql = "UPDATE Empresa SET nombre=?,correo=?,telefono=?,estado=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getNombre()); ps.setString(2, e.getCorreo()); ps.setString(3, e.getTelefono()); ps.setString(4, e.getEstado().name()); ps.setInt(5, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Empresa WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
}
