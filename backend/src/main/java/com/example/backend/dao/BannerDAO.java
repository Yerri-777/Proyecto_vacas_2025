package com.example.backend.dao;

import com.example.backend.DBConnection;
import com.example.backend.models.Banner;

import java.sql.*;
import java.util.*;

public class BannerDAO {
    public int create(Banner b) throws SQLException {
        String sql="INSERT INTO Banner(url_imagen,fecha_inicio,fecha_fin) VALUES(?,?,?)";
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,b.getUrlImagen());
            ps.setTimestamp(2, b.getFechaInicio()!=null ? new Timestamp(b.getFechaInicio().getTime()) : null);
            ps.setTimestamp(3, b.getFechaFin()!=null ? new Timestamp(b.getFechaFin().getTime()) : null);
            ps.executeUpdate(); try(ResultSet rs=ps.getGeneratedKeys()){ return rs.next()? rs.getInt(1):-1; }
        }
    }
    public List<Banner> listAll() throws SQLException {
        List<Banner> list=new ArrayList<>();
        String sql="SELECT id,url_imagen,fecha_inicio,fecha_fin FROM Banner";
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){
            while(rs.next()) list.add(new Banner(rs.getInt("id"), rs.getString("url_imagen"), rs.getTimestamp("fecha_inicio"), rs.getTimestamp("fecha_fin")));
        }
        return list;
    }
    public boolean delete(int id) throws SQLException {
        String sql="DELETE FROM Banner WHERE id=?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,id); return ps.executeUpdate()>0; }
    }
}
