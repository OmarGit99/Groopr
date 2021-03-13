package com.example.groopr.ui.public_feed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groopr.Photo;
import com.example.groopr.PhotoAdapter;
import com.example.groopr.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PublicFeedFragment extends Fragment {

    private PublicFeedViewModel publicFeedViewModel;

    private RecyclerView.LayoutManager lManagerpub;
    private PhotoAdapter adapterpub;
    String groupCatSelected;
    RecyclerView recyclerViewpub;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publicFeedViewModel =
                ViewModelProviders.of(this).get(PublicFeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_public_feed, container, false);

        groupCatSelected = "General";

        final Spinner groupcatspin = root.findViewById(R.id.groupcatspinpub);
        recyclerViewpub = root.findViewById(R.id.recycler_viewpub);

        lManagerpub = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerViewpub.setLayoutManager(lManagerpub);

        List<Photo> photoListpub = new ArrayList<>();
        adapterpub = new PhotoAdapter(getActivity().getApplicationContext(), photoListpub);
        recyclerViewpub.setAdapter(adapterpub);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                getActivity().getApplicationContext(),
                R.array.Spinner_items,
                R.layout.color_spinner_layout
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        final ArrayList<String> groupcatspub = new ArrayList<>();
        groupcatspub.add("General");
        groupcatspub.add("Art");
        groupcatspub.add("Sport");
        groupcatspub.add("Gaming");
        groupcatspub.add("Book club");
        groupcatspub.add("Biz Team");
        groupcatspub.add("Computers");
        groupcatspub.add("Family");
        groupcatspub.add("Band");
        groupcatspub.add("School");
        groupcatspub.add("Fashion");
        groupcatspub.add("Travel");
        groupcatspub.add("Other");



        groupcatspin.setAdapter(adapter);
        groupcatspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupCatSelected =groupcatspub.get(position);
                Log.i("designchangecheck", groupCatSelected);


                lManagerpub = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerViewpub.setLayoutManager(lManagerpub);

                List<Photo> photoListpub = new ArrayList<>();
                adapterpub = new PhotoAdapter(getActivity().getApplicationContext(), photoListpub);
                recyclerViewpub.setAdapter(adapterpub);

                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(groupCatSelected);
                parseQuery.orderByDescending("createdAt");
                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()>0){

                                for(ParseObject object: objects){
                                    String posted_by = object.getString("Posted_by");
                                    ParseFile parseFile = (ParseFile) object.get("Post");
                                    parseFile.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if(data != null) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                Photo photo = new Photo(bitmap);
                                                adapterpub.addPhoto(photo);
                                                ((LinearLayoutManager) lManagerpub).scrollToPositionWithOffset(0, 0);
                                            }
                                        }
                                    });
                                }

                            }
                            else{
                                Toast.makeText(getActivity().getApplicationContext(), "No posts :(", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(groupCatSelected);
        parseQuery.orderByDescending("createdAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        for(ParseObject object: objects){
                            String posted_by = object.getString("Posted_by");
                            ParseFile parseFile = (ParseFile) object.get("Post");
                            parseFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        Photo photo = new Photo(bitmap);
                                        adapterpub.addPhoto(photo);
                                        ((LinearLayoutManager) lManagerpub).scrollToPositionWithOffset(0, 0);
                                    }
                                }
                            });
                        }

                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "No posts :(", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    e.printStackTrace();
                }
            }
        });





        return root;
    }
}
