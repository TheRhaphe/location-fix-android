package com.example.driverfix;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.LocationSource;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationSource.OnLocationChangedListener, LocationListener {
    private int LOCATION_PERMISSION_REQUEST_CODE = 989;
    private LocationManager locationManager;
    private Location lastLocation;
    private float distanceInMetres;
    private TextView distanceText, distanceLogsText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Location Fix");
        }

        distanceText = findViewById(R.id.textView);
        distanceLogsText = findViewById(R.id.textView2);
        sharedPreferences = getSharedPreferences("distance_logs", 0);
        editor = sharedPreferences.edit();

        checkPermissions();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.button).setOnClickListener(v->{
            editor.putFloat("distance", 0);
            editor.commit();
            lastLocation = null;
            distanceText.setText("0m");
            distanceLogsText.setText("");
            Toast.makeText(this, "Distance reset", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "Location: ("+location.getLatitude()+", "+location.getLongitude()+")", Toast.LENGTH_SHORT).show();
        if (lastLocation == null) {
            lastLocation = location;
        }
        distanceInMetres = sharedPreferences.getFloat("distance", 0);
        distanceInMetres += location.distanceTo(lastLocation);
        editor.putFloat("distance", distanceInMetres);
        editor.commit();
        distanceLogsText.setText(distanceLogsText.getText()+"\n"+location.distanceTo(lastLocation)+"m");
        distanceText.setText(String.format(Locale.getDefault(), "%.3fm", sharedPreferences.getFloat("distance", 0)));
        lastLocation = location;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
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
}