package com.example.gopal.quikrdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gopal.quikrdemo.R;
import com.example.gopal.quikrdemo.pojo.Chat;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private List<Chat> chatList;
    private Context context;
    public  ChatAdapter(Context context, List<Chat> chatList){
        this.context = context;
        this.chatList = chatList;
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.chatCreatorTextView.setText(chat.getChatCreator());
        holder.chatBodyTextView.setText(chat.getChatBody());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView chatCreatorTextView, chatBodyTextView;
        public ChatViewHolder(View view) {
            super(view);
            chatBodyTextView = view.findViewById(R.id.chat_body_text_view);
            chatCreatorTextView = view.findViewById(R.id.chat_creator_text_view);

        }


    }
}
