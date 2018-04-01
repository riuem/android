package com.trace.apps;

import android.content.Context;

import com.handheld.uhfr.UHFRManager;
import com.uhf.api.cls.Reader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.ionic.starter.R;

import com.BRMicro.Tools;

import org.apache.cordova.LOG;
import org.apache.cordova.camera.CameraLauncher;

/**
 * Created by ZZH on 2018-03-16.
 */
public class ReadUHFTag {
    public static boolean scanFlag = true;
    public static Context context = null;
    private int code = 9999;
    private String msg = "扫描超时,请重新尝试或让设备靠近耳标";

    public void read() {
        scanFlag = true;
        Set<String> ecpIDs = new HashSet<>(); //高频标签ID集合
        try {
            LOG.i("UTF_OPERATION", "---------------------- Start read ------------------------");
            UHFRManager uhfrManager = UHFRManager.getIntance(); //获取高频操作实例
            //System.out.println(uhfrManager.getHardware());//硬件信息
            Reader.READER_ERR reader_err = uhfrManager.asyncStartReading();//开始扫描,获得响应信息

            int count = 0;
            for (int i = 0; i < 15 && scanFlag; i++) { //连续扫描15次
                if (ecpIDs.size() == 0 && count < 600) { //如果扫描不到，则最多持续扫描一分半钟
                    i--;
                    count++;
                }
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    LOG.e("UTF_ERROR", e.getMessage());
                }
                List<Reader.TAGINFO> taginfos = uhfrManager.tagInventoryRealTime();//以实时获取方式获得ID
                for (Reader.TAGINFO taginfo : taginfos) {
                    String ecpID = Tools.Bytes2HexString(taginfo.EpcId, taginfo.Epclen);
                    if (!ecpIDs.contains(ecpID)) { //扫描成功
                        Util.play(context, R.raw.beep);//播放扫描提示
                        ecpIDs.add(ecpID);//转为字符串，放入set
                    }
                }
                if (ecpIDs.size() != 0) {
                    msg = "success";
                    code = 10000;
                    Scanning.scan_flag = false;//扫描完成，修改提示框标志
                }
            }
            Reader.READER_ERR reader_err1 = uhfrManager.asyncStopReading();//停止扫描
            LOG.i("UTF_OPERATION", "---------------------- End read ------------------------");
            if(!scanFlag){
                code = 9997;//用户取消
                msg="用户已经取消";
            }
            CameraLauncher.ecpIDs = Util.formatStringToUI(code, ecpIDs, msg);
            CameraLauncher.awakenMe();//唤醒
        } catch (Exception e) {
            LOG.d("trace_uhf", e.getMessage());
            CameraLauncher.ecpIDs = Util.formatStringToUI(code, ecpIDs, msg);
            CameraLauncher.awakenMe();//唤醒
        }
    }
}