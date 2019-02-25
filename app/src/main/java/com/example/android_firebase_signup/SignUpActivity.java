package com.example.android_firebase_signup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText  mEmail, mPassword;
    ProgressBar mProggressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_tuto);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.signup_email);
        mPassword = findViewById(R.id.signup_password);
        mProggressBar = findViewById(R.id.progressbar);

        findViewById(R.id.signup_button).setOnClickListener(this);

    }

    public void registerUser()
    {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Invalid Email Address");
            mEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            mPassword.setError("minimum password should be 6");
            mPassword.requestFocus();
            return;
        }

        mProggressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                mProggressBar.setVisibility(View.GONE);

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(), "Email is already registered", Toast.LENGTH_SHORT).show();
                        mEmail.requestFocus();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signup_button:
                registerUser();
                break;
            case R.id.signup_email:

                break;
            case R.id.signup_password:

                break;
        }

    }
}
