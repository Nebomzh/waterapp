package ru.uu.voda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import android.support.v7.widget.Toolbar; //Тулбар

import android.text.Spanned; //Для добавления Html кода в textview

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView question = (TextView) findViewById(R.id.question);
        TextView answer = (TextView) findViewById(R.id.answer);

        Intent intent = getIntent();

        String[] questions; //массив с вопросами
        String[] answers; //массив с ответами

        switch (intent.getIntExtra("part",1)) {	//Определение номера раздела
            case 1:
                questions = getResources().getStringArray(R.array.questionsC);//секция common
                answers = getResources().getStringArray(R.array.answersC);//секция common
                break;
            case 2:
                questions = getResources().getStringArray(R.array.questionsG);//секция гражданам
                answers = getResources().getStringArray(R.array.answersG);//секция гражданам
                break;
            case 3:
                questions = getResources().getStringArray(R.array.questionsO);//секция организациям
                answers = getResources().getStringArray(R.array.answersO);//секция организациям
                break;
            default:
                questions = getResources().getStringArray(R.array.questionsC);//без дефаулта студия ругается
                answers = getResources().getStringArray(R.array.answersC);
        }

        int questionNumber=intent.getIntExtra("question",1); //номер вопроса

        question.setText(questions[questionNumber]);

        if(questionNumber<answers.length)              //Проверка На случай если в массиве с ответами ответов будет меньше, чем вопросов
        {
            Spanned htmlAsSpanned = HtmlCompat.fromHtml(answers[questionNumber]);
            answer.setText(htmlAsSpanned);
        }
        else
            answer.setText(R.string.noanswer);

    }
}
