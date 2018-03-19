package com.codex.talkntrace;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

public class fragment_groups extends Fragment{


    private RecyclerView recyclerView;
    private ArrayList<UsersGroup> userDetail = new ArrayList<>();
    String userEmail,username;
    DatabaseReference mDatabase;
    ArrayList<ArrayList<String>> outer = new ArrayList<ArrayList<String>>();
    private FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private String user_no;
    private String password;
     Dialog alert;

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

                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    userEmail = firebaseAuth.getCurrentUser().getEmail();

                    Query query1 = mDatabaseUsers.orderByChild("email").equalTo(userEmail);
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                user_no = (String) postSnapshot.child("no").getValue();
                                password = (String) postSnapshot.child("password").getValue();
                            }
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("groups");
                            mDatabase.child(user_no).child("users_groups").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    userDetail.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        UsersGroup user = snapshot.getValue(UsersGroup.class);
                                        userDetail.add(user);
                                    }
                                    recyclerView.setAdapter(new adapter(recyclerView.getContext(), userDetail, getActivity()));
                                    setUpRecyclerView(recyclerView);
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


            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) inflater.inflate(R.layout.groups_fragment,container,false);
        setUpRecyclerView(recyclerView);
        return recyclerView;
    }


    private void setUpRecyclerView(RecyclerView rv)
    {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(new adapter(rv.getContext(),userDetail, getActivity()) );
        Log.d("Firebase-data","user adapter");
    }

    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder>
    {

        ArrayList<UsersGroup> userDetail=new ArrayList<>();
        Context context;
        Activity activity;


        public class ViewHolder extends RecyclerView.ViewHolder
        {

            private TextView name;
            //   private TextView time;
            //private TextView lastmsg;
            private ImageView imgView;
            private CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.cardname);
                //       time = (TextView)itemView.findViewById(R.id.time);
                //    lastmsg = (TextView)itemView.findViewById(R.id.msg);
                imgView = (ImageView)itemView.findViewById(R.id.profilepic);
                cardView = (CardView)itemView.findViewById(R.id.cardView);
                Log.d("Firebase-data","user shown");
            }
        }
        @Override
        public adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.display_card, parent, false);
            return  new adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final adapter.ViewHolder holder, final int position) {
            final String group_type;
            holder.name.setText(userDetail.get(position).getName());
            group_type = userDetail.get(position).getType();





            // holder.time.setText(getDate(userDetail.get(position).getTime()));
            /*String type = userDetail.get(position).getType();
            if(type.equals("1")) {
                holder.lastmsg.setText("photo");
                holder.lastmsg.setCompoundDrawables(getResources().getDrawable(R.drawable.audio),null,null,null);
            }
            //android:drawableLeft="@android:drawable/ic_menu_search"
            else if(type.equals("2")) {
                holder.lastmsg.setText("video");
            }
            else if(type.equals("3")) {
                holder.lastmsg.setText("audio");
            }
            else
                holder.lastmsg.setText(userDetail.get(position).getLastmsg());*/
            Glide.with(context).load(userDetail.get(position).getPhotourl()).centerCrop().into(holder.imgView);
            Log.d("Firebase-data","user on the way");
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(group_type.equals("Secure Group"))
                    {

                        alert = new Dialog(getActivity());
                        alert.setContentView(R.layout.password_protected_group);
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alert.setTitle("Password Protected Group");
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
                                Log.d("password_group1",pwd);
                                if(pwd.equals(password))
                                {
                                    Log.d("password_group1",pwd+"under");
                                    ArrayList<String> al = new ArrayList<>();
                                    al.add(0,holder.name.getText().toString());
                                    al.add(1,userDetail.get(position).getGroupno());
                                    alert.dismiss();
                                    change(al);
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
                    }
                    else
                    {

                        ArrayList<String> al = new ArrayList<>();
                        al.add(0,holder.name.getText().toString());
                        al.add(1,userDetail.get(position).getGroupno());
                        change(al);
                    }

                }
            });
            //YoYo.with(Techniques.FadeIn).playOn(holder.cardView);
        }
        private String getDate(Long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = DateFormat.format("hh:mm a", cal).toString();
            return date;
        }
        public void change(ArrayList<String> al)
        {
            Intent i = new Intent(activity.getApplication(),Maps.class);
            i.putExtra("userdata",al);
            activity.startActivity(i);

        }
        @Override
        public int getItemCount() {
            return userDetail.size();        }

        public adapter(Context context,ArrayList<UsersGroup> userDetail, FragmentActivity activity) {
            this.context=context;
            this.userDetail=userDetail;
            this.activity = activity;
        }

    }


    public static Fragment newInstance(int i) {
        fragment_groups yf = new fragment_groups();
        return yf;
    }
}
