package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private View mView;
    Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
     private Location currentLocation;
    private Task<Location> task;
    FloatingActionButton getLocation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map,container,false);
        context = mView.getContext();
        getLocation = mView.findViewById(R.id.btnGetLocation);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!verifyLocationPermissions(getActivity())) {
            Toast.makeText(context, "Por favor habilite los permisos", Toast.LENGTH_SHORT).show();
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                    }
                }

            });
            getLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
                }
            });
            mapView = mView.findViewById(R.id.mapa);
        }
        if (mapView != null ) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(currentLocation!=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            MapsInitializer.initialize(Objects.requireNonNull(getContext()));

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            this.googleMap = googleMap;
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677056, -103.347000)).title("Catedral de Guadalajara").snippet(""));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.676287, -103.346172)).title("Museo de sitio Palacio de Gobierno").snippet("Palacio de gobierno de jalisco con un pequeño museo"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677812, -103.347020)).title("Rotonda de los jalisciences ilustres").snippet("Rotonda con los monumentos y restos de los jalisciences destacados de Jalisco"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677070, -103.344939)).title("Teatro Degollado").snippet("Teatro del siglo XV llamativo por su estructura griega"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677012, -103.337684)).title("Hospicio Cabañas").snippet("Hospicio lleno de historia con más de 40 murales"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677542, -103.349246)).title("Mercado Corona").snippet("Mercado Remodelado en el 2016"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677574, -103.347880)).title("Palacio municipal"));
            googleMap.addMarker(new MarkerOptions().position(latLng).snippet("").title("Tu ubicación"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }else {
            mapView.getMapAsync(this);
        }
    }

    private static boolean verifyLocationPermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED&&permission2!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else{
            return true;
        }
    }
}
