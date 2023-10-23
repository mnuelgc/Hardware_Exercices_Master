package es.eps.ua.app2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import es.eps.ua.app2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityMainBinding
    private lateinit var tm : TelephonyManager

    private lateinit var roaming_view :TextView
    private lateinit var imei_view :TextView
    private lateinit var suscriberId_view :TextView
    private lateinit var simSerial_view :TextView
    private lateinit var networkCountry_view :TextView
    private lateinit var simCountry_view :TextView
    private lateinit var softwareVersion_view :TextView
    private lateinit var voiceMail_view :TextView

 /*   isRoaming = manager.isNetworkRoaming
    val PhoneType : String = strPhoneType!!
    val IMEINumber = manager.deviceId
    val suscriberID = manager.subscriberId
    val SIMSerialNumber = manager.simSerialNumber
    val networkCountryISO = manager.networkCountryIso
    val SIMCountryISO = manager.simCountryIso
    val softwareVersion = manager.deviceSoftwareVersion
    val voiceMailNumbe
*/

    var strPhoneType : String? = null
    private val PERMISSION_READ_STATE = 123

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        roaming_view = viewBinding.roamingText
        imei_view = viewBinding.imeiText
        suscriberId_view =viewBinding.IdSubscriptorText
        simSerial_view = viewBinding.serialNumberText
        networkCountry_view =viewBinding.physicOperatorText
        simCountry_view = viewBinding.virtualOperatorText
        softwareVersion_view = viewBinding.softwareVersionText
        voiceMail_view = viewBinding.answerNumberText

        val button = viewBinding.getSpecsButton

        button.setOnClickListener{ Start()}
    }


    public fun Start( ){
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if ( permissionCheck == PackageManager.PERMISSION_DENIED){
            MyTelephonyManager()
        }else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_READ_STATE);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_READ_STATE -> {
                if (grantResults.count() >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    MyTelephonyManager()
                }else{
                    Toast.makeText(this,
                            "You don't have required permission to make the Action",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun MyTelephonyManager() {
        val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phoneType = manager.phoneType
        when(phoneType){
            TelephonyManager.PHONE_TYPE_CDMA -> {
                strPhoneType = "CDMA"
            }
            TelephonyManager.PHONE_TYPE_GSM -> {
                strPhoneType = "GSM"
            }
            TelephonyManager.PHONE_TYPE_NONE -> {
                strPhoneType = "NONE"
            }
        }
        val isRoaming = manager.isNetworkRoaming
        val IMEINumber = manager.deviceId
        val suscriberID = manager.subscriberId
        val SIMSerialNumber = manager.simSerialNumber
        val networkCountryISO = manager.networkCountryIso
        val SIMCountryISO = manager.simCountryIso
        val softwareVersion = manager.deviceSoftwareVersion
        val voiceMailNumber = manager.voiceMailNumber

        roaming_view.text = isRoaming.toString()
        imei_view.text = IMEINumber
        suscriberId_view.text = suscriberID
        simSerial_view.text = SIMSerialNumber
        networkCountry_view.text = networkCountryISO
        simCountry_view.text =SIMCountryISO
        softwareVersion_view.text = softwareVersion
        voiceMail_view.text = voiceMailNumber




    }
}