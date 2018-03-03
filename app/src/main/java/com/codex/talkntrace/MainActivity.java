package com.codex.talkntrace;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.codex.talkntrace.R.id.location;


public class MainActivity extends AppCompatActivity implements FragNavController.RootFragmentListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    /*Data members Global*/
    private BottomBar mBottomBar;
    private GoogleApiClient mgoogleapiclient;
    private FragNavController mNavController;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private LocationRequest mLocationrequest;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THREE = FragNavController.TAB3;
    private final int TAB_FOUR = FragNavController.TAB4;
    private final int TAB_FIVE = FragNavController.TAB5;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private String Userno;
    public static ImageButton menubtn;

    /*End of global data members*/

    /*OnCreate method override*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        try{
            firebaseDatabase.setPersistenceEnabled(true);
        }catch (Exception e){}
       /* if(!isMyServiceRunning(FirebaseBackgroundService.class))
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            startService(new Intent(this,FirebaseBackgroundService.class));
        }/*/
        if(mAuth == null && savedInstanceState == null && firebasepersistance.checkVar==0)
        {
            firebasepersistance.checkVar=1;
        }


        final ImageView userpic = (ImageView)findViewById(R.id.userpic);
        final TextView user = (TextView)findViewById(R.id.username);
        menubtn = (ImageButton)findViewById(R.id.menu_main);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        mgoogleapiclient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(MainActivity.this).build();
        mgoogleapiclient.connect();


        /*Check if user exists or not*/
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()== null)
                {
                    startActivity(new Intent(MainActivity.this,Google_Login.class));
                    MainActivity.this.finish();
                }
                else
                {
                    String username = firebaseAuth.getCurrentUser().getDisplayName();
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();
                    Picasso.with(MainActivity.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).resize(50,50).centerCrop().into(userpic);
                    user.setText(username);
                    Query query = mRef.child("users").orderByChild("email").equalTo(userEmail);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postdatasnapshot : dataSnapshot.getChildren()){
                                Userno = (String) postdatasnapshot.child("no").getValue();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            }
        };

        /*List of fragment used in bottombar*/
        List<Fragment> fragments = new ArrayList<>(5);
       /* fragments.add(fragment_chats.newInstance(0));
        fragments.add(fragment_groups.newInstance(0));
        fragments.add(fragment_people.newInstance(0));
        fragments.add(fragment_settings.newInstance(0));*/

        /*initialise*/
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        mBottomBar = (BottomBar)findViewById(R.id.bottomBar);
        mBottomBar.selectTabAtPosition(TAB_THREE);

        /*Controller to control movement of tabs*/
        mNavController = new FragNavController(savedInstanceState,getSupportFragmentManager(),R.id.container,this,5,TAB_SECOND);

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBottomBar.getCurrentTabPosition()==TAB_FIRST)
                {

                }
                else  if(mBottomBar.getCurrentTabPosition()==TAB_SECOND)
                {
                    startActivity(new Intent(MainActivity.this,ReplaceEmergencyContacts.class));
                }
                else  if(mBottomBar.getCurrentTabPosition()==TAB_THREE)
                {
                    Intent i = new Intent(MainActivity.this,SyncContacts.class);
                    i.putExtra("refresh","1");
                    startActivity(i);
                }
                else
                {

                }
            }
        });



        /*select tab to move from one fragment to another*/
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId)
                {
                    case R.id.tab_chats: {
                        mNavController.switchTab(TAB_FIRST);
                        menubtn.setVisibility(View.VISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.blue_new)));
                        break;
                    }

                    case R.id.tab_groups:
                        mNavController.switchTab(TAB_SECOND);
                        menubtn.setVisibility(View.VISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_group_add_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.blue_new)));
                        break;

                    case R.id.tab_People:
                        mNavController.switchTab(TAB_THREE);
                        menubtn.setVisibility(View.VISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.blue_new)));
                        break;

                    case R.id.tab_map:
                        mNavController.switchTab(TAB_FOUR);
                        menubtn.setVisibility(View.INVISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.red_600)));
                        break;

                    case R.id.tab_settings:
                        mNavController.switchTab(TAB_FIVE);
                        menubtn.setVisibility(View.INVISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.blue_new)));
                        break;
                }

            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                mNavController.clearStack();
            }
        });
    }
    /*End of oncreate*/

    boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && Userno != null)
        {
            Double Lat = location.getLatitude();
            Double Log = location.getLongitude();

            mRef.child("LocationUser").child(Userno).child("latitude").setValue(Lat+"");
            mRef.child("LocationUser").child(Userno).child("longitude").setValue(Log+"");

            //android.util.Log.d("latndlon1", Lat+" "+Log);

        }
    }

    @Override
    public void onBackPressed() {
        System.exit(1);
    }

    @Override
    public Fragment getRootFragment(int i) {
        switch (i)
        {
            case TAB_FIRST:
                return fragment_chats.newInstance(0);
            case TAB_SECOND:
                return fragment_groups.newInstance(0);
            case TAB_THREE:
                return fragment_people.newInstance(0);
            case TAB_FOUR:
                return fragment_emergency.newInstance(0);
            case TAB_FIVE:
                return fragment_settings.newInstance(0);
            default:
                return null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationrequest = LocationRequest.create();
        mLocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationrequest.setInterval(500);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleapiclient, mLocationrequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    /*End of OnCreate Method*/

}
