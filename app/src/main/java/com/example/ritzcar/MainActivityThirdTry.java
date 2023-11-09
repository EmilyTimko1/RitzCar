package com.example.ritzcar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivityThirdTry extends AppCompatActivity {
    VideoView videoView;
    TextView speedTextView;
    TextView locationTextView;
    String speedUnit = " MPH";
    double speedMPH;
    double minCarSpeed = 0;
    double maxCarSpeed = 80;
    double minVideoSpeed = 0;
    double maxVideoSpeed = 3;
    double adjustedSpeed;
    MediaPlayer mediaPlayer;
    int videoId = R.raw.ritz;
    String videoPath = "";

    protected LocationManager locationManager;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //10*1 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100; // .1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_third_try);

        videoView = findViewById(R.id.videoView);
        speedTextView = findViewById(R.id.speedTextView);
        locationTextView = findViewById(R.id.locationTextView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + videoId));
        //videoView.setVideoPath(videoPath);
        videoView.start();

        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mediaPlayer = mp;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                // If request is cancelled, the result arrays are empty.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("You have to allow precise location for this to work.");
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
        }

    LocationListener locationListener = new LocationListener() {
        Location location;
        @Override
        public void onLocationChanged(Location pCurrentLocation) {
            if (pCurrentLocation != null) {
                this.location = pCurrentLocation;

                if(speedMPH > 80){
                    speedMPH = 80;
                }
                if (pCurrentLocation.hasSpeed())
                    speedMPH = pCurrentLocation.getSpeed() * 2.23694;

                //Maps min/max car speed to min/max video speed
                //https://stackoverflow.com/questions/345187/math-mapping-numbers/345204#345204
                adjustedSpeed = (speedMPH-minCarSpeed)/(maxCarSpeed-minCarSpeed) * (maxVideoSpeed-minVideoSpeed) + minVideoSpeed;
                if(adjustedSpeed < 0.1){
                    adjustedSpeed = 0;
                }
                setVideoSpeed(adjustedSpeed);

                if(speedTextView != null){
                    speedTextView.setText(Double.toString(Math.floor(speedMPH*100)/100) + speedUnit);
                }
                /*if(locationTextView != null){
                    locationTextView.setVisibility(View.VISIBLE);
                    locationTextView.setText(pCurrentLocation.toString());
                }*/
            }
        }
    };

    public void setVideoSpeed(double speed) {
        PlaybackParams myPlayBackParams;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            myPlayBackParams = new PlaybackParams();
            myPlayBackParams.setSpeed((float) speed); //you can set speed here
            if (mediaPlayer != null)
                mediaPlayer.setPlaybackParams(myPlayBackParams);
        }
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
}