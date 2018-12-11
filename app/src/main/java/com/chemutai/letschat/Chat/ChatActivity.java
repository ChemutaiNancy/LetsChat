package com.chemutai.letschat.Chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chemutai.letschat.Matches.MatchesActivity;
import com.chemutai.letschat.Matches.MatchesAdapter;
import com.chemutai.letschat.Matches.MatchesObject;
import com.chemutai.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapetr;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserId, matchId, chatId;

    private EditText mMessage;
    private Button mBtnSend;

    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches").child(matchId).child("chatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();

        mRecyclerView = findViewById(R.id.recyclerViewMatches);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapetr = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapetr);

        mMessage = findViewById(R.id.etMessage);
        mBtnSend = findViewById(R.id.btnSend);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String sendMessage = mMessage.getText().toString();
        if (!sendMessage.isEmpty()){
            //creating a push
            DatabaseReference newMessageDb = mDatabaseChat.push();//creates new child

            Map newMessage = new HashMap();
            newMessage.put("craetedByUser",  currentUserId);
            newMessage.put("text", sendMessage);

            newMessageDb.setValue(newMessage);
        }

        mMessage.setText(null);
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;

                    if (dataSnapshot.child("text").getValue()!= null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }

                    if (dataSnapshot.child("createdByUser").getValue()!= null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if (message != null && createdByUser!= null){
                        Boolean currentUserBoolean = false;

                        if (createdByUser.equals(currentUserId)){
                            currentUserBoolean = true;
                        }

                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapetr.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }
}
