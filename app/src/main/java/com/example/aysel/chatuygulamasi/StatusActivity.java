package com.example.aysel.chatuygulamasi;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;
    EditText mStatus;
    Button mSaveChanges;

    DatabaseReference mStatusDatabase;
    FirebaseUser mCurrentUser;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        mToolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status");

        mStatus = findViewById(R.id.editStatus);
        mSaveChanges = findViewById(R.id.btnSaveChanges);
        mStatus.setText(status_value);
        mSaveChanges.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mSaveChanges) {
            //Progress
            mProgress = new ProgressDialog(StatusActivity.this);
            mProgress.setTitle("Saving Changes");
            mProgress.setMessage("Please wait while we save the changes");
            mProgress.show();

            String status = mStatus.getText().toString().trim();
            mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "There was some error in saving changes.", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }
}
