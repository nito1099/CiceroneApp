package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.nitoelchidoceti.ciceroneapp.LoginActivity;
import com.nitoelchidoceti.ciceroneapp.R;

public class AccountFragment extends Fragment {

    private TextView txtnombre;
    private CallbackManager callbackManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_account,container,false);
        txtnombre=myView.findViewById(R.id.txt_nombre);
        callbackManager=CallbackManager.Factory.create();

        return inflater.inflate(R.layout.fragment_account,container,false);
    }
}
