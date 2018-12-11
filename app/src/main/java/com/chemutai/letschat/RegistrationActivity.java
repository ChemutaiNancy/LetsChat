package com.chemutai.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private RadioGroup mRadioGroup;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    ProgressBar mProgressBar;
    String TAG="USER_REGISTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                   // startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                   // finish();
                    return;
                }
            }
        };
        mProgressBar=findViewById(R.id.progressBar);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);

        mRegister = findViewById(R.id.btnRegister);

        mRadioGroup = findViewById(R.id.rgGender);
        mName = findViewById(R.id.etName);


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectGender = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = findViewById(selectGender);

                if (radioButton.getText() == null)
                {
                    return;
                }

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                /*Log.d(TAG, "onClick: NAMES "+name);*/

                mProgressBar.setVisibility(View.VISIBLE);

                mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       mProgressBar.setVisibility(View.INVISIBLE);
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this, "Registration not successful", Toast.LENGTH_SHORT).show();
                            /*task.getException().printStackTrace();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());*/
                        }
                        else
                        {
                            Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            String userId = mFirebaseAuth.getCurrentUser().getUid();
                            /*Log.d(TAG, "onComplete: "+userId);
                            Log.d(TAG, "onComplete: "+name);*///checking for errors
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            //radioButton.getText().toString()

                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("profileImageUrl", "default");//setting defaut image
                            userInfo.put("gender", radioButton.getText().toString());

                            currentUserDb.updateChildren(userInfo);

                           /* currentUserDb.setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, "onComplete: registered");
                                    }else{
                                        task.getException().printStackTrace();
                                        Log.e(TAG, "onComplete: "+task.getException().getMessage() );
                                    }
                                }
                            });*/
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
