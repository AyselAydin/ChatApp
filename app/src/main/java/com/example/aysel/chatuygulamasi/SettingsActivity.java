package com.example.aysel.chatuygulamasi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    CircleImageView mDisplayImage;
    TextView mName;
    TextView mStatus;
    Button mSaveStatus, mSaveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = findViewById(R.id.circleImageView);
        mName = findViewById(R.id.settings_display_name);
        mStatus = findViewById(R.id.settings_status);
        mSaveImage = findViewById(R.id.settings_image_btn);
        mSaveStatus = findViewById(R.id.settings_status_btn);

        mSaveStatus.setOnClickListener(this);
        mSaveImage.setOnClickListener(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mSaveStatus) {
            String status_value = mStatus.getText().toString();
            Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
            statusIntent.putExtra("status", status_value);
            startActivity(statusIntent);
        }
    }
}
