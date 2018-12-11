package com.chemutai.letschat.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chemutai.letschat.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMessage;
    public LinearLayout mContainer;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        txtMessage = itemView.findViewById(R.id.txtMessage);
        mContainer = itemView.findViewById(R.id.container);

    }

    @Override
    public void onClick(View view) {
    }
}
