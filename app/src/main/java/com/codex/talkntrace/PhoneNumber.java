package com.codex.talkntrace;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneNumber extends AppCompatActivity {

    Button phoneButton;
    EditText phoneNumber;
    String phoneNum;
    int total;
    DatabaseReference mDatabaseUser;
    DatabaseReference mDatabaseLocationUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        phoneButton = (Button)findViewById(R.id.phoneButton);
        phoneNumber = (EditText)findViewById(R.id.phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PhoneNumber.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhoneNumber.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        10);

            }
        }

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = phoneNumber.getText().toString();
                phoneNum = clearformat(phoneNum);

                total = getIntent().getExtras().getInt("total");
                Toast.makeText(PhoneNumber.this,total+" phone",Toast.LENGTH_SHORT).show();
                Toast.makeText(PhoneNumber.this,phoneNum+"",Toast.LENGTH_SHORT).show();
                mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
                mDatabaseLocationUser = FirebaseDatabase.getInstance().getReference().child("LocationUser");

                mDatabaseUser.child(total+"").child("mob").setValue(phoneNum+"");
                mDatabaseLocationUser.child(total+"").child("mob").setValue(phoneNum+"");

                Toast.makeText(PhoneNumber.this,"Hi You logged in ",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PhoneNumber.this,Group_password.class);
                startActivity(intent);
                PhoneNumber.this.finish();
            }
        });

    }

    private String clearformat(String phoneNum) {


        int i = 0;

        StringBuilder str = new StringBuilder(phoneNum);
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


        return str.toString().trim();
    }

}

