package com.example.anbar.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anbar.Model.Serials
import com.example.anbar.R
import kotlinx.android.synthetic.main.serialcardview.view.*

class serialAdapter(var dataset :ArrayList<Serials>):RecyclerView.Adapter<serialAdapter.ViewHolder>() {
   class ViewHolder(view:View):RecyclerView.ViewHolder(view)
   {
       lateinit var Serial: TextView
       init {
           Serial = view.findViewById(R.id.serialTextView)

       }


   }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): serialAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.serialcardview , parent , false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: serialAdapter.ViewHolder, position: Int) {
        holder.Serial.text = dataset.get(position).serials.toString()


    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}