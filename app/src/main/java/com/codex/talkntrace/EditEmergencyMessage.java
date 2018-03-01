package com.codex.talkntrace;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditEmergencyMessage extends AppCompatActivity {

    private ImageView imageView;
    private EditText msg;
    DatabaseReference mDatabaseTrustedContacts;
    private String no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_emergency_message);

        Bundle bundle = getIntent().getExtras();
        no = bundle.getString("num1");

        mDatabaseTrustedContacts = FirebaseDatabase.getInstance().getReference().child("TrustedContacts");
        imageView = (ImageView)findViewById(R.id.imageView_edit);
        msg = (EditText) findViewById(R.id.emergency_msg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();

                mDatabaseTrustedContacts.child(no).child("1").child("msg").setValue(message+"");
                mDatabaseTrustedContacts.child(no).child("2").child("msg").setValue(message+"");
                mDatabaseTrustedContacts.child(no).child("3").child("msg").setValue(message+"");

            }
        });
    }
}
