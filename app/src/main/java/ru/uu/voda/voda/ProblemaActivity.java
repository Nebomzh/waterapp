package ru.uu.voda.voda;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class ProblemaActivity  extends AppCompatActivity implements NoticeDialogListener { //добавляем интерфейс для принятия событий диалога

    SharedPreferences sPref;    //объект сохранялок
    //ключи сохранялок
    final String DAMAGE = "damage";
    final String LOCATION_DAMAGE = "location_damage";
    final String SERVICE = "service";
    final String INIT_APP = "init_app";
    final String NEED_CALLBACK = "need_callback";
    final String PHONE_NUMBER = "phone_number";
    final String NAME = "name";
    final String ADDRESS = "address";
    final String SAVELAT = "savelat";
    final String SAVELNG = "savelng";

    //коды запусков для результатов других активти
    final int ADDRESS_REQUEST_CODE = 1;

    //Диалоги
    DialogFragment person_dialog;

    //теги диалогов
    final String PERSON_DIALOG_TAG = "person_dialog_tag";

    public static String server = "vodaonline74.ru";

    TextView persontext;
    ImageView personwarn;
    TextView personname;
    TextView personphone;
    ImageView phonewarn;
    public Spinner p_damage;
    public Spinner p_location_damage;
    public EditText p_service;
    public CheckBox p_init_app;
    TextView addresstext;
    ImageView addresswarn;

    File directory;
    final int TYPE_PHOTO = 1;
    final int REQUEST_CODE_PHOTO = 2;
    final String TAG = "myLogs";
    ImageView ivPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);

        sPref = getPreferences(MODE_PRIVATE);   //получаем сохранялки

        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //поля вызывающее диалог
        findViewById(R.id.personbox).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                person_dialog = new PersonDialogFragment();
                person_dialog.show(getFragmentManager(), PERSON_DIALOG_TAG);
            }
        });
        findViewById(R.id.addressbox).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddressPicker.class);
                intent.putExtra(ADDRESS, sPref.getString(ADDRESS, ""));//передаём в интент инфу, что уже есть
                intent.putExtra(SAVELAT, sPref.getFloat(SAVELAT, 0));
                intent.putExtra(SAVELNG, sPref.getFloat(SAVELNG, 0));
                startActivityForResult(intent,ADDRESS_REQUEST_CODE);//запускаем карту для результата
            }
        });

        //обратимся к нашим полям
        persontext = (TextView) findViewById(R.id.persontext);
        personwarn = (ImageView) findViewById(R.id.personwarn);
        personname = (TextView) findViewById(R.id.personname);
        personphone = (TextView) findViewById(R.id.personphone);
        phonewarn = (ImageView) findViewById(R.id.phonewarn);
        p_damage = (Spinner) findViewById(R.id.Spinner5);
        p_location_damage = (Spinner) findViewById(R.id.Spinner6);
        p_service = (EditText) findViewById(R.id.EditText7);
        p_init_app = (CheckBox) findViewById(R.id.CheckBox8);
        addresstext = (TextView) findViewById(R.id.addresstext);
        addresswarn = (ImageView) findViewById(R.id.addresswarn);

        //подгружаем значения из сохранялок
        setFields();//текст для простых полей
        setPersontext();//текст для поля с личными данными
        setAddresstext();//текст для поля с адресом

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        ivPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));    //с этой строчкой на моём телефоне приложение не возвращается из камеры.. 
                startActivityForResult(intent, REQUEST_CODE_PHOTO);
            }
        });
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
        }
    }
    /*@Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }*/
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    }

    //принятие инфы от активити запущенного на результат
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
            case ADDRESS_REQUEST_CODE:
                //сохраняем полученную от карты инфу
                SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
                ed.putString(ADDRESS, data.getStringExtra(ADDRESS));
                ed.putFloat(SAVELAT, data.getFloatExtra(SAVELAT, 0));
                ed.putFloat(SAVELNG, data.getFloatExtra(SAVELNG, 0));
                ed.commit();    //сохранение
                setAddresstext();//текст для поля с адресом
                break;
            case REQUEST_CODE_PHOTO:

                    if (resultCode == RESULT_OK) {
                        if (data == null) {
                            Log.d(TAG, "Intent is null");
                        } else {
                            Log.d(TAG, "Photo uri: " + data.getData());
                            Bundle bndl = data.getExtras();
                            if (bndl != null) {
                                Object obj = data.getExtras().get("data");
                                if (obj instanceof Bitmap) {
                                    Bitmap bitmap = (Bitmap) obj;
                                    Log.d(TAG, "bitmap " + bitmap.getWidth() + " x " + bitmap.getHeight());
                                    ivPhoto.setImageBitmap(bitmap);
                                }
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        Log.d(TAG, "Canceled");
                    }
                break;
        }
    }

    private void setFields() {
        p_damage.setSelection(sPref.getInt(DAMAGE, 0));
        p_location_damage.setSelection(sPref.getInt(LOCATION_DAMAGE, 0));
        p_service.setText(sPref.getString(SERVICE, ""));
        p_init_app.setChecked(sPref.getBoolean(INIT_APP, false));
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

    //текст для поля с адресом
    private void setAddresstext() {
        //подгружаем значения из сохранялок
        if (sPref.getString(ADDRESS, "").length()!=0) {
            addresstext.setText(sPref.getString(ADDRESS, ""));
            addresswarn.setVisibility(View.GONE);
        }
        else {  //если в сохранялке ничего нет
            addresstext.setText(getResources().getString(R.string.hpre1));
            addresswarn.setVisibility(View.VISIBLE);
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

        String temp_string="";  //временная строка с описанием что не заполнено
        Boolean correct=true;      //флаг, что всё верно

        //проверка на заполненность обязательных полей
        if (sPref.getString(PHONE_NUMBER, "").length()==0) {
            temp_string += getResources().getString(R.string.h10request);
            correct = false;
        }
        if (sPref.getString(ADDRESS, "").length()==0) {
            if (temp_string.length()!=0)
                temp_string += "\n";
            temp_string += getResources().getString(R.string.hpre1);
            correct = false;
        }

        if (!correct) //если что-то не заполнено
        {
            Toast.makeText(this, temp_string, Toast.LENGTH_LONG).show();    //отображаем сообщение, что не все поля заполнены
            return; //выходим из метода, не отправляя данные
        }

        try {
            new SendData().execute();
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

                String parameters =
                        "&p_damage=" + String.valueOf(sPref.getInt(DAMAGE, 0)) +
                        "&p_location_damage=" + String.valueOf(sPref.getInt(LOCATION_DAMAGE, 0)) +
                        "&p_service=" + sPref.getString(SERVICE, "") +
                        "&p_init_app=" + String.valueOf(sPref.getBoolean(INIT_APP, false)) +
                        "&p_need_callback=" + String.valueOf(sPref.getBoolean(NEED_CALLBACK, false)) +
                        "&p_phone_number=" + sPref.getString(PHONE_NUMBER, "") +
                        "&p_name=" + sPref.getString(NAME, "") +
                        "&p_address=" + sPref.getString(ADDRESS, "") +
                        "&p_lat=" + sPref.getString(SAVELAT, "") +
                        "&p_lng=" + sPref.getString(SAVELNG, ""); //TODO написать бэкенд, который принимает адрес и координаты
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

                    } else { //TODO написать что делать, если не всё ок (хотя бы тост, что не всё ок и не очищать поля)
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
            Toast.makeText(getApplicationContext(), "Данные переданы!", Toast.LENGTH_LONG).show(); //TODO сейчас пишет, что данные переданы, даже если нет инета. Разобраться, чтобы реально отслеживался успех передачи
            clearFields(); //очистка полей после отправки, чтобы не отправляли одну проблему по несколько раза
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

    //Очистка полей (после успешной отправки)
    private  void clearFields() {
        Editor ed = sPref.edit();   //объект для редактирования сохранений

        ed.putInt(DAMAGE, 0);
        ed.putInt(LOCATION_DAMAGE, 0);
        ed.putString(SERVICE, "");
        ed.putBoolean(INIT_APP, false);
        ed.putString(ADDRESS, "");
        ed.putFloat(SAVELAT, 0);
        ed.putFloat(SAVELNG, 0);

        ed.commit();    //сохранение

        setFields();//отображение
        setAddresstext();
    }
    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
                break;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }
}
