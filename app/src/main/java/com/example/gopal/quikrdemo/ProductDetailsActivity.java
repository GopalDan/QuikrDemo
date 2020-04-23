package com.example.gopal.quikrdemo;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gopal.quikrdemo.adapter.ImageAdapter;
import com.example.gopal.quikrdemo.pojo.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private String productId, sellerName;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        final RecyclerView productDetailsImageRecycler = findViewById(R.id.product_details_image_recycler_view);
        final TextView priceTextView = findViewById(R.id.product_price);
        final TextView name = findViewById(R.id.product_name);
        final TextView description = findViewById(R.id.product_description);
        final TextView category = findViewById(R.id.category);
        TextView  commentTextView = findViewById(R.id.comments_text_view);
        commentTextView.setPaintFlags(commentTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        getSupportActionBar().setTitle("Product Details");

        Bundle bundle = getIntent().getExtras();
        if ((bundle!=null)){
            productId = bundle.getString(getResources().getString(R.string.key_product_id));
        }

        commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.key_product_id), productId);
                bundle.putString(getResources().getString(R.string.key_seller_name), sellerName);
                Intent intent = new Intent(ProductDetailsActivity.this, ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        mFirebaseFirestore.collection("products")
                .whereEqualTo("productId", productId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                        if(documentSnapshots.size()!=0) {
                            Product product = documentSnapshots.get(0).toObject(Product.class);
                            List<String> imageList = product.getProductImageList();
                            productDetailsImageRecycler.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            productDetailsImageRecycler.setAdapter(new ImageAdapter(ProductDetailsActivity.this, imageList));
                            String price = getResources().getString(R.string.rupees) + product.getProductPrice();
                            priceTextView.setText(price);
                            name.setText(product.getProductName());
                            description.setText(product.getProductDescription());
                            category.setText(product.getProductCategory());
                            sellerName = product.getSellerName();
                        }


                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
}
