package com.example.backend.models;

public class Categoria {
    private int id_categoria;
    private String nombre;
    private String descripcion;

    public Categoria() {}
    public Categoria(int id, String nombre, String descripcion) { this.id_categoria = id; this.nombre = nombre; this.descripcion = descripcion; }
    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id) { this.id_categoria = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
