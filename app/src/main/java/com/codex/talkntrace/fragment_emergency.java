package com.codex.talkntrace;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

public class fragment_emergency extends Fragment {

    private Button Emergency_btn1;
    private Button Emergency_btn2;

    private Button maps;
    DatabaseReference mDatabaseUsers;
    DatabaseReference mDatabaseTrustedContacts;
    DatabaseReference mDatabaseLocationUser;
    private ArrayList<UsersGroup> userDetail = new ArrayList<>();

    private FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    private String useremail;
    private String User_No;
    Handler handler;
    private EditText num1;
    private EditText num2;
    private EditText num3;
    private TextView timer;
    private EditText message;
    private EditText message2;
    private String number1;
    private String number2;
    private String number3;
    private String messages="";
    private String messages2="";
    private String latitude;
    private String longitude;
    private String username;
    private Button call;
    int i=1;
    int j=1;
    public static int f=10,flag_emergency=0;

    private String trustLat1;
    private String trustLon1;
    private String trustLat2;
    private String trustLon2;
    private String trustLat3;
    private String trustLon3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.emergency_fragment,container,false);

        num1 = (EditText)view.findViewById(R.id.num11);
        num2 = (EditText)view.findViewById(R.id.num22);
        num3 = (EditText)view.findViewById(R.id.num33);
        timer = (TextView)view.findViewById(R.id.timer);
        Emergency_btn1 = (Button) view.findViewById(R.id.SendBtnMsg1);
        Emergency_btn2 = (Button) view.findViewById(R.id.SendBtnMsg2);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        maps = (Button) view.findViewById(R.id.nearMap);
        handler = new Handler();
        Log.d("hello","fragment");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_emergency=1;
                f=10;
            }
        });
        flag_emergency=0;
        new CountDownTimer(12000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {

                if(flag_emergency==0)
                {
                    timer.setText(f +"");
                    f--;

                    if(f==0)
                    {
                        number1 = num1.getText().toString();
                        number2 = num2.getText().toString();
                        number3 = num3.getText().toString();
                        messages2 = "";
                        messages2 += username +" needs help Emergency. Location Link:-";
                        messages2 += " http://maps.google.com/maps/place/"+latitude+","+longitude+"/@"+latitude+","+longitude+",17z ";
                        sendMessage(messages2,number1,number2,number3);
                    }
                }

            }
            public void onFinish() {
            }
        }.start();

        call=(Button)view.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_emergency=1;
                f=10;
                number1 = num1.getText().toString();
                number2 = num2.getText().toString();
                number3 = num3.getText().toString();
                Float u1,u2,u3;
                u1=minimization(Float.parseFloat(trustLat1),Float.parseFloat(trustLon1));
                u2=minimization(Float.parseFloat(trustLat2),Float.parseFloat(trustLon2));
                u3=minimization(Float.parseFloat(trustLat3),Float.parseFloat(trustLon3));
                Log.d("distancee",u1+ " " + u2 + " "+u3);
                if(u1<u2 && u1<u3)
                {
                    call(number1);
                }
                else if(u2<u3)
                {
                    call(number2);
                }
                else
                {
                    call(number3);
                }
            }
        });

        Emergency_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_emergency=1;
                f=10;
                number1 = num1.getText().toString();
                number2 = num2.getText().toString();
                number3 = num3.getText().toString();
                messages = "";
                messages += username + " needs help. Location link:-" ;
                messages += " http://maps.google.com/maps/place/"+latitude+","+longitude+"/@"+latitude+","+longitude+",17z ";
                sendMessage(messages,number1,number2,number3);


            }
        });

        Emergency_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_emergency=1;
                f=10;
                number1 = num1.getText().toString();
                number2 = num2.getText().toString();
                number3 = num3.getText().toString();
                messages2 = "";
                messages2 += username +" needs help Emergency. Location Link:-";
                messages2 += " http://maps.google.com/maps/place/"+latitude+","+longitude+"/@"+latitude+","+longitude+",17z ";
                sendMessage(messages2,number1,number2,number3);
            }
        });



        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_emergency=1;
                f=10;
                Intent i = new Intent(getActivity(),Placeslocation.class);
                startActivity(i);

            }
        });
        return view;
    }

    Float minimization(Float lat,Float lon)
    {
        Float res_lat=Math.abs(Float.parseFloat(latitude) - lat);
        Float res_lon=Math.abs(Float.parseFloat(longitude) - lon);
        return Math.abs(res_lat-res_lon);
    }

    void call(String phone_no)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            10);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            10);
                }
            }else{
                startCall(phone_no);
            }
        }
        else{
            startCall(phone_no);
        }
    }

    private void startCall(String phone_no) {
        Log.d("phone",phone_no);
        Intent i= new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+phone_no));
        startActivity(i);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        flag_emergency=0;
//        f=10;
               mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseLocationUser = FirebaseDatabase.getInstance().getReference().child("LocationUser");
        mDatabaseTrustedContacts = FirebaseDatabase.getInstance().getReference().child("TrustedContacts");


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getContext(), Google_Login.class));

                } else {
                    useremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    fetchInfo();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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

                Snackbar.make(getView(), "Messages Sent To trusted Contacts", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }


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


    private void retrieveCoordinate()
    {
        Query query = mDatabaseLocationUser.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {

                    latitude = (String)postsnapshot.child("latitude").getValue();
                    longitude = (String)postsnapshot.child("longitude").getValue();
                }
                retrieveTrustedPerson();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void retrieveTrustedPerson() {
        for(int k=0;k<userDetail.size();k++)
        {
            Query query = mDatabaseLocationUser.orderByChild("email").equalTo(userDetail.get(k).getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (i == 1) {
                            trustLat1 = (String) snapshot.child("latitude").getValue();
                            trustLon1 = (String) snapshot.child("longitude").getValue();
                            i = 2;
                        } else if (i == 2) {
                            trustLat2 = (String) snapshot.child("latitude").getValue();
                            trustLon2 = (String) snapshot.child("longitude").getValue();

                            i = 3;
                        } else if (i == 3) {
                            trustLat3 = (String) snapshot.child("latitude").getValue();
                            trustLon3 = (String) snapshot.child("longitude").getValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

    }

    private void retrieveTrustedContacts() {

        mDatabaseTrustedContacts.child(User_No).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetail.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    UsersGroup user = snapshot.getValue(UsersGroup.class);
                    userDetail.add(user);
                    Log.d("trusted_11",user.getEmail());
                    Log.d("trusted_11",user.getMob());
                    Log.d("trusted_11",user.getName());
                    Log.d("trusted_11",user.getNo());
                    Log.d("trusted_11",user.getPhotourl());

                    if(j==1)
                    {
                        num1.setText(user.getMob());
                        j++;
                    }
                    else if(j==2)
                    {
                        num2.setText(user.getMob());
                        j++;
                    }
                    else if(j==3)
                    {
                        num3.setText(user.getMob());
                    }

                    retrieveCoordinate();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static Fragment newInstance(int i) {
        fragment_emergency yf = new fragment_emergency();
        return yf;
    }
}

