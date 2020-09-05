package com.example.gopal.quikrdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gopal.quikrdemo.adapter.ImageAdapter;
import com.example.gopal.quikrdemo.pojo.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final int UPI_PAYMENT = 0;
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
        getSupportActionBar().setTitle("Book Details");

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

        Button contactButton = findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "8946877224";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
        final Button paytButton = findViewById(R.id.pay_button);
        final EditText upiEditText = findViewById(R.id.upi_id_edit_text);
        final EditText amountEditText = findViewById(R.id.amount_edit_text);
        paytButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = paytButton.getText().toString();
                if(label.equals("Pay Seller")){
                    upiEditText.setVisibility(View.VISIBLE);
                    amountEditText.setVisibility(View.VISIBLE);
                    paytButton.setText("Pay Now");
                }else {
                    String upiId = upiEditText.getText().toString();
                    String amount = amountEditText.getText().toString();

                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_name), MODE_PRIVATE);
                    String userName = sharedPref.getString(getString(R.string.key_user_name), null);
                    String phoneNumber = sharedPref.getString(getString(R.string.key_user_phone_number), null);

                    if (TextUtils.isEmpty(upiId)){
                        Toast.makeText(ProductDetailsActivity.this, "Enter Your Upi Id", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(amount)){
                        Toast.makeText(ProductDetailsActivity.this, "Enter the amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String note = "Paying to " + sellerName;
                    payUsingUpi(userName, upiId, note, amount);

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

    void payUsingUpi(String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(ProductDetailsActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(ProductDetailsActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(ProductDetailsActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(ProductDetailsActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(ProductDetailsActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(ProductDetailsActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

}
