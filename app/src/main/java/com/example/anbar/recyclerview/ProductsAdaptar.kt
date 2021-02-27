package com.example.anbar.recyclerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.anbar.Model.CodeProduct
import com.example.anbar.Model.Products
import com.example.anbar.R
import com.example.anbar.database.ProductDb

class ProductsAdaptar(private var dataset: ArrayList<Products>, var context: Context ) :
    RecyclerView.Adapter<ProductsAdaptar.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkBox: CheckBox
        var name: TextView
        var qty: TextView
    //    var next: Button
        var cardviewProduct: ConstraintLayout

        init {
            checkBox = view.findViewById(R.id.checkbox)
            name = view.findViewById(R.id.name)
            qty = view.findViewById(R.id.qty)
            cardviewProduct = view.findViewById(R.id.cardviewProduct)
//            next = view.findViewById(R.id.nextButton)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkboxcardveiw, parent, false)
        return ViewHolder(view)
    }

    //private var codeProductlist: ArrayList<CodeProduct> = ArrayList()
    private lateinit var list : ArrayList<String>
     var a :String = ","
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var db = Room.databaseBuilder(context.applicationContext , AppDatabase::class.java , "save code" ).build()

        var db = ProductDb(context)


        list = ArrayList()
        holder.name.text = dataset.get(position).name
        holder.qty.text = "تعداد: " + dataset.get(position).qty
        holder.checkBox.isClickable = false
        holder.cardviewProduct.setOnClickListener {

           // Log.d("Tests", dataset.get(position).code)
            var codeProduct = CodeProduct(dataset.get(position).ID, dataset.get(position).code)
            if (holder.checkBox.isChecked == false) {
                  list.add(holder.name.text.toString())
                holder.checkBox.isChecked = true
                dataset[position].checkbox = true
                a = ""
                db.deleteall()
              for (i in 0..dataset.size-1) {

                  if (dataset.get(i).checkbox == true) {

                      db.insertData(Products(dataset.get(i).name + "" , dataset.get(i).code +"" , i.toString() , dataset.get(i).qty + "" , dataset.get(i).checkbox ))
                  }
              }

            } else if (holder.checkBox.isChecked == true) {
                holder.checkBox.isChecked = false

                dataset[position].checkbox = false
                         a = ""
                db.deleteall()

                for (i in 0..dataset.size-1) {

                    if (dataset.get(i).checkbox == true) {
                        db.insertData(Products(dataset.get(i).name + "" , dataset.get(i).code + "" , i.toString() , dataset.get(i).qty + "" , dataset.get(i).checkbox ))

                    }
                }


            }
            var data  = db.readData()
            for (i in 0..data.size - 1) {
                Log.d("productCode","product code is: ${data[i].code}")
            }

        }


    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}