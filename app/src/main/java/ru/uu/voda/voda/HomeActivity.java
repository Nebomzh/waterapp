package ru.uu.voda.voda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;    //Для всплывающих сообщений

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
}

