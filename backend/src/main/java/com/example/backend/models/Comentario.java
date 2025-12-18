package com.example.backend.models;

import java.util.Date;

public class Comentario {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private String texto;
    private int puntuacion;
    private Date fecha;

    public Comentario() {}
    public Comentario(int id,int usuarioId,int videojuegoId,String texto,int puntuacion,Date fecha){
        this.id=id;this.usuarioId=usuarioId;this.videojuegoId=videojuegoId;this.texto=texto;this.puntuacion=puntuacion;this.fecha=fecha;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public int getUsuarioId(){return usuarioId;} public void setUsuarioId(int u){this.usuarioId=u;}
    public int getVideojuegoId(){return videojuegoId;} public void setVideojuegoId(int v){this.videojuegoId=v;}
    public String getTexto(){return texto;} public void setTexto(String t){this.texto=t;}
    public int getPuntuacion(){return puntuacion;} public void setPuntuacion(int p){this.puntuacion=p;}
    public Date getFecha(){return fecha;} public void setFecha(Date f){this.fecha=f;}
}
