package com.example.groopr.ui.bills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.groopr.R;
import com.example.groopr.ui.groups.GroupsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BillsFragment extends Fragment {

    private BillsViewModel billsViewModel;

    Spinner groupspinner;
    ArrayList<String> user_groups;
    String group_selected;
    String[] user_groupids;
    ParseUser user;
    String user_groupid_selected;
    String[] group_members;
    EditText amountedittext;
    int totalamount;
    Boolean isReady;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        billsViewModel =
                ViewModelProviders.of(this).get(BillsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_billing, container, false);

        user = ParseUser.getCurrentUser();
        Button button = root.findViewById(R.id.button4);
        group_selected = "";
        isReady = false;

        amountedittext = root.findViewById(R.id.cashamount);

        groupspinner = root.findViewById(R.id.groupcatsspin4);
        user_groups = new ArrayList<>();
        for(int i = 0; i< GroupsFragment.maplist.size(); i++){
            user_groups.add(GroupsFragment.maplist.get(i).get("title").toString());
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),R.layout.spinner_dropdown_layout, user_groups);
        groupspinner.setAdapter(arrayAdapter);

        groupspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group_selected = user_groups.get(position);
                user_groupids = user.get("Groups").toString().split(",");

                user_groupid_selected = "";

                for(int i = 0; i < user_groups.size(); i++){
                    if (user_groups.get(i).matches(group_selected)){
                        user_groupid_selected = user_groupids[i];
                        break;
                    }
                }


                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Groups");
                parseQuery.whereEqualTo("GroupID", user_groupid_selected);
                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()>0){
                                for(ParseObject parseObject: objects){
                                    String groupmembers = parseObject.getString("Members");
                                    group_members = groupmembers.split(",");
                                    isReady = true;

                                }

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




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReady) {
                    int noofmembers = group_members.length;
                    totalamount = Integer.parseInt(amountedittext.getText().toString());
                    float permember = totalamount / noofmembers;
                    Toast.makeText(getActivity().getApplicationContext(), "Per member: ₹" + String.valueOf(permember) , Toast.LENGTH_LONG).show();
                }


                /*
                Intent intent = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
                intent.putExtra("GROUP_SELECTED", group_selected);
                startActivity(intent);

                 */
            }
        });




        return root;
    }
}
