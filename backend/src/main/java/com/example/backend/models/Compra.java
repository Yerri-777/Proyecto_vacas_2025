package com.example.backend.models;

import java.util.Date;

public class Compra {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private Date fecha;
    private double total;

    public Compra() {}
    public Compra(int id,int usuarioId,int videojuegoId,Date fecha,double total){
        this.id=id;this.usuarioId=usuarioId;this.videojuegoId=videojuegoId;this.fecha=fecha;this.total=total;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public int getUsuarioId(){return usuarioId;} public void setUsuarioId(int u){this.usuarioId=u;}
    public int getVideojuegoId(){return videojuegoId;} public void setVideojuegoId(int v){this.videojuegoId=v;}
    public Date getFecha(){return fecha;} public void setFecha(Date f){this.fecha=f;}
    public double getTotal(){return total;} public void setTotal(double t){this.total=t;}
}
