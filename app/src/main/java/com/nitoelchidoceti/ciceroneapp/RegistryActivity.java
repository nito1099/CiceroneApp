package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class RegistryActivity extends AppCompatActivity {
    private static final String[] CITIES = new String[]{
      "Guadalajara", "Tlaquepaque", "Zapopan", "Tonalá", "Tlajomulco",
        "El Salto", "CDMX", "Monterrey", "Gomez Palacio", " Guanajuato"
    };
    EditText etxtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        AutoCompleteTextView aTxtViewCiudad = findViewById(R.id.aTxtViewCiudad);
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, CITIES);
        aTxtViewCiudad.setAdapter(adapterCiudad);

        etxtDate = findViewById(R.id.txt_nacimiento);
        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);



        etxtDate.setOnClickListener(new View.OnClickListener() {//ONCLICK DE FECHA DE NACIMIENTO
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegistryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month=month+1;
                        String date = day+"/"+ month+"/"+ year;

                        etxtDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();;
            }
        });
    }

    public void launchBottomActivity(View view) {
        Intent BottomNavActivity = new Intent(this,BottomNav.class);
        startActivity(BottomNavActivity);
    }
}
