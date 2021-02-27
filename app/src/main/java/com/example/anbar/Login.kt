package com.example.anbar

//import android.databinding.tool.writer.ViewBinder
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.refactor.lib.colordialog.ColorDialog
import cn.refactor.lib.colordialog.PromptDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import kotlinx.android.synthetic.main.login_activity.*
import org.json.JSONArray


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        var color = ContextCompat.getColor(this , R.color.red)
        //Login.setBackgroundResource(R.drawable.shape)
        Login.setBackgroundColor(Color.RED)
        Login.text = "ورود"
        Login.textSize = "20".toFloat()
        Login.backgroundTintList = ContextCompat.getColorStateList(this@Login , R.color.red)

    }

     fun load(view:View) {

        var progress :ProgressDialog = ProgressDialog(this , R.style.MyAlertDialogStyle)
        progress.setMessage("لطفا منتظر بمانید...")
//Check the password and username entered
        bindProgressButton(Login)
        Login.attachTextChangeAnimator()
        if (Username.text!!.isEmpty()) {
            Username.error = "نام کاربری را وارد نمایید"
            Username.requestFocus()

            return
        }
        if (Passwod.text!!.isEmpty()) {
            Passwod.error =  ( "رمز عبر خود را وارد نمایید")
            //password_Layout.isPasswordVisibilityToggleEnabled = false
            Passwod.requestFocus()
            return
        }

        //else if (Passwod.text!!.isNotEmpty()&& Username.text!!.isEmpty()){            password_Layout.isPasswordVisibilityToggleEnabled = true;  return }
        else if (Passwod.text!!.isNotEmpty() && Username.text!!.isNotEmpty()) {
            progress.show()

          //  Toast.makeText(this , "Login is succssefuly" , Toast.LENGTH_LONG).show()
            try {

                var checkLogin = ""
                val username = Username.text.toString()
                val password = Passwod.text.toString()
                //connect to web api
                var queue = Volley.newRequestQueue(this)
                var url: String =
                    "*/api/Users?UserName=${username}&Password=${password}&"
                var stringRequest: StringRequest = object : StringRequest(Request.Method.POST
                    ,
                    url
                    ,
                    com.android.volley.Response.Listener<String> { response ->
                        var jsonArray = JSONArray(response)


                        checkLogin = response.toString(); Log.d(
                        "LOGINSTATUS",
                        jsonArray.getJSONObject(0).optString("CheckStatusLogin")
                    )
                        var dialog= PromptDialog(this)
                        //var circleButton

                        var check=jsonArray.getJSONObject(0).optString("CheckStatusLogin")

                        progress.dismiss()

                        //show this warning if login failed
                        if(check == "Login faild"){

                            PromptDialog(this).setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                                .setAnimationEnable(true)
                                .setTitleText("خطا")
                                .setContentText("نام کاربری یا گذرواژه اشتباه است           ")
                                .setPositiveListener("تایید" , object:PromptDialog.OnPositiveListener{
                                    override fun onClick(dialog: PromptDialog?) {
                                        dialog!!.dismiss()
                                    }

                                }).show()

                            /* var Dialog = PromptDialog(applicationContext)
                                    Dialog.dialogType = PromptDialog.DIALOG_TYPE_WRONG
                                Dialog.setAnimationEnable(true)
                                Dialog.setTitleText("خطا")
                                Dialog.setContentText("نام کاربری یا گذرواژه اشتباه است           ")
                                Dialog.setPositiveListener("تایید" , object :PromptDialog.OnPositiveListener{
                                    override fun onClick(dialog: PromptDialog?) {
                                        dialog!!.dismiss()
                                    }

                                }).show()*/

                        }
                        //If the entry is successful, go to the next activity
                        else if (check == "Login successfuly")
                        {
                            progress.dismiss()

                            var intent:Intent = Intent(this , MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                        else{
                        }

                    }
                    ,
                    com.android.volley.Response.ErrorListener { res ->
                        Log.d(
                            "LOGINSTATUS",
                            "ERROR: " + res.localizedMessage
                        )
                    }
                ) {

                    override fun getParams(): Map<String, String> {
                        var params = HashMap<String, String>()

                        Log.d(
                            "user_pass",
                            Username.text.toString() + " " + Passwod.text.toString()
                        )
                        return params
                    }
                }
                queue.add(stringRequest)
            } catch (ex: Exception) {
                Log.d("LOGINSTATUS", ex.message)
            }
        }

    }
    fun getInAnimationTest(context: Context?): AnimationSet? {
        val out = AnimationSet(context, null)
        val alpha = AlphaAnimation(0.0f, 1.0f)
        alpha.setDuration(150)
        val scale = ScaleAnimation(
            0.6f,
            1.0f,
            0.6f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scale.setDuration(150)
        out.addAnimation(alpha)
        out.addAnimation(scale)
        return out
    }

}

