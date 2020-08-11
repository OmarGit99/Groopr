package com.example.groopr.ui.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.groopr.Photo;
import com.example.groopr.PhotoAdapter;
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
    TextView groupIDtextView;

    private RecyclerView.LayoutManager lManager;
    private PhotoAdapter adapter;

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

        groupIDtextView = findViewById(R.id.groupIDtextView);

        TextView groupHeader = findViewById(R.id.GroupHeader);
        groupicon = findViewById(R.id.imageView5);


        Intent intent = getIntent();
        groupname = intent.getStringExtra("GROUPCLICKED");
        groupId = intent.getStringExtra("GROUPIDCLICKED");


        groupHeader.setText(groupname);
        groupIDtextView.setText("Group ID: "+groupId);

        noofmembers = findViewById(R.id.noofmembers);

        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);

        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        List<Photo> photoList = new ArrayList<>();
        adapter = new PhotoAdapter(this, photoList);
        recycler.setAdapter(adapter);


        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Groups");
        parseQuery.whereEqualTo("GroupID", groupId);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e== null) {
                    if(objects.size()>0) {
                        for (final ParseObject object : objects
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
                                    if(e==null && objects.size()>0) {
                                        for (ParseObject parseObject: objects
                                        ) {
                                            ParseFile parseFile1 = parseObject.getParseFile("Post");
                                            parseFile1.getDataInBackground(new GetDataCallback() {
                                                @Override
                                                public void done(byte[] data, ParseException e) {
                                                    if(data != null) {
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                        Photo photo = new Photo(bitmap);
                                                        adapter.addPhoto(photo);
                                                        ((LinearLayoutManager) lManager).scrollToPositionWithOffset(0, 0);
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