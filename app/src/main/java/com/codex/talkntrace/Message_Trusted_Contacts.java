package com.codex.talkntrace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Message_Trusted_Contacts extends AppCompatActivity {

    private Button Emergency_btn;
    DatabaseReference mDatabaseUsers;
    DatabaseReference mDatabaseTrustedContacts;
    private ArrayList<Users> userDetail = new ArrayList<>();

    private FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    private String useremail;
    private String User_No;

    private EditText num1;
    private EditText num2;
    private EditText num3;
    private EditText message;
    private String number1;
    private String number2;
    private String number3;
    private String messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message__trusted__contacts);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        num1 = (EditText)findViewById(R.id.num1);
        num2 = (EditText)findViewById(R.id.num2);
        num3 = (EditText)findViewById(R.id.num3);
        message = (EditText)findViewById(R.id.message);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseTrustedContacts = FirebaseDatabase.getInstance().getReference().child("TrustedContacts");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()== null)
                {
                    startActivity(new Intent(Message_Trusted_Contacts.this,Google_Login.class));
                    Message_Trusted_Contacts.this.finish();
                }
                else
                {
                    useremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    fetchInfo();
                }
            }
        };

        Emergency_btn = (Button)findViewById(R.id.trust);
        Emergency_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number1 = num1.getText().toString();
                number2 = num2.getText().toString();
                number3 = num3.getText().toString();
                messages = message.getText().toString();
                Log.d("number111",number1);
                Log.d("number111",number2);
                Log.d("number111",number3);
                Log.d("number111",messages);

                sendMessage(messages,number1,number2,number3);
                Toast.makeText(Message_Trusted_Contacts.this,"hi",Toast.LENGTH_SHORT).show();


            }
        });


    }

    private void sendMessage(String messages, String number1,String number2,String number3) {


        for(int i=1;i<=3;i++)
        {
            if(i==1)
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number1, null, messages, null, null);
            }
            else if(i==2)
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number2, null, messages, null, null);
            }
            else if(i==3)
            {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number3, null, messages, null, null);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void fetchInfo() {

        Query query = mDatabaseUsers.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    User_No = (String) postsnapshot.child("no").getValue();
                    retrieveTrustedContacts();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveTrustedContacts() {

        mDatabaseTrustedContacts.child(User_No).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetail.clear();
                int i=1;
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Users user = snapshot.getValue(Users.class);
                    userDetail.add(user);
                    Log.d("trusted_11",user.getEmail());
                    Log.d("trusted_11",user.getMob());
                    Log.d("trusted_11",user.getMsg());
                    Log.d("trusted_11",user.getName());
                    Log.d("trusted_11",user.getNo());
                    Log.d("trusted_11",user.getPhotourl());

                    if(i==1)
                    {
                        num1.setText(user.getMob());
                        message.setText(user.getMsg());
                        i++;
                    }
                    else if(i==2)
                    {
                        num2.setText(user.getMob());
                        i++;
                    }
                    else if(i==3)
                    {
                        num3.setText(user.getMob());
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}