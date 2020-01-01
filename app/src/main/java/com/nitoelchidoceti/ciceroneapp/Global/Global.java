package com.nitoelchidoceti.ciceroneapp.Global;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;

import java.util.ArrayList;

public final class  Global {

    private static Global Turista;
    private String id,nombre ;
    private ArrayList<PojoLugar> lugares;
    private ArrayList<PojoGuia> guias;
    private ArrayList<String> malasPalabras;
    private Global(){
    }

    public ArrayList<PojoLugar> getLugares() {
        return lugares;
    }
    public static final String PAYPAL_CLIENT_ID = "Acqi8-a9YQjjslSknmR3m5CsjZeMrcvmY_NmJU4CEXyTP5SCTVP_iherM7V092HGCapC_UVhdlTJbn16";
    public void setLugares(ArrayList<PojoLugar> lugares) {
        this.lugares = lugares;
    }
    public static Global getObject(){
        if (Turista==null){
            Turista=new Global();
        }

        return Turista;
    }

    public String getId() {
        return id;
    }

    public  void setId(String id) {
        this.id = id;
    }

    public ArrayList<PojoGuia> getGuias() {
        return guias;
    }

    public void setGuias(ArrayList<PojoGuia> guias) {
        this.guias = guias;
    }

    public ArrayList<String> getMalasPalabras() {
        return malasPalabras;
    }

    public void setMalasPalabras(ArrayList<String> malasPalabras) {
        this.malasPalabras = malasPalabras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
