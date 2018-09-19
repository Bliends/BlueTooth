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




class MainActivity : AppCompatActivity() {
    private var btService: BlutoothService? = null
    var mWorkerThread :Thread? = null
    var mSocket : BluetoothSocket? = null
    var mOutputStream = null
    var mInputStream :InputStream?= null
    private val bt: BluetoothSPP? = null
    var adapter = BluetoothAdapter.getDefaultAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()

        if (btService == null) {
            btService = BlutoothService(this, handler)
            Log.e("asdf", "asdf")
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
        Log.e("반환", "1234")
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("asdf", "asdf")
                try {
                    if (btService!!.getDeviceFind("HC-06") != null) {
                        connectToSelectedDevice(btService!!.getDeviceFind("HC-06")!!)
                    } else {
                    }
                }catch (e : NullPointerException){
                    adapter.startDiscovery()
                    var filter = IntentFilter()
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
                    filter.addAction(BluetoothDevice.ACTION_FOUND) //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
                    registerReceiver(mReceiver,filter)
                    Log.e("null","null")
                }
            } else {
                toast("블루투스를 연결해 주세요.")
            }
        }
    }


    private var mReceiver = object : BroadcastReceiver() { //각각의 디바이스로부터 정보를 받으려면 만들어야함

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action){
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.e(device.name, device.address)
                if(device.name == "HC-06"){
                    connectToSelectedDevice(device)
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                Toast.makeText(this@MainActivity,"기기를 검색중입니다.\n잠시만 기다려 주세요",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mReceiver)
        }catch (e : IllegalArgumentException){}

        try{
            mWorkerThread!!.interrupt() // 데이터 수신 쓰레드 종료
            mInputStream!!.close()
            mSocket!!.close()
        }catch(e :Exception){}
    }
    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

        }
    }

    internal fun connectToSelectedDevice(selectedDeviceName: BluetoothDevice) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        var mRemoteDevie :BluetoothDevice = selectedDeviceName
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        val uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            var mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid)
            mSocket.connect() // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            var mOutputStream = mSocket.getOutputStream()
            var  mInputStream = mSocket.getInputStream()

            // 데이터 수신 준비.

        } catch (e: Exception) { // 블루투스 연결 중 오류 발생
            Toast.makeText(this, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
        }
    }



//
//    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
//    fun beginListenForData() {
//        val handler = Handler()
//        Log.e("14","43")
//        var readBufferPosition = 0                 // 버퍼 내 수신 문자 저장 위치.
//        var readBuffer = ByteArray(1024)            // 수신 버퍼.
//
//        // 문자열 수신 쓰레드.
//        mWorkerThread = Thread(Runnable {
//            // interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
//            // interrupt() 메소드는 하던 일을 멈추는 메소드이다.
//            // isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
//
//            Log.e("asdf","g")
//
//            while (!Thread.currentThread().isInterrupted) {
//                try {
//
//                    var mInputStream : InputStream? = null
//                    var mCharDelimiter = '\n' as Byte
//                    // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
//                    val byteAvailable = mInputStream!!.available()   // 수신 데이터 확인
//                    if (byteAvailable > 0) {                        // 데이터가 수신된 경우.
//                        val packetBytes = ByteArray(byteAvailable)
//
//                        Log.e("asdf","g")
//                        // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
//                        mInputStream.read(packetBytes)
//                        for (i in 0 until byteAvailable) {
//                            var b = packetBytes[i]
//                            if (b == mCharDelimiter) {
//                                val encodedBytes = ByteArray(readBufferPosition)
//                                //  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
//                                //  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
//                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.size)
//                                val data = String(encodedBytes,Charsets.US_ASCII)
//
//                                readBufferPosition = 0
//
//                                handler.post(Runnable // 수신된 문자열 데이터에 대한 처리.
//                                {
//                                    // mStrDelimiter = '\n';
//                                    Log.e("asdf","gjgdfzghfj")
//                                    Log.e(data,data)
//                                })
//                            } else {
//                                readBuffer[readBufferPosition++] = b
//                            }
//                        }
//                    }
//
//                } catch (e: Exception) {    // 데이터 수신 중 오류 발생.
//                    Toast.makeText(this, "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show()
//                    finish()            // App 종료.
//                }
//
//            }
//        })
// }


    //브로드캐스트리시버를 이용하여 블루투스 장치가 연결이 되고, 끊기는 이벤트를 받아 올 수 있다.
    internal var bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action //연결된 장치를 intent를 통하여 가져온다.
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            // 장치가 연결이 되었으면
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                Log.d("TEST", device.name.toString() + " Device Is Connected!")
                //장치의 연결이 끊기면
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                Log.d("TEST", device.name.toString() + " Device Is DISConnected!")
            }
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
