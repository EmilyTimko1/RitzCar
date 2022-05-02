package com.example.ritzcar;

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
    String speedUnit = " MPH";
    double speedMPH;
    double minCarSpeed = 0;
    double maxCarSpeed = 80;
    double minVideoSpeed = 0;
    double maxVideoSpeed = 5;
    double adjustedSpeed;
    MediaPlayer mediaPlayer;
    int videoId = R.raw.ritz;
    String videoPath = "";

    protected LocationManager locationManager;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //10*1 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_third_try);

        videoView = findViewById(R.id.videoView);
        speedTextView = findViewById(R.id.speedTextView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + videoId));
        //videoView.setVideoPath(videoPath);
        videoView.start();

        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        Location location;
        private Location mLastLocation;

        @Override
        public void onLocationChanged(Location pCurrentLocation) {
            if (pCurrentLocation != null) {
                this.location = pCurrentLocation;

                if(speedMPH < 80){
                    speedMPH = 80;
                }
                if (pCurrentLocation.hasSpeed())
                    speedMPH = pCurrentLocation.getSpeed() * 2.23694;
                this.mLastLocation = pCurrentLocation;

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
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public void setVideoSpeed(double speed) {
        PlaybackParams myPlayBackParams = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            myPlayBackParams = new PlaybackParams();
            myPlayBackParams.setSpeed((float) speed); //you can set speed here
            if (mediaPlayer != null)
                mediaPlayer.setPlaybackParams(myPlayBackParams);
        }
    }
}