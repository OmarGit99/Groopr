package com.example.groopr.ui.minimap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.groopr.R;
import com.example.groopr.ui.groups.GroupsFragment;

import java.util.ArrayList;

public class MinimapFragment extends Fragment {

    private MinimapViewModel minimapViewModel;
    Spinner groupspinner;
    ArrayList<String> user_groups;
    String group_selected;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        minimapViewModel =
                ViewModelProviders.of(this).get(MinimapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_minimap, container, false);
        Button button = root.findViewById(R.id.button3);
        group_selected = "";
        groupspinner = root.findViewById(R.id.groupcatsspin3);
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
                intent.putExtra("GROUP_SELECTED", group_selected);
                startActivity(intent);
            }
        });

        return root;
    }
}
