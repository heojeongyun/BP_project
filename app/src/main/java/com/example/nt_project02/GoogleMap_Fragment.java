package com.example.nt_project02;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class GoogleMap_Fragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {


    private static final LatLng DEFAULT_LOCATION=new LatLng(37.56,126.97);
    private static final String TAG="GooglaMap_Fragment";
    private static final int GPS_ENABLE_REAUEST_CODE=2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=2002;
    private static final int UPDATE_INTERVAL_MS=15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS=15000;
    private static final int AUTOCOMPLETE_REQUEST_CODE =1 ;
    private static final int REQUEST_CONTENT = 101;

    private GoogleMap mMap=null;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient=null;
    private Marker currentMarker=null;
    private LatLng s;
    private Place place;
    private Location location=new Location("");
    private String Content;
    private PlacesClient placesClient;
    private Bitmap bitmap;




    public GoogleMap_Fragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //파리미터 위치로 마크를 찍고 카메라 이동해주는 메서드
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet, String Content, Bitmap bitmap){
        if( currentMarker !=null)
            currentMarker.remove();

        if(location !=null){
            //현재위치의 위도 경도 가져옴

            
            LatLng currentLocation =new LatLng(location.getLatitude(),location.getLongitude());

            //마커 설정
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker=mMap.addMarker(markerOptions);

            //CustomWindow 설정
            View infoWindow = getLayoutInflater().inflate(R.layout.googlemap_custom_infowindow, null);
            CustomInfoAdapter customInfoAdapter = new CustomInfoAdapter(infoWindow, markerTitle,markerSnippet, Content, bitmap);
            mMap.setInfoWindowAdapter(customInfoAdapter);

            //지도 카메라 이동
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            return;
        }
    }










    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_google_map, container, false);

        mapView = (MapView)layout.findViewById(R.id.map);
        mapView.getMapAsync(this);


        //Google Places Api 초기 설튼 설정

        Places.initialize(getContext(), "AIzaSyA7YlrC4J7fuD4VCTkCNgoQzg8zTVJytsg");
        placesClient = Places.createClient(getContext());

        Button button=(Button) layout.findViewById(R.id.button);




        //검색 버튼
        button.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                if (!Places.isInitialized()) {
                    Places.initialize(getContext(), "AIzaSyA7YlrC4J7fuD4VCTkCNgoQzg8zTVJytsg");
                }






                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.ID,Place.Field.PHOTO_METADATAS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                place = Autocomplete.getPlaceFromIntent(data);

                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+ ", "+place.getLatLng());


                if(place.getPhotoMetadatas() !=null) {
                    PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);


                    // Create a FetchPhotoRequest.
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(500) // Optional.
                            .setMaxHeight(300) // Optional.
                            .build();

                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                        @Override
                        public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {


                            bitmap = fetchPhotoResponse.getBitmap();
                            //검색 된 위치 설정
                            setCurrentLocation(location, place.getName(), place.getAddress(), "장소를 설명해주세요!", bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                int statusCode = apiException.getStatusCode();
                                // Handle error with given status code.
                                Log.e("PlacesAPI", "Place not found: " + exception.getMessage());
                            }
                        }
                    });

                    //장소 사진 정보가 없을 때
                }else{
                    bitmap= BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bluepeople_logo);
                    setCurrentLocation(location, place.getName(), place.getAddress(), "장소를 설명해주세요!", bitmap);
                }













            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }

        if(requestCode == REQUEST_CONTENT){ //구글 맵 InfoWindow 클릭 시
            if(resultCode== RESULT_OK){ //결과가 정상적이라면


                Content=data.getStringExtra("Content"); // InfoWindow_Edit Activity 데이터 가져오기
                setCurrentLocation(location,place.getName(),place.getAddress(),Content,bitmap); //마커 정보 다시 설정
                Log.d(TAG,"확인");
            }

        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng SEOUL = new LatLng(37.56, 126.97);

        LatLng pknu = new LatLng(35.134023, 129.104697);

        mMap=googleMap;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pknu);
        markerOptions.title("부경대");
        markerOptions.snippet("가온관");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pknu));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        mMap.setOnInfoWindowClickListener(this);







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

    //Custom InfoWindow 클릭 시 마커정보 수정 액티비티로 이동
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "Info window clicked",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getContext(),InfoWindow_Edit.class);
        intent.putExtra("Place_Name",place.getName());
        intent.putExtra("Place_Adress",place.getAddress());
        getActivity().startActivityForResult(intent,REQUEST_CONTENT);

    }





}


