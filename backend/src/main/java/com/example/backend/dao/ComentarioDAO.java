package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Comentario;

import java.sql.*;
import java.util.*;

public class ComentarioDAO {
    public int create(Comentario cm) throws SQLException {
        String sql = "INSERT INTO Comentario(usuario_id,videojuego_id,texto,puntuacion,fecha) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cm.getUsuarioId()); ps.setInt(2, cm.getVideojuegoId()); ps.setString(3, cm.getTexto()); ps.setInt(4, cm.getPuntuacion()); ps.setTimestamp(5, new Timestamp(cm.getFecha().getTime()));
            ps.executeUpdate(); try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public List<Comentario> listByVideojuego(int vid) throws SQLException {
        List<Comentario> list = new ArrayList<>();
        String sql = "SELECT id,usuario_id,videojuego_id,texto,puntuacion,fecha FROM Comentario WHERE videojuego_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vid); try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Comentario(rs.getInt("id"), rs.getInt("usuario_id"), rs.getInt("videojuego_id"), rs.getString("texto"), rs.getInt("puntuacion"), rs.getTimestamp("fecha")));
            }
        }
        return list;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Comentario WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
}
