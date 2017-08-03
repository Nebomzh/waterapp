package ru.uu.voda.voda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;    //Для всплывающих сообщений
import android.view.Menu;       //меню
import android.view.MenuItem;   //пункт меню
import android.view.animation.Animation;        //Анимация
import android.view.animation.AnimationUtils;   //Анимационные утилиты

public class HomeActivity extends AppCompatActivity implements View.OnClickListener { //implements добавляет обработчик нажатий прямо в активити
    /** Главная страница*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Находим кнопки
        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);
        Button button3 = (Button)findViewById(R.id.button3);
        Button button4 = (Button)findViewById(R.id.button4);
        Button button5 = (Button)findViewById(R.id.button5);
        Button button6 = (Button)findViewById(R.id.button6);

        //Присваиваем кнопкам обработчик
        button1.setOnClickListener(this);   //Обработчик находится в самом активити
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

        //Анимация
        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.alpha);  //элемент анимации прозрачности
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation anim4 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation anim5 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation anim6 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim2.setStartOffset(250);       //задержка анимации
        anim3.setStartOffset(500);
        anim4.setStartOffset(750);
        anim5.setStartOffset(1000);
        anim6.setStartOffset(1250);
        button1.startAnimation(anim1);   //старт анимации на кнопках
        button2.startAnimation(anim2);
        button3.startAnimation(anim3);
        button4.startAnimation(anim4);
        button5.startAnimation(anim5);
        button6.startAnimation(anim6);

    }

    @Override
    public void onClick(View view) {
        Class target = ProblemaActivity.class;  //Переменная для класса в который нужно будет перейти
        // по id определеяем кнопку, вызвавшую этот обработчик
        switch (view.getId()) {
            case R.id.button1:
                target = ProblemaActivity.class;
                break;
            case R.id.button2:
                target = VoprosActivity.class;
                break;
            case R.id.button3:
                target = ContactsActivity.class;
                break;
            case R.id.button4:
            case R.id.button5:
            case R.id.button6:
                Toast.makeText(this, R.string.future_button, Toast.LENGTH_SHORT).show();    //Для остальных кнопок показываем тост, что они в разработке
                return; //и выходим из обработчика, никуда не переходя
        }
        Intent intent = new Intent(HomeActivity.this, target);
        startActivity(intent);
    }

    // создание меню
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exit, menu);  //создание меню из xml menu_exit
        return super.onCreateOptionsMenu(menu);
    }

    // обновление меню (в зависимости от настроек можно скрывать/показывать какие-нибудь элементы), пока не нужно.
    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // пункты меню с ID группы = 1 видны, если в CheckBox стоит галка
        menu.setGroupVisible(1, chb.isChecked());
        return super.onPrepareOptionsMenu(menu);
    }*/

    // обработка нажатий пунктов меню
    public boolean onOptionsItemSelected(MenuItem item) {                   //пока у нас один пункт в меню обрабатываем всегда одинаково без свича
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        finish(); // выход из приложения
        return super.onOptionsItemSelected(item);
    }
}

