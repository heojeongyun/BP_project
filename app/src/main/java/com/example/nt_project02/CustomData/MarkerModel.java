package com.example.nt_project02.CustomData;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class MarkerModel {


    public Map<String, MarkerData> CustomMarker=new HashMap<>(); //구글맵 내 추가한 마커정보


    public static class MarkerData{
        public String uid;
        public String Content;
        public Object timestamp;
        public Double Latitude;
        public Double Longitude;
        public String markerTitle;
        public String markerSnippet;
        public String ImageUrl;



    }
}

