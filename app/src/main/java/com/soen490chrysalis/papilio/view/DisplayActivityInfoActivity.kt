package com.soen490chrysalis.papilio.view

import android.os.Bundle
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.soen490chrysalis.papilio.R

class DisplayActivityInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_acitivity_info)


        var infoTile: TextView = findViewById(R.id.info_Title)
        var infoDescription: TextView = findViewById(R.id.info_Description)
        var infoIndividualCost: TextView = findViewById(R.id.individualCost)
        var infoGroupCost: TextView = findViewById(R.id.groupCost)
        var infoAddress:TextView = findViewById(R.id.info_Location)
        var infoImages0: ImageView =findViewById(R.id.info_imageView0)
        var infoImages1: ImageView =findViewById(R.id.info_imageView1)
        var infoImages2: ImageView =findViewById(R.id.info_imageView2)
        var infoImages3: ImageView =findViewById(R.id.info_imageView3)
        var infoImages4: ImageView =findViewById(R.id.info_imageView4)
        var infoHorizontalScroll: HorizontalScrollView = findViewById(R.id.info_horizontal_Scroll)

        val bundle: Bundle? = intent.extras
        val title = bundle!!.getString("title")
        val description = bundle!!.getString("description")
        val individualCost = bundle!!.getString("individualCost")
        val groupCost = bundle!!.getString("groupCost")
        val location = bundle!!.getString("location")
        val hasImages = bundle!!.getBoolean("images")
        if(hasImages){
            val image0 = bundle!!.getString("images0")
            val image1 = bundle!!.getString("images1")
            val image2 = bundle!!.getString("images2")
            val image3 = bundle!!.getString("images3")
            val image4 = bundle!!.getString("images4")

            if(image0 != ""){
                Glide.with(this).load(image0).into(infoImages0)
            }
            if(image1 != ""){
//                infoHorizontalScroll.isScrollContainer = true
                Glide.with(this).load(image1).into(infoImages1)
                infoImages1.isVisible = true

            }
            if(image2 != ""){
                Glide.with(this).load(image2).into(infoImages2)
                infoImages2.isVisible = true

            }
            if(image3 != ""){
                Glide.with(this).load(image3).into(infoImages3)
                infoImages3.isVisible = true
            }
            if(image4 != ""){
                Glide.with(this).load(image4).into(infoImages4)
                infoImages4.isVisible = true
            }

        }


        infoTile.text = title
        infoDescription.text = description
        infoIndividualCost.text = individualCost
        infoGroupCost.text = groupCost
        infoAddress.text = location



    }

}