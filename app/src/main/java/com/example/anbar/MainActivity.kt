package com.example.anbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.annotation.AnimRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.anbar.Model.AsnadInfo
import com.example.anbar.recyclerview.AsnadAdapter1
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                //WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
                supportActionBar!!.hide()
               // var view :View
               var recy = findViewById<RecyclerView>(R.id.recyclerview)
                //var recyClerView = RecyClerView(MainActivity@this, recy )
                show(recy)

    }

    fun show( recyclerView:RecyclerView ){
        var AsnadList :ArrayList<AsnadInfo> = ArrayList<AsnadInfo>()
        @AnimRes
        var layoutAnimation:Int=R.anim.layout_down_anim
        var animControl : LayoutAnimationController = AnimationUtils.loadLayoutAnimation(this , layoutAnimation)

        //var asnadInfo:AsnadInfo =
        var url :String = "http://*/api/Users?"
        var queue = Volley.newRequestQueue(this)
        var stringRequest =
                StringRequest(
                        Request.Method.GET, url , { response ->

                    var jsonArray = JSONArray(response)
                    //jsonObjects.put("ID" , response.get(0))
                    var count = jsonArray.length()
                    for (i in 0..count -1  ) {



                        var ID = jsonArray.getJSONObject(i).optString("ID")
                        var CustomerName = jsonArray.getJSONObject(i).optString("CustomerName")
                        var FactorNumber = jsonArray.getJSONObject(i).optString("FactorNumber")
                        var FactorDate = jsonArray.getJSONObject(i).optString("FactorDate")
                        var CheckStatusAsnad = jsonArray.getJSONObject(i).optString("CheckStatusAsnad")
                        AsnadList.add(AsnadInfo(ID , CustomerName, FactorNumber, FactorDate, CheckStatusAsnad))


                        Log.d("CheckGet" , jsonArray.getJSONObject(i).optString("CustomerName") + AsnadList.size)

                    }
                    // var recyclerView: RecyclerView = activity.findViewById(R.id.recyclerview)
                  //  var asnadAdapter = AsnadAdapter1(AsnadList)

                    recyclerView.apply {
                        adapter = AsnadAdapter1(AsnadList , applicationContext)

                      // layoutAnimation(recyclerView)
                      recyclerView.layoutAnimation = animControl
                        scheduleLayoutAnimation()
                        adapter!!.notifyDataSetChanged()
                        setHasFixedSize(true)
                        layoutManager = GridLayoutManager(context.applicationContext , 1)

                    }



                  /*   recyclerView.addOnItemTouchListener(RecyclerTouch(applicationContext,recyclerView, object :RecyclerTouch.CellClickListener{
                         @RequiresApi(Build.VERSION_CODES.O)
                         override fun onClick(view: View, pos: Int) {
                             var sanad = AsnadList.get(pos)
                             //Toast.makeText(this@MainActivity , sanad.FactorNumber , Toast.LENGTH_LONG).show()



                         }


                     }  ))
*/


                    //recyclerView.itemAnimator = DefaultItemAnimator()

                },
                        {res-> Log.e("Error" , "get from api error: " + " ${res.message}") }
                )


        queue.add(stringRequest)


    }



}