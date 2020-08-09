package com.example.groopr.ui.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groopr.LoadingScreenActivity;
import com.example.groopr.MainActivity;
import com.example.groopr.R;
import com.example.groopr.ui.groups.GroupsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadImage extends AppCompatActivity {
    EditText captionEditText;
    ImageView uploadedImage;
    Button submitButton;
    TextView errorupload;
    Spinner group_selector;
    ArrayList<String> user_groups;
    String groupSelected = "";
    Boolean isImageUploaded = false;
    String group_clicked_id = "";
    Bitmap bitmap;

    //on submit button click
    public void submitImage(View v){
        //if caption is not entered set an error else jump to other activity

        if(captionEditText.getText().toString().matches("") || !isImageUploaded){
            errorupload.setText("Please enter a caption....");
        }
        else {
            //go back to main activity / groups fragment
            Log.i("UploadImage", "success");

            String[] user_group_ids = ParseUser.getCurrentUser().get("Groups").toString().split(",");

            for(int i = 0; i<user_groups.size(); i++){
                if(user_groups.get(i).matches(groupSelected)){
                    group_clicked_id = user_group_ids[i];
                }
            }

            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Groups");
            parseQuery.whereEqualTo("GroupID", group_clicked_id);
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e== null){
                        if (objects.size()>0){
                            ParseObject parseObject = new ParseObject(objects.get(0).get("Category").toString());
                            parseObject.put("Posted_by", ParseUser.getCurrentUser().getUsername());
                            parseObject.put("Group_id", group_clicked_id);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                            byte[] bytearray = stream.toByteArray();
                            ParseFile file = new ParseFile(group_clicked_id + "_post.png", bytearray);

                            parseObject.put("Post", file);

                            Intent intentloader = new Intent(getApplicationContext(), LoadingScreenActivity.class);
                            intentloader.putExtra("PURPOSE", "Posting...");
                            intentloader.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentloader);
                            finish();

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){
                                        Toast.makeText(UploadImage.this, "Image posted to "+ groupSelected, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    }
                    else {
                        errorupload.setText("Failure while uploading");
                    }

                }
            });

        }

    }


    //camera function
    public void takephoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    //once asked for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
        else if(requestCode ==2){
            if(permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takephoto();
            }

        }
    }

    //get photo from media store
    public void getPhoto(){
        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent1, 1);
    }

    //Once the image arrives
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                uploadedImage.setImageBitmap(bitmap);
                isImageUploaded = true;


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //for camera activity result
        else if(requestCode == 2 && resultCode ==RESULT_OK && data != null){
            //Enter camera related code here
            bitmap = (Bitmap) data.getExtras().get("data");
            uploadedImage.setImageBitmap(bitmap);
            isImageUploaded = true;
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
        setContentView(R.layout.activity_upload_image);

        group_selector = findViewById(R.id.groupcatsspin2);

        //components init
        captionEditText = (EditText) findViewById(R.id.captionEditText);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImage);
        submitButton =(Button) findViewById(R.id.submit);
        errorupload = (TextView) findViewById(R.id.errorupload);
        user_groups = new ArrayList<>();

        //when activity starts get the purpose
        Intent intent = getIntent();
        String purpose = intent.getStringExtra("purpose");

        if(purpose.matches("UPLOAD")){
            //check for permissions
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else {
                //if granted get the photo
                getPhoto();
            }
        }
        //camera purpose
        else if(purpose.matches("CAMERA")){
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);

            }
            else{
                takephoto();
            }

        }

        group_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupSelected =user_groups.get(position);
                Log.i("designchangecheck", groupSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for(int i = 0; i<GroupsFragment.maplist.size(); i++){
            user_groups.add(GroupsFragment.maplist.get(i).get("title").toString());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_layout, user_groups);
        group_selector.setAdapter(arrayAdapter);



        //submit image if enter key is pressed
        captionEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    submitImage(v);
                }

                return false;
            }
        });

        ConstraintLayout uploadbackground = (ConstraintLayout) findViewById(R.id.uploadImagebackground);
        uploadbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                }
                catch(java.lang.NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        //if imageview is clicked start the get photo process
        uploadedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for permission
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                else {
                    //if granted get the photo
                    getPhoto();
                }

            }
        });

        //rotateimage
        ImageView rotateimage = (ImageView) findViewById(R.id.rotateimage);
        rotateimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadedImage.setRotation(uploadedImage.getRotation()+90);
            }
        });



    }
}
