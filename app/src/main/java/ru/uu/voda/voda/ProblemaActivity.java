package ru.uu.voda.voda;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.support.v7.widget.Toolbar; //Тулбар

import android.view.Menu;       //меню
import android.view.MenuItem;   //пункт меню

public class ProblemaActivity  extends AppCompatActivity {

    public static String p_street_in = "";
    public static String p_house_in = "";
    public static String p_level_in = "";
    public static String p_service_in = "";
    public static String p_phone_number_in = "";
    public static String p_name_in = "";
    public static String p_district_in = "";
    public static String p_damage_in = "";
    public static String p_location_damage_in= "";
    public static String p_init_app_in= "";

    public static String server = "vodaonline74.ru";

    public EditText p_house;
    public EditText p_street;
    public EditText p_level;
    public EditText p_service;
    public EditText p_phone_number;
    public EditText p_name;
    public Spinner p_district;
    public Spinner p_damage;
    public Spinner p_location_damage;
    public CheckBox p_init_app;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);

        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //обратимся к нашим полям
        p_street = (EditText) findViewById(R.id.EditText2);
        p_house = (EditText) findViewById(R.id.EditText3);
        p_level = (EditText) findViewById(R.id.EditText4);
        p_service = (EditText) findViewById(R.id.EditText7);
        p_phone_number = (EditText) findViewById(R.id.EditText10);
        p_name = (EditText) findViewById(R.id.EditText11);
        p_district = (Spinner) findViewById(R.id.Spinner1);
        p_damage = (Spinner) findViewById(R.id.Spinner5);
        p_location_damage = (Spinner) findViewById(R.id.Spinner6);
        p_init_app = (CheckBox) findViewById(R.id.CheckBox8);
    }

    public void sendProblem() {
        p_street_in = p_street.getText().toString();
        p_house_in = p_house.getText().toString();
        p_level_in = p_level.getText().toString();
        p_service_in = p_service.getText().toString();
        p_phone_number_in = p_phone_number.getText().toString();
        p_name_in = p_name.getText().toString();
        p_district_in = p_district.getSelectedItem().toString();
        p_damage_in = p_damage.getSelectedItem().toString();
        p_location_damage_in = p_location_damage.getSelectedItem().toString();

        try {
            new SendData().execute();
        } catch (Exception e) {
        }
    }

    // создание меню
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_problema, menu);  //создание меню из xml
        return super.onCreateOptionsMenu(menu);
    }

    // обработка нажатий пунктов меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // по id определеяем пункт меню, вызвавший этот обработчик
        switch (item.getItemId()) {
            case R.id.action_send_problem:    //Кнопка отправить
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();   //Тост отправить
                sendProblem();      //Кусок посылки проблемы
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    class SendData extends AsyncTask<Void, Void, Void> {

        String resultString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String myURL = "http://" + server + "/adm2/server.php";

                String parammetrs = "p_street=" + p_street_in + "&p_house=" + p_house_in + "&p_level=" + p_level_in + "&p_service=" + p_service_in + "&p_phone_number=" + p_phone_number_in + "&p_name=" + p_name_in + "&p_district=" + p_district_in + "&p_damage=" + p_damage_in + "&p_location_damage=" + p_location_damage_in;
                byte[] data = null;
                InputStream is = null;


                try {
                    URL url = new URL(myURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", "" + Integer.toString(parammetrs.getBytes().length));
                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    // конвертируем передаваемую строку в UTF-8
                    data = parammetrs.getBytes("UTF-8");


                    OutputStream os = conn.getOutputStream();


                    // передаем данные на сервер
                    os.write(data);
                    os.flush();
                    os.close();
                    data = null;
                    conn.connect();
                    int responseCode = conn.getResponseCode();


                    // передаем ответ сервер
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    if (responseCode == 200) {    // Если все ОК (ответ 200)
                        is = conn.getInputStream();

                        byte[] buffer = new byte[8192]; // размер буфера


                        // Далее так читаем ответ
                        int bytesRead;


                        while ((bytesRead = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }


                        data = baos.toByteArray();
                        resultString = new String(data, "UTF-8");  // сохраняем в переменную ответ сервера, у нас "OK"


                    } else {
                    }

                    conn.disconnect();

                } catch (MalformedURLException e) {

                    //resultString = "MalformedURLException:" + e.getMessage();
                } catch (IOException e) {

                    //resultString = "IOException:" + e.getMessage();
                } catch (Exception e) {

                    //resultString = "Exception:" + e.getMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Toast.makeText(getApplicationContext(), "Данные переданы!", Toast.LENGTH_LONG).show();
        }
    }
}
