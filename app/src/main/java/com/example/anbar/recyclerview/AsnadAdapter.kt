package com.example.anbar.recyclerview

import android.content.Context
import android.content.Intent
import android.service.autofill.Dataset
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.anbar.Model.AsnadInfo
import com.example.anbar.R
import com.example.anbar.popUpActivity
import com.example.anbar.scanner

class AsnadAdapter1 (private var dataset: ArrayList<AsnadInfo> , var context: Context):
RecyclerView.Adapter<AsnadAdapter1.ViewHolder>() {
    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        var CustomerName :TextView
        var FactorNumber :TextView
        var FactorDate :TextView
        //var FactorNumber :TextView
        init {
            CustomerName = view.findViewById(R.id.CustomerName)
            FactorDate = view.findViewById(R.id.Date)
            FactorNumber = view.findViewById(R.id.FactorNumber)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.FactorNumber.text = dataset.get(position).FactorNumber
        holder.FactorDate.text = dataset.get(position).FactorDate
        holder.CustomerName.text = dataset.get(position).CustomerName
        holder.itemView.setOnClickListener {

            var intent = Intent(it.context , popUpActivity::class.java)

            intent.putExtra("ID" , dataset.get(position).ID)
            intent.putExtra("CustomerName" , dataset.get(position).CustomerName)
            intent.putExtra("FactorNumber" , dataset.get(position).FactorNumber)
            intent.putExtra("FactorDate" , dataset.get(position).FactorDate)
            intent.putExtra("CheckStatusAsnad" , dataset.get(position).CheckStatusAsnad)

            it.context.startActivity(intent)


        }
    }

    override fun getItemCount(): Int {
       return dataset.size
    }


}