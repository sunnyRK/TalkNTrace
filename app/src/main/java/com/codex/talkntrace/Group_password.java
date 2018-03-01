package com.codex.talkntrace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Group_password extends AppCompatActivity {

    EditText password;
    Button submit;
    
    DatabaseReference mDatabaseUsers;
    DatabaseReference mDatabaseLocationUser;


    private FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    String userEmail;
    private String no;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_password);
        
        password = (EditText)findViewById(R.id.password);
        submit = (Button)findViewById(R.id.SubmitBtn);
        
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(Group_password.this,Google_Login.class));
                }
                else
                {
                    userEmail = firebaseAuth.getCurrentUser().getEmail();
                }
            }
        }; 
        
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseLocationUser = FirebaseDatabase.getInstance().getReference().child("LocationUser");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd = password.getText().toString();
                if(pwd.length()<6 )
                {
                    Snackbar.make(v, "Please Enter 6 Length Of PIN", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

                else {
                    Query query = mDatabaseUsers.orderByChild("email").equalTo(userEmail);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                no = (String) snapshot.child("no").getValue();
                                addPassword();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        
        
    }

    private void addPassword() {
        
        mDatabaseUsers.child(no).child("password").setValue(pwd);
        mDatabaseLocationUser.child(no).child("password").setValue(pwd);
        Intent intent = new Intent(Group_password.this,SyncContacts.class);
        startActivity(intent);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        
    }
}
