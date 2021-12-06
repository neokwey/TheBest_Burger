package my.edu.tarc.thebestburger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

import my.edu.tarc.thebestburger.customerPanel.NewAddress;


public class AddressBook extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView search;
    LatLng latLng;
    LocationRequest request;
    GoogleApiClient client;
    GoogleMap mMap;
    Marker currMark;
    FloatingActionButton fab;
    boolean isPermissionGranted;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;
    String destination;
    Button find;
    List<Field> fields = Arrays.asList(Field.ID, Field.NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        setTitle("Edit Location");
        fab = findViewById(R.id.fab);
        find = findViewById(R.id.button6);
        checkPermission();
        initMap();
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(AddressBook.this);
                startActivityForResult(intent, 200);
            }
        });

        mLocationClient = new FusedLocationProviderClient(this);

    }

    private void initMap() {
        if (isPermissionGranted) {
            if (isGPSenable()) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment);
                mapFragment.getMapAsync(this);

                String apiKey = getString(R.string.api_key);
                if (!Places.isInitialized()){
                    Places.initialize(getApplicationContext(),apiKey);
                }
            }

        }
    }

    private boolean isGPSenable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(gps, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {  }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnable){
                Toast.makeText(this,"GPS is enable", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"GPS is not enable", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 200){
            if (resultCode == RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                String name = place.getName();
                latLng = place.getLatLng();
                find.setText(name);
                if (currMark == null){
                    MarkerOptions op1 = new MarkerOptions();
                    op1.title("Pinned location");
                    op1.position(latLng);
                    currMark = mMap.addMarker(op1);
                }else{
                    currMark.setPosition(latLng);
                }
            }else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        if (location == null){
            Toast.makeText(this,"Location could not been found.",Toast.LENGTH_SHORT).show();
        }else{
            latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    LatLng center = mMap.getCameraPosition().target;

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currMark.remove();
                            MarkerOptions m2 = new MarkerOptions();
                            m2.position(center);
                            m2.title("Current location");
                            currMark = mMap.addMarker(m2);
                            latLng = currMark.getPosition();
                            String lat = String.valueOf((double)center.latitude);
                            String lon = String.valueOf((double) center.longitude);
                            Intent i = new Intent(AddressBook.this, NewAddress.class);
                            i.putExtra("lon", lon);
                            i.putExtra("lat", lat);
                            startActivity(i);
                        }
                    });
                }
            });

            if (currMark == null){
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title("Current Position");
                currMark = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }else{
                currMark.setPosition(latLng);
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setInterval(500);
        request.setFastestInterval(500);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(AddressBook.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                i.setData(uri);
                startActivity(i);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

}