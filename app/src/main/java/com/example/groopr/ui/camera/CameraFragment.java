package com.example.groopr.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.groopr.MainActivity;
import com.example.groopr.R;
import com.example.groopr.ui.groups.GroupsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.security.acl.Group;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {
    View root;
    ImageView takepic;
    ImageView uploadpic;


    private CameraViewModel notificationsViewModel;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(CameraViewModel.class);
        root = inflater.inflate(R.layout.fragment_camera, container, false);

        //Textviews on the fragment
        final TextView textView2 = (TextView) root.findViewById(R.id.textView2);
        final TextView textView3 = (TextView) root.findViewById(R.id.textView3);
        final TextView textView4 = (TextView) root.findViewById(R.id.textView4);


        //the images to be clicked
        takepic = (ImageView) root.findViewById(R.id.takepic);
        uploadpic = (ImageView) root.findViewById(R.id.uploadpic);


        //upload pic click listener
        uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                //start image upload activity
                Intent intent = new Intent(getActivity().getApplicationContext(), UploadImage.class);
                //send an extra for stating its for upload purpose
                intent.putExtra("purpose", "UPLOAD");
                startActivity(intent);

            }
        });

        //camera on click listener
        takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), UploadImage.class);
                //send extra for purpose
                intent.putExtra("purpose", "CAMERA");
                startActivity(intent);
            }
        });


        return root;
    }
}
