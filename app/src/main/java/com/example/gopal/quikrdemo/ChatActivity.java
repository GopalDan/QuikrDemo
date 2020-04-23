package com.example.gopal.quikrdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gopal.quikrdemo.adapter.ChatAdapter;
import com.example.gopal.quikrdemo.pojo.Chat;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";
    private String chatId;
    private Button mSendButton;
    private EditText mMessageEditText;
    private ArrayList<Chat> chatArrayList;
    private FirebaseFirestore mFirebaseFirestore;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        chatArrayList = new ArrayList<>();
        mMessageEditText = findViewById(R.id.message_edit_text);
        mSendButton = findViewById(R.id.send_button);
        getSupportActionBar().setTitle("Chat With Seller");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_name), MODE_PRIVATE);
        userName = sharedPref.getString(getString(R.string.key_user_name), null);
        String phoneNumber = sharedPref.getString(getString(R.string.key_user_phone_number), null);
        String sellerName = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatId = bundle.getString(getResources().getString(R.string.key_product_id));
            sellerName = bundle.getString(getResources().getString(R.string.key_seller_name));
        }

        if (userName.equals(sellerName)) {
            userName = userName + "(seller)";
        }

        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ChatAdapter chatAdapter = new ChatAdapter(this, chatArrayList);
        recyclerView.setAdapter(chatAdapter);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = mMessageEditText.getText().toString();
                if (TextUtils.isEmpty(body)) {
                    Toast.makeText(ChatActivity.this, "Type message!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String chatCreator = userName;
                Chat chat = new Chat(chatId, chatCreator, body);
                mFirebaseFirestore.collection("chats").add(chat);
                mMessageEditText.setText("");
            }
        });

        mFirebaseFirestore.collection("chats")
                .whereEqualTo("chatID", chatId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = querySnapshot.getDocumentChanges();
                        for (DocumentChange dc : documentChanges) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Chat chat = dc.getDocument().toObject(Chat.class);
                                    chatArrayList.add(chat);
                                    chatAdapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    Log.e(TAG, "onEvent: " + "Removed");
                                    break;
                                case MODIFIED:
                                    Log.e(TAG, "onEvent: " + "MODIFIED");
                                    break;
                            }

                        }

                       /* List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                        for(DocumentSnapshot documentSnapshot : documentSnapshots){
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            chatArrayList.add(chat);
                            chatAdapter.notifyDataSetChanged();

                        }*/
                    }
                });

       /* mFirebaseFirestore.collection("products")
                .whereEqualTo("productId", chatId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                        Product product = documentSnapshots.get(0).toObject(Product.class);
                        String sellerName = product.getSellerName();
                        if (sellerName.equals(userName)){
                            userName = userName + "(seller)";
                        }

                    }
                });*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

}

