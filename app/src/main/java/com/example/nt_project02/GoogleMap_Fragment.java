package com.example.nt_project02;

        import android.Manifest;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.media.audiofx.PresetReverb;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.core.app.ActivityCompat;
        import androidx.fragment.app.Fragment;

        import android.provider.Settings;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.location.places.Place;
        import com.google.android.gms.location.places.Places;
        import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
        import com.google.android.gms.location.places.ui.PlaceSelectionListener;
        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.MapsInitializer;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;


public class GoogleMap_Fragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final LatLng DEFAULT_LOCATION=new LatLng(37.56,126.97);
    private static final String TAG="GooglaMap_Fragment";
    private static final int GPS_ENABLE_REAUEST_CODE=2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=2002;
    private static final int UPDATE_INTERVAL_MS=15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS=15000;

    private GoogleMap googleMap=null;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient=null;
    private Marker currentMarker=null;
    private LatLng s;



    public GoogleMap_Fragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet){
        if( currentMarker !=null)
            currentMarker.remove();

        if(location !=null){
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation =new LatLng(location.getLatitude(),location.getLongitude());

            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker=this.googleMap.addMarker(markerOptions);

            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }
    }










    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_google_map, container, false);

        mapView = (MapView)layout.findViewById(R.id.map);
        mapView.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment=(PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                /*Location location= new Location("");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);

                setCurrentLocation(location, place.getName().toString(),place.getAddress().toString());*/
                s=place.getLatLng();



            }

            @Override
            public void onError(Status status) {
                Log.i(TAG,"An error occurred:"+status);
            }
        });


        return layout;




    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//액티비티가 처음 생성될 때 실행되는 함수

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }




    private void buildGoogleApiClient(){
        googleApiClient =new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        s=new LatLng(34.56, 126.97);
        LatLng SEOUL = new LatLng(37.56, 126.97);

        LatLng pknu = new LatLng(35.134023, 129.104697);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(s);
        markerOptions.title("부경대");
        markerOptions.snippet("가온관");
        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pknu));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        buildGoogleApiClient();

    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


