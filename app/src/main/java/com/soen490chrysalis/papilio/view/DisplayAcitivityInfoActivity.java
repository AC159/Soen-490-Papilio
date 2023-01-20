package com.soen490chrysalis.papilio.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.soen490chrysalis.papilio.R;

public class DisplayAcitivityInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_acitivity_info);


//        var actionBar = getSupportActionBar();
//
//        if(actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle("activity Info");
//        }
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        int id = item.getItemId();
//
//        if(id == android.R.id.home){
//            this.finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

}