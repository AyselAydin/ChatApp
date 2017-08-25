package com.example.aysel.chatuygulamasi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mRegisterBtn, mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegisterBtn = findViewById(R.id.btnNew);
        mLogin = findViewById(R.id.btnGiris);
        mRegisterBtn.setOnClickListener(this);
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mRegisterBtn) {
            Intent register = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(register);
        }
        else if(view == mLogin){
            Intent register = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(register);
        }
    }
}
