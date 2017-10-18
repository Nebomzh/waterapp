package ru.uu.voda.voda;
/** страница с контактами*/

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position1 = new LatLng(55.142159, 61.374544);
        LatLng position2 = new LatLng(55.141367, 61.372765);
        mMap.addMarker(new MarkerOptions().position(position1).title("Воровского 60А").snippet("Часы работы: пн., вт., ср., чт. с 9:00 до 17:00, пт. с 9:00 до 15:30,\n" +
                "перерыв с 12:00 до 12:45. ")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(position2).title("Варненская 13").snippet("Пн., вт., ср., чт. с 9:00 до 17:00, пт. с 9:00 до 15:30, перерыв с 12:00 до 12:45.")).showInfoWindow();
        mMap.getUiSettings().setCompassEnabled(true);//включение кнопки компаса
        mMap.getUiSettings().setZoomControlsEnabled(true);//включение кнопок зума

        //Проверка наличия разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
        else {
            mMap.setMyLocationEnabled(true);    //включение определения местоположения самой картой
            mMap.getUiSettings().setMyLocationButtonEnabled(true); //добавить кнопку перехода на текущее положение
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position1));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position1)
                .zoom(15)
                //.bearing(45)
                //.tilt(20)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }
}
