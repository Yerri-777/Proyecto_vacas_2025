package com.example.backend.models;

import com.example.backend.enums.EstadoVideojuego;

public class Videojuego {
    private int id;
    private String nombre;
    private String descripcion;
    private int empresaId;
    private double precio;
    private EstadoVideojuego estado;

    public Videojuego() {}
    public Videojuego(int id,String nombre,String descripcion,int empresaId,double precio,EstadoVideojuego estado){
        this.id=id;this.nombre=nombre;this.descripcion=descripcion;this.empresaId=empresaId;this.precio=precio;this.estado=estado;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getNombre(){return nombre;} public void setNombre(String n){this.nombre=n;}
    public String getDescripcion(){return descripcion;} public void setDescripcion(String d){this.descripcion=d;}
    public int getEmpresaId(){return empresaId;} public void setEmpresaId(int e){this.empresaId=e;}
    public double getPrecio(){return precio;} public void setPrecio(double p){this.precio=p;}
    public EstadoVideojuego getEstado(){return estado;} public void setEstado(EstadoVideojuego e){this.estado=e;}
}
