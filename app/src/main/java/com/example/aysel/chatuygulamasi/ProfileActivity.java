package com.example.aysel.chatuygulamasi;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    ImageView mProfileImage;
    Button mProfileSendReqBtn;
    DatabaseReference mUsersDatabase, mFriendReqDatabase, mFriendDatabase;
    ProgressDialog mProgressDialog;
    FirebaseUser mCurrentUser;
    String mCurrent_state, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mCurrent_state = "not_friends";
        mProfileSendReqBtn.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultuser).into(mProfileImage);


                //-------------Friends List / Request Feature
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "req_recevied";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                            } else if (req_type.equals("sent")) {
                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                            }
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mProfileSendReqBtn) {

            mProfileSendReqBtn.setEnabled(false);


            //------------Not Friends State
            if (mCurrent_state.equals("not_friends")) {
                mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    mCurrent_state = "req_sent";
                                    mProfileSendReqBtn.setText("Cancel Friend Request");

                                    // Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                        }
                        mProfileSendReqBtn.setEnabled(true);
                    }
                });
            }

            // ------------cancel Request State
            if (mCurrent_state.equals("req_sent")) {
                mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");
                            }
                        });
                    }
                });
            }


            //----------------- REQ RECEIVED STATE----------------
            if (mCurrent_state.equals("req_recevied")) {
                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                mProfileSendReqBtn.setEnabled(true);
                                                mCurrent_state = "friends";
                                                mProfileSendReqBtn.setText("Unfriend this Person");
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
            }
        }
    }
}
