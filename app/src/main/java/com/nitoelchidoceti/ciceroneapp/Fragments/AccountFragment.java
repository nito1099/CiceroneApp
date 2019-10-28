package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.nitoelchidoceti.ciceroneapp.LoginActivity;
import com.nitoelchidoceti.ciceroneapp.R;

public class AccountFragment extends Fragment {



    private TextView txtNombre,txtEmail,txtPlace,txtCell,txtBirthday;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_account,container,false);

        txtNombre=myView.findViewById(R.id.txt_nombre);
        txtEmail=myView.findViewById(R.id.txt_email);
        txtPlace=myView.findViewById(R.id.txt_lugar);
        txtCell=myView.findViewById(R.id.txt_telefono);
        txtBirthday=myView.findViewById(R.id.txt_nacimiento);

        return inflater.inflate(R.layout.fragment_account,container,false);
    }
}
