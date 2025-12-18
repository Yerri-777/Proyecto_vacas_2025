package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Compra;

import java.sql.*;
import java.util.*;

public class CompraDAO {
    public int create(Compra cObj) throws SQLException {
        String sql = "INSERT INTO Compra(usuario_id,videojuego_id,fecha,total) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cObj.getUsuarioId()); ps.setInt(2, cObj.getVideojuegoId()); ps.setTimestamp(3, new Timestamp(cObj.getFecha().getTime())); ps.setDouble(4, cObj.getTotal());
            ps.executeUpdate(); try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public List<Compra> listByUsuario(int usuarioId) throws SQLException {
        List<Compra> list = new ArrayList<>();
        String sql = "SELECT id,usuario_id,videojuego_id,fecha,total FROM Compra WHERE usuario_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId); try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Compra(rs.getInt("id"), rs.getInt("usuario_id"), rs.getInt("videojuego_id"), rs.getTimestamp("fecha"), rs.getDouble("total")));
            }
        }
        return list;
    }
}
