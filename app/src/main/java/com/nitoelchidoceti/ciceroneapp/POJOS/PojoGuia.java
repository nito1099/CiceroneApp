package com.nitoelchidoceti.ciceroneapp.POJOS;

import java.io.Serializable;

public class PojoGuia implements Serializable {

    private int Id;
    private String nombre, telefono, duracion, correo, sitio, fotografia;
    private Double[] costos;

    public PojoGuia() {
        Id = 0;
        nombre = "";
        telefono = "";
        duracion = "";
        correo = "";
        sitio = "";
        costos = new Double[3];
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public Double[] getCostos() {
        return costos;
    }

    public void setCostos(Double[] costos) {
        this.costos = costos;
    }

    public String getFotografia() {
        return fotografia;
    }

    public void setFotografia(String fotografia) {
        this.fotografia = fotografia;
    }
}
