package com.example.groopr.ui.minimap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.groopr.MainActivity;
import com.example.groopr.PermissionUtils;
import com.example.groopr.PhotoAdapter;
import com.example.groopr.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;

    String marker_id;

    private LocationCallback locationCallback;

    Boolean requestingLocationUpdates = true;

    HashMap<Integer, Marker> hashMapMarker;

    LocationRequest locationRequest;
    String[] user_groups;
    String[] user_group_ids;
    String user_group_selected;
    ParseUser parseUser;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    HashMap<String, ParseGeoPoint> markersandposters;
    ArrayList<ParseGeoPoint> markers;
    ArrayList<String> posters;
    ArrayList<String> userormarker;

    private GoogleMap map;
    String group_selected;

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            try {
                startLocationUpdates();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for hiding title and setting status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.blueappcolorthemedark));
        try {
            getSupportActionBar().hide();
        }catch (NullPointerException e){
            Log.i("check",e.getMessage());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    Log.i("MapsCheck", location.getLatitude() + " "+ location.getLongitude());
                    LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,16f));
                    // ...
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
        hashMapMarker = new HashMap<>();
        group_selected = "";

        parseUser = ParseUser.getCurrentUser();

        markersandposters = new HashMap<>();
        userormarker = new ArrayList<>();
        markers = new ArrayList<>();
        posters = new ArrayList<>();


        Intent intent =getIntent();
        group_selected = intent.getStringExtra("GROUP_SELECTED");
        Toast.makeText(MapsActivity.this, group_selected+"'s map!", Toast.LENGTH_SHORT).show();

        user_groups = parseUser.get("GroupNames").toString().split(",");
        user_group_ids = parseUser.get("Groups").toString().split(",");
        user_group_selected = "";

        for(int i = 0; i < user_groups.length; i++){
            if (user_groups[i].matches(group_selected)){
                user_group_selected = user_group_ids[i];
                break;
            }
        }

        ParseQuery<ParseObject> parseObjectParseQuery = ParseQuery.getQuery("Markers");
        parseObjectParseQuery.whereEqualTo("Group_id", user_group_selected);

        parseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        Log.i("MapMarkers", Integer.toString(objects.size()));
                        for (ParseObject object:objects) {
                            Log.i("MapMarkersdb", object.getString("Posted_by")+object.getParseGeoPoint("Coords").toString());
                            markers.add(object.getParseGeoPoint("Coords"));
                            posters.add(object.getString("Posted_by"));

                            userormarker.add(object.getString("User_or_Marker"));
                        }
                        Log.i("MapMarkers", markersandposters.toString());

                        for (String user: userormarker
                             ) {
                            Log.i("MapMarkers", user);

                        }

                        int x = 0;
                        for (int i = 0; i<posters.size();i++){
                            if(userormarker.get(x).matches("USER")){
                                if(posters.get(i).matches(parseUser.getUsername())) {
                                    LatLng latLng = new LatLng(markers.get(i).getLatitude(), markers.get(i).getLongitude());
                                    map.addMarker(new MarkerOptions().position(latLng).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                }
                                else{
                                    LatLng latLng = new LatLng(markers.get(i).getLatitude(), markers.get(i).getLongitude());
                                    map.addMarker(new MarkerOptions().position(latLng).title(posters.get(i) + "'s location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                                }
                            }
                            else{
                                LatLng latLng = new LatLng(markers.get(i).getLatitude(), markers.get(i).getLongitude());
                                map.addMarker(new MarkerOptions().position(latLng).title(posters.get(i) + "'s Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                            }


                            x++;
                        }


                    }
                }
                else{
                    e.printStackTrace();
                }
            }
        });



        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    if(hashMapMarker.size()>0){
                        Marker removemarker = hashMapMarker.get(1);
                        removemarker.remove();
                        hashMapMarker.remove(1);
                    }

                    List<Address> listaddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(listaddresses.get(0).getPostalCode()+" "+ listaddresses.get(0).getLocality()));

                    Toast.makeText(MapsActivity.this, "Location set", Toast.LENGTH_SHORT).show();

                    hashMapMarker.put(1, marker);

                    // intent.putExtra("CITYPLUSCODE", listaddresses.get(0).getCountryName()+" "+ listaddresses.get(0).getPostalCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*
                intent.putExtra("FIRSTTIME", "1");
                intent.putExtra("ARRAYOFSTUFF", cakelist);
                intent.putExtra("ARRAYOFCOORDLAT", cakelatlist);
                intent.putExtra("ARRAYOFCOORDLNG", cakelnglist);

                intent.putExtra("LAT", Double.toString(latLng.latitude));
                intent.putExtra("LNG", Double.toString(latLng.longitude));
                startActivity(intent);
                */
                final ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
                Log.i("MapsCheck", parseGeoPoint.toString());


                marker_id = parseUser.getString("Marker_id");

                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Markers");
                parseQuery.whereEqualTo("Marker_id", marker_id);
                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()>0){
                                for (ParseObject object: objects) {
                                    if(object.getString("Group_id").matches(user_group_selected)) {
                                        object.put("Coords", parseGeoPoint);
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.i("MapsCheck", "Done successfully");
                                                    finish();
                                                    startActivity(getIntent());
                                                } else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        ParseObject parseObject = new ParseObject("Markers");

                                        parseObject.put("Marker_id", marker_id);
                                        parseObject.put("Group_id", user_group_selected);
                                        parseObject.put("Group_name", group_selected);
                                        parseObject.put("Posted_by", parseUser.getUsername());
                                        parseObject.put("Coords", parseGeoPoint);
                                        parseObject.put("User_or_Marker", "MARKER");

                                        parseObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null){
                                                    Log.i("MapsCheck", "Done successfully");
                                                    finish();
                                                    startActivity(getIntent());
                                                }
                                                else{
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                            else{
                                ParseObject parseObject = new ParseObject("Markers");

                                parseObject.put("Marker_id", marker_id);
                                parseObject.put("Group_id", user_group_selected);
                                parseObject.put("Group_name", group_selected);
                                parseObject.put("Posted_by", parseUser.getUsername());
                                parseObject.put("Coords", parseGeoPoint);
                                parseObject.put("User_or_Marker", "MARKER");

                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Log.i("MapsCheck", "Done successfully");
                                            finish();
                                            startActivity(getIntent());
                                        }
                                         else{
                                            e.printStackTrace();
                                       }
                                    }
                                });


                            }
                        }
                        else {
                            e.printStackTrace();
                        }
                    }
                });






            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                final ParseGeoPoint usergeopoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                                Log.i("MapsCheck", usergeopoint.toString());

                                parseUser.put("Location", usergeopoint);

                                user_groups = ParseUser.getCurrentUser().get("GroupNames").toString().split(",");
                                user_group_ids = ParseUser.getCurrentUser().get("Groups").toString().split(",");
                                user_group_selected = "";

                                for(int i = 0; i < user_groups.length; i++){
                                    if (user_groups[i].matches(group_selected)){
                                        user_group_selected = user_group_ids[i];
                                        break;
                                    }
                                }

                                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Markers");
                                parseQuery.whereEqualTo("Marker_id", ParseUser.getCurrentUser().getUsername() + "_marker");
                                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if(e==  null){
                                            if(objects.size() >0){
                                                for (ParseObject parseobject:objects) {
                                                    if(parseobject.getString("Group_id").matches(user_group_selected)) {
                                                        parseobject.put("Coords", usergeopoint);
                                                        parseobject.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                } else {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else{
                                                        ParseObject parseObject = new ParseObject("Markers");

                                                        parseObject.put("Marker_id", ParseUser.getCurrentUser().getUsername()+"_marker");
                                                        parseObject.put("Group_id", user_group_selected);
                                                        parseObject.put("Group_name", group_selected);
                                                        parseObject.put("Posted_by", parseUser.getUsername());
                                                        parseObject.put("Coords", usergeopoint);
                                                        parseObject.put("User_or_Marker", "USER");

                                                        parseObject.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if(e == null){
                                                                    Log.i("MapsCheck", "Done successfully");
                                                                }
                                                                else{
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                            else{
                                                ParseObject parseObject = new ParseObject("Markers");

                                                parseObject.put("Marker_id", ParseUser.getCurrentUser().getUsername()+"_marker");
                                                parseObject.put("Group_id", user_group_selected);
                                                parseObject.put("Group_name", group_selected);
                                                parseObject.put("Posted_by", parseUser.getUsername());
                                                parseObject.put("Coords", usergeopoint);
                                                parseObject.put("User_or_Marker", "USER");

                                                parseObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e == null){
                                                            Log.i("MapsCheck", "Done successfully");
                                                        }
                                                        else{
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                        else{
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                parseUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            Log.i("MapsCheck", "Location saved successfully");
                                        }
                                        else{
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                LatLng userloc = new LatLng(location.getLatitude(), location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userloc,16f));
                            }

                        }
                    });
        }
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "This is you!", Toast.LENGTH_LONG).show();
    }


    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (requestCode == 1) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                            locationCallback,
                            Looper.getMainLooper());
                    return;
                }
            }
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }

    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}