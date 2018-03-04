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
    DatabaseReference mDatabase;
    int single_chat_numint;
    int num1;
    int PERMISSION_REQUEST_CONTACT = 10;
    private ArrayList<Users> alContacts = new ArrayList<>();
    private ArrayList<Users> CommonContacts = new ArrayList<>();
    private ArrayList<Users> refreshContact = new ArrayList<>();
    private ArrayList<String> tempContact = new ArrayList<>();
    private ArrayList<String> refreshContactString = new ArrayList<>();
    String Cemail;
    String Uemail;
    final int[] ref_flag = {0};
    int[] temp_flag = {0};
    int[] conflag = {0};
    int nums;
    int flag=0;
    String refresh_flag="0";
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    final int[] no = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        setContentView(R.layout.activity_sync_contacts);
        mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        refresh_flag = getIntent().getExtras().getString("refresh");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Uemail = firebaseAuth.getCurrentUser().getEmail();

                Query query = mDatabase.orderByChild("email").equalTo(Uemail);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            String no = (String) postSnapshot.child("no").getValue();
                            nums = Integer.parseInt(no);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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
        progressDialog = ProgressDialog.show(this, "Please wait", "Loading your Contacts...", true);
        progressDialog.show();
        new Thread(new Runnable(){

            @Override
            public void run() {
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
        }
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
        final int[] flag = {0};
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
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
                    if(refresh_flag.equals("1"))
                    {
                        Query q = mDatabaseContacts.child(nums+"").child(nums+"");
                        q.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               if(temp_flag[0]==0) {
                                   temp_flag[0] =1;
                                   tempContact.clear();
                                   refreshContact.clear();
                                   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                       String email = (String) snapshot.child("email").getValue();
                                       tempContact.add(email);
                                   }
                                   refreshContactString = commoncontactstring();
                                   for (String mail : refreshContactString) {
                                       for (Users user:CommonContacts) {
                                           if (user.getEmail().equals(mail)) {
                                               refreshContact.add(user);
                                           }
                                       }
                                   }
                                   if (flag[0] == 0) {
                                       writeData();
                                       flag[0] = 1;
                                   }
                               }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                    else
                    {
                        donesync();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public ArrayList<String> commoncontactstring() {
        ArrayList<String> s = new ArrayList<>();
        for (Users user:CommonContacts)
        {
            s.add(user.getEmail());
        }
        s.removeAll(tempContact);
        return s;
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
                Toast.makeText(SyncContacts.this,"Failed Done Sync",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void writeData()
    {
        progressDialog = new ProgressDialog(this);
        mDatabaseContacts = FirebaseDatabase.getInstance().getReference().child("contacts");
        mDatabaseContacts.keepSynced(true);
        int i=1;
        if(refresh_flag.equals("0"))
        {
            mDatabaseContacts.child("no").child("1").child("no").setValue(num1+"");
            for(Users user: CommonContacts)
            {
                mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("name").setValue(user.getName());
                mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("mob").setValue(user.getMob());
                mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("email").setValue(user.getEmail());
                mDatabaseContacts.child(num1+"").child(num1+"").child(i+"").child("no").setValue(i+"");
                mDatabaseContacts.child(num1+"").child("total").setValue(i+"");
                i++;
            }
        }
        else if(refresh_flag.equals("1"))
        {
            Query q = mDatabaseContacts.child(nums+"");
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   if(conflag[0] == 0) {
                       conflag[0] = 1;
                       int contact_total_int = 0;
                       String contact_total = (String) dataSnapshot.child("total").getValue();
                       contact_total_int = Integer.parseInt(contact_total);
                       contact_total_int++;
                       for (Users user : refreshContact) {
                           mDatabaseContacts.child(nums + "").child(nums + "").child(contact_total_int + "").child("name").setValue(user.getName());
                           mDatabaseContacts.child(nums + "").child(nums + "").child(contact_total_int + "").child("mob").setValue(user.getMob());
                           mDatabaseContacts.child(nums + "").child(nums + "").child(contact_total_int + "").child("email").setValue(user.getEmail());
                           mDatabaseContacts.child(nums + "").child(nums + "").child(contact_total_int + "").child("no").setValue(contact_total_int + "");
                           mDatabaseContacts.child(nums + "").child("total").setValue(contact_total_int + "");
                           contact_total_int++;
                       }
                   }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        writeDataIntoSingleChat();
    }

    public void writeDataIntoSingleChat()
    {
        mDatabaseSingleChat = FirebaseDatabase.getInstance().getReference().child("SingleChat");
        mDatabaseSingleChat.keepSynced(true);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.keepSynced(true);
        final int[] i = {1};
        int flag=0;
        final int[] no1 = new int[1];

        Query query = mDatabase.orderByChild("email").equalTo(Uemail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    String no = (String) postSnapshot.child("no").getValue();
                    no1[0] = Integer.parseInt(no);
                }
                if(refresh_flag.equals("0"))
                {
                    for(Users user:CommonContacts)
                    {
                        mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("email").setValue(user.getEmail());
                        mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("name").setValue(user.getName());
                        mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("totalmsg").setValue(0+"");
                        mDatabaseSingleChat.child(no1[0] + "").child("chats").child(i[0] +"").child("no").setValue(i[0] +"");
                        mDatabaseSingleChat.child(no1[0] + "").child("total").child("no").setValue(i[0] +"");
                        i[0]++;
                    }
                    mDatabaseSingleChat.child("total").child("no").child(no1[0]+"");
                    progressDialog.dismiss();
                    Intent intent = new Intent(SyncContacts.this,EmergencyContactSelection.class);
                    startActivity(intent);
                    SyncContacts.this.finish();

                }
                else if(refresh_flag.equals("1"))
                {

                    Query q = mDatabaseSingleChat.child("7").child("total");
                    q.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                String single_chat_num = (String) dataSnapshot.child("no").getValue();
                           // Log.d("hey1", String.valueOf(refreshContact.size()));
                            single_chat_numint = Integer.parseInt(single_chat_num);
                            single_chat_numint++;

                            if(ref_flag[0] ==0)
                            {
                                ref_flag[0] =1;
                                for(Users user:refreshContact)
                                {
                                    Log.d("hi123", String.valueOf(refreshContact.size()));
                                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(single_chat_numint +"").child("email").setValue(user.getEmail());
                                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(single_chat_numint +"").child("name").setValue(user.getName());
                                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(single_chat_numint +"").child("totalmsg").setValue(0+"");
                                    mDatabaseSingleChat.child(no1[0] + "").child("chats").child(single_chat_numint +"").child("no").setValue(single_chat_numint +"");
                                    mDatabaseSingleChat.child(no1[0] + "").child("total").child("no").setValue(single_chat_numint +"");
                                    single_chat_numint++;

                                }
                                progressDialog.dismiss();
                                Intent intent = new Intent(SyncContacts.this,MainActivity.class);
                                startActivity(intent);
                                SyncContacts.this.finish();

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
                Toast.makeText(SyncContacts.this,"Failed to Retrieve Chat",Toast.LENGTH_SHORT).show();
            }
        });
    }
}