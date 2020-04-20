package com.example.nt_project02;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.nt_project02.CustomData.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;

public class CustomInfoAdapter extends Fragment implements GoogleMap.InfoWindowAdapter {


    private Activity context;

    public CustomInfoAdapter(Activity context) {
        this.context=context;

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.news,container,false);

        return rootView;
    }

    @Override
    public View getInfoWindow(Marker marker) {


        return null;

    }

    @Override
    public View getInfoContents(Marker marker)

    {
        //Custom Window layou Inflater
        View infoWindow = context.getLayoutInflater().inflate(R.layout.googlemap_custom_infowindow, null);

        TextView PlaceName=(TextView) infoWindow.findViewById(R.id.googlemap_custom_infowindow_PlaceName_TextView);
        TextView PlaceAdress=(TextView) infoWindow.findViewById(R.id.googlemap_custom_infowindow_PlaceAdress_TextView);
        TextView Content_TextView=(TextView) infoWindow.findViewById(R.id.googlemap_custom_infowindow_Content_TextView);
        //ImageView PlaceImage_ImageView=(ImageView) infoWindow.findViewById(R.id.googlemap_custom_infowindow_PlaceImage_ImageView);


        PlaceName.setText(marker.getTitle());
        PlaceAdress.setText(marker.getSnippet());

        InfoWindowData infoWindowData=(InfoWindowData) marker.getTag();


        Content_TextView.setText(infoWindowData.getMarker_Content());



        return infoWindow;
    }
}
