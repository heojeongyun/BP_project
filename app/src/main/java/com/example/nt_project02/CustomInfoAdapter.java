package com.example.nt_project02;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;

public class CustomInfoAdapter extends Fragment implements GoogleMap.InfoWindowAdapter {


    View window;
    String markerTitle;
    String markerSnippet;
    String Content;
    Bitmap bitmap;

    public CustomInfoAdapter(View window, String markerTitle,String markerSnippet,String Content, Bitmap bitmap) {
        this.window= window;
        this.markerTitle=markerTitle;
        this.markerSnippet=markerSnippet;
        this.Content=Content;
        this.bitmap= bitmap;

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.news,container,false);

        return rootView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView PlaceName=(TextView) window.findViewById(R.id.googlemap_custom_infowindow_PlaceName_TextView);
        TextView PlaceAdress=(TextView) window.findViewById(R.id.googlemap_custom_infowindow_PlaceAdress_TextView);
        TextView Content_TextView=(TextView) window.findViewById(R.id.googlemap_custom_infowindow_Content_TextView);
        ImageView PlaceImage_ImageView=(ImageView) window.findViewById(R.id.googlemap_custom_infowindow_PlaceImage_ImageView);


        PlaceName.setText(markerTitle);
        PlaceAdress.setText(markerSnippet);
        Content_TextView.setText(Content);
        PlaceImage_ImageView.setImageBitmap(bitmap);


        return window;

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
