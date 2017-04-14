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
import com.example.tectest.data.PhotoList;
import com.example.tectest.services.PhotoService;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
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

    private void getRepos(String lat, String lng, String radio ) {
        //zoom = 14 - ln(radius)/ln(2)
//throw  round on there to make sure we get an integer
//and don't make the g-maps API angry
        //radiusToZoom: function(radius){
         //   return Math.round(14-Math.log(radius)/Math.LN2);
        //}
        //pixel = 156543.03392 * Math.cos(lat() * Math.PI / 180) / Math.pow(2, zoom)
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(final Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter("consumer_key", Constants.CONSUMER_KEY).build();
                        request = request.newBuilder().url(url).build();
                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.500px.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PhotoService service = retrofit.create(PhotoService.class);
        Call<PhotoList> call = service.getPhotoAround(lat+","+lng+","+radio+"km");

        call.enqueue(new Callback<PhotoList>() {
            @Override
            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {

            }

            @Override
            public void onFailure(Call<PhotoList> call, Throwable t) {

            }
        });
    }
}
