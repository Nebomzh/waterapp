package ru.uu.voda;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//для отправки
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

import android.view.ContextMenu;    //Контекстное меню

public class ProblemaActivity  extends AppCompatActivity implements View.OnClickListener, NoticeDialogListener { //добавляем обработчик нажатий прямо в активити; интерфейс для принятия событий диалога

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
    final int REQUEST_CODE_ADDRESS = 1;
    final int REQUEST_CODE_PHOTO = 2;
    final int REQUEST_CODE_GALLERY = 3;

    //ключи запросов пермишенов
    final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    //id Элементов контекстного меню
    final int CONTEXT_MENU_1 = 1;
    final int CONTEXT_MENU_2 = 2;
    final int CONTEXT_MENU_3 = 3;

    //Диалоги
    DialogFragment person_dialog;

    //теги диалогов
    final String PERSON_DIALOG_TAG = "person_dialog_tag";

    View content1;
    View content2;
    View plane;
    MenuItem sendbutton;
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
    ProgressBar progressBar;
    ImageView valve;

    File directory;
    ImageView ivPhoto;

    private SendData mSendData;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);
        content1 = findViewById(R.id.content1);//основной контент
        content2 = findViewById(R.id.content2);//альтернативный контент

        sPref = getPreferences(MODE_PRIVATE);   //получаем сохранялки

        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //кликабельные поля
        findViewById(R.id.personbox).setOnClickListener(this);
        findViewById(R.id.addressbox).setOnClickListener(this);
        ivPhoto.setOnClickListener(this);//прикрепление фоток

        //Добавляем контекстное меню
        registerForContextMenu(ivPhoto); //Для шестой кнопки

        //подгружаем значения из сохранялок
        setFields();//текст для простых полей
        setPersontext();//текст для поля с личными данными
        setAddresstext();//текст для поля с адресом
        setPic();//фотка в рамке

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        valve = (ImageView) findViewById(R.id.ivValve); //вращающийся кран

        //определяемся какой из контентов показать
        mSendData = (SendData) getLastCustomNonConfigurationInstance();//пытаемся получить уже существующий поток отправки (если был поворот экрана и данные уже отправлялись)
        if (mSendData != null) {//если поток был
            mSendData.link(this);//кидаем ссылку на это активити в поток
            if (mSendData.getStatus() == AsyncTask.Status.RUNNING) //если поток был и он ещё работает
                showContent2(false); //показываем экран с процессом отправки
        }
    }

    //обработчик нажатий
    @Override
    public void onClick(View view) {
        // по id определеяем кнопку, вызвавшую этот обработчик
        switch (view.getId()) {
            case R.id.personbox://поле вызывающее диалог
                person_dialog = new PersonDialogFragment();
                person_dialog.show(getFragmentManager(), PERSON_DIALOG_TAG);
                break;
            case R.id.addressbox://вызов адреспикера
                Intent intent = new Intent(getApplicationContext(), AddressPicker.class);
                intent.putExtra(ADDRESS, sPref.getString(ADDRESS, ""));//передаём в интент инфу, что уже есть
                intent.putExtra(SAVELAT, sPref.getFloat(SAVELAT, 0));
                intent.putExtra(SAVELNG, sPref.getFloat(SAVELNG, 0));
                startActivityForResult(intent, REQUEST_CODE_ADDRESS);//запускаем карту для результата
                break;
            case R.id.ivPhoto:
                attachPhoto();//прикрепление фоток
                break;
        }
    }

    // Создание контекстного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //проверка разрешения на чтение файлов
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            super.onCreateContextMenu(menu, view, menuInfo);//создаём меню только при наличии разрешений
            switch (view.getId()) {
                case R.id.ivPhoto:
                    menu.add(0, CONTEXT_MENU_1, 0, R.string.take_picture);
                    menu.add(0, CONTEXT_MENU_2, 0, R.string.take_gallery);
                    menu.add(0, CONTEXT_MENU_3, 0, R.string.detach);
                    break;
            }
        }
    }

    // обработка нажатий пунктов контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_1:
                takePhoto(); //делаем фотку
                break;
            case CONTEXT_MENU_2://аттач из галереи
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
                break;
            case CONTEXT_MENU_3://открепление фотки
                SharedPreferences.Editor ed = sPref.edit();   //объект для редактирования сохранений
                ed.putString(SAVE_PHOTO_PATH, "");//очистка пути к фотке в сохранялке
                ed.commit();    //сохранение
                setPic();//отображение отсутствия фотки
                break;
        }
        return super.onContextItemSelected(item);
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
            case REQUEST_CODE_ADDRESS:
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
            case REQUEST_CODE_GALLERY:
                Uri selectedImage = data.getData();//получаем ури выбранной фотки
                ed.putString(SAVE_PHOTO_PATH, getRealPathFromURI(this, selectedImage)); //сохраняем путь до фото
                ed.commit();    //сохранение
                setPic();//отображаем превьюшку*/
                break;
        }
    }

    //функция выцепления из переданного галереей УРИ настоящего пути до файла (работает какой-то магией, но работает)
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
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
        sendbutton = menu.findItem(R.id.action_send_problem);//находим кнопку отправки
        if (mSendData != null)
            if (mSendData.getStatus() == AsyncTask.Status.RUNNING) //если есть запущенный поток отправки
                sendbutton.setVisible(false);//скрываем кнопку отправки
        return super.onCreateOptionsMenu(menu);
    }

    // обработка нажатий пунктов меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // по id определеяем пункт меню, вызвавший этот обработчик
        switch (item.getItemId()) {
            case R.id.action_send_problem:    //Кнопка отправить
                //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();   //Тост отправить
                plane = findViewById(R.id.action_send_problem);//находим эту кнопку здесь, потому что в онкреэйте ещё не было создано меню
                sendProblem();      //начало посылки проблемы
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendProblem() {
        saveFields();   //сохранение введённых полей

        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);  //элемент анимации тряски
        Animation animScale = AnimationUtils.loadAnimation(this, R.anim.scale);  //элемент анимации масштаба

        String temp_string="";  //временная строка с описанием что не заполнено
        Boolean correct=true;      //флаг, что всё верно

        //проверка на заполненность обязательных полей
        if (sPref.getString(PHONE_NUMBER, "").length()==0) {
            temp_string += getResources().getString(R.string.h10request);
            correct = false;
            if(sPref.getString(NAME, "").length()==0) { //если ещё и имя не заполнено
                persontext.startAnimation(animScale);//встряска имянадписи
                personwarn.startAnimation(animScale);//встряска имяпредупрежухи
            } else { //если незаполнен только телефон, но есть имя
                personphone.startAnimation(animScale);//встряска телефононадписи
                phonewarn.startAnimation(animScale);//встряска телефонопредупрежухи
            }
        }
        if (sPref.getString(ADDRESS, "").length()==0) {
            if (temp_string.length()!=0)
                temp_string += "\n";
            temp_string += getResources().getString(R.string.hpre1);
            correct = false;
            addresstext.startAnimation(animScale);//встряска адресонадписи
            addresswarn.startAnimation(animScale);//встряска адресопредупрежухи
        }

        if (!correct) //если что-то не заполнено
        {
            plane.startAnimation(animShake);//встряска самолётика
            Toast.makeText(this, temp_string, Toast.LENGTH_SHORT).show();    //отображаем сообщение, что не все поля заполнены //TODO заменить тосты на нижние шторки
            return; //выходим из метода, не отправляя данные
        }

        //проверка, что отправка уже не запущена
        if(mSendData!=null)
            if (mSendData.getStatus()== AsyncTask.Status.RUNNING)
                return; //не запускаем новую отправку, если одна уже идёт

        //проверка наличия подключения
        ConnectivityManager myConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //менеджер подключений
        NetworkInfo networkinfo = myConnMgr.getActiveNetworkInfo();//узнаём инфу о сети
        if (networkinfo != null && networkinfo.isConnected()) { //если есть подключение
            mSendData = new SendData();
            mSendData.link(this);//кидаем ссылку на это активити в поток
            mSendData.execute();// запускаем в новом потоке отправку данных
        } else {
            plane.startAnimation(animShake);//анимация самолётика туда-сюда
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();//говорим, что нет Инета //TODO заменить тосты на нижние шторки
        }
    }

    static private class SendData extends AsyncTask<Void, Integer, Boolean> { //можно будет использовать вместо асинхтаска сервис, если времена отправок будут сильно большими

    String resultString = null; //строка с результатами отправки, можно использовать для отображения причин ошибок
    boolean result=false; //успешность результата (пока отправка не произошла считаем неудачным)

    ProblemaActivity activity;//родительское активити

    // получаем ссылку на Activity
    void link(ProblemaActivity act) {
        activity = act;
    }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.showContent2(true);//показываем анимацию отправки, скрывая контент с полями
        }

        final static String lineEnd = "\r\n";// Конец строки
        final static String twoHyphens = "--";// Два тире
        final static String boundary =  "*****";// Граница
        final static String separator = twoHyphens + boundary + lineEnd;// Разделитель

        @Override
        protected Boolean doInBackground(Void... params) {

            // Адрес метода api для загрузки файла на сервер
            final String API_FILES_UPLOADING_PATH = "http://vodaonline74.ru/adm2/server.php";//TODO указать действующий адрес
            //final String API_FILES_UPLOADING_PATH = "http://virtual-pc/formeasy.php";

            // Ключ, под которым файл передается на сервер
            final String FORM_FILE_NAME = "uploadfile";

            try {
                // Configure connection
                URL uploadUrl = new URL(API_FILES_UPLOADING_PATH);// Создание ссылки для отправки файла
                HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();// Создание соединения для отправки файла
                connection.setDoInput(true);// Разрешение ввода соединению
                connection.setDoOutput(true);// Разрешение вывода соединению
                connection.setUseCaches(false);// Отключение кеширования
                connection.setRequestMethod("POST");// Задание запросу типа POST
                // Задание необходимых свойств запросу
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                // Создание потока для записи в соединение
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                //Отправка текстовых полей
                writeTextField(outputStream, "damage", String.valueOf(activity.sPref.getInt(activity.DAMAGE, 0)));
                writeTextField(outputStream, "location_damage", String.valueOf(activity.sPref.getInt(activity.LOCATION_DAMAGE, 0)));
                writeTextField(outputStream, "service", activity.sPref.getString(activity.SERVICE, ""));
                writeTextField(outputStream, "init_app", String.valueOf(activity.sPref.getBoolean(activity.INIT_APP, false)));
                writeTextField(outputStream, "need_callback", String.valueOf(activity.sPref.getBoolean(activity.NEED_CALLBACK, false)));
                writeTextField(outputStream, "phone_number", activity.sPref.getString(activity.PHONE_NUMBER, ""));
                writeTextField(outputStream, "name", activity.sPref.getString(activity.NAME, ""));
                writeTextField(outputStream, "address", activity.sPref.getString(activity.ADDRESS, ""));
                writeTextField(outputStream, "lat", String.valueOf(activity.sPref.getFloat(activity.SAVELAT, 0)));
                writeTextField(outputStream, "lng", String.valueOf(activity.sPref.getFloat(activity.SAVELNG, 0)));

                // Путь к файлу в памяти устройства
                String filePath = activity.sPref.getString(activity.SAVE_PHOTO_PATH, "");

                //Проверки, что файл вообще получится отправить
                if (    filePath.length()!=0  &&  //если есть что отправлять
                        ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && //если есть разрешение на чтение файлов
                        (new File(filePath)).exists() ) { //если файл существует

                    // Формирование multipart контента с изображением

                    outputStream.writeBytes(separator);// Начало контента
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + FORM_FILE_NAME + "\"; " +
                            "filename=\"" + filePath + "\"" + lineEnd);// Заголовок элемента формы
                    outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);// Тип данных элемента формы
                    outputStream.writeBytes(lineEnd);// Конец заголовка

                    //TODO написать сжатие файла на случай если будут отправлять слишком крупные

                    // Поток для считывания файла в оперативную память
                    FileInputStream fileInputStream = new FileInputStream(new File(filePath));

                    //переменные для отображения прогресса
                    int fileSize = fileInputStream.available(); //определяем размер файла для подсчёта прогресса
                    int sentBytes = 0;

                    // Переменные для считывания файла в оперативную память
                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 8 * 1024; //можно и 1024*1024, но сделаем 8 КБ чтобы прогрессбар чаще обновлялся
                    bufferSize = Math.min(fileSize, maxBufferSize);
                    buffer = new byte[bufferSize];

                    publishProgress(0);//обнуляем прогресс

                    // Считывание файла в оперативную память и запись его в соединение
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);// Write buffer to socket
                        sentBytes += bufferSize;//записываем сколько байт уже послали

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        publishProgress((int) (sentBytes * 100 / fileSize));//обновляем прогресс (можно показывать и промежуточный прогресс вторым параметром (int)((sentBytes + bufferSize) * 100 / fileSize) но он получается слишком узким, поэтому не будем тратить на его просчёты время)
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // Конец элемента формы
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Закрытие соединений и потоков
                    fileInputStream.close();
                }
                outputStream.flush();
                outputStream.close();

                // Получение ответа от сервера
                int serverResponseCode = connection.getResponseCode();

                // Считка ответа от сервера в зависимости от успеха
                if(serverResponseCode == 200) {
                    result = true;
                    //можно получать дополнительные сведения об ошибках для отладки, но пользователям будем просто писать, что отправка не удалась
                    //resultString = readStream(connection.getInputStream());
                } else {
                    //resultString = readStream(connection.getErrorStream());
                    //resultString = connection.getResponseMessage() + " . Error Code : " + serverResponseCode;
                }
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //resultString = "MalformedURLException:" + e.getMessage();
            } catch (ProtocolException e) {
                e.printStackTrace();
                //resultString = "ProtocolException:" + e.getMessage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //resultString = "FileNotFoundException:" + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                //resultString = "IOException:" + e.getMessage();
            }

            //задержка, чтобы люди успели насладиться анимацией отправки
            //try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

            return result;
        }

        //отправка текстовых полей
        private void writeTextField(DataOutputStream outputStream, String field, String value) throws IOException {
            outputStream.writeBytes(separator);// Начало контента
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + field + "\"" +lineEnd);// Заголовок элемента формы
            outputStream.writeBytes("Content-Type: text/plain" + lineEnd);// Тип данных элемента формы
            outputStream.writeBytes(lineEnd);// Конец заголовка

            //передаём текстовые значения
            byte[] data = value.getBytes("UTF-8");// конвертируем передаваемую строку в UTF-8
            outputStream.write(data);

            outputStream.writeBytes(lineEnd);// Конец элемента формы
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        }

        // Считка потока в строку
        public String readStream(InputStream inputStream) throws IOException {
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            activity.progressBar.setIndeterminate(false);//отключаем режим неопределённости
            activity.progressBar.setProgress(values[0]);//записываем прошедший прогресс
            //activity.progressBar.setSecondaryProgress(values[1]);//записываем прогресс над которым сейчас идёт работа (получался незаметно узким, поэтому для быстродействия решено не отображать)
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            activity.showContent1();//показываем основной контент, убирая анимацию отправки
            if(result) { //если успешно отправилось
                Toast toastsendsuccess = Toast.makeText(activity.getBaseContext(), R.string.send_success, Toast.LENGTH_LONG);
                LinearLayout toastContainer = (LinearLayout) toastsendsuccess.getView();//перехватываем вид тоста
                ImageView sampleImageView = new ImageView(activity.getApplicationContext());//бахаем новую картинку
                sampleImageView.setImageResource(R.drawable.ic_send_white_48px);//помещаем значёк в картинку
                toastContainer.addView(sampleImageView, 0);//добавляем картинку в тост вперёд текста
                toastsendsuccess.show();//показываем
                activity.clearFields(); //очистка полей после отправки, чтобы не отправляли одну проблему по несколько раз //TODO раскомментить
            }
            else {
                Toast toastsenderror = Toast.makeText(activity.getBaseContext(), R.string.send_fail, Toast.LENGTH_LONG);//можно выводить resultString, для подробностей почему не прошла отправка
                LinearLayout toastContainer = (LinearLayout) toastsenderror.getView();//перехватываем вид тоста
                ImageView sampleImageView = new ImageView(activity.getApplicationContext());//бахаем новую картинку
                sampleImageView.setImageResource(R.drawable.error);//помещаем значёк в картинку
                toastContainer.addView(sampleImageView, 0);//добавляем картинку в тост вперёд текста
                toastsenderror.show();//показываем
            }
        }
    }

    //сохранение объекта отправки на случай поворота экрана
    public Object onRetainCustomNonConfigurationInstance() {
        return mSendData;
    }

    //показ первого контента
    private void showContent1() {
        content1.setVisibility(View.VISIBLE);//возвращаем все элементы
        Animation animFade = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade);  //элемент анимации исчезновешния
        content2.startAnimation(animFade);   //старт анимации исчезновения
        content2.setVisibility(View.GONE);//убираем альтернативный контент с анимацией
        sendbutton.setVisible(true);//делаем кнопку отправки видимой
        if (plane != null) //если есть ссылка на самолёт (значит его уже запускали и его нужно вернуть, а если ссылки нет, значит активити пересоздавали и возвращать ничего и не надо)
            plane.clearAnimation();//и возвращаем самолётик на место
    }

    //показ второго контента
    private void showContent2(boolean animation) {
        content1.setVisibility(View.GONE);//убираем элементы редактирования, чтобы пользователи не меняли содержимое во время отправки
        content2.setVisibility(View.VISIBLE);//показываем альтернативный контент (экран с анимацией)
        if (animation) { //если необходима анимация при смене экранов
            Animation animAlpha = AnimationUtils.loadAnimation(getBaseContext(), R.anim.alpha);  //элемент анимации прозрачности
            content2.startAnimation(animAlpha);   //старт анимации появления
            Animation animTrans = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate);  //элемент анимации передвижения
            plane.startAnimation(animTrans);//самолётик улетает
        }
        progressBar.setIndeterminate(true);//включаем режим неопределённости, пока отправка сама не задаст прогресс
        Animation animRotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);  //элемент анимации вращения
        valve.startAnimation(animRotate);   //старт анимации вращения
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

    //Сохраняем поля, если приложуха теряет фокус (лучше онДестроя, потому что будет вызвано, даже если приложуху убивают тасккилером)
    @Override
    protected void onStop() {
        saveFields();
        super.onStop();
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
            openContextMenu(ivPhoto);  //открываем контекстное меню с выбором сделать/прикрепить/открепить фотку
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
                    openContextMenu(ivPhoto);  //открываем контекстное меню с выбором сделать/прикрепить/открепить фотку
                } else { //чел не предоставил разшение
                    Toast.makeText(this, R.string.storage_denied, Toast.LENGTH_LONG).show(); //сообщение об отсутствии разрешения на прикрепление файлов
                }
            }
        }
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