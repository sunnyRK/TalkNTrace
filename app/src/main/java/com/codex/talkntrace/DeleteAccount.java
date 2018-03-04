package com.codex.talkntrace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class DeleteAccount extends AppCompatActivity {

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DeleteAccount.this,MainActivity.class));
    }
}
