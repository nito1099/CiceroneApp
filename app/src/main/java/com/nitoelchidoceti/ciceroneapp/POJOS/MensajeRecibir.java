package com.nitoelchidoceti.ciceroneapp.POJOS;

import android.util.Log;

public class MensajeRecibir extends  PojoMensaje{
    private Long hora;

    public MensajeRecibir() {
    }

    public MensajeRecibir(Long hora) {
        this.hora = hora;
    }

    public MensajeRecibir(String mensaje, String nombre, String type_mensaje, String urlFoto, Long hora) {
        super(mensaje, nombre, type_mensaje, urlFoto);
        this.hora = hora;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
