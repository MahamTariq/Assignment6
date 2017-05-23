package com.example.home.assignment6;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
  //  private Camera mCamera;
  //  private CameraPreview mPreview;
    private File imagefile;
    private String TAG;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Google Services Available", Toast.LENGTH_LONG).show();
            initMap();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't Connect", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gotoLocation(33.5466545, 73.1817243);
        //       gotoLocationzoom(33.5466545,73.1817243,15);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

  /* mGoogleApiClient =new GoogleApiClient.Builder(this)
           .addApi(LocationServices.API)
           .addConnectionCallbacks(this)
           .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();*/
    }

    private void gotoLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update =  CameraUpdateFactory.newLatLng(ll);
        mMap.moveCamera(update);
    }

    private void gotoLocationzoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update =  CameraUpdateFactory.newLatLngZoom(ll,15);
        mMap.moveCamera(update);
    }

    public void geoLocate(View view) throws IOException {
        EditText et= (EditText) findViewById(R.id.TFaddress);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> addressList = null;
        addressList = gc.getFromLocationName(location , 1);
        Address address = addressList.get(0);
        String locality = address.getLocality();
        Toast.makeText(this,locality,Toast.LENGTH_LONG).show();

        double lat=address.getLatitude();
        double lng=address.getLongitude();
        gotoLocationzoom(lat,lng,15);

        setMarker(locality, lat, lng);




      //  LatLng latLng = new LatLng(address.getLatitude() , address.getLongitude());
      //  mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
      //  mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }

    public void process(View view)
    {
        Intent getCameraImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagefile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"test.jpg");
        Uri tempuri= Uri.fromFile(imagefile);
        getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT,tempuri);
        startActivityForResult(getCameraImage,0);
    }

    protected void onActivityForResult(LatLng position,int requestCode,int resultCode, Intent data )
    {
        if(requestCode==0)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    if(imagefile.exists())
                    {
                        Toast.makeText(this,"This file was save at"+imagefile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                        mMap.addMarker(new MarkerOptions().position(position)
                                .icon(BitmapDescriptorFactory.fromPath(imagefile.getAbsolutePath())));
                    }
                    else
                    {
                        Toast.makeText(this,"There was error in saving the file",Toast.LENGTH_LONG).show();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private MarkerOptions createMarker(LatLng position, String title, String snippet, String image_path) {

        // Standard marker icon in case image is not found
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        if (!image_path.isEmpty()) {
            File iconfile = new File(image_path);
            if (iconfile.exists()) {
                BitmapDescriptor loaded_icon = BitmapDescriptorFactory
                        .fromPath(image_path);
                if (loaded_icon != null) {
                    icon = loaded_icon;
                } else {
                    Log.e(TAG, "loaded_icon was null");
                }
            } else {
                Log.e(TAG, "iconfile did not exist: " + image_path);
            }
        } else {
            Log.e(TAG, "iconpath was empty: " + image_path);
        }
        return new MarkerOptions().position(position).title(title).snippet(snippet).icon(icon);
    }

    private void setMarker(String locality, double lat, double lng) {
        MarkerOptions options =new MarkerOptions().title(locality).draggable(true).position(new LatLng(lat,lng)).snippet("Hiii");
        Marker marker = mMap.addMarker(options);
 /*       LatLng l= new LatLng(lat,lng);
        String path= imagefile.getAbsolutePath();
        Intent i=new Intent();
        createMarker(l,locality,"I'm here",path);
        onActivityForResult(l,0,1,i);*/

        // if(marker==null){
        //   marker.remove();
        //}
    }

}
/*    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void onSearch(View view) throws IOException {
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if(location != null || !location.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location , 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude() , address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }
}
*/