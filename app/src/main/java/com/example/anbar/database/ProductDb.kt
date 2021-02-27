package com.example.anbar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.anbar.Model.Products


class ProductDb(var context: Context) :SQLiteOpenHelper(context, DATABASENAME, null, 1) {
    companion object{
        const val DATABASENAME = "Products"
        const val v = 1
        const val TABLENAME = "product"
        const val COL_CODE = "code"
        const val COL_NAME = "name"
        const val COL_ID = "_ID"
        const val COL_CHECK = "checkBox"
        const val COL_QTY = "qty"
    }
    override fun onCreate(db: SQLiteDatabase?) {

        var createTable =
            "Create Table " + TABLENAME + " (" + COL_ID + " int PRIMARY KEY ," + COL_NAME + " nvarchar(1000) , " + COL_CODE + " nvarchar(1000)," + COL_CHECK + " nvarchar(1000)," + COL_QTY + "  nvarchar(1000))"
        db!!.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    fun insertData(product: Products) {
        var database = this.writableDatabase
        var contentValue = ContentValues()
        contentValue.put(COL_ID, product.ID)
        contentValue.put(COL_NAME, product.name)
        contentValue.put(COL_CODE, product.code)
        contentValue.put(COL_CHECK, product.checkbox)
        contentValue.put(COL_QTY, product.qty)

        val result = database.insert(TABLENAME, null, contentValue)
        if (result == (0).toLong()) {
            Toast.makeText(context, "faild", Toast.LENGTH_LONG).show()
        } else {
         //  Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()

        }
    }

    fun readData(): MutableList<Products> {
        var list: MutableList<Products> = ArrayList()
        var db = this.readableDatabase
        val query = "select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToNext()) {
            do {
                var products: Products = Products(
                    result.getString(result.getColumnIndex(COL_NAME)),
                    result.getString(result.getColumnIndex(COL_CODE)),
                    result.getString(result.getColumnIndex(COL_ID)).toString(),
                    result.getString(result.getColumnIndex(COL_QTY)),
                    result.getString(result.getColumnIndex(COL_CHECK)).toBoolean()
                )

                list.add(products)
            } while (
                result.moveToNext()
            )
        }
        return list

    }

    fun getWithSelect():Cursor{
        var db :SQLiteDatabase = this.writableDatabase
        var cursor:Cursor = db.rawQuery("Select * from ${TABLENAME}" , null)
        return cursor


    }

    fun deleteall()  {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLENAME");
    }
}