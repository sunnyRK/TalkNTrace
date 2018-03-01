package com.codex.talkntrace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMapOptions;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

public class Group_Chats extends AppCompatActivity {

    private DatabaseReference mDatabaseGroupChat;

    RecyclerView recyclerView;
    private static final int PERMISSION_REQUEST_CONTACT = 10;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private int reqCode = 2;
    private static final int CROP_PIC = 121;
    private TextView Name;

    ImageButton camera,Sendbtn;
    private ArrayList<String> al;
    private ArrayList<chatRet> al1;
    TextView contactName, contactNo;
    MenuItem info;
    BottomSheetBehavior behavior;
    NestedScrollView nestedScrollView;
    Toolbar tb;
    String email;
    private Parcelable recycleViewState;



    String username;
    String useremail;

    int flag=0;
    int flag1=0;
    EditText message;

    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton gallery_btn, photo_btn, video_btn, audio_btn, location_btn, contact_btn;
    public Uri picUri;
    private Bitmap bitmap;
    private Bitmap gallery_upload;
    String groupno;
    private String msg;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton back;
    private String main_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chats);

        View view = getCurrentFocus();
        initView(view);
        al = new ArrayList<>();
        al1 = new ArrayList<>();
        Intent i = getIntent();
        al = i.getStringArrayListExtra("userdata");

        //Toast.makeText(this,email,Toast.LENGTH_SHORT).show();

        Name = (TextView) findViewById(R.id.Name);
        Name.setText(al.get(0));

        groupno = al.get(1);
        //Toast.makeText(Group_Chats.this,groupno+"",Toast.LENGTH_SHORT).show();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_Chat_group);
        mDatabaseGroupChat = FirebaseDatabase.getInstance().getReference().child("GroupChat");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent i =new Intent(Group_Chats.this,Google_Login.class);
                    startActivity(i);

                }
                else
                {
                    username = firebaseAuth.getCurrentUser().getDisplayName();
                    useremail = firebaseAuth.getCurrentUser().getEmail();
                    main_photo = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
                }

            }
        };


        message = (EditText)findViewById(R.id.editText2);
        final ImageButton send = (ImageButton) findViewById(R.id.SendBtn);
        final ImageButton emoji = (ImageButton) findViewById(R.id.emoji);
        final ImageButton camera = (ImageButton) findViewById(R.id.CameraBtn);
        //final EmojIconActions emojiIcon=new EmojIconActions(this,view,message,emoji);
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


        back = (ImageButton)findViewById(R.id.backbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCamera();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                msg = message.getText().toString().trim();
                msg = encryption.encrypt(msg);
                sendMsg(msg,"0");



            }
        });

        mDatabaseGroupChat.child(groupno).child("msg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al1.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    chatRet chats = snapshot.getValue(chatRet.class);
                    al1.add(chats);
                }
                setUpRecyclerView(recyclerView);
                recyclerView.setAdapter(new adapter(getApplicationContext(),al1,useremail,bitmap));
                // setUpRecyclerView(recyclerView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void sendMsg(final String msg, final String type)
    {
        final int[] flag = {0};
        final int[] flag1 = {0};
        final int[] msg_no1 = new int[1];
        Query query = mDatabaseGroupChat.orderByChild("groupno").equalTo(groupno);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flag1[0] == 0)
                {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String msg_no = (String) postSnapshot.child("totalmsg").getValue();
                        msg_no1[0] = Integer.parseInt(msg_no);
                        msg_no1[0]++;
                        flag[0] = 1;
                        flag1[0]=1;
                        Toast.makeText(Group_Chats.this, msg_no1[0] + "", Toast.LENGTH_SHORT).show();
                    }
                }

                if(flag[0] == 1)
                {
                    mDatabaseGroupChat.child(groupno).child("msg").child("msg" + msg_no1[0]).child("msg").setValue(msg);
                    mDatabaseGroupChat.child(groupno).child("msg").child("msg" + msg_no1[0]).child("sender").setValue(useremail);
                    //mDatabaseGroupChat.child(groupno).child("msg").child("msg" + msg_no1[0]).child("name").setValue(username);
                    mDatabaseGroupChat.child(groupno).child("msg").child("msg" + msg_no1[0]).child("type").setValue(type);
                    mDatabaseGroupChat.child(groupno).child("msg").child("msg" + msg_no1[0]).child("time").setValue(ServerValue.TIMESTAMP);
                    mDatabaseGroupChat.child(groupno).child("totalmsg").setValue(msg_no1[0] + "");
                    message.setText("");
                    Toast.makeText(Group_Chats.this, "Sent", Toast.LENGTH_SHORT).show();
                    flag[0]++;
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
                Toast.makeText(Group_Chats.this,"hi",Toast.LENGTH_SHORT).show();
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
    public void OpenCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Group_Chats.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Group_Chats.this,
                        android.Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(Group_Chats.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CONTACT);
                } else {
                    ActivityCompat.requestPermissions(Group_Chats.this,
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


    public void startUploadVideo()
    {

        Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
        mediaChooser.setType("video/*");
        startActivityForResult(mediaChooser,RESULT_LOAD_IMAGE);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mref = storage.getReferenceFromUrl("gs://talkntrace-37543.appspot.com");
        StorageReference imageref = mref.child("image/"+ new Date().toString()+".jpeg");
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

            case R.id.location:
                Intent i =new Intent(Group_Chats.this,Maps.class);
                i.putExtra("groupno",groupno);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showBottomSheetFragment() {

        FragmentModelBottomSheet fragmentModelBottomSheet = new FragmentModelBottomSheet();
        fragmentModelBottomSheet.show(getSupportFragmentManager(),"bottomsheet fragment");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
        rv.setAdapter(new adapter(getApplicationContext(),al1,useremail,bitmap) );

    }

    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

        Context context;
        ArrayList<chatRet> aboutlist=new ArrayList<>();
        String user;
        Bitmap bitmap;

        public adapter(Context context, ArrayList<chatRet> aboutlist, String user, Bitmap bitmap) {
            this.context = context;
            this.aboutlist = aboutlist;
            this.user = user;
            this.bitmap = bitmap;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.left_chat_message_view, parent, false);
            return  new adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
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
            Picasso.with(context).load(main_photo).into(holder.left_user_icon);
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
                Picasso.with(context).load("https://lh5.googleusercontent.com/-av6ne-dsi6w/AAAAAAAAAAI/AAAAAAAABuk/TpVRbQYZ5SQ/s96-c/photo.jpg").into(holder.right_user_icon);
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
                        final String finalMsg1 = msg;
                        holder.mymsg.setText("Video File");
                        holder.mymsg.setBackgroundColor(R.color.grey_200);
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
                    Log.d("type_msg","msg "+aboutlist.get(position).getMsg());
                    holder.imgright.setVisibility(View.GONE);
                    holder.mymsg.setText(msg);
                    holder.mytime.setText(getDate((time)));
                }
                Log.d("holder-selection","right");

            }

        }
        private String getDate(long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = DateFormat.format("hh:mm a", cal).toString();
            return date;
        }

        @Override
        public int getItemCount() {
            return aboutlist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView left_user_icon,right_user_icon;
            TextView msg, mymsg,time,mytime,patti;
            ImageView imgleft,imgright;
            RelativeLayout right,left;
            public ViewHolder(View view) {
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
    }
}
