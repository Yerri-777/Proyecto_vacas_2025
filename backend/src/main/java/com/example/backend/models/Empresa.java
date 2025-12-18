package com.example.backend.models;

import com.example.backend.enums.EstadoCuenta;

public class Empresa {
    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private EstadoCuenta estado;

    public Empresa() {}
    public Empresa(int id, String nombre, String correo, String telefono, EstadoCuenta estado){
        this.id = id; this.nombre = nombre; this.correo = correo; this.telefono = telefono; this.estado = estado;
    }
    public int getId(){ return id; } public void setId(int id){ this.id = id; }
    public String getNombre(){ return nombre; } public void setNombre(String nombre){ this.nombre = nombre; }
    public String getCorreo(){ return correo; } public void setCorreo(String correo){ this.correo = correo; }
    public String getTelefono(){ return telefono; } public void setTelefono(String telefono){ this.telefono = telefono; }
    public EstadoCuenta getEstado(){ return estado; } public void setEstado(EstadoCuenta estado){ this.estado = estado; }
}
