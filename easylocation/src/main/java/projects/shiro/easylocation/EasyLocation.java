package projects.shiro.easylocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EasyLocation {
    Context context;
    Activity activity;
    private LocationListener listener;
    private LocationManager locationManager;
    private Location location;
    private double locLat = 0.0;
    private double locLon = 0.0;
    private double latitude = 0.0;
    private double longitude = 0.0;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions= new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET
    };

    public EasyLocation(Context ctx, Activity activity){
        this.context = ctx;
        this.activity = activity;
        getLocation();
    }

    private void getLocation() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locLat = location.getLatitude();
                locLon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER ,0,0,listener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }else {
            return;
        }
    }

    public double getLat(){
        getLocation();
        return latitude;
    }

    public double getLon(){
        getLocation();
        return longitude;
    }

    public double getDistance(double oldLatitude, double oldLongitude, double newLatitude, double newLongitude) {
        if(oldLatitude == 0.0 && oldLongitude == 0.0){
            return 0;
        }
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(oldLatitude);
        startPoint.setLongitude(oldLongitude);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(newLatitude);
        endPoint.setLongitude(newLongitude);

        double distance=startPoint.distanceTo(endPoint);

        return distance/1000;
    }


    public String getLocationName(double lat, double lon){
        String addressStr = "";
        try{
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> myList = geocoder.getFromLocation(lat, lon, 1);
            Address address = (Address) myList.get(0);
            addressStr = address.getAddressLine(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        return addressStr;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    //permission code
    public  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(context,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    public void permissionResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
                    String perm = "";
                    for (String per : permissions) {
                        perm += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }
}
