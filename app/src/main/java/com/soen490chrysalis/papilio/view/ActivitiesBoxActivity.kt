package com.soen490chrysalis.papilio.view;

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.soen490chrysalis.papilio.R

class ActivitiesBoxActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState : Bundle?, persistentState : PersistableBundle?)
    {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_activities_box)
    }
}