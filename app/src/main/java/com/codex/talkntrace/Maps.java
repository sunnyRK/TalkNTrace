package com.codex.talkntrace;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import module.DirectionFinder;
import module.DirectionFinderListener;
import module.Route;


public class Maps extends AppCompatActivity implements OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,DirectionFinderListener {
    GoogleMap mgoogleMap;

    GoogleApiClient mgoogleapiclient;
    Query query;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mauth;
    FirebaseAuth.AuthStateListener mAuthListner;

    Bitmap image = null;
    String useremail;
    String user_no;
    LocationRequest mLocationrequest;
    com.github.clans.fab.FloatingActionButton Normal,back;
    FloatingActionMenu Allmarker;
    com.github.clans.fab.FloatingActionButton Allmarker2;
    com.github.clans.fab.FloatingActionButton Allmarker3;
    com.github.clans.fab.FloatingActionButton Satellite;
    com.github.clans.fab.FloatingActionButton Terran;
    com.github.clans.fab.FloatingActionButton chatting;
    //FloatingActionButton Hybrid;
    BottomSheetBehavior behavior;
    NestedScrollView nestedScrollView;
    TextView ContactName;
    TextView ContactNo;
    ImageView ContactImg;
    private String groupno;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    LatLngBounds bounds;
    Target target;
    android.os.Handler handler;
    private ArrayList<String> al=new ArrayList<>();
    String groupnum;


    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        al  = i.getStringArrayListExtra("userdata");

