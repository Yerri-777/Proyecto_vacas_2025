package com.example.backend.models;

import com.example.backend.enums.Role;
import com.example.backend.enums.EstadoCuenta;

public class Usuario {
    private int id;
    private String correo;
    private String password;
    private Role role;
    private EstadoCuenta estado;

    public Usuario() {}
    public Usuario(int id,String correo,String password,Role role,EstadoCuenta estado){
        this.id=id;this.correo=correo;this.password=password;this.role=role;this.estado=estado;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getCorreo(){return correo;} public void setCorreo(String c){this.correo=c;}
    public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
    public Role getRole(){return role;} public void setRole(Role r){this.role=r;}
    public EstadoCuenta getEstado(){return estado;} public void setEstado(EstadoCuenta e){this.estado=e;}
}
