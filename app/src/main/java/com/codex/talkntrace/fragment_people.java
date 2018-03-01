package com.codex.talkntrace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

public class fragment_people extends Fragment {

    RecyclerView mRecyclerView;
    ArrayList<Users> contacts = new ArrayList<>();
    DatabaseReference mDatabaseContacts,mDatabseUsers;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    String UserEmail;
    private String User_No;
    private String User_Mobile;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    UserEmail = firebaseAuth.getCurrentUser().getEmail();
                    Log.d("Firebasestate",UserEmail);
                    fetchInfo();
                }
            }
        };
    }

    public void fetchInfo()
    {
        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = mDatabseUsers.orderByChild("email").equalTo(UserEmail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postsnapshot : dataSnapshot.getChildren())
                {
                    User_No = (String)postsnapshot.child("no").getValue();
                    User_Mobile = (String)postsnapshot.child("mob").getValue();
                    Log.d("Firebasestate",User_No);
                    contacts.clear();
                    mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts").child(User_No).child(User_No);
                    Query query1 = mDatabaseContacts.orderByChild("name");
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                            {
                                String email = (String)postSnapshot.child("email").getValue();
                                Query query2 = mDatabseUsers.orderByChild("email").equalTo(email);
                                query2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(DataSnapshot postsnapshot:dataSnapshot.getChildren())
                                        {
                                            int flag1=0;
                                            Users user = postsnapshot.getValue(Users.class);

                                            if(!user.getMob().equals(User_Mobile))
                                            {
                                                for(Users users:contacts)
                                                {
                                                    if(user.getMob().equals(users.getMob()))
                                                    {
                                                        flag1 = 1;
                                                    }
                                                }
                                                if(flag1==0) {
                                                    contacts.add(user);
                                                    Log.d("Firebasestate", user.getEmail());
                                                }
                                            }
                                        }
                                        mRecyclerView.setAdapter(new adapter(getContext(),contacts,getActivity()));
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView)inflater.inflate(R.layout.people_fragment,container,false);
        setUpRecyclerView(mRecyclerView);
        Log.d("Firebasestate","Updating data");
        return mRecyclerView;
    }

    public static Fragment newInstance(int i) {
        fragment_people yf = new fragment_people();
        return yf;
    }

    public void setUpRecyclerView(RecyclerView rv) {
        Log.d("Firebase-data","user adapter");
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(new adapter(rv.getContext(),contacts, getActivity()) );
    }

    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

        ArrayList<Users> contacts=new ArrayList<>();
        Context context;
        Activity activity;

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private TextView phoneNumber;
            private ImageView imgView;
            private CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.cardname);
                phoneNumber = (TextView)itemView.findViewById(R.id.msg);
                imgView = (ImageView)itemView.findViewById(R.id.profilepic);
                cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public adapter( Context context,ArrayList<Users> contacts, Activity activity) {
            this.contacts = contacts;
            this.context = context;
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.display_card, parent, false);
            return  new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(contacts.get(position).getName());
            holder.phoneNumber.setText(contacts.get(position).getMob());
            Picasso.with(context).load(contacts.get(position).getPhotourl()).into(holder.imgView);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> al = new ArrayList<>();
                    al.add(0,holder.name.getText().toString());
                    al.add(1,contacts.get(position).getEmail());
                    al.add(2,contacts.get(position).getPhotourl());
                    change(al);
                }
            });
        }
        public void change(ArrayList<String> al)
        {
            Intent i = new Intent(activity.getApplication(),Chats.class);
            i.putExtra("userdata",al);
            activity.startActivity(i);

        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }



    }
}
