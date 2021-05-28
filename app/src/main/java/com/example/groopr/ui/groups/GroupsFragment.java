package com.example.groopr.ui.groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.groopr.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;

    static GridAdapter adapter;

    String[] group_ids;



    //Textview for notifying empty group list
    TextView nogroupstext;

    //gridview
    private GridView gridview1;
    // Create a new Array of type HashMap
    public static ArrayList<HashMap<String, Object>> maplist = new ArrayList<>();

    ImageView addgroupbutton;

    //function to combine the icons
    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2, Bitmap bmp3, Bitmap bmp4) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth()*2, bmp1.getHeight()*2, bmp1.getConfig());

        Canvas canvas = new Canvas(bmOverlay);

        //formation of the icons
        canvas.drawBitmap(bmp1, 1400, 700, null);
        canvas.drawBitmap(bmp2, 100, 1000, null);
        canvas.drawBitmap(bmp3, 650, 50, null);
        canvas.drawBitmap(bmp4, 750, 1400, null);
        return bmOverlay;

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groupsViewModel =
                ViewModelProviders.of(this).get(GroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_groups, container, false);

        //find the gridview on the layout
        gridview1 = (GridView) root.findViewById(R.id.mainGridView1);
        //find the imageview for adding groups
        addgroupbutton = root.findViewById(R.id.addgroupbutton);

        //init for no groups notifier
        nogroupstext = root.findViewById(R.id.textView12);
        nogroupstext.setVisibility(View.INVISIBLE);

        //set on click listener for the add group button
        addgroupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on icon click the add group activity starts
                Intent intent = new Intent(getActivity().getApplicationContext(), AddGroup.class);
                startActivity(intent);
            }
        });

        //onclick listener for gridview items
        gridview1.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
                Toast.makeText(getActivity().getApplicationContext(), maplist.get(p3).get("title").toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), GroupFeed.class);
                intent.putExtra("GROUPCLICKED", maplist.get(p3).get("title").toString());
                intent.putExtra("GROUPIDCLICKED", group_ids[p3]);
                startActivity(intent);

            }
        });

        // Add items to the Map list

        maplist.clear();


        final String[] usergroups = ParseUser.getCurrentUser().getString("GroupNames").split(",");
        group_ids = ParseUser.getCurrentUser().getString("Groups").split(",");



        if(ParseUser.getCurrentUser().getString("Groups").matches("")){
            nogroupstext.setVisibility(View.VISIBLE);
        }
        else {
            adapter = new GridAdapter(getActivity().getApplicationContext());
            gridview1.setAdapter(adapter);

            for (String usergroup : usergroups
            ) {
                //TODO: FIX BORDER ISSUES FOR LONG GROUP NAMES
                {
                    HashMap<String, Object> _item = new HashMap<>();
                    _item.put("title", usergroup);
                    _item.put("icon", R.drawable.user_blank_icon_smol_compressed);
                    maplist.add(_item);
                    adapter.notifyDataSetChanged();
                }

            }
        }



        /*
        {
            HashMap<String, Object> _item = new HashMap<>();
            _item.put("title", "Family");
            _item.put("icon", R.drawable.user_blank_icon_smol_compressed);
            maplist.add(_item);
        }
         */


        return root;
    }

    public class GridAdapter extends BaseAdapter {
        private Context mContext;
        public GridAdapter(Context c) {
            mContext = c;
        }
        public int getCount() {
            return maplist.size();
        }
        public Object getItem(int position) {
            return null;
        }
        public long getItemId(int position) {
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            // Inflate the layout for each list

            //create a bitmap for all the user icons, maybe pfps?
            Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.user_blank_icon_compressednotsmol);
            Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.user_blank_icon_compressednotsmol);
            Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.user_blank_icon_compressednotsmol);
            Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.user_blank_icon_compressednotsmol);

            //call the combining icon function on the icons
            Bitmap finale = overlay(bm1,bm2,bm3,bm4);


            LayoutInflater _inflater = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (v == null) {
                v = _inflater.inflate(R.layout.list_item, null);
            }

            // Get the TextView and ImageView from CustomView for displaying item
            TextView txtview = (TextView) v.findViewById(R.id.listitemTextView1);
            ImageView imgview = (ImageView) v.findViewById(R.id.listitemImageView1);

            // Set the text and image for current item using data from map list
            txtview.setText(maplist.get(position).get("title").toString());

            //imgview.setImageResource((int)maplist.get(position).get("icon"));
            imgview.setImageBitmap(finale);            //set combined image bitmap to the imageview of the menu

            //final positioning touches
            txtview.setTranslationY(-100f);
            imgview.setTranslationX(-40f);

            //for different positions on even column
            if(position % 2 == 0){
                imgview.setTranslationX(20f);
                imgview.setTranslationY(20f);
            }
            else{
                imgview.setTranslationX(15f);
            }


            return v;
        }
    }


}
