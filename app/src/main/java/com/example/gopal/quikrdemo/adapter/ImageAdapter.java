package com.example.gopal.quikrdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gopal.quikrdemo.R;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imageList;
    private Context context;

    public ImageAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        String imageUri = imageList.get(position);
        Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.place_holder_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (imageList != null)
            return imageList.size();
        else return 0;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_item);
        }
    }

}
