package com.example.nt_project02;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InfoWindow_Edit extends AppCompatActivity {

    String Place_Name;
    String Place_Adress;
    String Content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow__edit);

        Intent data=getIntent();

        Place_Name=data.getStringExtra("Place_Name");
        Place_Adress=data.getStringExtra("Place_Adress");

        TextView PlaceName_TextView=(TextView) findViewById(R.id.activity_infowindow_edit_PlaceName);
        TextView PlaceAdress_TextView=(TextView) findViewById(R.id.activity_infowindow_edit_PlaceAdress);
        final EditText Content_EditText=(EditText) findViewById(R.id.activity_infowindow_edit_Content_EditText);
        Button Register_Button=(Button) findViewById(R.id.activity_infowindow_edit_Register_Button);

        PlaceName_TextView.setText(Place_Name);
        PlaceAdress_TextView.setText(Place_Adress);


        Register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Content= (Content_EditText.getText()).toString();
                Intent intent=new Intent();
                intent.putExtra("Content",Content);
                setResult(Activity.RESULT_OK,intent);
                finish();

            }
        });








    }
}
