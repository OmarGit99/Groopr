package com.example.groopr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingScreenActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView purposeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for hiding title and setting status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.blueappcolorthemedark));
        try {
            getSupportActionBar().hide();
        }catch (NullPointerException e){
            Log.i("check",e.getMessage());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        progressBar= findViewById(R.id.progressBar);
        progressBar.setProgress(100);

        purposeTextView = findViewById(R.id.purposeTextView);
        Intent intent = getIntent();
        purposeTextView.setText(intent.getStringExtra("PURPOSE"));

    }

}