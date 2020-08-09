package com.example.groopr.ui.minimap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        final TextView textView = root.findViewById(R.id.text_minimap);
        minimapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
