package com.example.android_firebase_signup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    private EditText mEmail, mPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.signin_email);
        mPassword = findViewById(R.id.signin_password);
        progressBar = findViewById(R.id.progressbar);

        findViewById(R.id.jump_to_sign_up).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.jump_to_sign_up:
                finish();
                startActivity( new Intent(getApplicationContext(), SignUpActivity.class));

                break;
            case R.id.login_button:
                userLogin();
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    private void userLogin()
    {
        String email = mEmail.getText().toString().trim();
        String password= mPassword.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful())
                {
                    finish();
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        });

    }


}
