package com.example.tectest.usecase.listInMap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.arsy.maps_library.MapRipple;
import com.example.tectest.utils.Constants;
import com.example.tectest.R;
import com.example.tectest.utils.DialogManager;
import com.example.tectest.utils.TaskManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private MapRipple mapRipple;

    private int hasPermission;
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 200;
    private Activity activity;
    private ImageButton reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();
        initValues();
        initListeners();
    }

    private void initViews(){
        activity = this;
        reload = (ImageButton)findViewById(R.id.btn_reload);


    }


    private void initValues(){

    }
    private void initListeners(){
        reload.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f));

        mapRipple = new MapRipple(mMap, sydney, this);
        if (!mapRipple.isAnimationRunning()) {
            configRipple();
        }
        //check permission this Location
        checkPermission();

    }
    private void configRipple(){
        mapRipple.withNumberOfRipples(Constants.NUMBER_OF_RIPPLES);
        mapRipple.withFillColor(ContextCompat.getColor(this,R.color.radar_color));
        mapRipple.withStrokeColor(ContextCompat.getColor(this,R.color.radar_color_dark));
        mapRipple.withStrokewidth(Constants.STROKE_WIDTH);
        mapRipple.withDistance(Constants.RADIO_RADAR);
        mapRipple.withRippleDuration(Constants.RIPPLE_DURATION);
        mapRipple.withTransparency(Constants.TRANSPARENCY);
        mapRipple.startRippleMapAnimation();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
    }

    private void checkPermission() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            List<String> permissions = new ArrayList<>();
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }
            else{
                String location_context = Context.LOCATION_SERVICE;
                LocationManager locationManager = (LocationManager) activity.getSystemService(location_context);
                List<String> providers = locationManager.getProviders(true);
                for (String provider : providers) {
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        Log.v("localizacion","LastLocation: "+location);
                    }
                    else {
                        Log.v("localizacion","Error LastLocation");
                    }

                    locationManager.requestLocationUpdates(provider, Constants.TIME_UPDATE_LOCATION_MILIS,0,new LocationListenerTescTest());

                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reload:
                    checkPermission();
                break;
            case R.id.btn_privado:

                break;
        }
    }

    private class LocationListenerTescTest implements LocationListener{
        public void onLocationChanged(Location location) {
            Log.v("localizacion","Location changed: "+location.toString());
        }
        public void onProviderDisabled(String provider) {
            Log.v("localizacion","Proveedor desabilitado");
        }
        public void onProviderEnabled(String provider) {
            Log.v("localizacion","Proveedor enabled");
        }
        public void onStatusChanged(String provider, int status,Bundle extras) {
            Log.v("localizacion","Estado changed: "+status);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        DialogManager.getTwoButtonAlertDialog(this, getString(R.string.ACC_PERMISSION_TITLE), getString(R.string.ACC_PERMISSION_MAPS),
                                getString(R.string.ACC_PERMISSION_BUTTON), getString(R.string.BTN_CLOSE), permissionListener, cancelListener);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * Open Settings
     */
    private DialogInterface.OnClickListener permissionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            TaskManager.openSettings(activity);
        }
    };

    /**
     * Cancel dialog
     */
    private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            DialogManager.hideCurrentDialog();
        }
    };


}
