package com.example.dell.firebaseintro.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dell.firebaseintro.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterStoreMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private GoogleMap mMap;
    private Button submitButton;
    private double lat;
    private double longi;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference databaseReference;
    private FirebaseUser mUser;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store_map);


        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        mUser = mAuth.getCurrentUser();

        progressBar = new ProgressDialog(this);


        submitButton = (Button) findViewById(R.id.submitMapButton);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        lat = 0.0;
        longi = 0.0;

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Create a dialog box

                AlertDialog.Builder alertDialogBuilder;
                final AlertDialog dialog;
                LayoutInflater inflater;

                alertDialogBuilder = new AlertDialog.Builder(RegisterStoreMap.this);

                inflater = LayoutInflater.from(RegisterStoreMap.this);
                final View view = inflater.inflate(R.layout.confirm_dialog, null);

                TextView textAlert = (TextView) view.findViewById(R.id.textAlert);
                Button noButton = (Button) view.findViewById(R.id.noButton);
                Button yesButton = (Button) view.findViewById(R.id.yesButton);

                textAlert.setText("Is this the location of your BOOKSTORE ?");
                alertDialogBuilder.setView(view);
                dialog = alertDialogBuilder.create();
                dialog.show();


                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        Bundle extras = getIntent().getExtras();

                        if(extras!=null)
                        {

                            //Upload to database and update hasbookstore of user to 1

//                            Log.d("Map : ", "Name - " + extras.getString("bName"));
//                            Log.d("Map : ", "Phone - " + extras.getString("bPhone"));
//                            Log.d("Map : ","Lat - " + String.valueOf(lat));
//                            Log.d("Map : ","Longi - " + String.valueOf(longi));
//
                            progressBar.setMessage("Uploading to database...");
                            progressBar.show();

                            String key = mDatabaseReference.push().getKey();

                            Map<String , String> dataTosave = new HashMap<>();

                            dataTosave.put("bookStoreName",extras.getString("bName"));
                            dataTosave.put("bookStorePhone",extras.getString("bPhone"));
                            dataTosave.put("bookStoreID",key);
                            dataTosave.put("userID",mUser.getUid());
                            dataTosave.put("latitude",String.valueOf(lat));
                            dataTosave.put("longitude",String.valueOf(longi));


                            mDatabaseReference.child(key).setValue(dataTosave).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {


                                    progressBar.dismiss();

                                    Snackbar.make(v,e.getMessage(),Snackbar.LENGTH_SHORT).show();

                                    startActivity(new Intent(RegisterStoreMap.this,PostListActivity.class));
                                    finish();

                                }

                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressBar.dismiss();

                                    DatabaseReference currentdb = databaseReference.child(mUser.getUid());
                                    currentdb.child("hasBookstore").setValue("1");

                                    startActivity(new Intent(RegisterStoreMap.this,MyBookStoreActivity.class));
                                    finish();

                                }
                            });





                        }

                    }
                });
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set Map type (if you need , not necessary though)
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng dhapasi = new LatLng(27.741797, 85.326927);


        mMap.setOnMapClickListener(this);

        // Add a marker in Dhapasi and move the camera

        mMap.addMarker(new MarkerOptions().position(dhapasi).title("Your Bookstore Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));    //set icon and color to the marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhapasi,13));     // zoom float value 1 -20, 20 being max zoomed in

    }

    @Override
    public void onMapClick(LatLng latLng) {


        mMap.clear();

        // Add a marker and move the camera

        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Bookstore Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));    //set icon and color to the marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));     // zoom float value 1 -20, 20 being max zoomed in

        lat = latLng.latitude;
        longi = latLng.longitude;

    }


}
