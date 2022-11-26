package com.soen490chrysalis.papilio.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.soen490chrysalis.papilio.R;

public class QuizPart2Activity extends AppCompatActivity {

    Button next;
    Button other;
    Button choice1;
    Button choice2;
    Button choice3;
    Button choice4;
    TextView question;
    TextInputEditText input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_part2);

        next = (Button) findViewById(R.id.next);
        other = (Button) findViewById(R.id.other);
        choice1 = (Button) findViewById(R.id.choise1);
        choice2 = (Button) findViewById(R.id.choise2);
        choice3=(Button) findViewById(R.id.choise3);
        choice4 = (Button) findViewById(R.id.choise4);
        question = (TextView) findViewById(R.id.question);
        input = (TextInputEditText) findViewById(R.id.otherInput);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question.setText("Which of the following arts do you enjoy more?");
                choice1.setText("ARTS AND CRAFTS");
                choice2.setText("DANCE");
                choice3.setText("MUSIC");
                choice4.setText("LITERATURE");
                other.setText("Other Arts");
                input.setVisibility(View.INVISIBLE);
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setVisibility(View.VISIBLE);
            }
        });
    }
}