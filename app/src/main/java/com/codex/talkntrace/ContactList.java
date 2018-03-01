package com.codex.talkntrace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private ArrayList<Users> contacts = new ArrayList<>();
    ArrayList<Users> selected_contacts = new ArrayList<>();


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String username;
    String useremail;

    DatabaseReference mDatabaseUsers;
    DatabaseReference mDatabaseContacts;
    DatabaseReference mDatabaseTrustedContacts;
    private String User_No;
    private String User_Mobile;
    private String email;
    private String name;
    private String mobs;
    private String no;
    private String no1;
    private String photourl;
    private String photo_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Bundle bundle = getIntent().getExtras();

        no = bundle.getString("num1");
        no1 = bundle.getString("no");


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_contctlist_Emergency);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts");
        mDatabaseTrustedContacts = FirebaseDatabase.getInstance().getReference().child("TrustedContacts");




        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(ContactList.this, Google_Login.class));
                } else {
                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    useremail = firebaseAuth.getCurrentUser().getEmail();

                    fetchInfo();

                }

            }
        };
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
                    User_Mobile = (String) postsnapshot.child("mob").getValue();
                    photourl = (String) postsnapshot.child("photourl").getValue();

                    Log.d("Firebasestate", User_No);
                    contacts.clear();
                    mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts").child(User_No).child(User_No);
                    Query query1 = mDatabaseContacts.orderByChild("name");
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String email = (String) postSnapshot.child("email").getValue();



                                Query query2 = mDatabaseUsers.orderByChild("email").equalTo(email);
                                query2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                                            int flag1 = 0;
                                            Users user = postsnapshot.getValue(Users.class);

                                            if (!user.getMob().equals(User_Mobile)) {
                                                for (Users users : contacts) {
                                                    if (user.getMob().equals(users.getMob())) {
                                                        flag1 = 1;
                                                    }
                                                }
                                                if (flag1 == 0) {
                                                    contacts.add(user);
                                                    Log.d("Firebasestate", user.getEmail());
                                                }
                                            }
                                        }
                                        mRecyclerView.setAdapter(new adapter(contacts));
                                        setUpRecyclerView(mRecyclerView);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpRecyclerView(RecyclerView rv) {
        Log.d("Firebase-data", "user adapter");
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(new adapter(contacts));
    }


    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

        ArrayList<Users> contacts = new ArrayList<>();
        int flag;


        public adapter(ArrayList<Users> contacts) {
            this.contacts = contacts;
            flag = 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CardView selectView;
            TextView name, num;
            CheckBox chkbox;
            ImageView profilepic;


            public ViewHolder(View itemView) {
                super(itemView);
                selectView = (CardView) itemView.findViewById(R.id.cardView_select);
                name = (TextView) itemView.findViewById(R.id.cardname);
                num = (TextView) itemView.findViewById(R.id.msg);
                profilepic = (ImageView) itemView.findViewById(R.id.profilepic);
                chkbox = (CheckBox) itemView.findViewById(R.id.chkbox);
            }
        }


        @Override
        public adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_list, parent, false);
            return new adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final adapter.ViewHolder holder, final int position) {
            holder.name.setText(contacts.get(position).getName());
            holder.num.setText(contacts.get(position).getMob());
            Picasso.with(getParent()).load(contacts.get(position).getPhotourl()).into(holder.profilepic);
            holder.selectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.chkbox.performClick();
                }
            });

            holder.chkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    email = contacts.get(position).getEmail();
                    mobs = contacts.get(position).getMob();
                    name = contacts.get(position).getName();
                    photo_url = contacts.get(position).getPhotourl();
                    addToTrustedContacts();
                }
            });

        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }


    }

    private void addToTrustedContacts() {
        Log.d("no1" , no);
        mDatabaseTrustedContacts.child(User_No).child(no).child("email").setValue(email);
        mDatabaseTrustedContacts.child(User_No).child(no).child("name").setValue(name);
        mDatabaseTrustedContacts.child(User_No).child(no).child("mob").setValue(mobs);
        mDatabaseTrustedContacts.child(User_No).child(no).child("photourl").setValue(photo_url);
        mDatabaseTrustedContacts.child(User_No).child(no).child("no").setValue(no);
        if(no1.equals("1"))
        {
            startActivity(new Intent(ContactList.this, EmergencyContactSelection.class));
        }
        else if(no1.equals("2")) {
            startActivity(new Intent(ContactList.this, Emergency_Contacts.class));
        }
    }

}