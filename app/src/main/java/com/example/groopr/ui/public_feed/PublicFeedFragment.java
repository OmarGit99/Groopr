package com.example.groopr.ui.public_feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    return true;

                }

                @Override
                public void onLongPress(MotionEvent e) {

                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (child != null && clickListener != null) {

                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));

                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {

                clickListener.onClick(child, rv.getChildAdapterPosition(child));

            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface ClickListener {

            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publicFeedViewModel =
                ViewModelProviders.of(this).get(PublicFeedViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_public_feed, container, false);

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
        final ArrayList<String> posted_bys = new ArrayList<>();
        final ArrayList<String> posted_bys_group = new ArrayList<>();




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



                /*
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

                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        final ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(groupCatSelected);
        parseQuery.orderByDescending("createdAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        ArrayList<String> stringArrayList = new ArrayList<>();
                        for(ParseObject object: objects){
                            String posted_by = object.getString("Posted_by");
                            String posted_by_group = object.getString("Group_id");

                            posted_bys.add(posted_by);


                            ParseFile parseFile = (ParseFile) object.get("Post");

                            ParseQuery<ParseObject> parseQuery1 =ParseQuery.getQuery("Groups");
                            parseQuery1.whereEqualTo("GroupID", posted_by_group);
                            parseQuery1.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    for (ParseObject object1:objects){
                                        String l = object1.getString("GroupName");
                                        Log.i("postgrups", l);
                                        posted_bys_group.add(l);
                                    }
                                }
                            });
                            Log.i("parsegrups", String.valueOf(posted_bys_group.size()));

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



                        recyclerViewpub.addOnItemTouchListener(new RecyclerTouchListener(root.getContext(), recyclerViewpub, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                new AlertDialog.Builder(root.getContext())
                                        .setTitle("Group Data")
                                        .setMessage(Html.fromHtml("<font color='#dbe0e6'>Posted by: "+ posted_bys.get(position)+ "<br> Group: "+posted_bys_group.get(position)+"</font>"))

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(R.drawable.groups_icon)

                                        .show()
                                        .getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dropdowncolorv2_1)));

                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));



                        Log.i("posted_bysg", String.valueOf(posted_bys_group.size()));
                        for(String str: posted_bys){
                            Log.i("posted_bys", str);
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
