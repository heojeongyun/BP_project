package com.native_code.bp_project02.CustomData;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MarkerModel implements Serializable {


    public Map<String, MarkerData> CustomMarker=new HashMap<>(); //구글맵 내 추가한 마커정보


    public static class MarkerData implements Serializable{
        public String key;
        public String uid;
        public String Content;
        public Object timestamp;
        public Double Latitude;
        public Double Longitude;
        public String markerTitle;
        public String markerSnippet;
        public String ImageUrl;
        public int  sequence;

        // [START post_to_map]
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("key",key);
            result.put("uid", uid);
            result.put("Content", Content);
            result.put("timestamp", timestamp);
            result.put("Latitude", Latitude);
            result.put("Longitude", Longitude);
            result.put("markerTitle", markerTitle);
            result.put("markerSnippet", markerSnippet);
            result.put("ImageUrl", ImageUrl);
            result.put("sequence",sequence);

            return result;
        }

    }


}

