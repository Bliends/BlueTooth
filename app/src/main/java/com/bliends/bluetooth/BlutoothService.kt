package com.bliends.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.ColorSpace.connect
import android.os.Build
import android.support.annotation.RequiresApi


class BlutoothService(activity: Activity, handler: Handler) {
    private val TAG: String = "BluetoothService"

    private var adapter: BluetoothAdapter? = null

    private var acitivity: Activity = activity

    private var handler: Handler = handler

    init {
        adapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun getDeviceState(): Boolean {
        return if (adapter == null) {
            acitivity.toast("블루투스를 지원하지 않는 기기입니다.")
            Log.e("test", "false")
            false
        } else {
            Log.e("test", true.toString())
            true
        }
    }

    fun listDevice(){ // 찾은 디바이스를 리스트로 보여주기
        var pairedDevices = adapter!!.bondedDevices
        if(pairedDevices.size > 0){ // 하나 이상 검색될 경우

            for(device in pairedDevices){

                Log.e(device.name,device.address)
            }
        }
    }

    fun enableBluetooth() {
        if (adapter!!.isEnabled) {
        } else {
            var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            acitivity.startActivityForResult(intent, 10)
        }
    }



//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getDeviceInfo(data : Intent) {
//        // Get the device MAC address
//        var address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
//        // Get the BluetoothDevice object
//        // BluetoothDevice device = btAdapter.getRemoteDevice(address);
//        var device = adapter.getRemoteDevice(address);
//        Log.d(TAG, "Get Device Info \n" + "address : " + address);
////        connect(device)
//    }


//    internal var device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)


    fun ensureDisoverle(){
        if(adapter!!.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            var disoverleintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            disoverleintent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,100)
            acitivity.startActivity(disoverleintent)
        }
    }
}

