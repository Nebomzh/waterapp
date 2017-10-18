package ru.uu.voda.voda;
/** выбиратель адреса*/

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.Menu;       //меню
import android.view.MenuItem;   //пункт меню

public class AddressPicker extends AppCompatActivity implements OnMapReadyCallback {

    TextView addresstext;

    SharedPreferences sPref;    //объект сохранялок
    final String ADDRESS = "address";  //ключи сохранялок
    final String SAVELAT = "savelat";
    final String SAVELNG = "savelng";

    boolean someaddress = false; //флаг, что в поле адреса что-то введено

    final int PERMISSIONS_FINE_LOCATION = 0;
    //LatLngBounds personLocation;
    private LocationManager locationManager;
    private GoogleMap mMap;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addresstext = (TextView) findViewById(R.id.addresstext);
        sPref = getPreferences(MODE_PRIVATE);   //получаем сохранялки
        setAddresstext();//текст для поля с адресом

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /*@Override //закомментил, чтобы определение положения останавливалось не по паузе, а само когда произойдёт хоть одно определение
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }*/

    // создание меню
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address, menu);  //создание меню из xml
        return super.onCreateOptionsMenu(menu);
    }

    // обработка нажатий пунктов меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // по id определеяем пункт меню, вызвавший этот обработчик
        switch (item.getItemId()) {
            case R.id.action_apply_address:    //Кнопка применить адрес
                //проверка, что что-то вообще введено
                if(addresstext.getText().toString().length()!=0 && someaddress) {
                    saveAddress();//сохранение введённого адреса
                    finish();
                } else
                    Toast.makeText(this, R.string.hpre1, Toast.LENGTH_SHORT).show();   //Тост указать место
                break;
        }
        return super.onOptionsItemSelected(item);
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
        //подготовка карты
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);//включение кнопки компаса
        mMap.getUiSettings().setZoomControlsEnabled(true);//включение кнопок зума
        //Проверка наличия разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Запрос разрешений
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_FINE_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);    //включение определения местоположения самой картой
            mMap.getUiSettings().setMyLocationButtonEnabled(true); //добавить кнопку перехода на текущее положение
        }

        // Добавление маркера и перемещение камеры
        if (sPref.contains(SAVELAT) && sPref.contains(SAVELNG)) {//при наличии сохранённых координат мечемся туда
            LatLng positionstart = new LatLng((double) sPref.getFloat(SAVELAT, 0), (double) sPref.getFloat(SAVELNG, 0));
            marker = mMap.addMarker(new MarkerOptions().position(positionstart));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(positionstart)
                    .zoom(15)
                    //.bearing(45)
                    //.tilt(20)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate);
        } else { //если нет сохранённых координат
            LatLng positionstart = new LatLng(55.159612, 61.402606); //стартуем с координат центра Челябинска
            marker = mMap.addMarker(new MarkerOptions().position(positionstart));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(positionstart)
                    .zoom(15)
                    //.bearing(45)
                    //.tilt(20)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() { //перемещаем камеру с отслеживанием её перемещения
                @Override
                public void onFinish() { //при завершении начальной анимации камеры
                    LocationStart(); //стартуем определение местоположения
                }
                @Override
                public void onCancel() { //или при прерывании начальной анимации камеры
                    LocationStart(); //стартуем определение местоположения
                }
            });
        }

        //слушатель тапов по карте
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker.setPosition(latLng);
                findAddresstext(String.valueOf(latLng.latitude+" "+String.valueOf(latLng.longitude)));
            }
        });
        //Слушатель перемещения маркера
        marker.setDraggable(true);  //маркер можно перемещать
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() { //обработчик событий перемещения маркера
            @Override
            public void onMarkerDragStart(Marker marker) { }
            @Override
            public void onMarkerDrag(Marker marker) { }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                findAddresstext(String.valueOf(marker.getPosition().latitude+" "+String.valueOf(marker.getPosition().longitude)));
            }
        });
    }

    //Обработка ответа пользователя на получение разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Чел предоставил разрешение
                    //Проверка наличия разрешений (без них студия ругается, хотя и так понятно, что здесь они есть)
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
                    else {
                        mMap.setMyLocationEnabled(true);    //включение определения местоположения самой картой
                        mMap.getUiSettings().setMyLocationButtonEnabled(true); //добавить кнопку перехода на текущее положение
                    }
                } else {} //чел не предоставил разшение
            }
        }
    }

    public void LocationStart () {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Проверка наличия разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { }//здесь должен быть запрос, но мы его уже делали раньше, не будем задалбливать пользователя
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, //минимальное время получения данных 10 секунд, минимальное изменение координат для обновления данных 10 метров
                    locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000 * 10, 10,//минимальное время получения данных 10 секунд, минимальное изменение координат для обновления данных 10 метров
                    locationListener);
        }
    }

    //местоопределитель
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude())); //перемещаем в это место маркер
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(mMap.getCameraPosition().zoom)
                        //.bearing(45)
                        //.tilt(20)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(cameraUpdate);//перемещаем в это место камеру
                locationManager.removeUpdates(locationListener); //после одного перемещения не будем больше отслеживать положение, пусть пользователь сам уточнит его передвигая маркер или вводя текст
                //TODO запускам определение названия этого места
                findAddresstext(String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()));
            }
        }
        @Override
        public void onProviderDisabled(String provider) {    }
        @Override
        public void onProviderEnabled(String provider) {    }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {    }
    };

    //текст для поля с адресом из сохранялки
    private void setAddresstext() {
        //подгружаем значения из сохранялок
        if (sPref.getString(ADDRESS, "").length()!=0) {
            someaddress = true;
            addresstext.setText(sPref.getString(ADDRESS, ""));
        }
        else   //если в сохранялке ничего нет
            addresstext.setText(getResources().getString(R.string.manual_edit));
    }

    //текст для поля с адресом по положению маркера
    private void findAddresstext(String string) {
        //TODO написать определение названия из координат
        addresstext.setText(string);
    }

    private void editAddress (View view) {
        //TODO написать диалог для редактирования текст
    }

    //сохранение и адреса и координат + передача в результат
    private void saveAddress () {
        SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
        ed.putString(ADDRESS, addresstext.getText().toString());
        ed.putFloat(SAVELAT, (float) marker.getPosition().latitude);//TODO сделать сохранение в double, а не в float
        ed.putFloat(SAVELNG, (float) marker.getPosition().longitude);//TODO сделать сохранение в double, а не в float
        ed.commit();    //сохранение

        Intent intent = new Intent();
        intent.putExtra(ADDRESS, addresstext.getText().toString());
        intent.putExtra(SAVELAT, marker.getPosition().latitude);
        intent.putExtra(SAVELNG, marker.getPosition().longitude);
        setResult(RESULT_OK, intent);
    }
}
