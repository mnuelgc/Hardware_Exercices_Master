package es.eps.ua.app3

import android.Manifest
import android.content.ContextWrapper
import android.content.Context
import android.content.pm.PackageManager

import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import es.eps.ua.app3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var ssidText : TextView

    lateinit var bssidText : TextView
    lateinit var wifiSpeedText : TextView
    lateinit var wifiStrengthText : TextView
    lateinit var encryptionText : TextView
    lateinit var frecuencyText : TextView
    lateinit var channelText : TextView
    lateinit var ipText : TextView
    lateinit var netMaskText : TextView
    lateinit var gatewayText : TextView
    lateinit var dhcpServerText : TextView
    lateinit var dns1Text : TextView
    lateinit var dns2Text : TextView
    lateinit var dhcpLeaseText : TextView
    lateinit var externalIpText : TextView
    lateinit var hiddenText : TextView


    lateinit var wifiManager: WifiManager

    lateinit var wifiInfo: WifiInfo


    lateinit var SSID : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wifiManager: WifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiInfo: WifiInfo = wifiManager.connectionInfo


        val button = binding.wifiButton


        if (gpsPermissionGranted()) {
            ssidText = binding.ssidText
            bssidText = binding.bssidText
            wifiSpeedText = binding.wifiSpeedText
            wifiStrengthText = binding.wifiStrengthText
            encryptionText = binding.encryptionText
            frecuencyText = binding.frequencyText
            channelText = binding.channelText
            ipText = binding.ipText
            netMaskText = binding.netmaskText
            gatewayText = binding.gatewayText
            dhcpServerText = binding.dhcpServerText
            dns1Text = binding.dns1Text
            dns2Text = binding.dns2Text
            dhcpLeaseText = binding.dhcpLeaseText
            externalIpText = binding.externalIPText
            hiddenText = binding.hiddenText

            button.setOnClickListener {
                showWifiData()
            }

        }
        else{

            ActivityCompat.requestPermissions(
                this@MainActivity, REQUIRED_GPS_PERMISSIONS, Companion.REQUEST_GPS_CODE_PERSMISSIONS
            )

        }/*
            text = binding.text



            text.text = "Permissions not granted"
            */
        }

    private fun showWifiInfo()
    {

        val ssid = wifiInfo.ssid

    }

    private fun showWifiData()
    {
        val wifiManager: WifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiInfo: WifiInfo = wifiManager.connectionInfo

        ssidText.text = wifiInfo.ssid

        bssidText.text = wifiInfo.bssid
        wifiSpeedText.text = wifiInfo.linkSpeed.toString()
        wifiStrengthText.text = wifiInfo.rssi.toString()
        ipText.text = wifiInfo.ipAddress.toString()

        if (wifiManager.dhcpInfo.netmask == 0)
        {
            netMaskText.text = "0.0.0.0"
        }else {
            netMaskText.text = wifiManager.dhcpInfo.netmask.toString()
        }

        gatewayText.text = wifiManager.dhcpInfo.gateway.toString()
        dhcpServerText.text = wifiManager.dhcpInfo.serverAddress.toString()

        if (wifiManager.dhcpInfo.dns1 == 0)
        {
            dns1Text.text = "0.0.0.0"
        }else {
            dns1Text.text = wifiManager.dhcpInfo.dns1.toString()
        }

        if (wifiManager.dhcpInfo.dns2 == 0)
        {
            dns2Text.text = "0.0.0.0"
        }else {
            dns2Text.text = wifiManager.dhcpInfo.dns2.toString()
        }
        dhcpLeaseText.text = wifiManager.dhcpInfo.leaseDuration.toString()
        externalIpText.text = wifiManager.dhcpInfo.ipAddress.toString()
    }

    private fun gpsPermissionGranted() = REQUIRED_GPS_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    private  val REQUIRED_GPS_PERMISSIONS =
        mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).toTypedArray()

    companion object {
        private const val REQUEST_GPS_CODE_PERSMISSIONS = 20
    }
}
