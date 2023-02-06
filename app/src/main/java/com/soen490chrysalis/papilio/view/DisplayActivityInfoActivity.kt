package com.soen490chrysalis.papilio.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.soen490chrysalis.papilio.R

class DisplayAcitivityInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_acitivity_info)


        var infoTile: TextView = findViewById(R.id.info_Title)
        var infoDescription: TextView = findViewById(R.id.info_Description)
        var infoIndividualCost: TextView = findViewById(R.id.individualCost)
        var infoGroupCost: TextView = findViewById(R.id.groupCost)
        var infoAddress:TextView = findViewById(R.id.info_Location)
        var infoImages: ImageView =findViewById(R.id.info_imageView)

        val bundle: Bundle? = intent.extras
        val title = bundle!!.getString("title")
        val description = bundle!!.getString("description")
        val individualCost = bundle!!.getString("individualCost")
        val groupCost = bundle!!.getString("groupCost")
        val location = bundle!!.getString("location")
        val image = bundle!!.getString("images")

        infoTile.text = title
        infoDescription.text = description
        infoIndividualCost.text = individualCost
        infoGroupCost.text = groupCost
        infoAddress.text = location

//        Glide.with(this)
//            .load(image)
//            .into(infoImages)
//
//        if (image != null) {
//            Log.d("Hello", image)
//        }
    }

}