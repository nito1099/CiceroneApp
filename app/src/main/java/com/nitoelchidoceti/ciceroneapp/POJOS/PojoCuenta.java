package com.nitoelchidoceti.ciceroneapp.POJOS;

public class PojoCuenta {


    private String nombre,telefono,correo,nacimiento,ciudad;

    public PojoCuenta(){
        this.nombre="";
        this.telefono="";
        this.correo="";
        this.nacimiento="";
        this.ciudad="";
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(String nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

}
