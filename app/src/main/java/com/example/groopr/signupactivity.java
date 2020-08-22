package com.example.groopr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.StatusBarManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import static com.parse.ParseException.INVALID_SESSION_TOKEN;

public class signupactivity extends AppCompatActivity {
    //background
    ConstraintLayout constraintLayout;

    TextView orlogin;

    //errors
    TextView usernameerror;
    TextView passworderror;
    TextView phonenumbererror;

    //edittext fields
    EditText usernamefield;
    EditText passwordfield;
    EditText repassfield;
    EditText phonenumberfield;

    //storing creds
    String username;
    String password;
    String phonenumber;

    //handle invalid sessiontoken
    public void handleInvalidSessionToken(){
        ParseUser.logOut();
    }

    //sign up process
    public void signup(View v){
        //get users details and store them in variables
        final int[] allfieldsvalid = {0};
        username = usernamefield.getText().toString();

        if(usernamefield.getText().toString().matches("") || usernamefield.getText().toString().contains("\\s")){
            usernameerror.setText("Please enter a valid username");

        }
        else{
            usernameerror.setText(""); //reset the error textview
            allfieldsvalid[0]++;


        }

        // checking valid passwordifield
        if(passwordfield.getText().toString().matches("") || passwordfield.getText().toString().contains("\\s+")){
            passworderror.setText("Please enter a valid password");
        }
        else{ //if valid
            passworderror.setText("");
            allfieldsvalid[0]++;
        }

        //checking valid repassfield
        if(repassfield.getText().toString().matches("")){
            passworderror.setText("Please enter a matching password");
        }
        else{
            passworderror.setText("");
            allfieldsvalid[0]++;
        }

        //checking phone number field
        if(phonenumberfield.getText().toString().matches("") || phonenumberfield.length() < 10){
            phonenumbererror.setText("Please enter a valid phone number");
        }
        else{      //if valid phone number
            phonenumbererror.setText("");
            allfieldsvalid[0]++;
        }
        Log.i("checkey", Integer.toString(allfieldsvalid[0]));

        //once all criteria has been met
        if(allfieldsvalid[0] == 4){
            //if reenter password and password match
            if(passwordfield.getText().toString().matches(repassfield.getText().toString())) {

                String regex = "^(?=.*[0-9])"             //the regex to match with the entered password
                        + "(?=.*[a-z])(?=.*[A-Z])"
                        + "(?=.*[@#$%^&+=])"
                        + "(?=\\S+$).{8,20}$";

                String passwordcheck = passwordfield.getText().toString(); //the password entered converted to a string
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(passwordcheck);

                if(m.matches()){              //if the password matches the criteria then sign up is successful
                    passworderror.setText("");     //set error to blank
                    password = passwordfield.getText().toString();
                    phonenumber = phonenumberfield.getText().toString();


                    ParseUser parseUser = new ParseUser();
                    //setting user credentials
                    parseUser.setUsername(username);
                    parseUser.setPassword(password);
                    parseUser.put("Phone_number", phonenumber);
                    parseUser.put("Groups", "");
                    parseUser.put("GroupNames", "");
                    parseUser.put("Marker_id", username + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+"marker");
                    Log.i("checkey", "parseuserexecuted");

                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Log.i("checkey", "upload to cloud successfull");

                                //if all goes correctly, start the login activity
                                //TODO: Add an otp system for phone number verification

                                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                                startActivity(intent);
                            }
                            else{
                                if(e.getCode() == INVALID_SESSION_TOKEN){
                                    handleInvalidSessionToken();
                                }
                                else{
                                    usernameerror.setText("Username already exists");
                                }

                                Log.i("checkey", e.toString());

                            }
                        }
                    });

                }

                else{              //if the password does not match the criteria
                    passworderror.setText("The password should be between 8-20 characters long,have a capital number and have atleast one symbol and one number");
                }
            }
            else{            //if the re and password fields dont match
                passworderror.setText("The passwords don't match..");
            }
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
        setContentView(R.layout.activity_main);


        //TODO: FIX UI ALIGNMENT
        //TODO: ADD COMMENTS TO ALL CODE
        //TODO: ADD PROFILE CUSTOMIZATION ON A NAVIGATION DRAWER ACTIVITY


        //Textview orlogin for jumping to login activity

        orlogin = (TextView) findViewById(R.id.orlogin);
        orlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
            }
        });

        //error textviews
        usernameerror = (TextView) findViewById(R.id.usernameerror);
        passworderror = (TextView) findViewById(R.id.passworderror);
        phonenumbererror = (TextView) findViewById(R.id.phonenumbererror);


        //initializing user creds
        username = "";
        password = "";
        phonenumber = "";

        //Background
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintlayout);

        //user's details fields
        usernamefield = (EditText) findViewById(R.id.usernamefield);
        passwordfield = (EditText) findViewById(R.id.passwordfield);
        repassfield = (EditText) findViewById(R.id.reenterpassfield);
        phonenumberfield = (EditText) findViewById(R.id.phonenumberfield);

        //parse init

        try {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId("myappID")
                    // if desired
                    .clientKey("")
                    .server("http://13.58.151.77/parse/")
                    .build()
            );

        }catch (java.lang.IllegalStateException e){
            e.printStackTrace();
        }

        ParseUser parseUser = ParseUser.getCurrentUser();
        if(parseUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }



        //Setting keyboard to drop if clicked anywhere els

        constraintLayout.setOnClickListener(new View.OnClickListener() {
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
        repassfield.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    signup(v);
                }
                return false;
            }
        });


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
