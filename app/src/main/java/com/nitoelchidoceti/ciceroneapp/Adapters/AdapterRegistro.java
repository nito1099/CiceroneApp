package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoRegistro;
import com.nitoelchidoceti.ciceroneapp.R;

public class AdapterRegistro {

    EditText nombre, correo, contraseña, telefono, fecha,ciudad;
    Context contexto;

    public AdapterRegistro(){//obtiene los ids de los editText y la vista
        View view = View.inflate(contexto, R.layout.activity_registry,null);
        nombre = view.findViewById(R.id.txt_nombre);
        correo = view.findViewById(R.id.txt_email);
        contraseña = view.findViewById(R.id.text_input_password);
        telefono = view.findViewById(R.id.txt_telefono);
        fecha = view.findViewById(R.id.txt_birthday);
        ciudad = view.findViewById(R.id.aTxtViewCiudad);
    }

    public PojoRegistro llenarDatosAlPojo(){//LLena los datos ingresados del editText
        PojoRegistro datos = new PojoRegistro();
        datos.setNombre(nombre.getText().toString());
        datos.setCorreo(correo.getText().toString());
        datos.setContraseña(contraseña.getText().toString());
        datos.setTelefono((telefono.getText().toString()));
        datos.setNacimiento(fecha.getText().toString());
        datos.setCiudad(ciudad.getText().toString());
        return datos;
    }
}
