package com.nitoelchidoceti.ciceroneapp.Global;

public final class  Global {

    private static Global Turista;
    private String id ;

    private Global(){
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
}
