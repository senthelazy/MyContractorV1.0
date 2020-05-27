package com.sen.mycontractor.customer;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Project;

import java.util.List;
import java.util.Locale;

public class LocationDetermine extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final int MY_PERMISSION_REQUEST_CODE_FOR_LOCATION = 7001;
    private final int MY_PERMISSION_REQUEST_CODE_FOR_STORAGE = 7002;
    private final int MY_PERMISSION_REQUEST_CODE_FOR_MANAGE_DOCUMENTS=998;
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 5001;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdate = false;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private TextView currentLocationTv;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private Project mProject;
    private TextView loginTv;
    private String mLocation = "";
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_determine);
        loginTv = (TextView) findViewById(R.id.loginTv);
        currentLocationTv = (TextView) findViewById(R.id.currentLocationTv);

        checkLocationPermission();
        checkExternalStoragePermission();
        checkManageDocumentsPermission();
        setupWindowAnimations();
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        mProject = getIntent().getParcelableExtra("Project");


        Button useLocationBtn = (Button) findViewById(R.id.useLocationBtn);
        useLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationDetermine.this);
                Intent intent = new Intent(LocationDetermine.this, UploadImages.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mProject.setLocation(mLocation);
                intent.putExtra("Project", mProject);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

        TextView loginTv = (TextView) findViewById(R.id.loginTv);
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationDetermine.this);
                Intent intent = new Intent(LocationDetermine.this, CustomerLogin.class);
                startActivity(intent,options.toBundle());
                finish();
            }
        });

    }

    private void setupWindowAnimations() {
        Slide enterTransition = new Slide();
        enterTransition.setSlideEdge(Gravity.RIGHT);
        enterTransition.setDuration(500);
        getWindow().setEnterTransition(enterTransition);
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(500);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
        getWindow().setAllowReturnTransitionOverlap(false);
        getWindow().setAllowEnterTransitionOverlap(true);
    }

    @Override
    public void onBackPressed() {

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, MY_PERMISSION_REQUEST_CODE_FOR_LOCATION);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
    }

    private void checkExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }, MY_PERMISSION_REQUEST_CODE_FOR_STORAGE);
        } else {
            //Toast.makeText(UploadImages.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkManageDocumentsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.MANAGE_DOCUMENTS
                    }, MY_PERMISSION_REQUEST_CODE_FOR_MANAGE_DOCUMENTS);
        } else {
            //Toast.makeText(UploadImages.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
        }
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    public String hereLocation(double lat, double lon) {
        String curCity = "";
        Geocoder geocoder = new Geocoder(LocationDetermine.this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() > 0) {
                curCity = addressList.get(0).getLocality();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return curCity;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE_FOR_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices())
                        buildGoogleApiClient();
                    createLocationRequest();
                }
                break;
            case MY_PERMISSION_REQUEST_CODE_FOR_STORAGE:
                break;
            case MY_PERMISSION_REQUEST_CODE_FOR_MANAGE_DOCUMENTS:
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onLocationChanged(Location location) {
        this.mLastLocation = location;
        displayLocation();
    }

    public void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        if (mRequestingLocationUpdate) {
            startLocationUpdate();
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mProject = new Project();
            double lat = mLastLocation.getLatitude();
            double lon = mLastLocation.getLongitude();
            currentLocationTv.setText(hereLocation(lat, lon));
            mLocation = hereLocation(lat, lon);
        } else {
            currentLocationTv.setText("Could not get your location!");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }
}
