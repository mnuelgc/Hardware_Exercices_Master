package es.eps.ua.app2

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import es.eps.ua.app2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityMainBinding
    lateinit var tm : TelephonyManager

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

         tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        Toast.makeText(this,tm.simSerialNumber.toString(),Toast.LENGTH_LONG ).show()

    }
}