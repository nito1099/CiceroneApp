package com.nitoelchidoceti.ciceroneapp.POJOS;

import java.util.Map;

public class MensajeEnviar extends PojoMensaje{
    private Map hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String type_mensaje, String idUsuario, Map hora) {
        super(mensaje, nombre, type_mensaje, idUsuario);
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String type_mensaje, String urlFoto, String idUsuario, Map hora) {
        super(mensaje, nombre, type_mensaje, urlFoto, idUsuario);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
