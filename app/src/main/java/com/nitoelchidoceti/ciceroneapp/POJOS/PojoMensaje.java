package com.nitoelchidoceti.ciceroneapp.POJOS;

public class PojoMensaje {
    private String mensaje,fecha,Nombre,type_mensaje;

    public PojoMensaje() {
    }

    public PojoMensaje(String mensaje, String fecha, String nombre, String type_mensaje) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        Nombre = nombre;
        this.type_mensaje = type_mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getType_mensaje() {
        return type_mensaje;
    }

    public void setType_mensaje(String type_mensaje) {
        this.type_mensaje = type_mensaje;
    }
}