        Log.d("intentpass",al.get(1));
         groupnum = al.get(1);

        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_maps);
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            initmap();
        } else {
            Toast.makeText(this, "Can't load map", Toast.LENGTH_LONG).show();
        }
        ContactName= (TextView)findViewById(R.id.contact_name);
        ContactNo= (TextView)findViewById(R.id.contact_no);
        ContactImg= (ImageView)findViewById(R.id.contact_img);
        Normal = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.Normal);
        back = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_back);
        Satellite = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.Satellite);
        Terran = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.Terran);
        Allmarker = (com.github.clans.fab.FloatingActionMenu)findViewById(R.id.fab_current);
        Allmarker2 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.ZoomView);
        Allmarker3 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.mPoints);
        chatting= (com.github.clans.fab.FloatingActionButton)findViewById(R.id.chat_fab);
        chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Maps.this,Group_Chats.class);
                i.putExtra("userdata",al);
                startActivity(i);
            }
        });



        //user_location = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_full_view);
        //Hybrid = (FloatingActionButton)findViewById(R.id.Hybrid);
        nestedScrollView = (NestedScrollView)findViewById(R.id.view1);
        behavior = BottomSheetBehavior.from(nestedScrollView);
        Normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        Satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        Terran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        Allmarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,25,25,5);
                mgoogleMap.animateCamera(cameraUpdate);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Maps.this.finish();
            }
        });
      /*  user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] f = {0};
                mRef.child("LocationUser").child(user_no).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(f[0] ==0) {
                            Double user_Latitude = Double.parseDouble((String) dataSnapshot.child("latitude").getValue());
                            Double user_Longitude = Double.parseDouble((String) dataSnapshot.child("longitude").getValue());
                            LatLng latlng = new LatLng(user_Latitude, user_Longitude);
                            CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latlng,15);
                            mgoogleMap.animateCamera(cameraUpdate1);
                            f[0] =1;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
*/

        mauth = FirebaseAuth.getInstance();
        mAuthListner= new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(Maps.this,Google_Login.class));
                }
                else
                {
                    useremail = firebaseAuth.getCurrentUser().getEmail();
                    Query getuserno = mRef.child("LocationUser").orderByChild("email").equalTo(useremail);
                    getuserno.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postdatasnapshot:dataSnapshot.getChildren())
                                user_no = (String)postdatasnapshot.child("no").getValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        };


    }

    private void initmap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't Connect to Play Services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mgoogleMap = googleMap;
        mgoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                DatabaseReference mref2 = mRef.child("GroupChat").child(groupnum).child("terrorspot").push();
                mref2.child("lat").setValue(latLng.latitude+"");
                mref2.child("long").setValue(latLng.longitude+"");
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mgoogleapiclient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mgoogleapiclient.connect();
        mgoogleMap.setMyLocationEnabled(true);


        final Double[] Latitude = new Double[1];
        final Double[] Longitude = new Double[1];
        final String[] name = new String[1];
        final String[] email = new String[1];
        final DatabaseReference mRef1 = mRef.child("LocationUser");

        final String[] emailid = new String[1];
        final String[] flag = new String[1];
        flag[0] = "0";
        Query grpquery = mRef.child("GroupChat").child(groupnum).child("members").orderByChild("email");
        grpquery.addValueEventListener(new ValueEventListener() {
            //LatLngBounds.Builder builder = new LatLngBounds.Builder();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    emailid[0] = (String) Snapshot.child("email").getValue();
                    Query query = mRef1.orderByChild("email").equalTo(emailid[0]);
                    Log.d("emailid123", emailid[0]);

                    query.addValueEventListener(new ValueEventListener() {
                        Marker marker = null;
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (marker != null && flag[0] != "0" ) {
                                marker.remove();
                            }

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                Latitude[0] = Double.parseDouble((String) postSnapshot.child("latitude").getValue());
                                name[0] = (String) postSnapshot.child("name").getValue();
                                email[0] = (String) postSnapshot.child("email").getValue();
                                Longitude[0] = Double.parseDouble((String) postSnapshot.child("longitude").getValue());
                                final LatLng latLng = new LatLng(Latitude[0], Longitude[0]);
                                final String photourl = (String) postSnapshot.child("photourl").getValue();
                                Log.d("name123",name[0]);
                                URL url = null;
                                HttpURLConnection urlConnection = null;
                                try {
                                    url = new URL(photourl);
                                    urlConnection = (HttpURLConnection) url.openConnection();
                                    urlConnection.connect();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                                try {
                                    image = BitmapFactory.decodeStream(urlConnection.getInputStream());
                                    urlConnection.disconnect();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                image = getCroppedBitmap(image);




                                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,25,25,5);
                                //mgoogleMap.animateCamera(cameraUpdate);
                                //     Picasso.with(Maps.this).load(photourl).into(temp);
                                //   image = ((BitmapDrawable) temp.getDrawable()).getBitmap();
                                MarkerOptions M1 = new MarkerOptions().position(latLng).title(email[0]).icon(BitmapDescriptorFactory.fromBitmap(image)).anchor(0.5f,1);
                                mgoogleMap.setOnMarkerClickListener(Maps.this);
                                marker = mgoogleMap.addMarker(M1);
                                Boundsupdate(groupnum);
//                                builder.include(latLng);
                                //                              bounds = builder.build();
                            }
                        }

                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        flag[0] = "1";

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

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && user_no != null)
        {
            Double Lat = location.getLatitude();
            Double Log = location.getLongitude();

            mRef.child("LocationUser").child(user_no).child("latitude").setValue(Lat+"");
            mRef.child("LocationUser").child(user_no).child("longitude").setValue(Log+"");
            //android.util.Log.d("latndlon1", Lat+" "+Log);

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Integer[] Flag = {0};
        final String[] Latitude = new String[1];
        final String[] user_Latitude = new String[1];
        final String[] Longitude = new String[1];
        final String[] user_Longitude = new String[1];
        final String[] Origin = new String[1];
        final String[] Destination = new String[1];
        Query Marker = mRef.child("LocationUser").orderByChild("email").equalTo(marker.getTitle());
        Marker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (Flag[0] == 0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        ContactName.setText((String) postSnapshot.child("name").getValue());
                        //    ContactNo.setText((String) postSnapshot.child("mob").getValue());
                        Latitude[0] = (String) postSnapshot.child("latitude").getValue();
                        Longitude[0] = (String) postSnapshot.child("longitude").getValue();
                        Picasso.with(getApplication()).load((String) postSnapshot.child("photourl").getValue()).resize(50, 50).centerCrop().into(ContactImg);
                        Destination[0] = Latitude[0] + ","+ Longitude[0];
                    }
                    Flag[0] = 1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("LocationUser").child(user_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user_Latitude[0] = (String)dataSnapshot.child("latitude").getValue();
                user_Longitude[0] = (String)dataSnapshot.child("longitude").getValue();
                Origin[0] = user_Latitude[0]+","+ user_Longitude[0];
                if (Flag[0]==1){
                    try {
                        new DirectionFinder(Maps.this, Origin[0], Destination[0]).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return true;
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            //mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //    ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //  ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            ContactNo.setText(route.duration.text+" "+route.distance.text);
           /* originMarkers.add(mgoogleMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mgoogleMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));*/

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mgoogleMap.addPolyline(polylineOptions));
        }
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private void Boundsupdate(String groupno ){
        final String[] flag = new String[1];
        final String[] flag1 = new String[1];
        final Double[] Latitude = new Double[1];
        final Double[] Longitude = new Double[1];
        flag[0] = "0";
        flag1[0] = "0";
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Query grpquery = mRef.child("GroupChat").child(groupno).child("members").orderByChild("email");
        grpquery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(flag[0]== "0") {
                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                        Query query = mRef.child("LocationUser").orderByChild("email").equalTo((String) Snapshot.child("email").getValue());
                        Log.d("emailid12345", String.valueOf(query));
                        flag1[0] = "0";
                        query.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //  if(flag1[0] == "0") {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    Latitude[0] = Double.parseDouble((String) postSnapshot.child("latitude").getValue());
                                    Longitude[0] = Double.parseDouble((String) postSnapshot.child("longitude").getValue());
                                    final LatLng latLng = new LatLng(Latitude[0], Longitude[0]);
                                    Log.d("latlng12345", String.valueOf(latLng));
                                    builder.include(latLng);
                                    bounds = builder.build();


                                }

                                //  flag1[0] = "1";
                                //}
                            }

                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    flag[0] ="1";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }
}
