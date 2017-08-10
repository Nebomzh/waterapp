package ru.uu.voda.voda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        TextView question = (TextView) findViewById(R.id.question);
        TextView answer = (TextView) findViewById(R.id.answer);

        Intent intent = getIntent();

        switch (intent.getIntExtra("part",1)) {
            case 1:
                switch (intent.getIntExtra("question",1)) {
                    case 1:
                        question.setText(R.string.qc1);
                        answer.setText(R.string.ac1);
                        break;
                    case 2:
                        question.setText(R.string.qc2);
                        answer.setText(R.string.ac2);
                        break;
                    case 3:
                        question.setText(R.string.qc3);
                        answer.setText(R.string.ac3);
                        break;
                }
                break;
            case 2:
                switch (intent.getIntExtra("question",1)) {
                    case 1:
                        question.setText(R.string.qg1);
                        answer.setText(R.string.ag1);
                        break;
                    case 2:
                        question.setText(R.string.qg2);
                        answer.setText(R.string.ag2);
                        break;
                    case 3:
                        question.setText(R.string.qg3);
                        answer.setText(R.string.ag3);
                        break;
                }
                break;
            case 3:
                switch (intent.getIntExtra("question",1)) {
                    case 1:
                        question.setText(R.string.qo1);
                        answer.setText(R.string.ao1);
                        break;
                    case 2:
                        question.setText(R.string.qo2);
                        answer.setText(R.string.ao2);
                        break;
                    case 3:
                        question.setText(R.string.qo3);
                        answer.setText(R.string.ao3);
                        break;
                }
                break;
        }
    }
}
