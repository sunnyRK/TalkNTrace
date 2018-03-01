package com.codex.talkntrace;

import android.*;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class Chats extends AppCompatActivity  implements View.OnClickListener{


    final ArrayList<chatRet> al1 =new ArrayList<>();
    DatabaseReference mDatabaseUsers1;
    DatabaseReference mDatabaseSingleChat;
    DatabaseReference mDatabaseSingleChat2;
    DatabaseReference mDatabaseSingleChat3;
    DatabaseReference mDatabaseSingleChat4;
    DatabaseReference mDatabaseContacts;
    String num5;
    String num9;
    int number;
    int flag2=0;
    int flag3=0;
    int flag4=0;
    RecyclerView recyclerView;
    private static final int PERMISSION_REQUEST_CONTACT = 10;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private int reqCode = 2;
    private static final int CROP_PIC = 121;
    private TextView Name;
    private TextView No;
    ImageButton camera,Sendbtn;
    private ArrayList<String> al;
    TextView contactName, contactNo;
    DatabaseReference mDatabase;
    MenuItem info;
    BottomSheetBehavior behavior;
    NestedScrollView nestedScrollView;
    Toolbar tb;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String username;
    String useremail;

    DatabaseReference mDatabase1;
    DatabaseReference mDatabase2;
    String msg;
    ImageButton back;

    String num;
    String num2;
    String num3;
    String num4;
    String email;
    int total_msg;
    int total_msg2;
    private Parcelable recycleViewState;


    int flag=0;
    int flag1=0;
    RelativeLayout rl;
    EmojiconEditText message;

    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton gallery_btn, photo_btn, video_btn, audio_btn, location_btn, contact_btn;
    public Uri picUri;
    private Bitmap bitmap;
    private Bitmap gallery_upload;
    private String photo_user;
    private String photo_main;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        View view = getCurrentFocus();
        initView(view);
        al = new ArrayList<>();
        Intent i = getIntent();
        al = i.getStringArrayListExtra("userdata");
        photo_user=al.get(2);
        email = al.get(1);
        Toast.makeText(this,email,Toast.LENGTH_SHORT).show();

        Name = (TextView) findViewById(R.id.Name);
        Name.setText(al.get(0));

        mDatabaseUsers1 = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseSingleChat = FirebaseDatabase.getInstance().getReference().child("SingleChat");
        mDatabaseSingleChat2 = FirebaseDatabase.getInstance().getReference().child("SingleChat");
        mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts");
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_Chat);
        nestedScrollView = (NestedScrollView)findViewById(R.id.view);
        behavior = BottomSheetBehavior.from(nestedScrollView);

       // video_btn=(ImageButton)findViewById(R.id.video_img_btn);
        Log.d("scrollView","focus changed");

        rl = (RelativeLayout)findViewById(R.id.hide_bottom_sheet);
        rl.setVisibility(View.GONE);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                rl.setVisibility(View.GONE);
            }
        });



        back = (ImageButton)findViewById(R.id.backbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(Chats.this,Google_Login.class));
                }
                else
                {
                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    useremail = firebaseAuth.getCurrentUser().getEmail();
                    photo_main = firebaseAuth.getCurrentUser().getPhotoUrl().toString();


                    fetchInfo();

                }

            }
        };

        message = (EmojiconEditText)findViewById(R.id.editText2);
        final ImageButton send = (ImageButton) findViewById(R.id.SendBtn);
        final ImageButton emoji = (ImageButton) findViewById(R.id.emoji);
        final ImageButton camera = (ImageButton) findViewById(R.id.CameraBtn);
        final EmojIconActions  emojiIcon=new EmojIconActions(this,view,message,emoji);
        message.setUseSystemDefault(true);
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Audio"),reqCode);
            }
        });
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if((s.toString().trim().length()) == 0)
                {
                    send.setVisibility(View.INVISIBLE);
                    camera.setVisibility(View.VISIBLE);
                }
                else
                {
                    send.setVisibility(View.VISIBLE);
                    camera.setVisibility(View.INVISIBLE);
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCamera();
//startUploadVideo();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                msg = message.getText().toString().trim();

                msg = encryption.encrypt(msg);
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        Log.d("nums","3");
                        sendMsg(msg,"0");
                    }
                } ).start();


            }
        });

    }




    private void sendMsg(final String msg, final String type)
    {
        final int[] flag = {0};
        Query querys = mDatabaseSingleChat.child(num).child("chats").orderByChild("email").equalTo(email);
        querys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    num9 = (String) postSnapshot.child("totalmsg").getValue();
                    total_msg = Integer.parseInt(num9);
                    total_msg++;
                    flag[0]++;

                }
                if(flag[0] ==1)
                {
                    Map<String,Object> timestamp = new HashMap<>();
                    timestamp.put("msg",msg+"");
                    timestamp.put("sender",useremail+"");
                    timestamp.put("type",type);
                    timestamp.put("time", ServerValue.TIMESTAMP);
                    mDatabaseSingleChat2.child(num).child("chats").child(num2).child("msgs").child("msg" + total_msg).setValue(timestamp);

                    Map<String,Object> timestamp2 = new HashMap<>();
                    timestamp2.put("msg",msg+"");
                    timestamp2.put("sender",useremail+"");
                    timestamp2.put("type",type);
                    timestamp2.put("time", ServerValue.TIMESTAMP);
                    mDatabaseSingleChat2.child(num3).child("chats").child(num4).child("msgs").child("msg" + total_msg).setValue(timestamp2);
                    mDatabaseSingleChat2.child(num).child("chats").child(num2).child("totalmsg").setValue(total_msg + "");
                    mDatabaseSingleChat2.child(num3).child("chats").child(num4).child("totalmsg").setValue(total_msg + "");

                    flag[0]++;

                    message.setText("");
                    Toast.makeText(Chats.this, "Sent", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setUpRecyclerView(RecyclerView rv)
    {

        LinearLayoutManager llm = new LinearLayoutManager(rv.getContext());
        llm.setStackFromEnd(true);
        llm.setSmoothScrollbarEnabled(true);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv.setAdapter(new adapter(al1,useremail,bitmap,getApplicationContext()) );

    }
    public void fetchInfo()
    {
        Log.d("Firebase-213","Inside fetch info with user "+useremail);
        Query query = mDatabaseUsers1.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    num = (String)postSnapshot.child("no").getValue();
                    Log.d("Firebase-213","num"+num);
                    //Toast.makeText(Chat.this,num+" num",Toast.LENGTH_SHORT).show();
                    Query query1 = mDatabaseSingleChat.child(num).child("chats").orderByChild("email").equalTo(email);
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                num2 = (String)postSnapshot.child("no").getValue();
                                Log.d("Firebase-213","num2"+num2);
                                //          Toast.makeText(Chat.this,num2+" num2",Toast.LENGTH_SHORT).show();
                                Query query2 = mDatabaseUsers1.orderByChild("email").equalTo(email);
                                query2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                        {
                                            num3 = (String)postSnapshot.child("no").getValue();
                                            //                    Toast.makeText(Chat.this,num3+" num3",Toast.LENGTH_SHORT).show();
                                            Log.d("Firebase-213","num3"+num3);
                                            Query query3 = mDatabaseSingleChat.child(num3).child("chats").orderByChild("email").equalTo(useremail);
                                            query3.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                                    {
                                                        if(flag2==0)
                                                        {
                                                            num4 = (String) postSnapshot.child("no").getValue();
                                                            Log.d("Firebase-213","num4"+num4);
                                                            flag2++;
                                                            //                                      Toast.makeText(Chat.this,num4+" num4",Toast.LENGTH_SHORT).show();
                                                            Log.d("num","0");
                                                        }
                                                    }
                                                    if(num4==null)
                                                    {
                                                        Log.d("num","1");
                                                        mDatabaseSingleChat4 = FirebaseDatabase.getInstance().getReference().child("SingleChat").child(num3).child("total").child("no");
                                                        mDatabaseSingleChat4.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                num5 = dataSnapshot.getValue(String.class).toString();
                                                                number = Integer.parseInt(num5);
                                                                Log.d("num","2");

                                                                if(flag1==0) {
                                                                    Log.d("num", "3");
                                                                    number++;
                                                                    WriteNewChat();
                                                                    Log.d("num", "9");

                                                                    flag1++;
                                                                }
                                                                if (flag3 == 1)
                                                                {
                                                                    Log.d("num", "9.1");
                                                                    Query query3 = mDatabaseSingleChat.child(num3).child("chats").orderByChild("email").equalTo(useremail);
                                                                    query3.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                                Log.d("num", "10");
                                                                                num4 = (String) postSnapshot.child("no").getValue();
                                                                                Log.d("num", "11");
                                                                                Log.d("Firebase-213","num4"+num4);
                                                                                //                                                        Toast.makeText(Chat.this, num4 + " num4s", Toast.LENGTH_SHORT).show();
                                                                                Query queryret = FirebaseDatabase.getInstance().getReference().child("SingleChat").child(num).child("chats").child(num2).child("msgs").orderByChild("time");

                                                                              //  mDatabaseSingleChat3.keepSynced(true);
                                                                                Log.d("num", "12");
                                                                                queryret.addValueEventListener(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                                                        al1.clear();
                                                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                            chatRet msgRet = snapshot.getValue(chatRet.class);
                                                                                            al1.add(msgRet);
                                                                                            Log.d("test-123","adding");
                                                                                        }

                                                                                        for (chatRet user:al1)
                                                                                        {
                                                                                            Log.d("ascen",user.getMsg());
                                                                                        }
                                                                                        recyclerView.setAdapter(new adapter(al1,useremail,bitmap,getApplicationContext()));
                                                                                        setUpRecyclerView(recyclerView);
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
                                                    else
                                                    {
                                                        mDatabaseSingleChat3 = FirebaseDatabase.getInstance().getReference().child("SingleChat").child(num).child("chats").child(num2).child("msgs");
                                                        Query queryret = mDatabaseSingleChat3.orderByChild("time");
                                                        mDatabaseSingleChat3.keepSynced(true);
                                                        queryret.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                al1.clear();
                                                                for(DataSnapshot snapshot : dataSnapshot.getChildren() )
                                                                {
                                                                    chatRet msgRet = snapshot.getValue(chatRet.class);
                                                                    al1.add(msgRet);
                                                                }
                                                                for (chatRet user:al1
                                                                        ) {
                                                                    Log.d("ascen",user.getMsg());
                                                                }
                                                                recyclerView.setAdapter(new adapter(al1,useremail,bitmap,getApplicationContext()));
                                                                setUpRecyclerView(recyclerView);
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

        //getChats();
    }




    private void WriteNewChat() {

        Log.d("num","4");
        final int[] flag = {0};
        final int[] flag1 = {0};
        final String[] mob = new String[1];
        mDatabaseContacts.child(num3).child("total").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String no = (String)dataSnapshot.getValue();
                final int[] no1 = {Integer.parseInt(no)};
                if(flag[0] ==0)
                {
                    Log.d("num","5");
                    Query query = mDatabaseUsers1.orderByChild("email").equalTo(useremail);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                Log.d("num","6");
                                mob[0] = (String) postSnapshot.child("mob").getValue();
                                flag1[0] = 1;
                            }
                            if(flag1[0] ==1)
                            {
                                Log.d("num","7");
                                no1[0]++;
                                mDatabaseSingleChat.child(num3).child("chats").child(number+"").child("totalmsg").setValue(0+"");
                                mDatabaseSingleChat.child(num3).child("chats").child(number+"").child("email").setValue(useremail);
                                mDatabaseSingleChat.child(num3).child("chats").child(number+"").child("no").setValue(number+"");
                                mDatabaseSingleChat.child(num3).child("chats").child(number+"").child("name").setValue(username);
                                mDatabaseSingleChat.child(num3).child("total").child("no").setValue(number+"");

                                mDatabaseContacts.child(num3+"").child(num3+"").child(no1[0] +"").child("name").setValue(username);
                                mDatabaseContacts.child(num3+"").child(num3+"").child(no1[0] +"").child("mob").setValue(mob[0]);
                                mDatabaseContacts.child(num3+"").child(num3+"").child(no1[0] +"").child("email").setValue(useremail );
                                mDatabaseContacts.child(num3+"").child(num3+"").child(no1[0] +"").child("no").setValue(no1[0] +"");
                                mDatabaseContacts.child(num3+"").child("total").setValue(no1[0] +"");
                                flag[0]++;
                                flag3++;
                                Log.d("num","8");
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public   class adapter extends RecyclerView.Adapter<adapter.ViewHolder>
    {
        private static final long FADE_DURATION = 1000;
        Context context;
        ArrayList<chatRet> aboutlist=new ArrayList<>();
        String user;
        Activity ac;
        Uri picUri;
        Bitmap bitmap;
        int dateChange=0,month=0,year=0;
        public  class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView msg, mymsg,time,mytime,patti;
            ImageView imgleft,imgright,left_user_icon,right_user_icon;
            RelativeLayout right,left;
            public ViewHolder(View view)
            {
                super(view);
                msg = (TextView)view.findViewById(R.id.text_view_left);
                patti = (TextView)view.findViewById(R.id.patti);
                mymsg = (TextView)view.findViewById(R.id.text_view_right);
                time = (TextView)view.findViewById(R.id.text_view_time_left);
                mytime = (TextView)view.findViewById(R.id.text_view_time_right);
                right = (RelativeLayout)view.findViewById(R.id.right);
                left = (RelativeLayout)view.findViewById(R.id.left);
                imgleft = (ImageView)view.findViewById(R.id.image_left);
                imgright = (ImageView)view.findViewById(R.id.image_rigth);
                left_user_icon = (ImageView)view.findViewById(R.id.left_user_icon);
                right_user_icon = (ImageView)view.findViewById(R.id.rigth_user_icon);
            }
        }
        public adapter( ArrayList<chatRet> list,String username,Bitmap bitmap,Context context)
        {
            aboutlist=list;
            user= username;
            this.bitmap = bitmap;
            this.context = context;
        }
        @Override
        public adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.left_chat_message_view, parent, false);
            return  new adapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final adapter.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            String msg = aboutlist.get(position).getMsg();
            String sender = aboutlist.get(position).getSender();
            String type = aboutlist.get(position).getType();
            final Long time = aboutlist.get(position).getTime();
            Log.d("holder-selection",aboutlist.get(position).getSender()+" == "+user);
            //  Long a = aboutlist.get(position).getTime();
//            Log.d("checking",dateChange +"!="+ Integer.parseInt(getDates(a)) +"||"+ month +"!= "+Integer.parseInt(getMonth(a)) +"||"+ year +"!="+ Integer.parseInt(getYear(a)));
          /*  if(dateChange == Integer.parseInt(getDates(a)) || month == Integer.parseInt(getMonth(a)) || year == Integer.parseInt(getYear(a)) )
            {
                Log.d("checking","yes "+dateChange +"!="+ Integer.parseInt(getDates(a)) +"||"+ month +"!= "+Integer.parseInt(getMonth(a)) +"||"+ year +"!="+ Integer.parseInt(getYear(a)));
            }
            else {
                Log.d("checking", "No " + dateChange + "!=" + Integer.parseInt(getDates(a)) + "||" + month + "!= " + Integer.parseInt(getMonth(a)) + "||" + year + "!=" + Integer.parseInt(getYear(a)));
                holder.patti.setText("Date Changed");
            }*/
            Log.d("rcview_chk","in work");
            Picasso.with(context).load(photo_main).into(holder.left_user_icon);
            holder.imgleft.setImageDrawable(null);
            holder.imgright.setImageDrawable(null);
            Log.d("holder-selection",""+sender.toString()+" = "+ user);
            if((sender.toString().equals(user)))
            {
                Log.d("holder-selection","left");
                holder.left.setVisibility(View.VISIBLE);
                holder.right.setVisibility(View.INVISIBLE);
                Log.d("holder-selection"," "+aboutlist.get(position).getType());
                Log.d("type_msg",aboutlist.get(position).getType());
                if(type.equals("1"))
                {
                    Log.d("type_msg","load image "+aboutlist.get(position).getMsg());
                    // holder.msg.setText("Loading...");
                    Picasso.with(context).setIndicatorsEnabled(false);
                    Picasso.with(context).load(aboutlist.get(position).getMsg()).resize(250,250).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgleft, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(aboutlist.get(position).getMsg()).resize(250,250).into(holder.imgleft);
                        }
                    });
                    holder.time.setText(getDate((time)));
                    final String finalMsg = msg;
                    holder.imgleft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context,Image_preview.class);
                            i.putExtra("url", finalMsg);
                            context.startActivity(i);

                        }
                    });
                }
                else if(type.equals("2"))
                {
                    try {
                        holder.time.setText(getDate((time)));
                        holder.msg.setBackgroundColor(R.color.grey_200);
                        final String finalMsg1 = msg;
                        holder.msg.setText("Video File");
                        holder.msg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalMsg1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                else if(type.equals("3"))
                {
                    try {
                        holder.time.setText(getDate((time)));
                        holder.msg.setBackgroundColor(R.color.grey_200);
                        final String finalMsg1 = msg;
                        holder.msg.setText("Audio File");
                        holder.msg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalMsg1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }else if(type.equals("4"))
                {
                    try {
                        holder.time.setText(getDate((time)));
                        holder.msg.setBackgroundColor(R.color.grey_200);
                        holder.msg.setText("PDF File");
                        final String finalMsg1 = msg;
                        holder.msg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalMsg1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                else
                {
                    msg=encryption.dcrypt(msg);
                    holder.imgleft.setVisibility(View.GONE);
                    holder.msg.setText(msg);
                    holder.time.setText(getDate((time)));
                    // Log.d("type_msg","msg "+aboutlist.get(position).getMsg());
                }
            }
            else
            {

                holder.right.setVisibility(View.VISIBLE);
                holder.left.setVisibility(View.INVISIBLE);
                Picasso.with(context).load(photo_user).into(holder.right_user_icon);
                if(aboutlist.get(position).getType().equals("1"))
                {
                    //Log.d("type_msg","load image "+aboutlist.get(position).getMsg());
                    //holder.mymsg.setText("LOADING..");
                    Picasso.with(context).setIndicatorsEnabled(true);
                    Picasso.with(context).load(aboutlist.get(position).getMsg()).resize(250,250).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgright, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(aboutlist.get(position).getMsg()).resize(250,250).into(holder.imgleft);
                        }
                    });

                    holder.mytime.setText(getDate((time)));

                    final String finalMsg2 = msg;
                    holder.imgright.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context,Image_preview.class);
                            i.putExtra("url", finalMsg2);
                            context.startActivity(i);

                        }
                    });
                }
                else if(type.equals("2"))
                {
                    try {
                        holder.time.setText(getDate((time)));
                        holder.mymsg.setBackgroundColor(R.color.grey_200);
                        final String finalMsg1 = msg;
                        holder.mymsg.setText("Video File");
                        holder.mymsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalMsg1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                else if(type.equals("3"))
                {
                    try {
                        holder.time.setText(getDate((time)));
                        holder.mymsg.setBackgroundColor(R.color.grey_200);
                        final String finalMsg1 = msg;
                        holder.mymsg.setText("Audio File");
                        holder.mymsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalMsg1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }else if(type.equals("4")) {
                    try {
                        holder.time.setText(getDate((time)));
                        holder.mymsg.setBackgroundColor(R.color.grey_200);
                        holder.mymsg.setText("PDF File");
                        final String finalMsg1 = msg;
                        holder.mymsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(context,aboutlist.get(position).getMsg(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalMsg1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                else
                {
                    msg=encryption.dcrypt(msg);
                    Log.d("type_msg","msg "+aboutlist.get(position).getMsg());
                    holder.imgright.setVisibility(View.GONE);
                    holder.mymsg.setText(msg);
                    holder.mytime.setText(getDate((time)));
                }
                Log.d("holder-selection","right");

            }

           /* dateChange =Integer.parseInt(getDates(a));
            month =Integer.parseInt(getMonth(a));
            year =Integer.parseInt(getYear(a));*/
        }
        private String getDate(long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = DateFormat.format("hh:mm a", cal).toString();
            return date;
        }
        private String getDates(long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = DateFormat.format("dd", cal).toString();
            return date;
        }
        private String getMonth(long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = DateFormat.format("MM", cal).toString();
            return date;
        }
        private String getYear(long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = DateFormat.format("yy", cal).toString();
            return date;
        }

        public Bitmap retriveVideoFrameFromVideo(String videoPath)
                throws Throwable
        {
            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try
            {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new Throwable(
                        "Exception in retriveVideoFrameFromVideo(String videoPath)"
                                + e.getMessage());

            }
            finally
            {
                if (mediaMetadataRetriever != null)
                {
                    mediaMetadataRetriever.release();
                }
            }
            return bitmap;
        }
        @Override
        public int getItemCount() {
            return aboutlist.size();
        }

        @Override
        public void onViewDetachedFromWindow(adapter.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }
        public void change(ArrayList<String> al)
        {
            Intent i = new Intent(ac.getApplication(),Chats.class);
            i.putExtra("userdata",al);
            ac.startActivity(i);
        }
    }



    private void initView(View view) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);

        gallery_btn = (ImageButton) findViewById(R.id.gallery_img_btn);
        video_btn = (ImageButton) findViewById(R.id.video_img_btn);
        audio_btn = (ImageButton) findViewById(R.id.audio_img_btn);

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
            }
        });

        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Chats.this,"hi",Toast.LENGTH_SHORT).show();
                startUploadVideo();
            }
        });
        audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select PDF"),112);
            }
        });

    }

    public void startUploadVideo()
    {

        Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
        mediaChooser.setType("video/*");
        startActivityForResult(mediaChooser,RESULT_LOAD_IMAGE);
    }


    void animate()
    {
        int cx = (mRevealView.getLeft() + mRevealView.getRight())*8/10;
        int cy = mRevealView.getTop()/2;
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        //Below Android LOLIPOP Version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(700);

            SupportAnimator animator_reverse = animator.reverse();

            if (hidden) {
                mRevealView.setVisibility(View.VISIBLE);
                animator.start();
                hidden = false;
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        mRevealView.setVisibility(View.INVISIBLE);
                        hidden = true;

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();
            }
        }
        // Android LOLIPOP And ABOVE Version
        else {
            if (hidden) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                mRevealView.setVisibility(View.VISIBLE);
                anim.start();
                hidden = false;
            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRevealView.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                anim.start();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clip:
                animate();
                // Do whatever you want to do on logout click.
                return true;

            case R.id.info:
               showBottomSheetFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showBottomSheetFragment() {

        FragmentModelBottomSheet fragmentModelBottomSheet = new FragmentModelBottomSheet();
        fragmentModelBottomSheet.show(getSupportFragmentManager(),"bottomsheet fragment");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.info:
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case R.id.gallery_img_btn:
                Toast.makeText(this, "This is my Toast message! for GALLERY",
                        Toast.LENGTH_LONG).show();
                break;

            case R.id.video_img_btn:
                Toast.makeText(this, "This is my Toast message! for VIDEO",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.audio_img_btn:
                Toast.makeText(this, "This is my Toast message! for AUDIO",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void OpenCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Chats.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Chats.this,
                        android.Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(Chats.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CONTACT);
                } else {
                    ActivityCompat.requestPermissions(Chats.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CONTACT);
                }
            }else{
                startCamera();
            }
        }
        else{
            startCamera();
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,100);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            picUri = data.getData();
            if (data.getData() == null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                Log.d("cheking_file", "0");
                upload();
            }
        }
        else if(requestCode ==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!= null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://talkntrace-37543.appspot.com");
            Uri uri= data.getData();

            StorageReference ref = storageRef.child("video/"+new Date().toString()+".mp4");
            UploadTask uploadTask = ref.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    final String msg = taskSnapshot.getDownloadUrl().toString();
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                            Log.d("nums","3");
                            sendMsg(msg,"2");
                        }
                    } ).start();

                }
            });
        }
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!= null)
        {
            filePath = data.getData();
            try {
                gallery_upload =MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            gallery_upload.compress(Bitmap.CompressFormat.JPEG,70,baos);
            byte[] data1 = baos.toByteArray();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://talkntrace-37543.appspot.com");
            StorageReference ref = storageRef.child("gallery/"+new Date().toString()+"image.jpeg");

            UploadTask uploadTask = ref.putBytes(data1);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    final String msg = taskSnapshot.getDownloadUrl().toString();
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                            Log.d("nums","3");
                            sendMsg(msg,"1");
                        }
                    } ).start();

                }
            });
        }
        else if(requestCode == reqCode && resultCode==RESULT_OK && data!=null && data.getData()!= null)
        {
            Uri filePathAudioURi = data.getData();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://talkntrace-37543.appspot.com");
            StorageReference ref = storageRef.child("audio/"+new Date().toString()+"audio.mp3");

            UploadTask uploadTask = ref.putFile(filePathAudioURi);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    final String msg = taskSnapshot.getDownloadUrl().toString();
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                            Log.d("nums","3");
                            sendMsg(msg,"3");
                        }
                    } ).start();

                }
            });
        }
        else if(requestCode == 112 && resultCode==RESULT_OK && data!=null && data.getData()!= null)
        {
            Uri filePathAudioURi = data.getData();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://talkntrace-37543.appspot.com");
            StorageReference ref = storageRef.child("pdf/"+new Date().toString()+".pdf");

            UploadTask uploadTask = ref.putFile(filePathAudioURi);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    final String msg = taskSnapshot.getDownloadUrl().toString();
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                            Log.d("nums","3");
                            sendMsg(msg,"4");
                        }
                    } ).start();

                }
            });
        }


    }

    private void upload() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,10,baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mref = storage.getReferenceFromUrl("gs://talkntrace-37543.appspot.com");
        StorageReference imageref = mref.child("image/"+ new Date().toString()+".png");
        UploadTask uploadtask = imageref.putBytes(data);
        uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final String msg = taskSnapshot.getDownloadUrl().toString();
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        Log.d("nums","3");
                        sendMsg(msg,"1");
                    }
                } ).start();


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
