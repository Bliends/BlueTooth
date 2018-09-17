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
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.widget.Toast
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat





class MainActivity : AppCompatActivity() {
    private var btService : BlutoothService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()

        if(btService ==null){
            btService = BlutoothService(this,handler)
            Log.e("asdf","asdf")
        }

       var btn = findViewById<Button>(R.id.mainbtn)

        btn.onClick {

            if (btService!!.getDeviceState()) {
                // 블루투스가 지원 가능한 기기일 때
                btService!!.enableBluetooth()
            } else {
                finish()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("반환","1234")
        if(requestCode == 10){
            if(resultCode == Activity.RESULT_OK){
                Log.e("asdf","asdf")
//                btService!!.listDevice()
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(mReceiver, filter)
            }else{
                toast("블루투스를 연결해 주세요.")
            }
        }

        if(resultCode == 300){
            Log.e("asdf","Adsf")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }



    private val mReceiver = object : BroadcastReceiver() { //각각의 디바이스로부터 정보를 받으려면 만들어야함
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.e(device.name,device.address)
            }
        }
    }

    var handler  = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
        }
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
