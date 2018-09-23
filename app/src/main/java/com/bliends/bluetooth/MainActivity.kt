package com.bliends.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Button
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import android.content.IntentFilter
import android.widget.Toast
import android.support.v4.app.ActivityCompat
import android.bluetooth.BluetoothAdapter
import android.text.method.TextKeyListener.clear
import android.app.ProgressDialog
import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.net.URLEncoder
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState


class MainActivity : AppCompatActivity() {
    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

        }
    }
    private var btService: BlutoothService? = null
    var bt: BluetoothSPP? = BluetoothSPP(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()

        var btn = findViewById<Button>(R.id.mainbtn)

        if (btService == null) {
            btService = BlutoothService(this, handler)
        }


        btn.onClick {
            if (!bt!!.isBluetoothAvailable) {
                toast("블루투스를 지원하지 않는 기기입니다.")
            } else {
                if(btService!!.enableBluetooth() == "있음"){
                    blstart()
                    Log.e("GKDL","ASDF")
                }else{
                    Log.e("GKDL","GKDL")
                }
                toast("블루투스를 지원하는 기기입니다.")
            }
        }

        bt!!.setOnDataReceivedListener { data, message ->
            Log.e("test","test")
            Log.e(data.toString(),message)
        }


        bt!!.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceDisconnected() {
                toast("기기와 연걸이 정상적으로 완료되었습니다.")
            }

            override fun onDeviceConnected(name: String?, address: String?) {

                toast("기기와 연걸이 끊어졌습니다.")
            }

            override fun onDeviceConnectionFailed() {

                toast("기기와 연걸이 실패하였습니다.")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("반환", "1234")
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("asdf", "asdf")
                blstart()
                }
            } else {
                toast("블루투스를 연결해 주세요.")
            }
        }


    public override fun onStart() {
        super.onStart()
        if (!bt!!.isBluetoothEnabled) {
            // Do somthing if bluetooth is disable
        } else {
            // Do something if bluetooth is already enable
        }
    }

    fun blstart(){
        bt!!.setupService()
        bt!!.startService(BluetoothState.DEVICE_OTHER)
        bt!!.autoConnect("BLIENDS")
    }

    //권한 설정
    fun getPermission() {
        ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                0)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults[0] != 0) {
                Toast.makeText(this, "권한이 거절 되었습니다. 어플을 이용하려면 권한을 승낙하여야 합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
