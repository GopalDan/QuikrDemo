package com.example.gopal.quikrdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.gopal.quikrdemo.adapter.ProductAdapter;
import com.example.gopal.quikrdemo.pojo.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ArrayList<Product> productArrayList;
    private FirebaseFirestore mFirebaseFirestore;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        final ProductAdapter adapter = new ProductAdapter(this, productArrayList);
        recyclerView.setAdapter(adapter);
        showProgressDialog("Loading Data...");

        mFirebaseFirestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if (productArrayList.size() != 0)
                            productArrayList.clear();
                        List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            Product product = documentSnapshot.toObject(Product.class);
                            productArrayList.add(product);
                            adapter.notifyDataSetChanged();
                        }
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }
                });

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductActivity.class));
            }
        });

    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }


}
