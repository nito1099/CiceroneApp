package com.nitoelchidoceti.ciceroneapp.POJOS;

import java.io.Serializable;

public class PojoComentario implements Serializable {

    private String comentario, userName, fecha;

    public PojoComentario() {
        comentario="";
        userName="";
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = userName;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
