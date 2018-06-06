package ru.uu.voda;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.support.v7.app.AppCompatCallback;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisableActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Отключения");
        getSupportActionBar().setSubtitle("Узнать о плановых и аварийных отключениях воды");

        // TODO: Remove the redundant calls to getSupportActionBar()
        //       and use variable actionBar instead






        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.142159, 61.374544), 10));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        new AsyncTaskGetMareker().execute();


    }








    /*public String getJSONFromAssets() {
        String json = null;
        try {
            InputStream inputData = getAssets().open("stations.json");
            int size = inputData.available();
            byte[] buffer = new byte[size];
            inputData.read(buffer);
            inputData.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
*/

    private class AsyncTaskGetMareker extends AsyncTask<Void, Void, String> {

        private static final String LOG_TAG = "ExampleApp";


        private static final String SERVICE_URL = "http://vodaonline74.ru/adm2/voda.json";


        protected GoogleMap mMap;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(Void... args) {

            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                // Connect to the web service
                URL url = new URL(SERVICE_URL);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Read the JSON data into the StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    json.append(buff, 0, read);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to service", e);
                //throw new IOException("Error connecting to service", e); //uncaught
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return json.toString();
        }


        // Executed after the complete execution of doInBackground() method
     /*   @Override
        protected void onPostExecute(String json) {

            try {

                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    LatLng latLng = new LatLng(jsonObj.getJSONArray("coordinates").getDouble(0),
                            jsonObj.getJSONArray("coordinates").getDouble(1));

                    String name=jsonObj.getString("name");



                    if (i == 0) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng).zoom(13).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }


                    drawMarker (new LatLng(latLng));

                    mMap.addMarker(new MarkerOptions()
                            .title(jsonObj.getString("name"))
                            .snippet(Integer.toString(jsonObj.getInt("content")))
                            .position(latLng));

                }




            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error processing JSON", e);
                e.printStackTrace();



            }


       }


}
*/

      /*  protected void onPostExecute (JSONArray result){
            if (result !=null){
                for (int i =0; i <result.length(); i++){

                    JSONObject jsonObject= null;
                    try {
                        jsonObject= result.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        String content=jsonObject.getString("content");
                        LatLng latLng = new LatLng(jsonObject.getJSONArray("coordinates").getDouble(0),
                               jsonObject.getJSONArray("coordinates").getDouble(1));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng).zoom(13).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));

                        drawMarker(latLng, name);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "Error processing JSON", e);
                    }
                }
            }

        }

        private void drawMarker(LatLng latLng, String name) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.snippet(name);
            mMap.addMarker(markerOptions);

        }


    }

}*/


        protected void onPostExecute(String json) {

                try {

                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);


                        String name = jsonObj.getString("name");
                        String content = jsonObj.getString("content");

                        LatLng latLng = new LatLng(jsonObj.getJSONArray("coordinates").getDouble(1),
                                jsonObj.getJSONArray("coordinates").getDouble(0));


                        drawMarker(latLng, name, content);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error processing JSON", e);
                    e.printStackTrace();
                }


            }

        }

        private void drawMarker(final LatLng latLng, final String content, final String name) {
            MarkerOptions markerOptions = new MarkerOptions();
            //markerOptions.position(latLng);
            //markerOptions.snippet(content);
            //markerOptions.title(name);
            //mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
              //  @Override public View getInfoWindow(Marker arg0) { return null; }
                //@Override public View getInfoContents(Marker marker) { LinearLayout info = new LinearLayout(latLng); info.setOrientation(LinearLayout.VERTICAL); TextView title = new TextView(mContext); title.setTextColor(Color.BLACK); title.setGravity(Gravity.CENTER); title.setTypeface(null, Typeface.BOLD); title.setText(marker.getTitle()); TextView snippet = new TextView(mContext); snippet.setTextColor(Color.GRAY); snippet.setText(marker.getSnippet()); info.addView(title); info.addView(snippet); return info; } });


            //  Toast.makeText(
            //        getApplicationContext(),
              //      "Marker " + marker.getTitle() + content, Toast.LENGTH_LONG).show();


               // marker.showInfoWindow();



            //mMap.addMarker(markerOptions);
            mMap.addMarker(new MarkerOptions().position(latLng).title(content).snippet(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getApplicationContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getApplicationContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });

            LatLng position1 = new LatLng(55.142159, 61.374544);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position1)
                    .zoom(11)
                    //.bearing(45)
                    //.tilt(20)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate);


        }
    }

