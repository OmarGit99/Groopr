package com.example.groopr.ui.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.groopr.LoadingScreenActivity;
import com.example.groopr.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class GroupFeed extends AppCompatActivity {
    String groupname= "";
    String groupId = "";
    LinearLayout linearLayout;
    ImageView groupicon;
    String[] group_members;
    TextView noofmembers;
    ProgressBar progressBar;
    String groupcat = "";

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
        setContentView(R.layout.activity_group_feed);

        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        TextView groupHeader = findViewById(R.id.GroupHeader);
        groupicon = findViewById(R.id.imageView5);



        Intent intent = getIntent();
        groupname = intent.getStringExtra("GROUPCLICKED");
        groupId = intent.getStringExtra("GROUPIDCLICKED");

        groupHeader.setText(groupname);

        noofmembers = findViewById(R.id.noofmembers);

        linearLayout = findViewById(R.id.linearlay);
        /*
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

         */

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Groups");
        parseQuery.whereEqualTo("GroupID", groupId);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e== null) {
                    if(objects.size()>0) {
                        for (ParseObject object : objects
                        ) {
                            String group_membersstr = object.getString("Members");
                            group_members = group_membersstr.split(",");
                            noofmembers.setText(Integer.toString(group_members.length));

                            groupcat = object.getString("Category");

                            ParseFile parseFile = (ParseFile) object.get("Banner");
                            parseFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e== null && data != null){

                                        Bitmap bmp= BitmapFactory.decodeByteArray(data, 0, data.length);
                                        groupicon.setImageBitmap(bmp);

                                    }
                                    else{
                                        e.printStackTrace();
                                    }

                                }
                            });

                            ParseQuery<ParseObject> parseQuery1 = ParseQuery.getQuery(groupcat);
                            parseQuery1.whereEqualTo("Group_id", groupId);
                            parseQuery1.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null) {
                                        for (ParseObject parseObject: objects
                                        ) {
                                            ParseFile parseFile1 = parseObject.getParseFile("Post");
                                            parseFile1.getDataInBackground(new GetDataCallback() {
                                                @Override
                                                public void done(byte[] data, ParseException e) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                    ImageView imageView = new ImageView(getApplicationContext());
                                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                                    ));
                                                    imageView.setImageBitmap(bitmap);
                                                    linearLayout.addView(imageView);

                                                }
                                            }, new ProgressCallback() {
                                                @Override
                                                public void done(Integer percentDone) {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    progressBar.setProgress(percentDone);
                                                    if(percentDone == 99){
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });













    }
}