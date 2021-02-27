package com.example.anbar

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.util.JsonReader
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.textclassifier.TextLinks
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import cn.refactor.lib.colordialog.PromptDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.anbar.Model.InfoProducts
import com.example.anbar.Model.Serials
import com.example.anbar.database.ProductDb
import com.example.anbar.recyclerview.serialAdapter
import com.google.zxing.Result
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_scan.*
import kotlinx.android.synthetic.main.login_activity.*
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class scan : AppCompatActivity(), ZBarScannerView.ResultHandler {
    private lateinit var mScannerView: ZBarScannerView
    private val CAMREA_CODE = 1
    var dataset: ArrayList<Serials> = ArrayList<Serials>()
    var dataset2: ArrayList<String> = ArrayList<String>()

    lateinit var vibe: Vibrator
    var InfoProductsList: ArrayList<InfoProducts> = ArrayList<InfoProducts>()
    public lateinit var Codes: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        Codes = " "
        supportActionBar?.hide()
        mScannerView = findViewById(R.id.barcodeScanner)
        requestPermission();
        vibe = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        var db = ProductDb(applicationContext)
        var dbGetCodes: Cursor = db.getWithSelect()
        Log.d("Check", intent.extras!!.getString("ID"))
        while (dbGetCodes.moveToNext()) {
            Log.d(
                "LocalDB",
                "Name:  ${dbGetCodes.getString(1)} ,Code: ${dbGetCodes.getString(2)} , ID: ${
                    dbGetCodes.getString(3)
                } , QTY: ${dbGetCodes.getString(4)} "
            )


            var infoproduct = InfoProducts(dbGetCodes.getString(2), dbGetCodes.getString(4))
            InfoProductsList.add(infoproduct)





            Codes = Codes + dbGetCodes.getString(2) + " "
        }

        scanButton.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> startScanner()
                    MotionEvent.ACTION_UP -> mScannerView.stopCamera()


                }
                return true
            }


        })
        SENDButton.setOnClickListener {
            for (i in 0..dataset2.size - 1) {
                UpdateSerials(dataset2.get(i).toString(),
                    intent.extras!!.getString("ID").toString())
            }

            if (dataset2.size > 0) {
                PromptDialog(this).setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                    .setAnimationEnable(true)
                    .setTitleText("موفق")
                    .setContentText("ثبت شد                               ")

                    .setPositiveListener("تایید", object : PromptDialog.OnPositiveListener {
                        override fun onClick(dialog: PromptDialog?) {
                            finish()
                            dialog!!.dismiss()
                        }

                    }).show()
            }
            if (dataset2.size == 0) {
                PromptDialog(this).setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                    .setAnimationEnable(true)
                    .setTitleText("ناموفق")
                    .setContentText("سریالی اسکن نشده است                          ")

                    .setPositiveListener("تایید", object : PromptDialog.OnPositiveListener {
                        override fun onClick(dialog: PromptDialog?) {
                            dialog!!.dismiss()
                        }

                    }).show()
            }
        }
    }


    fun startScanner() {
        // mScannerView.setResultHandler(this)
        mScannerView.startCamera()
        mScannerView.flash = false
        mScannerView.isSoundEffectsEnabled = true
        mScannerView.setAutoFocus(true)


        mScannerView.setResultHandler(this)
    }

