package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nitoelchidoceti.ciceroneapp.R;

public class HomeFragment extends Fragment  {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home,container,false);

        Spinner spinner = myView.findViewById(R.id.spinnerCategory);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                myView.getContext(),R.array.categorias,android.R.layout.simple_spinner_item);//CREACION DE ADAPTER

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //AGREGAR LA ANIMACION DE VISTA DE LOS ITEMS DEL SPINNER

        spinner.setAdapter(spinnerAdapter);//ASIGNAR ADAPTER AL SPINNER

        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return myView;
    }


}
