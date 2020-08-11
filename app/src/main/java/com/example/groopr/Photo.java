package com.example.groopr;

import android.graphics.Bitmap;

public class Photo {
    private Bitmap bitmap;

    public Photo(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
