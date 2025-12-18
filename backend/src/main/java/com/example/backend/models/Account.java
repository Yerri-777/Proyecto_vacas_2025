package com.example.backend.models;

import com.example.backend.enums.Role;
import com.example.backend.enums.EstadoCuenta;

public class Account {
    private int id;
    private String correo;
    private String password;
    private Role rol;
    private EstadoCuenta estado;

    public Account() {}
    public Account(int id, String correo, String password, Role rol, EstadoCuenta estado) {
        this.id = id; this.correo = correo; this.password = password; this.rol = rol; this.estado = estado;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRol() { return rol; }
    public void setRol(Role rol) { this.rol = rol; }
    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }
}
