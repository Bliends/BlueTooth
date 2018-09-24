package com.bliends.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Handler
import android.util.Log
import org.jetbrains.anko.toast
import android.bluetooth.BluetoothDevice
import android.widget.Toast
import java.io.InputStream
import java.net.URLEncoder



class BlutoothService(activity: Activity, handler: Handler) {
    private val TAG: String = "BluetoothService"

    private var adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var acitivity: Activity = activity

    private var handler: Handler = handler


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


    // 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
    fun getDeviceFind(name: String): BluetoothDevice? {
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        var selectedDevice: String = ""
        var returndevice: BluetoothDevice? = null
        var pairedDevices = adapter!!.bondedDevices
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        for (device in pairedDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if (name == device.name) {
                selectedDevice = device.name
                returndevice = device
                break
            }
        }

        return if (selectedDevice == "") {
            returndevice!!
        }else if(selectedDevice != "") {
            returndevice!!
        }else{
            null
        }
    }


    fun enableBluetooth() {
        if (adapter!!.isEnabled) {
        } else {
            var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            acitivity.startActivityForResult(intent, 10)
        }
    }
}