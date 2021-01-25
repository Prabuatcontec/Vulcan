package com.example.vulcan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

public class Detrash extends AppCompatActivity   {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detrash);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

}

