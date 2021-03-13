package com.example.groopr;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photos;
    private Context context;

    public PhotoAdapter(Context context, List<Photo> photos) {
        this.photos = photos;
        this.context = context;
    }

    public void addPhoto(Photo photo) {
        // Add the event at the beggining of the list
        photos.add(0, photo);
        // Notify the insertion so the view can be refreshed
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView photoImageView;

        public PhotoViewHolder(View v) {
            super(v);
            photoImageView = (ImageView) v.findViewById(R.id.photo);
        }
    }
    @Override


    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_item, viewGroup, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Bitmap bitmap = photo.getBitmap();

        holder.photoImageView.setImageBitmap(bitmap);

    }

}
