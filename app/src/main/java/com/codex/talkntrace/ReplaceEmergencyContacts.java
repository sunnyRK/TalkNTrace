package com.codex.talkntrace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class ReplaceEmergencyContacts extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<Users> contacts = new ArrayList<>();
    ArrayList<Users> selected_contacts=new ArrayList<>();
    DatabaseReference mDatabaseContacts,mDatabseUsers,mDatabaseGroup,mDatabaseGroupChat;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    String UserEmail;
    private String User_No;
    Button createbtn;
    private String User_Mobile;
    EditText groupname;
    private String group_name;
    private String total_group;
    private int total_group1;
    private int groups_total1;
    private String member_no;
    private String member_totalGroups_no;
    private int member_totalGroups_no1;
    private int flags=0;
    private int flags1=0;
    Spinner spinner;
    String [] group_type = {"Secure Group","Simple Group"};
    String type;
    private ImageView backtogroup;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_emergency_contacts);
        mDatabaseGroup = FirebaseDatabase.getInstance().getReference().child("groups");
        mDatabaseGroupChat = FirebaseDatabase.getInstance().getReference().child("GroupChat");
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_Edit);
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
                else
                {
                    startActivity(new Intent(ReplaceEmergencyContacts.this,Google_Login.class));
                    ReplaceEmergencyContacts.this.finish();
                }
            }
        };
        createbtn = (Button)findViewById(R.id.createGroup);
        backtogroup = (ImageView) findViewById(R.id.backtogrp);
        groupname = (EditText)findViewById(R.id.groupname);
        spinner = (Spinner) findViewById(R.id.dropdown);
        backtogroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceEmergencyContacts.this.finish();
            }
        });
        ArrayAdapter<String> adapterdropdown = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, group_type);
        spinner.setAdapter(adapterdropdown);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
                // TODO Auto-generated method stub

                type = group_type[position];
                Toast.makeText(getBaseContext(),type, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                group_name = groupname.getText().toString().trim();

                if(group_name.equals(""))
                {
                    groupname.setError("Please enter group name.");
                }

                if( (group_name.charAt(0)>=65 && group_name.charAt(0)<=90) || (group_name.charAt(0)>=97 && group_name.charAt(0)<=122))
                {
                    if(group_name.length()>3){
                        retrieve_groupLastno();
                        groupname.setText("");
                        Toast.makeText(ReplaceEmergencyContacts.this,"You created "+ group_name + " Group Now You can Trace Your group member Location.",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        groupname.setError("Group name must be greater than 3 characters");
                    }
                }
                else
                {
                    groupname.setError("GroupName Must start with characters");
                }

            }
        });
    }

    private void retrieve_groupLastno() {
        Log.d("Grpstotal","1 fun");
        mDatabaseGroupChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(flags1==0) {
                    String groups_total = (String) dataSnapshot.child("Grpstotal").getValue();
                    groups_total1 = Integer.parseInt(groups_total);
                    groups_total1++;
                    flags1=1;
                    createGroup();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createGroup() {
        mDatabaseGroup.child(User_No).child("totalgroups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(flags==0) {
                    total_group = (String) dataSnapshot.getValue();
                    total_group1 = Integer.parseInt(total_group);
                    total_group1++;
                    flags=1;
                    addToGroupChat();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void addToGroupChat()
    {
        final String[] url = new String[1];
        int i=2;
        mDatabaseGroup.child(User_No).child("users_groups").child(total_group1+"").child("groupno").setValue(groups_total1+"");
        mDatabaseGroup.child(User_No).child("users_groups").child(total_group1+"").child("name").setValue(group_name+"");
        mDatabaseGroup.child(User_No).child("users_groups").child(total_group1+"").child("type").setValue(type);
        final Random r = new Random();
        final int number = r.nextInt(3)+1;
        String intial_charcter = (group_name.charAt(0)+"").toLowerCase()+number;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("group_icon/"+intial_charcter+".png");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url[0] = uri.toString();
                mDatabaseGroup.child(User_No).child("users_groups").child(total_group1+"").child("photourl").setValue(url[0]);

                for(Users user:selected_contacts)
                {
                    final int[] flags = {0};
                    final String member_email = user.getEmail();
                    Query query = mDatabaseGroup.orderByChild("email").equalTo(member_email);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                if(flags[0] ==0) {
                                    member_no = (String) snapshot.child("user_no").getValue();
                                    member_totalGroups_no = (String) snapshot.child("totalgroups").getValue();
                                    member_totalGroups_no1 = Integer.parseInt(member_totalGroups_no);
                                    member_totalGroups_no1++;
                                    mDatabaseGroup.child(member_no).child("users_groups").child(member_totalGroups_no1 + "").child("groupno").setValue(groups_total1+"");
                                    mDatabaseGroup.child(member_no).child("users_groups").child(member_totalGroups_no1 + "").child("name").setValue(group_name);
                                    mDatabaseGroup.child(member_no).child("users_groups").child(member_totalGroups_no1 + "").child("type").setValue(type);
                                    mDatabaseGroup.child(member_no).child("users_groups").child(member_totalGroups_no1 + "").child("photourl").setValue(url[0]);
                                    mDatabaseGroup.child(member_no).child("totalgroups").setValue(member_totalGroups_no1 + "");
                                    flags[0] =1;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }
        });

        mDatabaseGroup.child(User_No).child("totalgroups").setValue(total_group1+"");
        mDatabaseGroupChat.child(groups_total1+"").child("groupname").setValue(group_name+"");
        mDatabaseGroupChat.child(groups_total1+"").child("groupno").setValue(groups_total1+"");
        mDatabaseGroupChat.child(groups_total1+"").child("totalmsg").setValue("0");
        mDatabaseGroupChat.child(groups_total1+"").child("totalmember").setValue("0");
        mDatabaseGroupChat.child(groups_total1+"").child("members").child("1").child("email").setValue(UserEmail+"");
        mDatabaseGroupChat.child("Grpstotal").setValue(groups_total1+"");

        for(Users user:selected_contacts)
        {
            mDatabaseGroupChat.child(groups_total1+"").child("members").child(i+"").child("email").setValue(user.getEmail());
            mDatabaseGroupChat.child(groups_total1+"").child("totalmember").setValue(i+"");
            i++;
        }

    }

    private void fetchInfo() {
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
        Log.d("Firebase-data","user adapter");
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(new adapter(contacts) );
    }
    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

        ArrayList<Users> contacts=new ArrayList<>();
        int flag;


        public adapter(ArrayList<Users> contacts) {
            this.contacts = contacts;flag=0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CardView selectView;
            TextView name,num;
            CheckBox chkbox;
            ImageView profilepic;


            public ViewHolder(View itemView) {
                super(itemView);
                selectView = (CardView)itemView.findViewById(R.id.cardView_select);
                name =(TextView)itemView.findViewById(R.id.cardname);
                num =(TextView)itemView.findViewById(R.id.msg);
                profilepic = (ImageView) itemView.findViewById(R.id.profilepic);
                chkbox = (CheckBox)itemView.findViewById(R.id.chkbox);
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.select_list, parent, false);
            return  new adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(contacts.get(position).getName());
            holder.num.setText(contacts.get(position).getMob());
            Picasso.with(getParent()).load(contacts.get(position).getPhotourl()).into(holder.profilepic);
           /* holder.createbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(Users user:selected_contacts)
                    {
                        Toast.makeText(ReplaceEmergencyContacts.this,user.getName(),Toast.LENGTH_SHORT);
                    }
                }
            });*/
            holder.selectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  /*  flag=1;
                    Log.d("test1234","card");
                   if(holder.chkbox.isChecked())
                    {
                        int i=0;
                        for(Users user:selected_contacts)
                        {
                            if(user.getName().equals(holder.name.getText().toString()))
                            {
                                Log.d("test1234","checking "+user.getName()+" = "+holder.name.getText().toString());
                                break;
                            }
                            i++;
                        }
                        Log.d("test1234","removed "+selected_contacts.get(i).getName());
                        selected_contacts.remove(i);
                    }
                    else
                    {
                        selected_contacts.add(contacts.get(position));
                        Log.d("test1234","added "+contacts.get(position).getName());
                    }*/
                    holder.chkbox.performClick();
                }
            });

            holder.chkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if(flag==0)
                    {
                        Log.d("test1234","chkbox");
                        if(!holder.chkbox.isChecked())
                        {
                            int i=0,flag1=0;
                            for(Users user:selected_contacts)
                            {
                                if(user.getName().equals(holder.name.getText().toString()))
                                {
                                    Log.d("test1234","checking "+user.getName()+" = "+holder.name.getText().toString());
                                    flag1=1;
                                    break;
                                }
                                i++;
                            }
                            if(flag1==1){
                                Log.d("test1234","removed "+selected_contacts.get(i).getName());
                                selected_contacts.remove(i);
                            }
                        }
                        else
                        {
                            selected_contacts.add(contacts.get(position));
                            Log.d("test1234","added "+contacts.get(position).getName());
                        }
                    }
                    flag=0;
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }


    }


}