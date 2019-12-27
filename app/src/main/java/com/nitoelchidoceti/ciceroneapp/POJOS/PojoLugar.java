package com.nitoelchidoceti.ciceroneapp.POJOS;

import java.io.Serializable;
import java.util.ArrayList;

public class PojoLugar implements Serializable {

    private int PK_ID,FK_Categoria;
    private String Descripcion,Nombre,Telefono,
            Direccion,Horario_Inicio,Horario_Final,Fotografia;
    private Double[] costos;
    private ArrayList<String> imagenes;

    public PojoLugar(){
        PK_ID=0;
        FK_Categoria=0;
        Descripcion="";
        Nombre="";
        Telefono="";
        Direccion="";
        Horario_Final="";
        Horario_Inicio="";
        Fotografia="null";
        costos = new Double[3];
        imagenes = new ArrayList<>();
    }

    public int getPK_ID() {
        return PK_ID;
    }

    public void setPK_ID(int PK_ID) {
        this.PK_ID = PK_ID;
    }

    public int getFK_Categoria() {
        return FK_Categoria;
    }

    public void setFK_Categoria(int FK_Categoria) {
        this.FK_Categoria = FK_Categoria;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getHorario_Inicio() {
        return Horario_Inicio;
    }

    public void setHorario_Inicio(String horario_Inicio) {
        Horario_Inicio = horario_Inicio;
    }

    public String getHorario_Final() {
        return Horario_Final;
    }

    public void setHorario_Final(String horario_Final) {
        Horario_Final = horario_Final;
    }

    public String getFotografia() {
        return Fotografia;
    }

    public void setFotografia(String fotografia) {
        Fotografia = fotografia;
    }

    public Double[] getCostos() {
        return costos;
    }

    public void setCostos(Double[] costos) {
        this.costos = costos;
    }

    public ArrayList<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(ArrayList<String> imagenes) {
        this.imagenes = imagenes;
    }
}
