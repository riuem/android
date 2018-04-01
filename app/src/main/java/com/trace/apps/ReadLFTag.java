package com.trace.apps;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.dgzrdz.mobile.lf.library.LFReader;

import java.util.Map;

import io.ionic.starter.R;


/**
 * Created by ZZH on 2018-03-17.
 */

public class ReadLFTag {
    public static Context context = null;


    public ReadLFTag(Context context) {
        this.context = context;
        connectionBlue();//启动连接
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case LFReader.STATE_CONNECTING://正在连接
                    break;
                case LFReader.STATE_CONNECTED://成功连接
                    break;
                case LFReader.STATE_CONNECT_FAIL://连接失败

                    break;
                case LFReader.STATE_READ_TAG_SUCCESS://读卡成功
                    Map<String, String> m = (Map<String, String>) msg.obj;//map结构：{type=FDXB, serial=999 001801242637} ，其中type不固
                    String id = m.toString().split("=")[2].replace("}", "");
                    //回调js方法：
                    Toast.makeText(context, id, 0);
                    break;
                case LFReader.STATE_READ_TAG_FAIL://读卡失败
                    Toast.makeText(context, "读取失败，请靠近一点", 0).show();
                    System.out.println("==========================失败====================");
            }
        }
    };
    private LFReader lfReader = LFReader.getInstance(mHandler);
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    public void connectionBlue() {
        if (!mBluetoothAdapter.isEnabled()) {//如果蓝牙关闭
//            // 弹出对话框提示用户是后打开
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, 1);
            // 不做提示，强行打开蓝牙
            mBluetoothAdapter.enable();
        }
        String address = "00:15:83:00:A4:AC";
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        System.out.println("+++++++++++++++++++++++++++++++++++++++++");
        if (lfReader != null) {
            System.out.println("tyep : " + device.getType());
            if (device.getType() <= 1) { // 蓝牙2.0
                lfReader.connect(device, context);
            } else if (device.getType() == 2) {//4.0
                lfReader.connect(context, device);
            }
        }

    }

    /**
     * 读取数据
     *
     * @param context
     */
    public void readTag() {
        System.out.println("start ========================== read");
//        if (!lfReader.pingDevice()) { //检查连接状态
//            this.connectionBlue();
//        }
        System.out.println("***********************");
        lfReader.bleReadTag();
        System.out.println("end ========================== read");
    }

    /****************************************************************************/


}