//Request permission to access the camera
    //start

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMREA_CODE)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMREA_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show()
                val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (!showRationale) {
                    openSettingsDialog()
                }
            }
        }
    }

    private fun openSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@scan)
        builder.setTitle("Required Permissions")
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.")
        builder.setPositiveButton("Take Me To SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityForResult(intent, 101)
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()


    }
    //END


    //Send serials to web api
    //start

    fun UpdateSerials(serial: String, IDVocher: String) {
        //connect to web api

        var queue = Volley.newRequestQueue(this)
        var url = "http://*/api/Serials?IDSerial=${serial}&IDVocher=${IDVocher}"
        var stringReq: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->

            },
            Response.ErrorListener { }

        ) {}
        queue.add(stringReq)

    }
    //END`

    override fun handleResult(rawResult: me.dm7.barcodescanner.zbar.Result?) {
        Log.d("barcode", rawResult!!.contents)
        var Scannedbarcode = rawResult!!.contents
        var ch = barcodeCheck(rawResult!!.contents)
        if (ch) {
            mScannerView.resumeCameraPreview(this)
            //play beep sound
            mScannerView.playSoundEffect(1)
            //connect to web api
            var queue = Volley.newRequestQueue(this)
            var url = "http://*/api/Serials?Serial=${Scannedbarcode}"
            var stringReq: StringRequest = object : StringRequest(
                Request.Method.GET,
                url,
                com.android.volley.Response.Listener<String> { response ->
                    var jsonArray = JSONArray(response)
                    var serialActive = ""
                    // Checks the inventory of scanned serials in other serials.
                    for (i in 0..jsonArray.length() - 1) {
                        var serial = jsonArray.getJSONObject(i).optString("CountSerial");
                        Log.d("Serials", serial)

                        serialActive = serial

                        break

                    }
                    if (serialActive.toInt() != 1) {
                        //Vibrate for 200ms
                        var vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vib.vibrate(200)
                        Toast.makeText(this, "بارکد اسکن شده نامعتبر میباشد", Toast.LENGTH_SHORT)
                            .show()
                        //resume scan
                        mScannerView.resumeCameraPreview(this)

                    } else {
                        Log.d("SerialActive", serialActive)

                        var flag: Boolean
                        flag = true

                        for (i in 0..dataset.size - 1) {
                            if (dataset.get(i).serials.contains(Scannedbarcode)) {
                                Toast.makeText(this,
                                    " این بارکد قبلا ثبت شده است",
                                    Toast.LENGTH_SHORT)
                                    .show()
                                //Vibrate for 500ms
                                vibe.vibrate(500)
                                flag = false
                            }
                        }


                        if (flag) {
                            //Get the first 4 letters of the series
                            var fourchar = foursubstring(Scannedbarcode)
                            var db = ProductDb(applicationContext)
                            var dbGetCodes: Cursor = db.getWithSelect()
                            var qty = 0
                            while (dbGetCodes.moveToNext()) {
                                if (dbGetCodes.getString(2) == fourchar) {
                                    qty = dbGetCodes.getString(4).toInt()
                                }
                            }
                            var Count = 0
                            for (i in 0..dataset.size - 1) {
                                if (foursubstring(dataset.get(i).serials) == fourchar) {
                                    Count = Count + 1
                                }
                            }

                            if (Count < qty) {
                                //get date and time
                                val dateNow =
                                    Calendar.getInstance().time.hours.toString() + ":" + Calendar.getInstance().time.minutes.toString() + ":" + Calendar.getInstance().time.seconds.toString()
                                Log.d("Timee", dateNow)
                                var serialModel: Serials = Serials(Scannedbarcode, dateNow)


                                dataset.add(serialModel)
                                dataset2.add(Scannedbarcode)
                                var mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.a)
                                mediaPlayer.start()

                                //apply to adapter and layout manager for recyClerView
                                recySerial.apply {
                                    adapter = serialAdapter(dataset)
                                    adapter!!.notifyDataSetChanged()
                                    layoutManager = GridLayoutManager(applicationContext, 1)
                                }
                                //show the number of scanned barcodes
                                CountSerial.text = "تعداد: " + dataset.size
                                mScannerView.resumeCameraPreview(this)
                            } else {
                                //warn if more than allowed
                                Toast.makeText(this, "تعداد غیر مجاز", Toast.LENGTH_SHORT).show()
                                vibe.vibrate(500)
                                mScannerView.resumeCameraPreview(this)

                            }
                        }
                    }
                },
                Response.ErrorListener {

                }
            ) {

                override fun getParams(): Map<String, String> {
                    var params = HashMap<String, String>()

                    Log.d(
                        "Serials", "A"
                    )
                    return params
                }
            }
            queue.add(stringReq)


        } else {
            Toast.makeText(this, "بارکد نامعتبر", Toast.LENGTH_SHORT).show()
            vibe.vibrate(500)
            mScannerView.resumeCameraPreview(this)
        }

    }


    fun barcodeCheck(a: String): Boolean {
        var aa = a.substring(0, 4).toString()
        Log.d("SubString ", aa + "\t${Codes}")
        if (Codes.contains(aa)) {
            Log.d("BarcodeCheck", "true")
            return true
        } else {
            Log.d("BarcodeCheck", "false")

            return false
        }

    }

    fun foursubstring(a: String): String {
        var aa = a.substring(0, 4).toString()
        Log.d("SubString ", aa + "\t${Codes}")

        return aa


    }
}