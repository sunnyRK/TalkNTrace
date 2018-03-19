package com.codex.talkntrace;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Rutviz Vyas on 25-03-2017.
 */


public class FirebaseBackgroundService extends Service {

    Handler handler = new Handler();

    @Override
    public void onStart(Intent intent, int startId) {
        startnotifying();
        super.onStart(intent, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void postNotif(String notifString,String sender) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Uri uri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification n  = new Notification.Builder(this)
                .setContentTitle(sender)
                .setContentText(notifString)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSound(uri)
                .setStyle(new Notification.BigTextStyle().bigText("")).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, n);
    }
    public void startnotifying()
    {

/*
        Log.d("sunny","hii");



            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                                for(int i=0;i<10;i++)
                                {
                                    Toast.makeText(getApplication()," sunny " ,Toast.LENGTH_SHORT).show();
                                }


                        }
                    });



                }
            });
            t.start();
*/




       /* final DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference().child("SingleChat");
        Query q = mFirebase.orderByChild("email").equalTo(User_email);
        Log.d("Notificationtnt","singlechat");
        q.addValueEventListener(new ValueEventListener() {
            public String totalmsg;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot data:dataSnapshot.getChildren())
                {
                    User_no = (String)data.child("no").getValue();
                    Total_Users = (String)data.child("total").child("no").getValue();
                    for(int i=1;i<=Integer.parseInt(Total_Users);i++)
                    {
                        Log.d("Notificationtnt","totalmsg");
                        Query q1 = mFirebase.child(User_no).child("chats").child(i+"").child("totalmsg");
                        final int finalI = i;
                        q1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                totalmsg = (String)dataSnapshot.getValue();
                                if(!totalmsg.equals("0")) {
                                    mFirebase.child(User_no).child("chats").child(finalI + "").child("msgs").child("msg" + totalmsg).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String sender = (String) dataSnapshot.child("sender").getValue();
                                            String msg = (String) dataSnapshot.child("msg").getValue();
                                            Log.d("Notificationtnt", sender + " test");
                                            Log.d("Notificationtnt", sender + " = " + User_email);
                                            if (!sender.equals(User_email)) {
                                                if (flag != 0) {
                                                    postNotif(msg, sender);

                                                }
                                                flag = 1;

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}
