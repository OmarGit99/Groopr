package com.example.groopr.ui.minimap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.groopr.R;

public class MinimapFragment extends Fragment {

    private MinimapViewModel minimapViewModel;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        minimapViewModel =
                ViewModelProviders.of(this).get(MinimapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_minimap, container, false);
        Button button = root.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
