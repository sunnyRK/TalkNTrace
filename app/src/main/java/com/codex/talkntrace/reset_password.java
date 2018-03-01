package com.codex.talkntrace;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class reset_password extends AppCompatActivity {


    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private String useremail;
    private String User_No;
    private DatabaseReference mDatabase;
    EditText pass1,pass2;
    Button submit_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        pass1 = (EditText) findViewById(R.id.password1);
        pass2 = (EditText) findViewById(R.id.password2);
        submit_pin = (Button) findViewById(R.id.submit_pin);
        submit_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass1.getText().length()==6)
                {
                    if(pass1.getText().toString().equals(pass2.getText().toString()) )
                    {
                        mDatabase.child(User_No).child("password").setValue(pass1.getText()+"");
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("pin");
                        mDatabase.child(User_No).child("pin").setValue("0");
                        startActivity(new Intent(reset_password.this,MainActivity.class));
                    }
                    else
                    {
                        pass1.setError("Pin Didn't match");
                        pass1.setText("");
                        pass2.setText("");

                    }
                }
                else
                {
                    pass1.setError("Pin must be of six digit");
                    pass1.setText("");
                    pass2.setText("");
                }

            }
        });

        mauth = FirebaseAuth.getInstance();
        mAuthListner= new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(reset_password.this,Google_Login.class));
                }
                else
                {
                    useremail = firebaseAuth.getCurrentUser().getEmail();
                    fetchInfo();
                }
            }
        };
    }
    private void fetchInfo() {

        Query query = mDatabase.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    User_No = (String) postsnapshot.child("no").getValue();
                    // retrieveTrustedContacts();

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
