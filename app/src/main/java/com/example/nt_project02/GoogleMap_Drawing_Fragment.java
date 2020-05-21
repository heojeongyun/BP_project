package com.example.nt_project02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.CustomData.InfoWindowData;
import com.example.nt_project02.CustomData.MarkerItem;
import com.example.nt_project02.CustomData.MarkerModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class GoogleMap_Drawing_Fragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {


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
    public static Marker currentMarker=null;

    private Place place;
    private Location location=new Location("");
    public static PlacesClient placesClient;
    private Bitmap bitmap;

    private StorageReference MapImageref;
    private ArrayList<MarkerModel.MarkerData> markerModels;
    private  DatabaseReference mMarkerDatabase;
    private  ValueEventListener postListener;

    private View marker_root_view;
    private TextView tv_marker;


    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public GoogleMap_Drawing_Fragment() {

        markerModels=new ArrayList<MarkerModel.MarkerData>();



    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    //파리미터 위치로 마크를 찍고 카메라 이동해주는 메서드
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet, String Content, Bitmap bitmap){


        if(currentMarker !=null){
            currentMarker.remove();
        }

        if(location !=null) {
            //현재위치의 위도 경도 가져옴


            final LatLng[] currentLocation = {new LatLng(location.getLatitude(), location.getLongitude())};

            // 마커 설정
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentLocation[0])
                    .title(markerTitle)
                    .snippet(markerSnippet)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


            InfoWindowData info = new InfoWindowData();
            info.setMarker_Content(Content);

            currentMarker.setTag(info);


            //지도 카메라 이동
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation[0]));
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




        Button button=(Button) layout.findViewById(R.id.button);

        //Google Places Api 초기 설튼 설정

        Places.initialize(getContext(), "AIzaSyA7YlrC4J7fuD4VCTkCNgoQzg8zTVJytsg");
        placesClient = Places.createClient(getContext());




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

       /* if(mMap != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            mMap.clear();
            mMarkerDatabase.addValueEventListener(postListener);

        }*/
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
                            .setMaxWidth(1000) // Optional.
                            .setMaxHeight(500) // Optional.
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
                String Content=data.getStringExtra("Content"); // InfoWindow_Edit Activity 데이터 가져오기
                Log.d(TAG,"Content:"+Content);
                //setCurrentLocation(location,place.getName(),place.getAddress(),Content,bitmap); //마커 정보 다시 설정
                Log.d(TAG,"확인");
            }

        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng SEOUL = new LatLng(37.56, 126.97);

        LatLng pknu = new LatLng(35.134023, 129.104697);



        mMap=googleMap;
        setCustomMarkerView();




        mMarkerDatabase =FirebaseDatabase.getInstance().getReference().child("chatrooms").child(MessageActivity.chatRoomUid).child("CustomMarker");



         postListener= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {





                mMap.clear();
                int i=1;
                PolygonOptions polygonOptions = new PolygonOptions();
                // Get Post object and use the values to update the UI
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){


                    Log.e(TAG,"변경");
                    Log.e(TAG,"snapshot:"+ snapshot);

                    MarkerModel.MarkerData markerModel=snapshot.getValue(MarkerModel.MarkerData.class);

                    LatLng currentLocation =new LatLng(markerModel.Latitude,markerModel.Longitude);



                    tv_marker.setText(Integer.toString(i));
                    i+=1;

                    // 마커 설정
                    Marker Marker= mMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title(markerModel.markerTitle)
                            .snippet(markerModel.markerSnippet)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(),marker_root_view))));

                   /* MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(price);
                    markerOptions.position(position);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker_root_view)));*/

                    InfoWindowData info=new InfoWindowData();
                    info.setMarker_Content(markerModel.Content);

                    Marker.setTag(info);


                    polygonOptions.add(currentLocation);
                    polygonOptions.strokeJointType(JointType.ROUND);
                    polygonOptions.strokeColor(Color.RED);
                    polygonOptions.strokeWidth(10);

                    mMap.addPolygon(polygonOptions);
                    /*MarkerItem markerItem=new MarkerItem(35.134023, 129.104697, "1");
                    addMarker(markerItem,false);*/


                }



                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mMarkerDatabase.addValueEventListener(postListener);





        mMap.moveCamera(CameraUpdateFactory.newLatLng(pknu));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        mMap.setOnInfoWindowClickListener(this);


        //CustomWindow 설정
        CustomInfoAdapter customInfoAdapter = new CustomInfoAdapter(getActivity());
        mMap.setInfoWindowAdapter(customInfoAdapter);




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
    public void onInfoWindowClick(final Marker marker) {
/*
        Toast.makeText(getContext(), "Info window clicked",Toast.LENGTH_SHORT).show();
*/

        //검색 마커 클릭 (빨간색)
        if(marker.isDraggable()){
            Intent intent=new Intent(getContext(),InfoWindow_Edit.class);
            intent.putExtra("Place",place);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("image",byteArray);

            //intent.putExtra("Bitmap",bitmap);
            //intent.putExtra("Place_Name",marker.getTitle());
            //intent.putExtra("Place_Adress",marker.getSnippet());
            getActivity().startActivityForResult(intent,REQUEST_CONTENT);


        }else //등록 마커 클릭 시(파란색)
            {

                Intent intent=new Intent(getContext(),InfoWindow_Edit.class);
                intent.putExtra("MarkerAdress",marker.getSnippet());
                getActivity().startActivityForResult(intent,REQUEST_CONTENT);

        }


    }

    private void setCustomMarkerView() {

        marker_root_view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {


        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        String price = markerItem.getPrice();



        tv_marker.setText(price);

        if (isSelectedMarker) {
            tv_marker.setBackgroundResource(R.drawable.ic_marker_phone_blue);
            tv_marker.setTextColor(Color.WHITE);
        } else {
            tv_marker.setBackgroundResource(R.drawable.ic_marker_phone);
            tv_marker.setTextColor(Color.BLACK);
        }






        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(price);
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker_root_view)));



        return mMap.addMarker(markerOptions);

    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }














}


