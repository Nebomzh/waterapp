package ru.uu.voda.voda;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.Toolbar; //Тулбар

import android.view.Menu;       //меню
import android.view.MenuItem;   //пункт меню

import android.content.SharedPreferences;           //для работы с сохранялками
import android.content.SharedPreferences.Editor;    //для редактирования сохранялок

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProblemaActivity  extends AppCompatActivity implements NoticeDialogListener { //добавляем интерфейс для принятия событий диалога

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
    ListView listView;

    //Диалоги
    DialogFragment person_dialog;
    DialogFragment address_dialog;

    //теги диалогов
    final String PERSON_DIALOG_TAG = "person_dialog_tag";
    final String ADDRESS_DIALOG_TAG = "address_dialog_tag";

    public static String server = "vodaonline74.ru";
    static List<Location> locations = new ArrayList<>();
    static SharedPreferences sp;

    static ArrayList<String> places = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    TextView persontext;
    ImageView personwarn;
    TextView personname;
    TextView personphone;
    ImageView phonewarn;
    TextView placetext;
    ImageView placewarn;
    TextView placedistrict;
    TextView placestreet;
    ImageView streetwarn;
    TextView placehouse;
    ImageView housewarn;
    TextView placelevel;
    ImageView levelwarn;
    //public Spinner p_district;
    //public EditText p_street;
    //public EditText p_house;
    //public EditText p_level;
    public Spinner p_damage;
    public Spinner p_location_damage;
    public EditText p_service;
    public CheckBox p_init_app;
    //public CheckBox p_need_callback;
    //public EditText p_phone_number;
    //public EditText p_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);

        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);

        // Retrieve data with the Gson dependency by converting List<Location> into a JSON representation
        sp = this.getSharedPreferences("com.example.cliff.locationgetter", Context.MODE_PRIVATE);
        retrieveLocations();

        // update the ListView depending on the SharedPreferences data
        if (locations == null) {
            locations = new ArrayList<>();
            locations.add(new Location("Add a location...", 91.0, 181.0));
            updatePlaces();
        }
        else {
            updatePlaces();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {

                if (i == 0) {
                    Toast.makeText(getApplicationContext(), "You don't want to do that!", Toast.LENGTH_LONG).show();
                    return true;
                }

                final int itemToDelete = i;

                new AlertDialog.Builder(ProblemaActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this location?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                locations.remove(itemToDelete);
                                places.remove(itemToDelete);
                                arrayAdapter.notifyDataSetChanged();

                                saveLocations();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });



        sPref = getPreferences(MODE_PRIVATE);   //получаем сохранялки

        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //findViewById(R.id.personwarn).setVisibility(View.GONE);

        //поля вызывающее диалог
        findViewById(R.id.personbox).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                person_dialog = new PersonDialogFragment();
                person_dialog.show(getFragmentManager(), PERSON_DIALOG_TAG);
            }
        });
        findViewById(R.id.placebox).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                address_dialog = new AddressDialogFragment();
                address_dialog.show(getFragmentManager(), ADDRESS_DIALOG_TAG);
            }
        });

        //обратимся к нашим полям
        persontext = (TextView) findViewById(R.id.persontext);
        personwarn = (ImageView) findViewById(R.id.personwarn);
        personname = (TextView) findViewById(R.id.personname);
        personphone = (TextView) findViewById(R.id.personphone);
        phonewarn = (ImageView) findViewById(R.id.phonewarn);
        placetext = (TextView) findViewById(R.id.placetext);
        placewarn = (ImageView) findViewById(R.id.placewarn);
        placedistrict = (TextView) findViewById(R.id.placedistrict);
        placestreet = (TextView) findViewById(R.id.placestreet);
        streetwarn = (ImageView) findViewById(R.id.streetwarn);
        placehouse = (TextView) findViewById(R.id.placehouse);
        housewarn = (ImageView) findViewById(R.id.housewarn);
        placelevel = (TextView) findViewById(R.id.placelevel);
        levelwarn = (ImageView) findViewById(R.id.levelwarn);
        //p_district = (Spinner) findViewById(R.id.Spinner1);
        //p_street = (EditText) findViewById(R.id.EditText2);
        //p_house = (EditText) findViewById(R.id.EditText3);
        //p_level = (EditText) findViewById(R.id.EditText4);
        p_damage = (Spinner) findViewById(R.id.Spinner5);
        p_location_damage = (Spinner) findViewById(R.id.Spinner6);
        p_service = (EditText) findViewById(R.id.EditText7);
        p_init_app = (CheckBox) findViewById(R.id.CheckBox8);
        //p_need_callback = (CheckBox) findViewById(R.id.CheckBox9);
        //p_phone_number = (EditText) findViewById(R.id.EditText10);
        //p_name = (EditText) findViewById(R.id.EditText11);

        //подгружаем значения из сохранялок
        setPersontext();//текст для поля с личными данными
        setPlacetext();//текст для поля с адресом
        p_damage.setSelection(sPref.getInt(DAMAGE, 0));
        p_location_damage.setSelection(sPref.getInt(LOCATION_DAMAGE, 0));
        p_service.setText(sPref.getString(SERVICE, ""));
        p_init_app.setChecked(sPref.getBoolean(INIT_APP, false));
        //p_need_callback.setChecked(sPref.getBoolean(NEED_CALLBACK, false));
        //p_phone_number.setText(sPref.getString(PHONE_NUMBER, ""));
        //p_name.setText(sPref.getString(NAME, ""));
    }

    private void updatePlaces() {
        places.clear();
        places.add("Add a location...");

        for (int i = 1; i < locations.size(); i++) {
            places.add(locations.get(i).getPlace());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public static void addLocation (String address, double latitude, double longitude) {
        locations.add(new Location(address, latitude, longitude));
        places.add(address);
        arrayAdapter.notifyDataSetChanged();
    }

    private void retrieveLocations() {
        Gson gson = new Gson();
        String response = sp.getString("locations" , "");

        // Return the Type representing the direct superclass of the entity
        Type type = new TypeToken<List<Location>>(){}.getType();
        locations = gson.fromJson(response, type);
    }

    public static void saveLocations() {
        // Convert the ArrayList to a Json-formatted string using the Gson dependency, which handles generic Lists
        SharedPreferences.Editor prefsEditor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(ProblemaActivity.locations);
        prefsEditor.putString("locations", json);
        prefsEditor.apply();
    }

    //Интерфейсы принятия инфы от диалоговых окон
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Editor ed = sPref.edit();   //объект для редактирования сохранений
        switch (dialog.getTag()) {  //определение диалога
            case PERSON_DIALOG_TAG: //диалог с личными данными
                CheckBox p_need_callback = (CheckBox) dialog.getDialog().findViewById(R.id.CheckBox9);
                EditText p_phone_number = (EditText) dialog.getDialog().findViewById(R.id.EditText10);
                EditText p_name = (EditText) dialog.getDialog().findViewById(R.id.EditText11);

                ed.putBoolean(NEED_CALLBACK, p_need_callback.isChecked());
                ed.putString(PHONE_NUMBER, p_phone_number.getText().toString());
                ed.putString(NAME, p_name.getText().toString());
                ed.commit();    //сохранение

                setPersontext(); //текст для поля с личными данными
                break;
            case ADDRESS_DIALOG_TAG: //диалог с адресом
                Spinner p_district = (Spinner) dialog.getDialog().findViewById(R.id.Spinner1);
                EditText p_street = (EditText) dialog.getDialog().findViewById(R.id.EditText2);
                EditText p_house = (EditText) dialog.getDialog().findViewById(R.id.EditText3);
                EditText p_level = (EditText) dialog.getDialog().findViewById(R.id.EditText4);

                ed.putInt(DISTRICT, p_district.getSelectedItemPosition());
                ed.putString(STREET, p_street.getText().toString());
                ed.putString(HOUSE, p_house.getText().toString());
                ed.putString(LEVEL, p_level.getText().toString());
                ed.commit();    //сохранение

                setPlacetext(); //текст для поля с местом
                break;
        }
    }
    /*@Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }*/
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    }

    //текст для поля с личными данными
    private void setPersontext() {
        Boolean something=false;      //флаг, что что-то ввели
        //подгружаем значения из сохранялок
        if (sPref.getString(NAME, "").length()!=0) {
            something = true;
            personname.setText(sPref.getString(NAME, ""));
            personname.setVisibility(View.VISIBLE);
        }
        else
            personname.setVisibility(View.GONE);
        if (sPref.getString(PHONE_NUMBER, "").length()!=0) {
            something = true;
            personphone.setText(sPref.getString(PHONE_NUMBER, ""));
            personphone.setVisibility(View.VISIBLE);
            if (sPref.getBoolean(NEED_CALLBACK, false))
                phonewarn.setImageResource(R.drawable.alarm);
            else
                phonewarn.setImageResource(R.drawable.disable_alarm);
            phonewarn.setVisibility(View.VISIBLE);
        }
        else {
            if (something) {
                personphone.setText(R.string.h10request);
                personphone.setVisibility(View.VISIBLE);
                phonewarn.setImageResource(R.drawable.warning);
                phonewarn.setVisibility(View.VISIBLE);
            }
            else {
                personphone.setVisibility(View.GONE);
                phonewarn.setVisibility(View.GONE);
            }
        }

        if (something) { //если что-то есть, скрываем общее предупреждение
            persontext.setVisibility(View.GONE);
            personwarn.setVisibility(View.GONE);
        }
        else { //иначе отображаем
            persontext.setVisibility(View.VISIBLE);
            personwarn.setVisibility(View.VISIBLE);
        }
    }

    //текст для поля с местом
    private void setPlacetext() {
        Boolean something=false;      //флаг, что что-то ввели
        //подгружаем значения из сохранялок
        if (sPref.getInt(DISTRICT, 0)!=0)
            something = true;
        placedistrict.setText(getResources().getStringArray(R.array.Spinner1_list) [sPref.getInt(DISTRICT, 0)]);
        placedistrict.setVisibility(View.VISIBLE);
        if (sPref.getString(STREET, "").length()!=0 && sPref.getString(HOUSE, "").length()!=0) {
            something = true;
            placestreet.setText(sPref.getString(STREET, "") + " " + sPref.getString(HOUSE, ""));
            placestreet.setVisibility(View.VISIBLE);
            streetwarn.setVisibility(View.GONE);
            placehouse.setVisibility(View.GONE);
            housewarn.setVisibility(View.GONE);
        }
        else {
            if (sPref.getString(STREET, "").length() != 0) {
                something = true;
                placestreet.setText(sPref.getString(STREET, ""));
                placestreet.setVisibility(View.VISIBLE);
                streetwarn.setVisibility(View.GONE);
            } else {
                placestreet.setText(getResources().getString(R.string.h2request));
                placestreet.setVisibility(View.VISIBLE);
                streetwarn.setVisibility(View.VISIBLE);
            }
            if (sPref.getString(HOUSE, "").length() != 0) {
                something = true;
                placehouse.setText(sPref.getString(HOUSE, ""));
                placehouse.setVisibility(View.VISIBLE);
                housewarn.setVisibility(View.GONE);
            } else {
                placehouse.setText(getResources().getString(R.string.h3request));
                placehouse.setVisibility(View.VISIBLE);
                housewarn.setVisibility(View.VISIBLE);
            }
        }
        if (sPref.getString(LEVEL, "").length()!=0) {
            something = true;
            placelevel.setText("Этаж " + sPref.getString(LEVEL, ""));
            placelevel.setVisibility(View.VISIBLE);
            levelwarn.setVisibility(View.GONE);
        }
        else {
            placelevel.setText(getResources().getString(R.string.h4request));
            placelevel.setVisibility(View.VISIBLE);
            levelwarn.setVisibility(View.VISIBLE);
        }

        if (something) { //если что-то есть, скрываем общее предупреждение
            placetext.setVisibility(View.GONE);
            placewarn.setVisibility(View.GONE);
        }
        else { //иначе отображаем, скрывая остальное
            placetext.setVisibility(View.VISIBLE);
            placewarn.setVisibility(View.VISIBLE);
            placedistrict.setVisibility(View.GONE);
            placestreet.setVisibility(View.GONE);
            streetwarn.setVisibility(View.GONE);
            placehouse.setVisibility(View.GONE);
            housewarn.setVisibility(View.GONE);
            placelevel.setVisibility(View.GONE);
            levelwarn.setVisibility(View.GONE);
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

        ed.commit();    //сохранение
    }

    //При уничтожении
    @Override
    protected void onDestroy() {
        saveFields();
        super.onDestroy();
    }
}
