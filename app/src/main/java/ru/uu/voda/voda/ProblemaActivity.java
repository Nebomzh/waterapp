package ru.uu.voda.voda;


import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

//для проверки пермишенов
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;//для запроса пермишена
import android.support.annotation.NonNull;

import android.view.ViewTreeObserver; //для прорисовки ImageView с фоткой

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
    final String CURRENT_PHOTO_PATH = "сurrentPhotoPath";
    final String SAVE_PHOTO_PATH = "savePhotoPath";

    //коды запусков для результатов других активти
    final int ADDRESS_REQUEST_CODE = 1;

    //ключи запросов пермишенов
    final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

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
    final int REQUEST_CODE_PHOTO = 2;
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
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        //подгружаем значения из сохранялок
        setFields();//текст для простых полей
        setPersontext();//текст для поля с личными данными
        setAddresstext();//текст для поля с адресом
        setPic();//фотка в рамке

        //прикрепление фоток
        ivPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attachPhoto();
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
        if (resultCode != RESULT_OK)
            return;
        SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
        switch (requestCode) {
            case ADDRESS_REQUEST_CODE:
                if (data == null)
                    return;
                //сохраняем полученную от карты инфу
                ed.putString(ADDRESS, data.getStringExtra(ADDRESS));
                ed.putFloat(SAVELAT, data.getFloatExtra(SAVELAT, 0));
                ed.putFloat(SAVELNG, data.getFloatExtra(SAVELNG, 0));
                ed.commit();    //сохранение
                setAddresstext();//текст для поля с адресом
                break;
            case REQUEST_CODE_PHOTO:
                //сохраняем путь до фото
                ed.putString(SAVE_PHOTO_PATH, sPref.getString(CURRENT_PHOTO_PATH, "")); //из временного в постоянный
                ed.commit();    //сохранение
                setPic();//отображаем превьюшку
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
        ed.putString(SAVE_PHOTO_PATH, "");

        ed.commit();    //сохранение

        setFields();//отображение
        setAddresstext();
        setPic();
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));
        if (!directory.exists())
            directory.mkdirs();
    }

    //прикрепить фотку
    private void attachPhoto() {
        //проверка разрешения на чтение файлов
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.storage_permission, Toast.LENGTH_LONG).show(); //сообщение об отсутствии разрешения на прикрепление файлов
            //Запрос разрешений
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
        else //если разрешение есть
            selectPhotoAction();//показываем выбор сделать/прикрепить/открепить фотку
    }

    //Обработка ответа пользователя на получение разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Чел предоставил разрешение
                    selectPhotoAction();//показываем выбор сделать/прикрепить/открепить фотку
                } else { //чел не предоставил разшение
                    Toast.makeText(this, R.string.storage_denied, Toast.LENGTH_LONG).show(); //сообщение об отсутствии разрешения на прикрепление файлов
                }
            }
        }
    }

    //выбор способа прикрепления фотки
    private void selectPhotoAction() {
        //TODO написать здесь контекстное меню с фоткой, атачем и удалением
        takePhoto(); //делаем фотку
    }

    //сделать фотку
    private void takePhoto() {
        //проверка наличия камеры в устройстве
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) { //если камеры нет
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_LONG).show();//сообщение об отсутствии камеры
            return;//не фоткаем
        }
        // create new Intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //проверка наличия приложения для съёмки (защита от краша, при старте активитифоррезалт, если такого приложения не будет на устройстве)
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) { //если приложухи нет
            Toast.makeText(this, R.string.no_camera_app, Toast.LENGTH_LONG).show(); //сообщение об отсутствии камерной приложухи
            return;//не фоткаем
        }
        createDirectory();//создаём папку для фоток
        String mCurrentPhotoPath = directory.getPath() + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"; //путь фотки = путь папки + имя файла
        SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
        ed.putString(CURRENT_PHOTO_PATH, mCurrentPhotoPath); //сохраняем возможный путь будущей фотки
        ed.commit();    //сохранение
        android.net.Uri mPhotoUri = Uri.fromFile(new File(mCurrentPhotoPath)); //ури фотки
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);//суём ури в интент
        startActivityForResult(takePictureIntent, REQUEST_CODE_PHOTO);//запускаем приложуху с камерой
    }

    //функция масштабирующая и ставящая фотку в превью
    private void setPic() {
        String savePhotoPath = sPref.getString(SAVE_PHOTO_PATH, "");
        if (savePhotoPath.length()==0) { //если нечего вставлять
            ivPhoto.setImageResource(R.drawable.ic_add_a_photo_black_18px); //ставим значёк с фотиком
            return;
        }
        //проверка разрешения на чтение файлов
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ivPhoto.setImageResource(R.drawable.ic_add_a_photo_black_18px); //ставим значёк с фотиком
            return;
        }
        //проверка что файл существует
        if(! (new File(savePhotoPath)).exists() ) { //если файла не существует
            SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
            ed.putString(SAVE_PHOTO_PATH, ""); //очищаем сохранённый путь
            ed.commit();    //сохранение
            ivPhoto.setImageResource(R.drawable.ic_add_a_photo_black_18px); //ставим значёк с фотиком
            return;
        }

        // Get the dimensions of the View
        int targetW = ivPhoto.getWidth();
        int targetH = ivPhoto.getHeight();

        if( targetW == 0 && targetH == 0 ) { //если ширина и высота ещё не известны (скорее всего вьюха ещё не отрисована)
            //чтобы получать ширину и высоту после прорисовки, а не когда они дают 0
            ViewTreeObserver vto = ivPhoto.getViewTreeObserver(); //вешаем на вьюху листенер
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    // Remove after the first run so it doesn't fire forever
                    ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                    setPic();//вызываем сами себя (но этот вызов случится позже, при прорисовке)
                    return true;
                }
            });
            return; //пока фотку не вставляем ибо ещё некуда
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(savePhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(savePhotoPath, bmOptions);
        ivPhoto.setImageBitmap(bitmap);
    }
}
//TODO подгрузка фоток из галереи
//TODO отправка фотки на сервак