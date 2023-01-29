package com.soen490chrysalis.papilio.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.soen490chrysalis.papilio.R;

public class QuizPart1 extends AppCompatActivity {

    Button next;
    Button choice1;
    Button choice2;
    TextView question;
    TextView title;
    TextView skip;
    boolean firstClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_part1);

        next = (Button) findViewById(R.id.next);
        choice1 = (Button) findViewById(R.id.choise1);
        choice2 = (Button) findViewById(R.id.choise2);
        question = (TextView) findViewById(R.id.question);
        title = (TextView) findViewById(R.id.textView4);
        skip = (TextView) findViewById(R.id.skip);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizPart1.this, QuizPart2Activity.class));
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizPart1.this, MainActivity.class));
            }
        });

        var actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Activity Quiz");
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}