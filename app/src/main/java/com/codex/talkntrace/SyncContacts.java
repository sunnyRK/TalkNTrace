package com.codex.talkntrace;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class SyncContacts extends AppCompatActivity {


    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabaseContacts;
    private DatabaseReference mDatabaseSingleChat;

    int num1;
    int PERMISSION_REQUEST_CONTACT = 10;
    private ArrayList<Users> alContacts = new ArrayList<>();
    private ArrayList<Users> CommonContacts = new ArrayList<>();
    String Cemail;
    String Uemail;
    private ProgressDialog mProgress;

    int flag=0;
    int flag1 = 0;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    final int[] no = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        setContentView(R.layout.activity_sync_contacts);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("nums","1");
                Uemail = firebaseAuth.getCurrentUser().getEmail();
                Log.d("nums",Uemail);
                // Toast.makeText(SyncContacts.this,Uemail + "ok",Toast.LENGTH_SHORT).show();
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SyncContacts.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SyncContacts.this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_CONTACT);

            }else{
                startAsync();
            }
        }
        else{
            startAsync();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("permission_chk","REached here");
        Log.d("permission_chk",""+requestCode+" "+ PERMISSION_REQUEST_CONTACT);
        Log.d("permission_chk","grant " + grantResults[0]);
        if(requestCode == PERMISSION_REQUEST_CONTACT && grantResults[0] == 0)
        {
            Log.d("permission_chk","done");
            startAsync();
        }
    }

    public void startAsync()
    {


        progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Loading your Contacts...");

        progressDialog = ProgressDialog.show(this, "Please wait",
                "Loading your Contacts...", true);

        progressDialog.show();
        //Toast.makeText(SyncContacts.this,"Loading contacts",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable(){

            @Override
            public void run() {
                Log.d("nums","3");
                getContacts();
            }
        } ).start();

    }

    public void getContacts()
    {
        Context mContext = getApplicationContext();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        alContacts.clear();
        Log.d("nums","4");
        if(cursor.moveToFirst())
        {
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        final String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        final String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        Users user = new Users(contactName,contactNumber,"empty","1");
                        alContacts.add(user);
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;
            Log.d("nums","5");
        }
        Log.d("nums","6");
        checkMembers();
    }

    public void checkMembers()
    {
        clearformat();
        int last = alContacts.size();
        for (Users user: alContacts)
        {

            getdata(user.getMob(),user.getName(),last);
            last--;
        }
    }

    public void getdata(final String contactNumber, final String contactName, final int last)
    {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.keepSynced(true);
        Query query = mDatabase.orderByChild("mob").equalTo(contactNumber);

        final String[] Value = new String[1];
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Value[0] = (String) postSnapshot.child("name").getValue();
                    Cemail = (String) postSnapshot.child("email").getValue();

                    if(Value[0]!=null)
                    {

                        int flag=0;
                        for (Users user:CommonContacts)
                        {

                            if(user.getMob().equals(contactNumber))
                            {

                                flag=1;
                            }
                        }
                        if(flag==0)
                        {

                            Users user = new Users(contactName,contactNumber,Cemail,"1");
                            CommonContacts.add(user);
                        }
                    }

                }
                if(last==1)
                {

                    donesync();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void clearformat() {
        int index=0;
        for(Users user : alContacts) {
            int i = 0;
            Log.d("numberChk", "number : " + user.getMob());
            StringBuilder str = new StringBuilder(user.getMob());
            if (str.charAt(0) == '+') {
                str.delete(0, 3);
            }
            char[] temp = str.toString().trim().toCharArray();
            str = new StringBuilder(str.toString().trim());
            // Log.d("numberChk","temp string "+str.toString().trim());
            for (char c : temp) {
                if (c == ' ' || c == '-') {
                    str.deleteCharAt(i);
                    // Log.d("numberChk","deleting at "+i+" remaining "+ str.toString()+" with c "+c);
                    i--;
                }
                i++;
            }
            Log.d("numberChk", "number formated :" + str.toString().trim());
            alContacts.get(index).setMob(str.toString().trim());
            index++;
        }
    }


    private void donesync() {

        progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Syncing your Contacts...");

        progressDialog = ProgressDialog.show(this, "Please wait",
                "Syncing your Contacts...", true);
        progressDialog.show();
        final int[] flag = {0};
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("contacts").child("no").child("1").child("no");
        mDatabase2.keepSynced(true);
        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                String num = dataSnapshot.getValue(String.class).toString();
                num1 = Integer.parseInt(num);
                num1++;
                if(flag[0] ==0)
                {

                    writeData();
                    flag[0] =1;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("nums","failed sync");
                Toast.makeText(SyncContacts.this,"Failed Done Sync",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void writeData()
    {
        Log.d("nums","14");
        progressDialog = new ProgressDialog(this);
        mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts");
        mDatabaseContacts.keepSynced(true);
        int i=1;
        mDatabaseContacts.child("no").child("1").child("no").setValue(num1+"");
        for(Users user: CommonContacts)
        {
            mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("name").setValue(user.getName());
            mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("mob").setValue(user.getMob());
            mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("email").setValue(user.getEmail());
            mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("no").setValue(i+"");
            mDatabaseContacts.child(num1+"").child("total").setValue(i+"");
            i++;
            Log.d("nums","15");

        }
        Log.d("nums","16");
        writeDataIntoSingleChat();
    }

    public void writeDataIntoSingleChat()
    {
        Log.d("nums","17");
        mDatabaseSingleChat = FirebaseDatabase.getInstance().getReference().child("SingleChat");
        mDatabaseSingleChat.keepSynced(true);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.keepSynced(true);
        Log.d("nums","18");
        final int[] i = {1};
        int flag=0;
        final int[] no1 = new int[1];

        Query query = mDatabase.orderByChild("email").equalTo(Uemail);
        Log.d("nums","19");
        Log.d("nums",Uemail+" uwmail");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Log.d("nums","20");
                    String no = (String) postSnapshot.child("no").getValue();
                    no1[0] = Integer.parseInt(no);
                }
                for(Users user:CommonContacts)
                {
                    Log.d("nums","21");
                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("email").setValue(user.getEmail());
                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("name").setValue(user.getName());
                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("totalmsg").setValue(0+"");
                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("no").setValue(i[0] +"");
                    mDatabaseSingleChat.child(no1[0] + "").child("total").child("no").setValue(i[0] +"");
                    i[0]++;
                }
                mDatabaseSingleChat.child("total").child("no").child(no1[0]+"");
                progressDialog.dismiss();
                Log.d("nums","22");
                Intent intent = new Intent(SyncContacts.this,EmergencyContactSelection.class);
                //Intent intent = new Intent(SyncContacts.this,MainActivity.class);
                startActivity(intent);
               SyncContacts.this.finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("nums","failed to write");
                Toast.makeText(SyncContacts.this,"Failed to Retrieve Chat",Toast.LENGTH_SHORT).show();

            }
        });
    }
}