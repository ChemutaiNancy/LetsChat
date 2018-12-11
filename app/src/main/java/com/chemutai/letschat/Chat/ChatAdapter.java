package com.chemutai.letschat.Chat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.chemutai.letschat.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {

    private List<ChatObject> chatList;
    private Context context;

    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chats, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rvc = new ChatViewHolders((layoutView));
        return rvc;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
            holder.txtMessage.setText(chatList.get(position).getMessage());
            if (chatList.get(position).getCurrentUser()){
                holder.txtMessage.setGravity(Gravity.END);
                holder.txtMessage.setTextColor(Color.parseColor("#404040"));
                holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
            }
            else
            {
                holder.txtMessage.setGravity(Gravity.START);
                holder.txtMessage.setTextColor(Color.parseColor("#FFFFFF"));
                holder.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
            }
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
