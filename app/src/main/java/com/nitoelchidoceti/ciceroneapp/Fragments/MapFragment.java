package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nitoelchidoceti.ciceroneapp.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mapView;
    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map,container,false);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = mView.findViewById(R.id.mapa);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677056, -103.347000)).title("Catedral de Guadalajara").snippet(""));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.676287, -103.346172)).title("Museo de sitio Palacio de Gobierno").snippet("Palacio de gobierno de jalisco con un pequeño museo"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677812, -103.347020)).title("Rotonda de los jalisciences ilustres").snippet("Rotonda con los monumentos y restos de los jalisciences destacados de Jalisco"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677070, -103.344939)).title("Teatro Degollado").snippet("Teatro del siglo XV llamativo por su estructura griega"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677012, -103.337684)).title("Hospicio Cabañas").snippet("Hospicio lleno de historia con más de 40 murales"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677542, -103.349246)).title("Mercado Corona").snippet("Mercado Remodelado en el 2016"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(20.677574, -103.347880)).title("Palacio municipal"));

        CameraPosition catedral = CameraPosition.builder().target(new LatLng(20.677056, -103.347000)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(catedral));
    }
}
