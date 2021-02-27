package com.example.anbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.anbar.Model.AsnadInfo
import com.example.anbar.Model.Products
import com.example.anbar.database.ProductDb
import com.example.anbar.recyclerview.ProductsAdaptar
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_pop_up.*
import org.json.JSONArray
import java.time.LocalDate

class popUpActivity : AppCompatActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up)
        var data = ProductDb(applicationContext)

        data.insertData(Products("","","","",false))
        data.deleteall()
        //var db = Room.databaseBuilder(applicationContext , AppDatabase::class.java , "savecode" ).build()
        var actionbar=   supportActionBar
        actionbar!!.hide()
        //change to window mode activity
        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var width = dm.widthPixels
        var height = dm.heightPixels

        //var lp:LinearLayout.LayoutParams = LinearLayout.LayoutParams()
        var set = ConstraintSet()
        set.constrainPercentHeight(R.id.popUp , (height * 0.5).toFloat())
        set.constrainPercentWidth(R.id.popUp , (width * 0.8).toFloat())
        set.applyTo(popUp)

        ConstraintLayout.LayoutParams((width*0.8).toInt() , (height * 0.8).toInt())
        window.setLayout((width * 0.8).toInt(), (height * .5).toInt())
        var parms : WindowManager.LayoutParams = window.attributes
        parms.gravity = Gravity.CENTER
        parms.x = 8
        parms.y = -20
        window.attributes = parms
        //END

        var asnadInfo = bundle()
        getDataFromAPI(asnadInfo.ID)


nextButton.setOnClickListener {
   var data = ProductDb(applicationContext)

    if(data.readData().isNotEmpty()) {
        var Intent = Intent(this, scan::class.java)
        Log.d("Check" , intent.extras!!.getString("ID"))
        Intent.putExtra("ID" ,intent.extras!!.getString("ID") )
        startActivity(Intent)
        finish()
    }
    else
    {
        Toast.makeText(this , "لطفا یک گزینه را انتخاب نمایید" , Toast.LENGTH_SHORT).show()
        var vib :Vibrator= getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(200)
    }
}



    }

    private fun getDataFromAPI(ID:String) {

        var ProductList = ArrayList<Products>()
        var url = "http://*/api/Users?id=$ID"
        var queue = Volley.newRequestQueue(this)
        var stringRequest = StringRequest(
                Request.Method.GET , url , {response ->
            var jsonArrayList  = JSONArray(response)
            var count = jsonArrayList.length()
            for (i in 0..count-1){
                var ID = jsonArrayList.getJSONObject(i).optString("ID")
                var code = jsonArrayList.getJSONObject(i).optString("code")
                var name = jsonArrayList.getJSONObject(i).optString("name")
                var qty = jsonArrayList.getJSONObject(i).optString("qty")

                var products:Products = Products(name, code, ID , qty , false)
                ProductList.add(products)
               // Log.e("ErrorApiProduct" , products.ID + products.code)
            }
            checkboxRecy.apply {
                adapter = ProductsAdaptar(ProductList , applicationContext  )
                adapter!!.notifyDataSetChanged()
                layoutManager = GridLayoutManager(applicationContext , 1)

            }

        }, {res -> Log.e("ErrorApiProduct" , res.message) }
        )

       queue.add(stringRequest)


    }

    //fun get data from Intent
    private fun bundle():AsnadInfo{
        var bundle = intent.extras
        var ID = bundle!!.getString("ID" ).toString()
        var CustomerName = bundle!!.getString("CustomerName" ).toString()
        var FactorNumber = bundle!!.getString("FactorNumber" ).toString()
        var FactorDate = bundle!!.getString("FactorDate" ).toString()
        var CheckStatusAsnad = bundle!!.getString("CheckStatusAsnad" ).toString()
        var asnadInfo:AsnadInfo = AsnadInfo(ID , CustomerName , FactorNumber ,FactorDate , CheckStatusAsnad)
        Log.d("PopUp" , "${asnadInfo.ID} $CustomerName $FactorDate $FactorNumber $CheckStatusAsnad")
        return asnadInfo
    }

}