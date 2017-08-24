package com.example.aysel.chatuygulamasi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEmail, mPassword;
    Button mLogin;
    Toolbar mToolbar;
    ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPassword);
        mLogin = findViewById(R.id.btnLogin);

        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mLogin) {
            String Email = mEmail.getText().toString().trim();
            String Password = mPassword.getText().toString().trim();

            if(!TextUtils.isEmpty(Email) || !TextUtils.isEmpty(Password))
            {
                mProgressDialog.setTitle("Logging In");
                mProgressDialog.setMessage("Please wait while we check your credentials");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                LoginUser(Email, Password);
            }
        }
    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                   // mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    mProgressDialog.hide();
                    Toast.makeText(LoginActivity.this, "Cannot sign in. Please check the form and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
