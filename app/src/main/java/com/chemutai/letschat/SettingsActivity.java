package com.chemutai.letschat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ImageView profilePic;
    private EditText profileName, profilePhone;
    private Button mConfirm, mBack;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    private String userId, profileImageUrl, name, phone, userGender;

    private Uri resultUri;
    private Provider downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String userGender = getIntent().getExtras().getString("userGender");

        profileName = findViewById(R.id.etProfileName);
        profilePhone = findViewById(R.id.etProfilePhone);
        profilePic = findViewById(R.id.imgProfile);

        mConfirm = findViewById(R.id.btnConfirm);
        mBack = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mReference = FirebaseDatabase.getInstance().getReference().child("Users").child("userGender").child(userId);

        getUserInfo();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);//a number that will be attached to the image
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo() {
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null)
                    {
                        name = map.get("name").toString();
                        profileName.setText(name);
                    }

                    if (map.get("phone") != null)
                    {
                        phone = map.get("phone").toString();
                        profilePhone.setText(phone);
                    }

                    if (map.get("gender") != null)
                    {
                        userGender = map.get("gender").toString();
                        /*profilePhone.setText(userGender);*/
                    }

                    Glide.clear(profilePic);
                    if (map.get("profileImageUrl") != null)
                    {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        profilePhone.setText(profileImageUrl);
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher_circle).into(profilePic);
                                break;

                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(profilePic);
                                break;
                        }
                        /*Glide.with(getApplication()).load(profileImageUrl).into(profilePic);*/
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        name = profileName.getText().toString();
        phone = profilePhone.getText().toString();

        //saving data to the db
        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);

        mReference.updateChildren(userInfo);

        if (resultUri != null)
        {
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);

            /*uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = filepath.getDownloadUrl();

                    Map userInfo = new HashMap();
                    userInfo.put("profileImageUrl", downloadUrl.toString());
                    mReference.updateChildren(userInfo);

                    finish();
                    return;
                }
            });*/

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if (!task.isSuccessful()){
                       throw task.getException();
                   }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        Map userInfo = new HashMap();
                        userInfo.put("profileImageUrl", downloadUrl.toString());
                        mReference.updateChildren(userInfo);
                    } else{
                        Toast.makeText(SettingsActivity.this, "Could not upload the file", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });

        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && requestCode == Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profilePic.setImageURI(resultUri);//uploading profile image to database
        }
    }
}
