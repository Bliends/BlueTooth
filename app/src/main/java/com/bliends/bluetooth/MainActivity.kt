package com.bliends.bluetooth

import android.Manifest
import android.annotation.SuppressLint
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
import android.speech.tts.TextToSpeech
import java.net.URLEncoder
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import java.io.*


class MainActivity : AppCompatActivity() {
    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

        }
    }
    private var btService: BlutoothService? = null
    var bt: BluetoothSPP = BluetoothSPP(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()
//        BLIENDS

        var btn = findViewById<Button>(R.id.mainbtn)

        if (btService == null) {
            btService = BlutoothService(this, handler)
        }


        btn.onClick {
            if (!bt.isBluetoothEnabled()) {
                bt.enable();
            } else {
                if (!bt.isServiceAvailable()) {
                    bt.setupService()
                    bt.startService(BluetoothState.DEVICE_OTHER)
                    bt!!.autoConnect("BLIENDS")
                }
            }

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

        bt.setOnDataReceivedListener(object : BluetoothSPP.OnDataReceivedListener {
            override fun onDataReceived(data: ByteArray?, message: String?) {
                toast(message!!)
                if (message == "1t") {
                    TTSUtil.usingTTS(this@MainActivity, "1000원이 인식되었습니다.")
                }else if(message == "5t"){
                    TTSUtil.usingTTS(this@MainActivity, "5000원이 인식되었습니다.")
                }else if(message == "1m"){

                    TTSUtil.usingTTS(this@MainActivity, "10000원이 인식되었습니다.")
                }else if(message == "5m"){
                    TTSUtil.usingTTS(this@MainActivity, "50000원이 인식되었습니다.")
                }
                else {

                    TTSUtil.usingTTS(this@MainActivity, message)
                }
            }
        })

        bt.setAutoConnectionListener(object : BluetoothSPP.AutoConnectionListener {
            override fun onNewConnection(name: String, address: String) {
                //새로운 연결일때
                Log.e("new", "succes")
            }


            override fun onAutoConnectionStarted() {
                //자동 연결
                Log.e("auto", "succes")
                toast("모듈과 정상적으로 연결되었습니다.")
            }
        })
    }



    public override fun onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER)
                bt!!.autoConnect("BLIENDS")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bt.stopService()
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

