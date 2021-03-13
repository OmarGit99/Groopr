package com.example.groopr;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileCustomization extends AppCompatActivity {
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    ArrayList<EditText> editexts;
    ArrayList<String> user_tags;
    ParseUser current_user;

    int no_of_tags;



    public void tagSubmitter(View view){
        for(EditText et:editexts){
            if(!et.getText().toString().matches("")){
                no_of_tags++;
                user_tags.add(et.getText().toString());
            }
        }

        if(no_of_tags == 0){
            Toast.makeText(ProfileCustomization.this, "NO TAGS ENTERED", Toast.LENGTH_SHORT).show();

        }else{
            String tags2upload = user_tags.get(0);
            for(int i = 1; i<no_of_tags; i++){
                tags2upload = tags2upload +","+ user_tags.get(i);
            }
            Toast.makeText(ProfileCustomization.this, tags2upload, Toast.LENGTH_SHORT).show();

            current_user.put("user_tags", tags2upload);
            current_user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e!=null){
                        e.printStackTrace();
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
        setContentView(R.layout.activity_profile_customization);

        no_of_tags = 0;
        editText1 = findViewById(R.id.editTextTextPersonName);
        editText2 = findViewById(R.id.editTextTextPersonName3);
        editText3 = findViewById(R.id.editTextTextPersonName4);
        editText4 = findViewById(R.id.editTextTextPersonName5);

        user_tags = new ArrayList<>();

        editexts = new ArrayList<>();
        editexts.add(editText1);
        editexts.add(editText2);
        editexts.add(editText3);
        editexts.add(editText4);
        current_user = ParseUser.getCurrentUser();
        String[] user_old_attrs = current_user.getString("user_tags").split(",");

        for(int x = 0; x<user_old_attrs.length; x++){
            editexts.get(x).setText(user_old_attrs[x]);
        }

    }
}