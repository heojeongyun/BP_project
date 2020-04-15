package com.example.nt_project02.CustomData;

import android.graphics.Bitmap;

public class InfoWindowData {

    private Bitmap Marker_Bitmap;
    private String Marker_Content;

    public Bitmap getMarker_Bitmap() {
        return Marker_Bitmap;
    }

    public void setMarker_Bitmap(Bitmap marker_Bitmap) {
        Marker_Bitmap = marker_Bitmap;
    }

    public String getMarker_Content() {
        return Marker_Content;
    }

    public void setMarker_Content(String marker_Content) {
        Marker_Content = marker_Content;
    }
}
