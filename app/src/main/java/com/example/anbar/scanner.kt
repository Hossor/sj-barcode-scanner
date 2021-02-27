package com.example.anbar

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class scanner : Activity(),ZXingScannerView.ResultHandler{
     lateinit var mScannerView: ZXingScannerView

    val RECORD_REQUEST_CODE = 123
    lateinit var formats :ArrayList<BarcodeFormat>
    override fun onCreate(savedInstanceState: Bundle?) {
        mScannerView =  ZXingScannerView(MainActivity@ this);
        formats = ArrayList()
        formats.add(BarcodeFormat.QR_CODE)
        mScannerView.setFormats(formats)
        super.onCreate(savedInstanceState)
         // Programmatically initialize the scanner view

        setContentView(mScannerView)

        setupPermissions()
    }


private fun setupPermissions(){
    val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA)

    if (permission != PackageManager.PERMISSION_GRANTED) {
        Log.i("Tests", "Permission to record denied")
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.CAMERA)) {

        } else {
            makeRequest()
        }
    }
}
    private fun makeRequest() {

        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                RECORD_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Tests", "Permission has been denied by user")
                } else {
                    Log.i("Tests", "Permission has been granted by user")
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()

        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera() // Start camera on resume

    }
    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera() // Stop camera on pause
    }
    override fun handleResult(rawResult: Result?) {
        Log.v("Tests", rawResult!!.getText()); // Prints scan results
        Log.v("Tests", rawResult!!.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
       // mScannerView.resumeCameraPreview(this);
    }


}