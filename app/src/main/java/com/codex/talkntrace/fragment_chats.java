package com.codex.talkntrace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import static java.util.Comparator.comparing;


/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

public class fragment_chats extends Fragment {

    /*Data members global*/
    private RecyclerView recyclerView;
    private ArrayList<Users> userDetail = new ArrayList<>();

    private DatabaseReference mDatabaseSingleChat;
    private DatabaseReference mDatabaseSingleChat2;
    private DatabaseReference mDatabaseUsers;

    private FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    String userEmail;
    String username;

    String num;
    private int flag=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()== null)
                {
                    startActivity(new Intent(getContext(),Google_Login.class));
                }
                else
                {
                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    userEmail = firebaseAuth.getCurrentUser().getEmail();

                    mDatabaseSingleChat = FirebaseDatabase.getInstance().getReference().child("users");
                    final Query query = mDatabaseSingleChat.orderByChild("email").equalTo(userEmail);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //userDetail.clear();
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                num = (String)postSnapshot.child("no").getValue();
                                if(num != null) {
                                    addToChat();
                                    Log.d("Firebase-data140325","user no 0 "+ num);

                                }
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

    private void addToChat()
    {
        Log.d("Firebase-data140325","user no 1 "+ num);
        mDatabaseSingleChat2 = FirebaseDatabase.getInstance().getReference().child("SingleChat").child(num+"").child("chats");
        Log.d("DebugnMyapp","gettings uses in single chat ");

        mDatabaseSingleChat2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email;
                String totalmsg=null;
                final String[] msg = new String[1];
                final Long[] time = new Long[1];
                final String[] type=new String[1];
                String msgno;
                String no;
                userDetail.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    email = (String)postSnapshot.child("email").getValue();
                    no = (String)postSnapshot.child("no").getValue();
                    msgno = (String)postSnapshot.child("totalmsg").getValue();
                    totalmsg = (String)postSnapshot.child("totalmsg").getValue();
                    if(totalmsg != null && totalmsg != "0")
                    {
                        mDatabaseSingleChat2.child(no).child("msgs").child("msg"+totalmsg).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                msg[0] = (String)dataSnapshot.child("msg").getValue();
                                type[0] = (String)dataSnapshot.child("type").getValue();
                                time[0] = (Long)dataSnapshot.child("time").getValue();
                                Log.d("total_msg","msg : "+msg[0]);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    Log.d("Firebase-data","user email "+email);
                    userDetail.clear();
                    Log.d("Firebase-data","clearing no 2");

                    mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabaseUsers.keepSynced(true);
                    Query query1 = mDatabaseUsers.orderByChild("email").equalTo(email);
                    //if(flag==0){
                    userDetail.clear();

                    final String finalMsgno = msgno;
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren() )
                            {
                                Users user = snapshot.getValue(Users.class);
                                user.setLastmsg(msg[0]);
                                user.setType(type[0]);
                                user.setTime(time[0]);
                                int flag1=0;
                                for(Users users:userDetail)
                                {
                                    if(user.getMob().equals(users.getMob()))
                                    {
                                        flag1=1;
                                    }
                                }
                                if(flag1==0 && finalMsgno != "0")
                                {
                                    userDetail.add(user);
                                }
                                Log.d("Firebase-data","user added"+user.getPhotourl());
                            }
                          /*  Collections.sort(userDetail, new Comparator<Users>() {
                                        @Override
                                        public int compare(Users users, Users t1) {
                                            return Long.valueOf(users.getTime()).compareTo(t1.getTime());
                                        }
                                    }
                            );
                                       Collections.reverse(userDetail);*/
                                     recyclerView.setAdapter(new adapter(recyclerView.getContext(), userDetail, getActivity()));
                            // setUpRecyclerView(recyclerView);
                            flag++;

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                // }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            recyclerView = (RecyclerView) inflater.inflate(R.layout.chats_fragment,container,false);
            setUpRecyclerView(recyclerView);
            return recyclerView;
        }

        private void setUpRecyclerView(RecyclerView rv)
        {
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
            rv.setAdapter(new adapter(rv.getContext(),userDetail, getActivity()) );
            Log.d("Firebase-data","user adapter");
        }

        /*Inner Class Adapter*/
    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder>
    {
        /*Global data member*/
       ArrayList<Users> userDetail=new ArrayList<>();
        Context context;
        Activity activity;


        public class ViewHolder extends RecyclerView.ViewHolder
        {

            private TextView name;
            private TextView time;
            private TextView lastmsg;
            private ImageView imgView;
            private CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.cardname);
                time = (TextView)itemView.findViewById(R.id.time);
                lastmsg = (TextView)itemView.findViewById(R.id.msg);
                imgView = (ImageView)itemView.findViewById(R.id.profilepic);
                cardView = (CardView)itemView.findViewById(R.id.cardView);
                Log.d("Firebase-data","user shown");
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.display_card, parent, false);
            return  new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(userDetail.get(position).getName());
            // holder.time.setText(getDate(userDetail.get(position).getTime()));
            String type = userDetail.get(position).getType()+"";
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
                holder.lastmsg.setText(userDetail.get(position).getLastmsg());
            Glide.with(context).load(userDetail.get(position).getPhotourl()).centerCrop().into(holder.imgView);
            Log.d("Firebase-data","user on the way");
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> al = new ArrayList<>();
                    al.add(0,holder.name.getText().toString());
                    al.add(1,userDetail.get(position).getEmail());
                    al.add(2,userDetail.get(position).getPhotourl());
                    al.add(3,userDetail.get(position).getNo());
                    al.add(4,num);
                    change(al);
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
            Intent i = new Intent(activity.getApplication(),SingleMap.class);
            i.putExtra("userdata",al);
            activity.startActivity(i);

        }
        @Override
        public int getItemCount() {
            return userDetail.size();        }

        public adapter(Context context,ArrayList<Users> userDetail, FragmentActivity activity) {
            this.context=context;
            this.userDetail=userDetail;
            this.activity = activity;
        }
    }

    public static Fragment newInstance(int i) {
        fragment_chats yf = new fragment_chats();
        return yf;
    }



}
