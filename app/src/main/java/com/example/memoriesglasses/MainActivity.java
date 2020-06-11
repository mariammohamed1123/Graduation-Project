package com.example.memoriesglasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void btnHome(View view){
        startActivity(new Intent(this ,MapsActivity.class));

    }
    public void btnRetrieveLocation(View view){
        startActivity(new Intent(getApplicationContext() , RetrieveMapActivity.class));



    }
}