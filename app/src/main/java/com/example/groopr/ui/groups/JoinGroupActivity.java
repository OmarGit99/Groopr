package com.example.groopr.ui.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groopr.LoadingScreenActivity;
import com.example.groopr.MainActivity;
import com.example.groopr.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class JoinGroupActivity extends AppCompatActivity {
    TextView orCreate;
    EditText groupidfield;
    TextView errorjoingroup;

    public void joinGroup(View v){
        int validitycounter = 0;

        if(groupidfield.getText().toString().matches("")){
            errorjoingroup.setText("Please enter a valid ID..");
        }
        else{
            String[] user_groups = ParseUser.getCurrentUser().getString("Groups").split(",");
            for(int i = 0; i<user_groups.length; i++){
                if(user_groups[i].matches(groupidfield.getText().toString())){
                    errorjoingroup.setText("You are already in this group..");
                    validitycounter++;

                }
            }
            final String groupid = groupidfield.getText().toString();

            if(validitycounter == 0){
                    final ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Groups");
                    parseQuery.whereEqualTo("GroupID", groupid);
                    parseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> objects, ParseException e) {
                            if(e==null){
                                if(objects.size()>0){
                                    ParseUser parseUser = ParseUser.getCurrentUser();
                                    String groups = parseUser.getString("Groups");
                                    if(!groups.matches("")) {
                                        groups = groups + "," + groupid;
                                    }
                                    else {
                                        groups = groups+groupid;
                                    }

                                    parseUser.put("Groups",groups);

                                    String group_names = parseUser.getString("GroupNames");
                                    if(!group_names.matches("")) {
                                        group_names = group_names + "," + objects.get(0).getString("GroupName");
                                    }
                                    else {
                                        group_names = group_names+objects.get(0).getString("GroupName");
                                    }
                                    parseUser.put("GroupNames", group_names);

                                    String members = objects.get(0).getString("Members");
                                    members = members+","+ parseUser.getUsername();
                                    objects.get(0).put("Members", members);

                                    Intent intentloader = new Intent(getApplicationContext(), LoadingScreenActivity.class);
                                    intentloader.putExtra("PURPOSE", "Joining...");
                                    startActivity(intentloader);

                                    objects.get(0).saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                Log.i("Joincheck","object saved successfully" );

                                            }
                                            else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                    parseUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                Toast.makeText(JoinGroupActivity.this, "Joined "+ objects.get(0).getString("GrouName"), Toast.LENGTH_SHORT);
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
                                e.printStackTrace();
                            }
                        }
                    });
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
        setContentView(R.layout.activity_join_group);

        orCreate = findViewById(R.id.orCreate);
        orCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddGroup.class);
                startActivity(intent);
            }
        });

        groupidfield = findViewById(R.id.groupidfield);
        errorjoingroup = findViewById(R.id.errorjoingroup);



    }
}