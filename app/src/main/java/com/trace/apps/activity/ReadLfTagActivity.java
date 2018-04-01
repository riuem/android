package com.trace.apps.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dgzrdz.mobile.lf.library.Constant;
import com.dgzrdz.mobile.lf.library.LFReader;
import com.trace.apps.Scanning;
import com.trace.apps.Util;

import org.apache.cordova.LOG;
import org.apache.cordova.camera.CameraLauncher;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.ionic.starter.R;


public class ReadLfTagActivity extends Activity implements View.OnClickListener,Serializable {

    public static ReadLfTagActivity rlft = null;
    //回调消息
    public static String callBackMsg = null;
    private Set<String> set = new HashSet<>();//id集合
    private int code = 9999;//状态码，成功改为10000
    private String backMsg = "未读到保险标";

    private static LFReader mReader;//读取类
    private BluetoothAdapter mBluetoothAdapter;
    private TextView mConnect;
    private TextView mDisconnect;
    private TextView mRead;
    private TextView mStopRead;

    private Context mContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            set.clear();//每次进来重置消息
            switch (msg.what) {
                case LFReader.STATE_CONNECTING://正在连接
                    Util.showMassage("正在连接");
                    LOG.i("trace_blue", "########## 正在连接");
                    break;
                case LFReader.STATE_CONNECTED://已经连接
                    Util.showMassage("已经连接");
                    LOG.i("trace_blue", "########## 已经连接");
                    break;
                case LFReader.STATE_CONNECT_FAIL://连接失败
                    Util.showMassage("连接失败连接");
                    LOG.i("trace_blue", "########## 连接失败");
                    break;
                case LFReader.STATE_READ_TAG_SUCCESS://读卡成功
                    Map<String, String> resMap = (Map<String, String>) msg.obj;//obj是底层返回，类型为Map
                    String id = resMap.get(LFReader.SERIAL); //获取低频号码（我们视为动物ID）
                    set.add(id);//传值
                    //String type = resMap.get(LFReader.TYPE);//保险标类型。我们不处理。
                    code = 10000;//成功状态码
                    backMsg = "success";//成功消息
                    Scanning.scan_flag = false;//杀掉弹窗
                    LOG.i("trace_blue", "########## 读标签成功");
                    Util.showMassage("标签读取成功");
                    ReadLfTagActivity.this.finish();//关闭并释放资源
                    break;
                case LFReader.STATE_NO_TAG_FOUND://未发现标签
                    Util.showMassage("未发现标签");
                    LOG.i("trace_blue", "########## 未发现标签");
                    break;
                case LFReader.STATE_READ_TAG_FAIL://读标签失败
                    Util.showMassage("读标签失败");
                    LOG.i("trace_blue", "########## 读标签失败");
                    break;
                case LFReader.STATE_READING_TAG://正在读标签
                    Util.showMassage("正在读标签");
                    LOG.i("trace_blue", "########## 正在读标签");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rlft = this;
        setContentView(R.layout.activity_read_lf);
        mContext = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        requestPermission();
        initView();
        initListener();
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(ReadLfTagActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有获取定位权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(ReadLfTagActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //已经禁止,提示用户
                Util.showMassage("您已禁止定位权限，需要重新开启");
            } else {
                ActivityCompat.requestPermissions(ReadLfTagActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
            }
        }
    }

    private void initListener() {
        mConnect.setOnClickListener(this);
        mDisconnect.setOnClickListener(this);
        mRead.setOnClickListener(this);
        mStopRead.setOnClickListener(this);
    }
    private void initView() {
        mConnect = (TextView) findViewById(R.id.connect);
        mDisconnect = (TextView) findViewById(R.id.disconnect);
        mRead = (TextView) findViewById(R.id.read);
        mStopRead = (TextView) findViewById(R.id.stop_read);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constant.REQUEST_BLUETOOTH_ENABLE);
        } else {
            mReader = LFReader.getInstance(mHandler);
        }
        serializableThis();
    }

    /**
     * 自我序列化 待完成
     */
    private void serializableThis() {
        try {
            FileOutputStream fos = openFileOutput("readfle", Activity.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //oos.writeObject(oos);
        }catch (Exception e){
            LOG.e("trace_blue",e.getMessage());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LOG.d("trace_blue", "onActivityResult " + resultCode);
        switch (requestCode) {
            case Constant.REQUEST_BLUETOOTH_ENABLE:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mReader = LFReader.getInstance(mHandler);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Util.showMassage("不可用");
                }
                break;
            case Constant.REQUEST_BLUETOOTH_CONNECT: //
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActctivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    if (mReader != null) {
                        if (device.getType() == 1) { //蓝牙2.0
                            mReader.connect(device, mContext);
                        } else if (device.getType() == 2) { //蓝牙4.0
                            mReader.connect(mContext, device);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect://连接
                connectBlu();//连接指令
                break;
            case R.id.disconnect://断开连接
                if (mReader != null) {
                    if (mReader.getBluetoothType() == Constant.BLUETOOTH2) { //传统蓝牙
                        mReader.close();
                    } else if (mReader.getBluetoothType() == Constant.BLE) { //低功耗蓝牙
                        mReader.closeBLE();
                    }
                    Util.showMassage("已经断开连接");
                    LOG.i("trace_blue", "########## 已经断开连接");
                }
                break;
            case R.id.read://开始读卡
                readBlu();
                break;
            case R.id.stop_read://停止读卡
                if (mReader != null) {
                    if (mReader.getBluetoothType() == Constant.BLUETOOTH2) { //传统蓝牙
                        mReader.stopReadTags();
                    } else if (mReader.getBluetoothType() == Constant.BLE) { //低功耗蓝牙
                        mReader.stopReadTags();
                    }
                }
                break;
            default:
                break;
        }
    }
    /**
     * 读取数据。
     */
    public void readBlu(){
        System.out.println("====================this系列===========================");
        System.out.println(this.mBluetoothAdapter);
        System.out.println(mReader);
        System.out.println(this.mHandler);
        System.out.println("====================this结束===========================");
        if (mReader != null && mReader.getState() == LFReader.STATE_CONNECTED) {//连接状态
            if (mReader.getBluetoothType() == Constant.BLUETOOTH2) { //传统蓝牙
                mReader.startReadTag();
            } else if (mReader.getBluetoothType() == Constant.BLE) { //低功耗蓝牙
                mReader.bleReadTag();
            }
        } else {
            Util.showMassage("蓝牙未连接");
            LOG.i("trace_blue", "########## 蓝牙未连接");
        }
    }
    /**
     * 关闭页面时，唤醒主界面，设置响应消息。
     */
    @Override
    public void finish() {
        callBackMsg = Util.formatStringToUI(code, set, backMsg);
        CameraLauncher.awakenMe();//在此唤醒
        super.finish();
    }
    /**
     * 连接蓝牙
     */
    public void connectBlu() {
        if (ContextCompat.checkSelfPermission(ReadLfTagActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有获取定位权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(ReadLfTagActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //已经禁止提示了
                Util.showMassage("您已禁止定位权限，需要重新开启");
                return;
            } else {
                ActivityCompat.requestPermissions(ReadLfTagActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
            }
        }
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {//finding device dialog.
                Intent serverIntent = new Intent(ReadLfTagActivity.this, DeviceListActctivity.class);
                startActivityForResult(serverIntent, Constant.REQUEST_BLUETOOTH_CONNECT);
            } else {
                Util.showMassage("您的蓝牙功能未打开，请您在设置中打开！");
            }
        } else {
            Util.showMassage("您的蓝牙功能不可用！");
        }
    }
}