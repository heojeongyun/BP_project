package com.native_code.bp_project02.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.native_code.bp_project02.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        LatLng pknu = new LatLng(35.134023, 129.104697);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pknu);
        markerOptions.title("부경대");
        markerOptions.snippet("가온관");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pknu));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

}