package ru.uu.voda.voda;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;


public class DisconnectionsActivity extends AppCompatActivity implements DatePickerListener {   //добавляем интерфейс обмена инфой с диалогом датапикером

    int year;
    int month;
    int day;
    final String YEAR = "year";
    final String MONTH = "month";
    final String DAY = "day";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnections);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use the current date as the default date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        setCalendartext();
    }

    private void setCalendartext () {   //отображение в поле с календарём даты из текущих переменных
        ((TextView) findViewById(R.id.calendartext)).setText(day + " " + getResources().getStringArray(R.array.month)[month] + " " + year);
    }

    //сохранение состояния (при повороте экрана или неявном уничтожении)
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(YEAR, year);
        outState.putInt(MONTH, month);
        outState.putInt(DAY, day);
    }

    //восстановление при пересоздании
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        year = savedInstanceState.getInt(YEAR);
        month = savedInstanceState.getInt(MONTH);
        day = savedInstanceState.getInt(DAY);
        setCalendartext();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Интерфейсы принятия инфы от диалоговых окон
    @Override
    public void onDateSet(int year, int month, int day) {    //при выборе новой даты
        this.year = year;       //сохраняем значения в переменные класса
        this.month = month;
        this.day = day;
        setCalendartext();  //обновляем отображаемый текст
    }
    @Override
    public int getYear()  {return year;}
    @Override
    public int getMonth() {return month;}
    @Override
    public int getDay()   {return day;}


}
