package com.codex.talkntrace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddMember extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String username;
    private String useremail;
    DatabaseReference mDatabaseUsers;
    private String User_No;
    private String totalContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent i =new Intent(AddMember.this,Google_Login.class);
                    startActivity(i);

                }
                else
                {
                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    useremail = firebaseAuth.getCurrentUser().getEmail();
                    fetchInfo();

                }

            }
        };

    }


    private void fetchInfo() {

        Query query = mDatabaseUsers.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    User_No = (String) postsnapshot.child("no").getValue();
                    retrieveContacts();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveContacts() {
        DatabaseReference mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts");
        mDatabaseContacts.child(User_No).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalContacts = (String)dataSnapshot.child("total").getValue();
              //  totalContacts++;
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
