package com.example.android_firebase_signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.print.PrinterId;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mFullName;
    private ImageView mProfilePicture;
    private Button mSubmit;
    private ProgressBar mProgressBar;
    FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    Uri URIProfilePicture;
    String mProfileImageURL;

    private static final int CHOOSE_IMAGE = 101;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        mProfilePicture = findViewById(R.id.image_profile);
        mFullName = findViewById(R.id.full_name);
        mSubmit = findViewById(R.id.submit_profile);
        mProgressBar = findViewById(R.id.progressbar);

        loadUserInformation();

        mProfilePicture.setOnClickListener(this);
        mFullName.setOnClickListener(this);
        mSubmit.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }



    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.image_profile:
                showImageChooser();

                break;
            case R.id.full_name:

                break;
            case R.id.submit_profile:
                saveUserInformation();
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            URIProfilePicture = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), URIProfilePicture);
                mProfilePicture.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void loadUserInformation()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null)
        {
            if(firebaseUser.getPhotoUrl() != null)
            {
                Log.d(TAG, "loadUserInformation: " + firebaseUser.getPhotoUrl().toString());
                Glide.with(this)
                        .load(firebaseUser.getPhotoUrl().toString())
                        .into(mProfilePicture);

            }
            if(firebaseUser.getDisplayName() != null)
            {
                mFullName.setText(firebaseUser.getDisplayName());

            }
        }


    }

    private void saveUserInformation()
    {
        String fullName = mFullName.getText().toString().trim();

        if(fullName.isEmpty())
        {
            mFullName.setError("Full Name is Naccessary");
            mFullName.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null && mProfileImageURL != null)
        {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(Uri.parse(mProfileImageURL))
                    .build();

            user.updateProfile(profileChangeRequest)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(ProfileActivity.this, "Error !!", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            ;
        }
    }

    private void showImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Pic"), CHOOSE_IMAGE);

    }

    private void uploadImageToFirebaseStorage()
    {
        if(URIProfilePicture != null)
        {
            mProgressBar.setVisibility(View.VISIBLE);
            mStorageRef.putFile(URIProfilePicture)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mProgressBar.setVisibility(View.GONE);
                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Log.d(TAG, "onSuccess: " + mProfileImageURL);
                                    mProfileImageURL = uri.toString();


                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}
