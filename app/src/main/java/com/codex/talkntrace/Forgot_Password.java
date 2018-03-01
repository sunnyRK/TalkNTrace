package com.codex.talkntrace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.ForegroundLinearLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class Forgot_Password extends AppCompatActivity {

    DatabaseReference mDatabase;
    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private String useremail;
    private String User_No;
    private DatabaseReference mDatabaseTrustedContacts;
    private ArrayList<Users> userDetail = new ArrayList<>();
    Button sendPin;
    int pin;
    private String pins;
    private int flag=0;
    private int flag2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseTrustedContacts = FirebaseDatabase.getInstance().getReference().child("TrustedContacts");
        mauth = FirebaseAuth.getInstance();
        sendPin = (Button)findViewById(R.id.send_pin);
        mAuthListner= new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(Forgot_Password.this,Google_Login.class));
                    Forgot_Password.this.finish();
                }
                else
                {
                    useremail = firebaseAuth.getCurrentUser().getEmail();
                    fetchInfo();
                }
            }
        };

        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveTrustedContacts();

            }
        });

    }

    private void sendPintoTrust(final String email) {

       final DatabaseReference mDatabbase = FirebaseDatabase.getInstance().getReference().child("users");
        final int[] flag1 = {0};
        final int[] flag = {0};
        final int[] flag2 = {0};
        final Query query = mDatabbase.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(final DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(flag[0] ==0){
                        flag[0] =1;
                    final String no = (String) snapshot.child("no").getValue();

                    final DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("SingleChat");
                    final Query query1 = mDatabase1.child(no).child("chats").orderByChild("email").equalTo(useremail);
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(flag1[0] ==0){
                                flag1[0] =1;
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String nonew = (String) snapshot.child("no").getValue();
                                int totalmsg = Integer.parseInt((String) snapshot.child("totalmsg").getValue());
                                totalmsg+=1;
                                Log.d("emergency",totalmsg+"");

                                mDatabase1.child(no).child("chats").child(nonew).child("msgs").child("msg"+totalmsg).child("msg").setValue(pins+"");
                                mDatabase1.child(no).child("chats").child(nonew).child("msgs").child("msg"+totalmsg).child("sender").setValue(useremail);
                                mDatabase1.child(no).child("chats").child(nonew).child("msgs").child("msg"+totalmsg).child("time").setValue(ServerValue.TIMESTAMP);
                                mDatabase1.child(no).child("chats").child(nonew).child("msgs").child("msg"+totalmsg).child("type").setValue("0");
                                mDatabase1.child(no).child("chats").child(nonew).child("totalmsg").setValue(totalmsg+"");
                                Query query2 = mDatabase1.child(User_No).child("chats").orderByChild("email").equalTo(email);
                                final int finalTotalmsg = totalmsg;
                                query2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(flag2[0] ==0) {
                                            flag2[0] = 1;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String no2 = (String) snapshot.child("no").getValue();
                                                mDatabase1.child(User_No).child("chats").child(no2).child("totalmsg").setValue(finalTotalmsg + "");
                                            }
                                        }
                                        startActivity(new Intent(Forgot_Password.this,enter_pin.class));
                                        Forgot_Password.this.finish();
                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        /*pinRef.child(User_No).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void fetchInfo() {

        Query query = mDatabase.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    if (flag == 0)
                    {
                        User_No = (String) postsnapshot.child("no").getValue();
                    // retrieveTrustedContacts();
                    DatabaseReference mDatajump = FirebaseDatabase.getInstance().getReference().child("pin").child(User_No);
                    mDatajump.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (flag2 == 0) {
                                String str = (String) dataSnapshot.child("pin").getValue();

                                if (str == null || str.equals("0")) {
                                } else {
                                    startActivity(new Intent(Forgot_Password.this, enter_pin.class));
                                    Forgot_Password.this.finish();

                                }
                                flag2++;
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                        flag++;
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void retrieveTrustedContacts() {

        mDatabaseTrustedContacts.child(User_No+"").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetail.clear();
                int i=1;
                Random r = new Random();
                pin = r.nextInt(1000000);

                pins = String.valueOf(pin);
                Log.d("pinsss",pins);
                pins = encryption.encrypt(pins);
                Log.d("pinsss",pins);
                Log.d("pintouser", String.valueOf(pin));
                DatabaseReference pinRef = FirebaseDatabase.getInstance().getReference().child("pin");
                pinRef.child(User_No).child("pin").setValue(pin+"");
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Users user = snapshot.getValue(Users.class);
                    userDetail.add(user);
                    Log.d("trusted_11",user.getEmail());
                    Log.d("trusted_11",user.getMob());
                    Log.d("trusted_11",user.getName());
                    Log.d("trusted_11",user.getNo());
                    Log.d("trusted_11",user.getPhotourl());
                    sendPintoTrust(user.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mAuthListner);
    }
}
