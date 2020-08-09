package com.example.groopr.ui.public_feed;

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

public class PublicFeedFragment extends Fragment {

    private PublicFeedViewModel publicFeedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publicFeedViewModel =
                ViewModelProviders.of(this).get(PublicFeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_public_feed, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        publicFeedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
