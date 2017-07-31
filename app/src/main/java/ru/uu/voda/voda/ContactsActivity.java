package ru.uu.voda.voda;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class ContactsActivity extends AppCompatActivity {

    String[] spinner20 = {"Другой", "Калининский район", "Курчатовский район", "Центарльный район", "Советский район", "Тракторозоводский район" , "Металургический район"};
    public static String fio_in = "";
    public static String dol_in = "";
    public static String tel_in = "";
    public static String server = "vodaonline74.ru";

    public EditText fio;
    public EditText dol;
    public EditText tel;
    //public EditText p_district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Button btn = (Button) findViewById(R.id.button1);
        fio = (EditText) findViewById(R.id.editText1);
        dol = (EditText) findViewById(R.id.editText2);
        tel = (EditText) findViewById(R.id.editText3);
        //p_district = (EditText) findViewById(R.id.spinner20);


        Spinner spinner = (Spinner) findViewById(R.id.spinner20);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinner20);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);




        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                fio_in = fio.getText().toString();
                dol_in = dol.getText().toString();
                tel_in = tel.getText().toString();



                try {

                    new SendData().execute();


                } catch (Exception e) {

                }

            }
        });
    }


    static class SendData extends AsyncTask<Void, Void, Void> {

        String resultString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String myURL = "http://"+server+"/adm2/server.php";

                String parammetrs = "name="+fio_in+"&dol="+dol_in+"&tel="+tel_in;
                byte[] data = null;
                InputStream is = null;



                try {
                    URL url = new URL(myURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
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
                    int responseCode= conn.getResponseCode();


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




      // @Override
        //protected void onPostExecute(Void result) {
          //  super.onPostExecute(result);

            //Toast toast = Toast.makeText(getApplicationContext(), "Данные переданы!", Toast.LENGTH_SHORT);


       // }




    }










}



