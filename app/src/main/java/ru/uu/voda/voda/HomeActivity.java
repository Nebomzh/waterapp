package ru.uu.voda.voda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;    //Для всплывающих сообщений
import android.view.Menu;       //меню
import android.view.MenuItem;   //пункт меню
import android.view.animation.Animation;        //Анимация
import android.view.animation.AnimationUtils;   //Анимационные утилиты
import android.net.Uri;

import android.content.SharedPreferences;           //для работы с настройками
import android.content.SharedPreferences.Editor;    //для редактирования настроек

public class HomeActivity extends AppCompatActivity implements View.OnClickListener { //implements добавляет обработчик нажатий прямо в активити
    /** Главная страница*/

    SharedPreferences sPref;    //объект настроек
    final String ANIM_STATE = "anim_state"; //ключ состояния анимации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sPref = getPreferences(MODE_PRIVATE);   //настройки

        //Находим кнопки
        View button1 = (View)findViewById(R.id.button1);
        View button2 = (View)findViewById(R.id.button2);
        View button3 = (View)findViewById(R.id.button3);
        View button4 = (View)findViewById(R.id.button4);
        View button5 = (View)findViewById(R.id.button5);
        View button6 = (View)findViewById(R.id.button6);

        //Присваиваем кнопкам обработчик
        button1.setOnClickListener(this);   //Обработчик находится в самом активити
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

        //Анимация
        if(sPref.getBoolean(ANIM_STATE, true)) //узнаём состояние анимации из настроек(по умолчанию есть)
        {
            Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.alpha);  //элемент анимации прозрачности
            Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.alpha);
            Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.alpha);
            Animation anim4 = AnimationUtils.loadAnimation(this, R.anim.alpha);
            Animation anim5 = AnimationUtils.loadAnimation(this, R.anim.alpha);
            Animation anim6 = AnimationUtils.loadAnimation(this, R.anim.alpha);
            anim1.setStartOffset(250);       //задержка анимации
            anim2.setStartOffset(500);       //задержка анимации
            anim3.setStartOffset(750);
            anim4.setStartOffset(1000);
            anim5.setStartOffset(1250);
            anim6.setStartOffset(1500);
            button1.startAnimation(anim1);   //старт анимации на кнопках
            button2.startAnimation(anim2);
            button3.startAnimation(anim3);
            button4.startAnimation(anim4);
            button5.startAnimation(anim5);
            button6.startAnimation(anim6);
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();   //intent в который нужно будет перейти

        // по id определеяем кнопку, вызвавшую этот обработчик
        switch (view.getId()) {
            case R.id.button1:
                intent.setClass(this, ProblemaActivity.class);
                break;
            case R.id.button2:
                intent.setClass(this, QuestionsActivity.class);
                break;
            case R.id.button3:
                intent.setClass(this, ContactsActivity.class);
                break;
            case R.id.button4:
                Toast.makeText(this, R.string.future_button, Toast.LENGTH_SHORT).show();    //Для неготовых кнопок показываем тост, что они в разработке
                return; //и выходим из обработчика, никуда не переходя
                /*intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:55.754283,37.62002"));    //Вариант открытия координат на карте
                break;*/
            case R.id.button5:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://voda.uu.ru"));
                break;
            case R.id.button6:
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+73517299559"));
                break;
        }
        startActivity(intent);
    }

    // создание меню
    public boolean onCreateOptionsMenu(Menu menu) {
        if(sPref.getBoolean(ANIM_STATE, true)) //узнаём состояние анимации из настроек(по умолчанию есть)
            menu.add(0, 0, 0, R.string.animSetOff);
        else
            menu.add(0, 0, 0, R.string.animSetOn);
        menu.add(0, 1, 1, R.string.exit);
        return super.onCreateOptionsMenu(menu);
    }

    // обновление меню (в зависимости от настроек меняем элементы)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(sPref.getBoolean(ANIM_STATE, true)) //узнаём состояние анимации из настроек(по умолчанию есть)
            menu.getItem(0).setTitle(R.string.animSetOff);
        else
            menu.getItem(0).setTitle(R.string.animSetOn);
        return super.onPrepareOptionsMenu(menu);
    }

    // обработка нажатий пунктов меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // по id определеяем пункт меню, вызвавший этот обработчик
        switch (item.getItemId()) {
            case 0:
                Boolean anim_state = sPref.getBoolean(ANIM_STATE, true);    //текущее состояние настроек
                Editor ed = sPref.edit();   //объект для редактирования настроек
                ed.putBoolean(ANIM_STATE, !anim_state); //изменение настроек
                ed.commit();    //сохрание настроек
                if (anim_state)                                                         //сообщения о смене анимации
                    Toast.makeText(this, R.string.animOff, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, R.string.animOn, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                finish(); // выход из приложения
        }
        return super.onOptionsItemSelected(item);
    }
}

