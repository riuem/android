package com.trace.apps;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by ZZH on 2018-03-16.
 */
public class Util {
    public static Context context = null;

    /**
     * 提示音工具
     *
     * @param context
     * @param rawId
     */
    public static void play(Context context, int rawId) {
        MediaPlayer mp = MediaPlayer.create(context, rawId);
        mp.start();
    }

    /**
     * 弹出提醒
     *
     * @param msg
     */
    public static void showMassage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * 操作超时
     */
    public static String ERROR_TIMEOUT = "ERROR_TIMEOUT";
    /**
     * 蓝牙连接异常 : 棒性扫描器异常
     */
    public static String ERROR_BLUETOOTH_EXCEPTION = "ERROR_BLUETOOTH_EXCEPTION";
    /**
     * 高频设备异常
     */
    public static String ERROR_UFH_EXCEPTION = "ERROR_UFH_EXCEPTION";

    /**
     * 给UI的消息格式
     * @param code 状态码 ： 10000成功，其他失败
     * @param data 扫描到的ID号码，可能为多个。
     * @param msg  成功为success ，失败为各种不同信息，具体内容由调用者提供。
     * @return
     */
    public static String formatStringToUI(int code, Set<String> data, String msg) {
        // 3-26要求的格式：{"code":10000,"data":["E20094C6A0B48148F33C3E05","E2000016880401580370EC8A"],"msg":"success"}
        JSONObject jsonObject = new JSONObject();
        //状态信息
        try {
            jsonObject.put("code", code);//状态码
            JSONArray jsonArray = new JSONArray();//ID内容
            for (String str : data) {
                jsonArray.put(str);
            }
            jsonObject.put("data", jsonArray);
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            LOG.e("TRACE_ERROR", "formatString");
        }
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
    }
}