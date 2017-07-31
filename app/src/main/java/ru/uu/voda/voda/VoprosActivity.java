package ru.uu.voda.voda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class VoprosActivity extends AppCompatActivity {

    String[] colors = { "Красны", "Оранжевый", "Желтый", "Зелёный", "Голубой", "Синий", "Фиолетовый"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vopros);

        ListView list = (ListView) findViewById(R.id.lvMain);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, colors);

        // устанавливаем адаптер списку
        list.setAdapter(adapter);
    }
}
