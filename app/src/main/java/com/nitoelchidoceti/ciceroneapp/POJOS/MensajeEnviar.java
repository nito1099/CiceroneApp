package com.nitoelchidoceti.ciceroneapp.POJOS;

import java.util.Map;

public class MensajeEnviar extends PojoMensaje{
    private Map hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String type_mensaje, String urlFoto, Map hora) {
        super(mensaje, nombre, type_mensaje, urlFoto);
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String type_mensaje, Map hora) {
        super(mensaje, nombre, type_mensaje);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
