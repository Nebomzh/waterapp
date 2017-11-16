package ru.uu.voda;
/** выбиратель адреса*/

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressPicker extends AppCompatActivity implements OnMapReadyCallback, NoticeDialogListener {

    TextView addresstext;

    SharedPreferences sPref;    //объект сохранялок
    final String ADDRESS = "address";  //ключи сохранялок
    final String SAVELAT = "savelat";
    final String SAVELNG = "savelng";
    final String MAPTYPE = "maptype";

    final String ROTATE = "rotate"; //ключ поворота экрана
    boolean rotate = false; //флаг, что был поворот экрана
    boolean firsttime = true; //флаг, что был активити запускается в первый раз (а не восстановливается из свёрнутого)

    //Диалоги
    DialogFragment addressedit_dialog;

    //теги диалогов
    final String ADDRESSEDIT_DIALOG_TAG = "addressedit_dialog_tag";

    final int PERMISSIONS_FINE_LOCATION = 0;
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override //отключаем обновления при уничтожении, а то если неудаётся определить, а экран уже закрыли, то попытка определения так и продолжит висеть
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    //сохранение состояния перед поворотом экрана
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ROTATE, true);
    }

    //восстановление состояния после поворота экрана
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rotate = savedInstanceState.getBoolean(ROTATE, false);
    }

    protected void onResume() {
        super.onResume();
        if(firsttime && !rotate) { //если активити запускается перый раз и не после поворота экрана
            Intent intent = getIntent();//получаем данные из вызвавшего экрана
            saveAddress(intent.getStringExtra(ADDRESS)); //и сохраняем эти данные
            saveAddress(intent.getFloatExtra(SAVELAT, 0), intent.getFloatExtra(SAVELNG, 0));
        }
        //иначе будем брать данные из сохранялок этого экрана
        firsttime=false; //следующие запуски будут уже не в первый раз
        setAddresstext();//задаём текст для поля с адресом
    }

    // создание меню
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address, menu);  //создание меню из xml
        return super.onCreateOptionsMenu(menu);
    }

    // обработка нажатий пунктов меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // по id определеяем пункт меню, вызвавший этот обработчик
        switch (item.getItemId()) {
            case R.id.action_change_map_type:    //Кнопка смены типа карты
                switch (mMap.getMapType()) {
                    case GoogleMap.MAP_TYPE_NORMAL:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case GoogleMap.MAP_TYPE_HYBRID:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case GoogleMap.MAP_TYPE_SATELLITE:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                }
                SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
                ed.putInt(MAPTYPE, mMap.getMapType());//добавление нового типа карты к сохранялке
                ed.apply();    //фоновое сохранение
                break;
            case R.id.action_apply_address:    //Кнопка применить адрес
                //проверка, что что-то вообще введено
                if(sPref.getString(ADDRESS, "").length()!=0) {
                    sendResult();//отправка результата в вызвавший экран
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
        mMap.setMapType(sPref.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL));//тип карты из сохранялки
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
        if (sPref.getFloat(SAVELAT, 0) != 0 && sPref.getFloat(SAVELNG, 0) != 0) {//при наличии сохранённых координат мечемся туда
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
                findAddresstext(new LatLng(latLng.latitude, latLng.longitude));
                saveAddress((float) latLng.latitude,(float) latLng.longitude);
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
                findAddresstext(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                saveAddress((float) marker.getPosition().latitude, (float) marker.getPosition().longitude);
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
                    1000, 1, //минимальное время получения данных 1 секунда, минимальное изменение координат для обновления данных 1 метр
                    locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000, 1,//минимальное время получения данных 1 секунда, минимальное изменение координат для обновления данных 1 метр
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
                findAddresstext(new LatLng(location.getLatitude(), location.getLongitude()));
                saveAddress((float) marker.getPosition().latitude, (float) marker.getPosition().longitude);
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
        if (sPref.getString(ADDRESS, "").length()!=0)
            addresstext.setText(sPref.getString(ADDRESS, ""));
        else  //если в сохранялке ничего нет
            addresstext.setText(getResources().getString(R.string.manual_edit));
    }

    //текст для поля с адресом по положению маркера
    private void findAddresstext(LatLng point) {
        String result = "";
        // A Locale if the format for the address
        Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = gc.getFromLocation(point.latitude, point.longitude, 1); // Return 1 result
            if(addresses != null && addresses.size() > 0){
                Address address = addresses.get(0);

                // Build addressName
                    /*if (address.getPostalCode() != null) {
                            result += address.getPostalCode() + " ";
                    }*/
                if (address.getLocality() != null) {
                    result += address.getLocality();
                }
                if (address.getSubThoroughfare() != null && address.getThoroughfare() != null) {
                    if(result.length()!=0)
                        result += "\n";
                    result += address.getThoroughfare() + " " + address.getSubThoroughfare();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(result.length()==0) //если неудачно определится
            Toast.makeText(this, R.string.address_not_found, Toast.LENGTH_SHORT).show();//то отображаем сообщение
        saveAddress(result);//сохраняем результат (если адрес не был определён будет предложено ввести вручную)
        setAddresstext();//отображаем
    }

    public void editAddress (View view) {
        addressedit_dialog = new AddressEditDialogFragment();
        addressedit_dialog.show(getFragmentManager(), ADDRESSEDIT_DIALOG_TAG);
    }

    //Интерфейсы принятия инфы от диалоговых окон
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        switch (dialog.getTag()) {  //определение диалога
            case ADDRESSEDIT_DIALOG_TAG: //диалог с редактированием адреса
                EditText addressedit = (EditText) dialog.getDialog().findViewById(R.id.addressedit);
                saveAddress(addressedit.getText().toString());
                setAddresstext(); //текст для поля с местом
                break;
        }
    }
    /*@Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }*/
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    }

    //сохранение и адреса и координат
    private void saveAddress (String string) {
        SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
        ed.putString(ADDRESS, string);
        ed.commit();    //сохранение
    }
    private void saveAddress (float lat, float lng) {
        SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
        ed.putFloat(SAVELAT, lat);
        ed.putFloat(SAVELNG, lng);
        ed.commit();    //сохранение
    }

    //передача результата
    private void sendResult () {
        Intent intent = new Intent();
        intent.putExtra(ADDRESS, sPref.getString(ADDRESS, ""));
        intent.putExtra(SAVELAT, sPref.getFloat(SAVELAT, 0));
        intent.putExtra(SAVELNG, sPref.getFloat(SAVELNG, 0));
        setResult(RESULT_OK, intent);
    }
}
