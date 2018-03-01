package com.codex.talkntrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Emergency_Contacts extends AppCompatActivity {

    private ArrayList<Users> Trustedcontacts = new ArrayList<>();

    CardView mCardView;
    CardView mCard1;
    CardView mCard2;
    CardView mCard3;

    ImageView imag1;
    ImageView imag2;
    ImageView imag3;

    TextView textName1;
    TextView textName2;
    TextView textName3;

    TextView textNum1;
    TextView textNum2;
    TextView textNum3;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String username;
    String useremail;

    DatabaseReference mDatabaseUsers;
    DatabaseReference mDatabaseTrustedContacts;
    private String no;
    private int flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseTrustedContacts = FirebaseDatabase.getInstance().getReference().child("TrustedContacts");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(Emergency_Contacts.this,Google_Login.class));
                }
                else
                {
                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    useremail = firebaseAuth.getCurrentUser().getEmail();

                    fetchInfo();

                }

            }
        };
        mCardView = (CardView)findViewById(R.id.edit_em_msg);
        mCard1 = (CardView)findViewById(R.id.card1);
        mCard2 = (CardView)findViewById(R.id.card2);
        mCard3 = (CardView)findViewById(R.id.card3);

        imag1 = (ImageView)findViewById(R.id.imageView1);
        imag2 = (ImageView)findViewById(R.id.imageView2);
        imag3 = (ImageView)findViewById(R.id.imageView3);

        textName1 = (TextView)findViewById(R.id.em_con_1_name);
        textName2 = (TextView)findViewById(R.id.em_con_2_name);
        textName3 = (TextView)findViewById(R.id.em_con_3_name);

        textNum1 = (TextView)findViewById(R.id.em_con_1_no);
        textNum2 = (TextView)findViewById(R.id.em_con_2_no);
        textNum3 = (TextView)findViewById(R.id.em_con_3_no);

        mCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String card_1 = "1";
                String no ="2";
                Intent i = new Intent(Emergency_Contacts.this,ContactList.class);
                i.putExtra("num1",card_1);
                i.putExtra("no",no);
                startActivity(i);

            }
        });
        mCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String card_2 = "2";
                String no ="2";
                Intent i = new Intent(Emergency_Contacts.this,ContactList.class);
                i.putExtra("num1",card_2);
                i.putExtra("no",no);
                startActivity(i);
            }
        });
        mCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String card_3 = "3";
                String no ="2";
                Intent i = new Intent(Emergency_Contacts.this,ContactList.class);
                i.putExtra("num1",card_3);
                i.putExtra("no",no);
                startActivity(i);
            }
        });

        mCardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Emergency_Contacts.this,EditEmergencyMessage.class);
                        i.putExtra("num1",no);
                        startActivity(i);
                    }
                }
        );



    }


    private void fetchInfo() {

        Query query = mDatabaseUsers.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap:dataSnapshot.getChildren()) {
                    no = (String)snap.child("no").getValue();
                    Log.d("no11",no);
                     retrieveTustedContactsOnCard();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void retrieveTustedContactsOnCard()
    {

        mDatabaseTrustedContacts.child(no).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trustedcontacts.clear();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    Log.d("no11",postsnapshot.getValue().toString());
                    Users user = postsnapshot.getValue(Users.class);
                    Trustedcontacts.add(user);
                }
                textName1.setText(Trustedcontacts.get(0).getName());
                textName2.setText(Trustedcontacts.get(1).getName());
                textName3.setText(Trustedcontacts.get(2).getName());

                textNum1.setText(Trustedcontacts.get(0).getMob());
                textNum2.setText(Trustedcontacts.get(1).getMob());
                textNum3.setText(Trustedcontacts.get(2).getMob());

                Picasso.with(Emergency_Contacts.this).load(Trustedcontacts.get(0).getPhotourl()).into(imag1);
                Picasso.with(Emergency_Contacts.this).load(Trustedcontacts.get(1).getPhotourl()).into(imag2);
                Picasso.with(Emergency_Contacts.this).load(Trustedcontacts.get(2).getPhotourl()).into(imag3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Emergency_Contacts.this,MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
