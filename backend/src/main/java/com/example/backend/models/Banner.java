package com.example.backend.models;

import java.util.Date;

public class Banner {
    private int id;
    private String urlImagen;
    private Date fechaInicio;
    private Date fechaFin;

    public Banner() {}
    public Banner(int id,String urlImagen,Date fechaInicio,Date fechaFin){
        this.id=id;this.urlImagen=urlImagen;this.fechaInicio=fechaInicio;this.fechaFin=fechaFin;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getUrlImagen(){return urlImagen;} public void setUrlImagen(String u){this.urlImagen=u;}
    public Date getFechaInicio(){return fechaInicio;} public void setFechaInicio(Date d){this.fechaInicio=d;}
    public Date getFechaFin(){return fechaFin;} public void setFechaFin(Date d){this.fechaFin=d;}
}
