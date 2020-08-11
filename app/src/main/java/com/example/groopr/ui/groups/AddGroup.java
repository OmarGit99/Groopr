package com.example.groopr.ui.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groopr.LoadingScreenActivity;
import com.example.groopr.MainActivity;
import com.example.groopr.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroup extends AppCompatActivity {
    EditText groupnamefield;
    ImageView addphotoclicker;
    Spinner groupcatsspin;
    TextView addphototextviewclicker;
    Boolean isImageUploaded;
    String groupCatSelected;
    Bitmap bitmap;
    TextView orJoin;

    private long mLastClickTime = 0;

    //TODO: ADD A SMS INVITATION FOR MEMBERS ON GROUP CREATION

    //get permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {

                //TODO:Make the imageview more clean
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                addphototextviewclicker.setVisibility(View.INVISIBLE);


                addphotoclicker.setImageBitmap(bitmap);


                //since image is now uploaded
                isImageUploaded = true;


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void createGroup(View v){

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        TextView errorcreategroup = findViewById(R.id.errorcreategroup);
        if(groupnamefield.getText().toString().matches("")){
            errorcreategroup.setText("Please enter a username...");
        }
        else{

            v.setEnabled(false);


            ParseObject parseObject = new ParseObject("Groups");

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            final String group_id = groupnamefield.getText().toString().replaceAll("\\s", "_")+ timeStamp;

            parseObject.put("GroupID", group_id);

            //TODO: DISALLOW SYMBOLS IN GROUP NAMES
            parseObject.put("GroupName", groupnamefield.getText().toString());
            parseObject.put("Admin", ParseUser.getCurrentUser().getUsername());

            String[] membersgroup = new String[]{ParseUser.getCurrentUser().getUsername()};

            String members= "";
            for(int i = 0; i<membersgroup.length; i++){
                if(i ==0){
                    members= members+membersgroup[i];
                }
                else {
                    members = members + "," + membersgroup[i];
                }
            }

            parseObject.put("Members", members);

            if(isImageUploaded){
                Intent intentloader = new Intent(getApplicationContext(), LoadingScreenActivity.class);
                intentloader.putExtra("PURPOSE", "Creating...");
                intentloader.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentloader);
                finish();


                int bitmap_size = bitmap.getRowBytes()*bitmap.getHeight();
                Log.i("image_upload", Integer.toString(bitmap_size));

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] bytearray = stream.toByteArray();
                ParseFile file = new ParseFile(group_id + "_banner.png", bytearray);

                parseObject.put("Banner", file);
                Log.i("groupcreation", "Yes Image is uploaded!");

            }


            parseObject.put("Category", groupCatSelected);
            parseObject.put("Feedid",group_id+"_feed");

            
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        String groups = parseUser.getString("Groups");
                        if(!groups.matches("")) {
                            groups = groups + "," + group_id;
                        }
                        else {
                            groups = groups+group_id;
                        }

                        parseUser.put("Groups",groups);

                        String group_names = parseUser.getString("GroupNames");
                        if(!group_names.matches("")) {
                            group_names = group_names + "," + groupnamefield.getText().toString();
                        }
                        else {
                            group_names = group_names+groupnamefield.getText().toString();
                        }
                        parseUser.put("GroupNames", group_names);


                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Toast.makeText(AddGroup.this, "Group created", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("CREATED_GROUP", groupnamefield.getText().toString());
                                    startActivity(intent);

                                }
                            }
                        });

                    }
                    else {
                        Log.i("Image_upload", e.toString());
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
        setContentView(R.layout.activity_add_group);

        orJoin = findViewById(R.id.orJoin);

        orJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinGroupActivity.class);
                startActivity(intent);
            }
        });

        //group category selected by the user
        groupCatSelected = "General";  //Default

        //setting the boolean to false at first
        isImageUploaded =false;

        //initializing fields
        groupcatsspin = findViewById(R.id.groupcatsspin);
        groupnamefield = findViewById(R.id.groupnamefield);
        addphotoclicker = findViewById(R.id.imageView4);
        addphototextviewclicker = findViewById(R.id.textView9);


        //upload images for group pic
        addphotoclicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                        else{
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 1);
                        }
            }
        });


        final ArrayList<String> groupcats = new ArrayList<>();
        groupcats.add("General");
        groupcats.add("Art");
        groupcats.add("Sport");
        groupcats.add("Gaming");
        groupcats.add("Book club");
        groupcats.add("Biz Team");
        groupcats.add("Computers");
        groupcats.add("Family");
        groupcats.add("Band");
        groupcats.add("School");
        groupcats.add("Other");

        //same code but for textview below the imageview
        addphototextviewclicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECKING PERMISSIONS
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }

            }
        });


        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items,
                R.layout.color_spinner_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);



        groupcatsspin.setAdapter(adapter);


        //set onclick listener for spinner
        groupcatsspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupCatSelected =groupcats.get(position);
                Log.i("designchangecheck", groupCatSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //hide keyboard if background clicked
        ConstraintLayout background = findViewById(R.id.createGroupBackground);
        background.setOnClickListener(new View.OnClickListener() {
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


    }
}
