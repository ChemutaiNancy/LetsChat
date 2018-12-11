package com.chemutai.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.chemutai.letschat.Cards.ArrayAdapter;
import com.chemutai.letschat.Cards.Cards;
import com.chemutai.letschat.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Cards mCards_data[];
    private ArrayAdapter ArrayAdapter;
    private int i;

    private FirebaseAuth mFirebaseAuth;

    private String currentUId;

    ListView mListView;
    List<Cards> rowItems;

    private DatabaseReference usersDb;

  /*  String TAG = "OPPOSITE_GENDER";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUId = mFirebaseAuth.getCurrentUser().getUid();

        checkUserGender();

        rowItems = new ArrayList<Cards>();

        ArrayAdapter = new ArrayAdapter(this, R.layout.item, rowItems);

        final SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);


        flingContainer.setAdapter(ArrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                ArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                Cards cards_obj = (Cards) dataObject;
                String userId = cards_obj.getUserId();
                usersDb.child(oppositeUserGender).child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                /*usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);*/
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
                /*makeToast(MainActivity.this, "Left!");child(oppositeUserGender)*/
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Cards cards_obj = (Cards) dataObject;
                String userId = cards_obj.getUserId();
                usersDb.child(oppositeUserGender).child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                /*usersDb.child(userId).child("connections").child("yep").child(currentUId).setValue(true);*/
                isConnectionMatch(userId);
                
                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
                /*makeToast(MainActivity.this, "Right!");.child(oppositeUserGender)*/
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                /*al.add("XML ".concat(String.valueOf(i)));
                ArrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;*/
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                /*view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);*/
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                /*makeToast(MainActivity.this, "Clicked!");*/
            }
        });

    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnections = usersDb.child(userGender).child(currentUId).child("connections").child("yep").child(userId);
        currentUserConnections.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();
                    usersDb.child(oppositeUserGender).child(dataSnapshot.getKey()).child("connection").child("matches").child(currentUId).setValue(true);
                    usersDb.child(userGender).child(currentUId).child("connection").child("matches").child(dataSnapshot.getKey()).setValue(true);


                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    //saving mates to database
                    /*usersDb.child(dataSnapshot.getKey()).child("connection").child("matches").child(currentUId).child("chatId").setValue(key);
                    usersDb.child(currentUId).child("connection").child("matches").child(dataSnapshot.getKey()).child("chatId").setValue(key);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String userGender;
    private String oppositeUserGender;

    public void checkUserGender(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userDb = usersDb.child("Users").child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    {
                        if (dataSnapshot.child("gender").getValue() != null)
                        {
                            userGender = dataSnapshot.child("gender").getValue().toString();
                            switch (userGender)
                            {
                                case "Male":
                                    oppositeUserGender = "Female";
                                    break;

                                case "Female":
                                    oppositeUserGender = "Male";
                                    break;
                            }
                            getOppositeGenderUsers();
                        }
                    }
                    getOppositeGenderUsers();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /*DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals(user.getUid()))
                {
                    userGender = "Female";
                    oppositeUserGender = "Male";
                    getOppositeGenderUsers();
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
        });*/
    }

    public void getOppositeGenderUsers(){
        /*DatabaseReference oppositeDb = FirebaseDatabase.getInstance().getReference().child("Users");*/
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: "+dataSnapshot.toString());
                if (dataSnapshot.child("gender").getValue() != null){
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yep").hasChild(currentUId) && !dataSnapshot.child("gender").getValue().toString().equals(oppositeUserGender));
                    {
                        String profileImageUrl = "default";
                        /*Log.d(TAG, "addChild: PROFILEIMAGEURL"+profileImageUrl);*/

                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default"))
                        {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                            /*Log.d(TAG, "onChildAdded: "+dataSnapshot.getException().getMessage());*/
                        }


//                        Log.d(TAG, "onChildAdded: "+dataSnapshot.child("name").getValue().toString());
                        HashMap<String,String> mapp=new HashMap<>();

                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                            mapp.put(snapshot.getKey(), snapshot.getValue().toString());
                        }

                        Log.d(TAG, "onChildAdded: "+mapp.get("name"));
                        Log.d(TAG, "onChildAdded: "+mapp.get("gender"));
                        Log.d(TAG, "onChildAdded: "+mapp.get("profileImageUrl"));

                        //Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl);
                        //rowItems.add(item);
                        //ArrayAdapter.notifyDataSetChanged();
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

    String TAG="DATA_FIRE";

    public void logoutUser(View view) {
        mFirebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class));
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userGender", userGender);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
}
