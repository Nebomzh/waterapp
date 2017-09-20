package ru.uu.voda.voda;


import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import android.content.SharedPreferences;           //для работы с сохранялками
import android.content.SharedPreferences.Editor;    //для редактирования сохранялок

public class ProblemaActivity  extends AppCompatActivity implements AddressDialogFragment.NoticeDialogListener { //добавляем интерфейс для принятия событий диалога

    SharedPreferences sPref;    //объект сохранялок
    final String DISTRICT = "district"; //ключи сохранялок
    final String STREET = "street";
    final String HOUSE = "house";
    final String LEVEL = "level";
    final String DAMAGE = "damage";
    final String LOCATION_DAMAGE = "location_damage";
    final String SERVICE = "service";
    final String INIT_APP = "init_app";
    final String NEED_CALLBACK = "need_callback";
    final String PHONE_NUMBER = "phone_number";
    final String NAME = "name";

    DialogFragment dialog;

    public static String server = "vodaonline74.ru";

    TextView placetext;
    //public Spinner p_district;
    //public EditText p_street;
    //public EditText p_house;
    //public EditText p_level;
    public Spinner p_damage;
    public Spinner p_location_damage;
    public EditText p_service;
    public CheckBox p_init_app;
    public CheckBox p_need_callback;
    public EditText p_phone_number;
    public EditText p_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);

        sPref = getPreferences(MODE_PRIVATE);   //получаем сохранялки

        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //поле вызывающее диалог
        findViewById(R.id.placebox).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new AddressDialogFragment();
                dialog.show(getFragmentManager(), "dlg1");
            }
        });

        //обратимся к нашим полям
        placetext = (TextView) findViewById(R.id.placetext);
        //p_district = (Spinner) findViewById(R.id.Spinner1);
        //p_street = (EditText) findViewById(R.id.EditText2);
        //p_house = (EditText) findViewById(R.id.EditText3);
        //p_level = (EditText) findViewById(R.id.EditText4);
        p_damage = (Spinner) findViewById(R.id.Spinner5);
        p_location_damage = (Spinner) findViewById(R.id.Spinner6);
        p_service = (EditText) findViewById(R.id.EditText7);
        p_init_app = (CheckBox) findViewById(R.id.CheckBox8);
        p_need_callback = (CheckBox) findViewById(R.id.CheckBox9);
        p_phone_number = (EditText) findViewById(R.id.EditText10);
        p_name = (EditText) findViewById(R.id.EditText11);

        //подгружаем значения из сохранялок
        setPlacetext();//текст для первого поля
        p_damage.setSelection(sPref.getInt(DAMAGE, 0));
        p_location_damage.setSelection(sPref.getInt(LOCATION_DAMAGE, 0));
        p_service.setText(sPref.getString(SERVICE, ""));
        p_init_app.setChecked(sPref.getBoolean(INIT_APP, false));
        p_need_callback.setChecked(sPref.getBoolean(NEED_CALLBACK, false));
        p_phone_number.setText(sPref.getString(PHONE_NUMBER, ""));
        p_name.setText(sPref.getString(NAME, ""));
    }

    //Интерфейсы принятия инфы от диалоговых окон
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Spinner p_district = (Spinner) dialog.getDialog().findViewById(R.id.Spinner1);
        EditText p_street = (EditText) dialog.getDialog().findViewById(R.id.EditText2);
        EditText p_house = (EditText) dialog.getDialog().findViewById(R.id.EditText3);
        EditText p_level = (EditText) dialog.getDialog().findViewById(R.id.EditText4);

        Editor ed = sPref.edit();   //объект для редактирования сохранений

        ed.putInt(DISTRICT, p_district.getSelectedItemPosition());
        ed.putString(STREET, p_street.getText().toString());
        ed.putString(HOUSE, p_house.getText().toString());
        ed.putString(LEVEL, p_level.getText().toString());

        ed.commit();    //сохранение

        setPlacetext(); //текст для поля с местом
    }
    /*@Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }*/
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    }

    //текст для поля с местом
    private void setPlacetext() {
        String temp_string="";  //временная строка
        Boolean something=false;      //флаг, что что-то ввели
        //подгружаем значения из сохранялок
        if (sPref.getInt(DISTRICT, 0)!=0)
            something = true;
        temp_string = getResources().getStringArray(R.array.Spinner1_list) [sPref.getInt(DISTRICT, 0)];
        temp_string += "\n";
        if (sPref.getString(STREET, "").length()!=0) {
            something = true;
            temp_string += sPref.getString(STREET, "");
        }
        else
            temp_string += getResources().getString(R.string.h2request);
        if (sPref.getString(STREET, "").length()!=0 && sPref.getString(HOUSE, "").length()!=0)
            temp_string += " ";
        else
            temp_string += "\n";
        if (sPref.getString(HOUSE, "").length()!=0) {
            something = true;
            temp_string += sPref.getString(HOUSE, "");
        }
        else
            temp_string += getResources().getString(R.string.h3request);
        temp_string += "\n";
        if (sPref.getString(LEVEL, "").length()!=0) {
            something = true;
            temp_string += "Этаж " + sPref.getString(LEVEL, "");
        }
        else
            temp_string += getResources().getString(R.string.h4request);

        if (something)
            placetext.setText(temp_string);
        else
            placetext.setText(R.string.hpre1);
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
                //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();   //Тост отправить
                sendProblem();      //Кусок посылки проблемы
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendProblem() {
        saveFields();   //сохранение введённых полей

        String temp_string=getResources().getString(R.string.warn_empty_fields);  //временная строка
        Boolean correct=true;      //флаг, что всё верно

        //проверка на заполненность обязательных полей
        if (sPref.getString(STREET, "").length()==0) {
            temp_string += getResources().getString(R.string.h2) + "\n";
            correct = false;
        }
        if (sPref.getString(HOUSE, "").length()==0) {
            temp_string += getResources().getString(R.string.h3) + "\n";
            correct = false;
        }
        if (sPref.getString(LEVEL, "").length()==0) {
            temp_string += getResources().getString(R.string.h4) + "\n";
            correct = false;
        }
        if (sPref.getString(PHONE_NUMBER, "").length()==0) {
            temp_string += getResources().getString(R.string.h10);
            correct = false;
        }

        if (!correct) //если что-то не заполнено
        {
            Toast.makeText(this, temp_string, Toast.LENGTH_LONG).show();    //отображаем сообщение, что не все поля заполнены
            return; //выходим из метода, не отправляя данные
        }

        try {
            new SendData().execute();
            finish(); //если всё пошлётся выходим из активити, чтобы не ддосили по сто раз нажимая отправку
        } catch (Exception e) {
        }
    }

    private class SendData extends AsyncTask<Void, Void, Void> {

        String resultString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String myURL = "http://" + server + "/adm2/server.php";

                String parameters = "p_district=" + String.valueOf(sPref.getInt(DISTRICT, 0)) +
                        "&p_street=" + sPref.getString(STREET, "") +
                        "&p_house=" + sPref.getString(HOUSE, "") +
                        "&p_level=" + sPref.getString(LEVEL, "") +
                        "&p_damage=" + String.valueOf(sPref.getInt(DAMAGE, 0)) +
                        "&p_location_damage=" + String.valueOf(sPref.getInt(LOCATION_DAMAGE, 0)) +
                        "&p_service=" + sPref.getString(SERVICE, "") +
                        "&p_init_app=" + String.valueOf(sPref.getBoolean(INIT_APP, false)) +
                        "&p_need_callback=" + String.valueOf(sPref.getBoolean(NEED_CALLBACK, false)) +
                        "&p_phone_number=" + sPref.getString(PHONE_NUMBER, "") +
                        "&p_name=" + sPref.getString(NAME, "");
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
                    conn.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    // конвертируем передаваемую строку в UTF-8
                    data = parameters.getBytes("UTF-8");


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

    //сохранение полей
    private void saveFields (){

        Editor ed = sPref.edit();   //объект для редактирования сохранений

        ed.putInt(DAMAGE, p_damage.getSelectedItemPosition());
        ed.putInt(LOCATION_DAMAGE, p_location_damage.getSelectedItemPosition());
        ed.putString(SERVICE, p_service.getText().toString());
        ed.putBoolean(INIT_APP, p_init_app.isChecked());
        ed.putBoolean(NEED_CALLBACK, p_need_callback.isChecked());
        ed.putString(PHONE_NUMBER, p_phone_number.getText().toString());
        ed.putString(NAME, p_name.getText().toString());

        ed.commit();    //сохранение
    }

    //При уничтожении
    @Override
    protected void onDestroy() {
        saveFields();
        super.onDestroy();
    }
}
