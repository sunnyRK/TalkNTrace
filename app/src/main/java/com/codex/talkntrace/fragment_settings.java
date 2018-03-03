package com.codex.talkntrace;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

public class fragment_settings extends Fragment {

    CardView mCardView;
    CardView password;
    CardView deleteAcc;
    Button logout;
    Dialog alert;
    String userEmail,pswds;
    DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getContext(), Google_Login.class));
                }
                else {
                    userEmail = firebaseAuth.getCurrentUser().getEmail();

                    Query query1 = mDatabaseUsers.orderByChild("email").equalTo(userEmail);
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                pswds = (String) postSnapshot.child("password").getValue();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }



        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /* View view = getView();
        mCardView = (CardView)view.findViewById(R.id.cardView_emergency);
        mCardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(),Emergency_Contacts.class));
                    }
                }
        );*/
        return inflater.inflate(R.layout.settings_fragment, container, false);


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mCardView = (CardView)getActivity().findViewById(R.id.cardView_emergency);
        password= (CardView)getActivity().findViewById(R.id.for_pass);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Forgot_Password.class));
            }
        });
        mCardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert = new Dialog(getActivity());
                        alert.setContentView(R.layout.password_protected_group);
                        alert.setTitle("Password Protected");
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final EditText password_group = (EditText)alert.findViewById(R.id.pwd_groups);
                        Button submitpwd = (Button) alert.findViewById(R.id.submitpwd);
                        Button forgot_pass = (Button) alert.findViewById(R.id.forgot_pass);

                        forgot_pass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(),Forgot_Password.class));
                            }
                        });

                        submitpwd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String pwd = password_group.getText().toString();
                                if(pwd.equals(pswds))
                                {
                                    alert.dismiss();
                                    startActivity(new Intent(getContext(),Emergency_Contacts.class));
                                }
                                else
                                {
                                    password_group.setText("");
                                    password_group.setError("Wrong Passsword");
                                }

                            }
                        });


                        alert.create();
                        alert.show();

                        //startActivity(new Intent(getContext(),Emergency_Contacts.class));
                    }
                }
        );

        deleteAcc = (CardView)getActivity().findViewById(R.id.cardView_delete);
        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),DeleteAccount.class));
            }
        });
    }

    public static Fragment newInstance(int i) {
        fragment_settings yf = new fragment_settings();
        return yf;
    }
}
