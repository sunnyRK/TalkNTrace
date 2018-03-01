package com.codex.talkntrace;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

public class fragment_settings extends Fragment {

    CardView mCardView;
    CardView password;
    CardView deleteAcc;
    Button logout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /* View view = getView();
        mCardView = (CardView)view.findViewById(R.id.cardView_emergency);
        mCardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(),Emergency_Contacts.class));
                    }
                }
        );*/
        return inflater.inflate(R.layout.settings_fragment, container, false);


    }

    @Override
    public void onStart() {
        super.onStart();
        mCardView = (CardView)getActivity().findViewById(R.id.cardView_emergency);

        password= (CardView)getActivity().findViewById(R.id.for_pass);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Forgot_Password.class));
            }
        });
        mCardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(),Emergency_Contacts.class));
                    }
                }
        );

        deleteAcc = (CardView)getActivity().findViewById(R.id.cardView_delete);
        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),DeleteAccount.class));
            }
        });
    }

    public static Fragment newInstance(int i) {
        fragment_settings yf = new fragment_settings();
        return yf;
    }
}
