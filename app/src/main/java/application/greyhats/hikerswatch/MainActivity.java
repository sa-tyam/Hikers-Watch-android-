package application.greyhats.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView latTextView;
    TextView lonTextView;
    TextView accTextView;
    TextView altTextView;
    TextView addressTextView;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    final float MIN_DISTANCE = 0;
    final long MIN_TIME = 0;

    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        accTextView = findViewById(R.id.accTextView);
        altTextView = findViewById(R.id.altTextView);
        addressTextView = findViewById(R.id.addressTextView);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    updateUi(location);
                    Log.i("onLocationChanged", location.toString());
                } catch (Exception e) {
                    e.printStackTrace();
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

        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LOCATION_PROVIDER);
            if (lastKnownLocation != null) {
                try {
                    updateUi(lastKnownLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateUi (Location location)  {
        latTextView.setText("Latitude : " + Double.toString(location.getLatitude()));
        lonTextView.setText("Longitude : " + Double.toString(location.getLongitude()));
        accTextView.setText("Accuracy : " + Double.toString(location.getAccuracy()));
        altTextView.setText("Altitude : " + Double.toString(location.getAltitude()));

        String address = "Couldn't find address :(";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                address = "Address :\n";
                if (listAddresses.get(0).getThoroughfare() != null ){
                    address += listAddresses.get(0).getThoroughfare() + "\n";
                }
                if (listAddresses.get(0).getLocality() != null ){
                    address += listAddresses.get(0).getLocality();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addressTextView.setText(address);
    }
}