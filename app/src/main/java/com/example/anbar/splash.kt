package com.example.anbar

import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import androidx.core.util.Pair

import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import kotlinx.android.synthetic.main.activity_splash.*

class splash : AppCompatActivity() {
    lateinit var topAnim: Animation
    lateinit var bottomAnim: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //Logo animations

        topAnim = AnimationUtils.loadAnimation(this, R.anim.up_anim)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.down_anim)
        Logo.animation = bottomAnim
        name.animation = topAnim
        Handler().postDelayed(object : Runnable {
            override fun run() {
                var intent: Intent = Intent(this@splash, Login::class.java)
                var pairs1 = Pair.create<View ,String>(Logo ,"Logo_image" )
                var pairs2 = Pair.create<View  , String>(name , "Logo_text")
                var pairList  = ArrayList<Pair<View , String>>()


                pairList.add(pairs1)
                pairList.add(pairs2)

                //var pairArray:Array<Pair>

                //to Login
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    var options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@splash  , pairs1 ,pairs2
                    )
                    startActivity(intent , options.toBundle())
                    finish()
                }

            }
        }, 2500)
    }
   // data  class T (val view:View , val string:String)
}