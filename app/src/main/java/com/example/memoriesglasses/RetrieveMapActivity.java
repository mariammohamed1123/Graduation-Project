package com.example.memoriesglasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.List;
import java.util.Locale;



public class RetrieveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static String latitude;
    public static String longitude;
    public static Double latHome;
    public static Double lngHome;
    public static Double lat;
    public static Double lng;
    public static Double change;
    public static LatLng location;
    public static LatLng locationHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_one");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               latitude = dataSnapshot.child("location").child("lat_updated").getValue().toString();
                longitude = dataSnapshot.child("location").child("lng_updated").getValue().toString();
              latHome = dataSnapshot.child("Home").child("latitude").getValue(Double.class);
              lngHome = dataSnapshot.child("Home").child("longitude").getValue(Double.class);
             //   change = dataSnapshot.child("change").getValue(Double.class);
              //  Log.d("TAG", "Value is: " + lat);




                 lat = Double.parseDouble(latitude);
                 lng = Double.parseDouble(longitude);




                 location = new LatLng(lat, lng);
                 locationHome = new LatLng(latHome,lngHome);


                mMap.addMarker(new MarkerOptions().position(location).title(getCOmpleteAddress(lat,lng)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14F));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("user_one").child("location");

        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               // Toast.makeText(RetrieveMapActivity.this, " here", Toast.LENGTH_SHORT).show();

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toast.makeText(RetrieveMapActivity.this, "the patient is not here", Toast.LENGTH_SHORT).show();

              // Double distance =  distancebetween(latHome,lngHome,lat,lng);
                Double distance = getDistanceFromLatLonInKm(latHome,lngHome,lat,lng);
               if (distance >= 0.5 ){
                  // Toast.makeText(RetrieveMapActivity.this, "The distance equal"+distance, Toast.LENGTH_SHORT).show();
                   notificationDialog();
               }
              else {
                  // Toast.makeText(RetrieveMapActivity.this, "distance less than 500", Toast.LENGTH_SHORT).show();
                   //Toast.makeText(RetrieveMapActivity.this, "The distance equal"+distance, Toast.LENGTH_SHORT).show();
               }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(RetrieveMapActivity.this, "the patient  not here", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private String getCOmpleteAddress(double Latitude , double Longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(RetrieveMapActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
            if (address != null) {
                Address returnAddress = addresses.get(0);
                StringBuilder StringBuilderReturnAddress = new StringBuilder("");
                for (int i = 0; i <= returnAddress.getMaxAddressLineIndex(); i++) {
                    StringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }

                address = StringBuilderReturnAddress.toString();

            } else {
                Toast.makeText(this, "Address Not Found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


        return address;
    }

    private double distancebetween(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



  //  public static Double distanceBetween(LatLng location, LatLng locationHome) {
    //    location = new LatLng(lat, lng);
      //    locationHome = new LatLng(latHome,lngHome);


        //if (location == null || locationHome == null) {
          //  return null;
        //}
      //  return SphericalUtil.computeDistanceBetween(location, locationHome);
   // }









        private void notification() {
       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            NotificationChannel channel =
                    new NotificationChannel("n","n",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        */


        String message = "This a Message Example";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                RetrieveMapActivity.this
        )
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("New Notification")
                .setContentText("Patient is not here")
                .setAutoCancel(true);
        Intent intent = new Intent(RetrieveMapActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(RetrieveMapActivity.this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }


             //   .setContentText("Code Sphere")
                //.setSmallIcon(R.drawable.ic_not)
               // .setAutoCancel(true)
                //.setContentText("Patient is not here");
        //NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        //managerCompat.notify(999,builder.build());

   // }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationDialog() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("New Notification")
                .setContentText("The Patient is not Here , "   + getCOmpleteAddress(lat,lng))
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());
    }

    Double getDistanceFromLatLonInKm(Double lat1,Double lon1,Double lat2,Double lon2) {
        Double R = 6371.0; // Radius of the earth in km
        Double dLat = deg2rad(lat2-lat1);  // deg2rad below
        Double dLon = deg2rad(lon2-lon1);
        Double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c; // Distance in km
        return d;
    }

    Double deg2rad(Double deg) {
        return deg * (Math.PI/180);
    }





}



