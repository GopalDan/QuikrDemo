package com.example.gopal.quikrdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.gopal.quikrdemo.ProductDetailsActivity;
import com.example.gopal.quikrdemo.R;
import com.example.gopal.quikrdemo.RecyclerTouchListner;
import com.example.gopal.quikrdemo.pojo.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<Product> productArrayList;
    private Context context;

    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {

        final Product product = productArrayList.get(position);
        String name = product.getProductName();
        String price = context.getResources().getString(R.string.rupees) + product.getProductPrice();
        List<String> imageList = product.getProductImageList();

       /* LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.mainPageImageRecycler.setLayoutManager(lm);
        holder.mainPageImageRecycler.setAdapter(new ImageAdapter(context, imageList));*/

      /*  Glide.with(context)
                .load(imageList.get(0))
                .placeholder(R.drawable.place_holder_image)
                .into(holder.productImageView);
*/
        Glide.with(context)
                .load(imageList.get(0))
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.place_holder_image)
                .into(new BitmapImageViewTarget(holder.productImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius( 24.0f);
//                circularBitmapDrawable.setCircular(true);
                holder.productImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        holder.productNameTextView.setText(name);
        holder.productPriceTextView.setText(price);
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(context.getResources().getString(R.string.key_product_id), product.getProductId());
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });
        holder.mainPageImageRecycler.addOnItemTouchListener(new RecyclerTouchListner(context, holder.mainPageImageRecycler, new RecyclerTouchListner.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//               Toast.makeText(context, "Direct Click: " + position, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString(context.getResources().getString(R.string.key_product_id), product.getProductId());
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
//               Toast.makeText(context, "Direct Long Click: " + position, Toast.LENGTH_SHORT).show();

            }
        }));

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        RecyclerView mainPageImageRecycler;
        TextView productNameTextView, productPriceTextView;
        ImageView productImageView;
        LinearLayout rootLayout;

        public ProductViewHolder(View view) {
            super(view);
            mainPageImageRecycler = view.findViewById(R.id.main_page_image_recycler_view);
            productNameTextView = view.findViewById(R.id.product_name);
            productPriceTextView = view.findViewById(R.id.product_price);
            productImageView = view.findViewById(R.id.product_image);
            rootLayout = view.findViewById(R.id.root_layout);
        }
    }

}
