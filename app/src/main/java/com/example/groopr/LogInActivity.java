package com.example.groopr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LogInActivity extends AppCompatActivity {
    //background
    ConstraintLayout loginbackground;

    //user entry fields
    EditText usernameloginfield;
    EditText passwordloginfield;

    //error textviews
    TextView usernameloginerror;
    TextView passwordloginerror;

    //orsignup textview
    TextView orsignup;

    //is logged out?
    Boolean loggedout;

    //field checker counter
    int validfieldscounter;



    public void logIn(View v){
        //valid field counter
        validfieldscounter =0;
        loggedout = false;
        ParseUser.logOut();


        //checking fields for valid entry from user
        if(usernameloginfield.getText().toString().matches("") ||usernameloginfield.getText().toString().contains("\\s+")){
            usernameloginerror.setText("Please enter a valid username...");
        }
        else{
            validfieldscounter++;
        }

        //checking passsword field

        if (passwordloginfield.getText().toString().matches("")||passwordloginfield.getText().toString().contains("\\s+")){
            passwordloginerror.setText("Please enter a valid password...");
        }
        else{
            validfieldscounter++;
        }

        //if validity = 2 i.e.both fields have valid values
        if(validfieldscounter == 2){
            String usernamelogin = usernameloginfield.getText().toString();
            String passwordlogin = passwordloginfield.getText().toString();

            //if u dont want to start server
            /*
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
             */


            //TODO: ADD A WAY TO LOGIN USING PHONE NUMBER
            ParseUser.logInInBackground(usernamelogin, passwordlogin, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e == null){
                        Log.i("logincheck", "successfully logged in as "+ user.getUsername());

                        //start main activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        //put extras here..

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                    else{

                        Log.i("logincheck", e.toString());
                        e.printStackTrace();

                        passwordloginerror.setText("Invalid username/password");
                    }
                }
            });

        }


    }

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
        setContentView(R.layout.activity_log_in);



        //username and password fields fetching
        usernameloginfield = (EditText) findViewById(R.id.usernamelogingield);
        passwordloginfield = (EditText) findViewById(R.id.passwordloginfield);

        //init password and username error textview
        usernameloginerror = (TextView) findViewById(R.id.usernameloginerror);
        passwordloginerror = (TextView) findViewById(R.id.passwordloginerror);

        //if orsignup textview is clicked switch to sign up activity
        orsignup = (TextView) findViewById(R.id.orsignup);
        orsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signupactivity.class);
                startActivity(intent);
            }
        });

        //bring keyboard down if clicked
        loginbackground = (ConstraintLayout) findViewById(R.id.loginbackground);
        loginbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                catch (java.lang.NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        //password enter key listener
        passwordloginfield.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    logIn(v);
                }
                return false;
            }
        });




    }
}
