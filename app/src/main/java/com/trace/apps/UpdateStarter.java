package com.trace.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.apache.cordova.LOG;
import org.apache.cordova.camera.CameraLauncher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ZZH on 2018-03-19.
 * 更新启动器。
 */
public class UpdateStarter {
    public static String callBackMsg;
    public static int code=9999;//弹出消息
    public static Set<String> set = new HashSet<>();
    public static Context context;
    /**
     * 传入指定容器，将异步进行更新检查，并自动升级版本。

     */
    public static void start() {
        new Thread() {
            @Override
            public void run() {
                System.out.println("更新检查开始。。。。。。");
                LOG.i("Trace_update","start check update");
                String newApkFileName = "";//新版本安装文件名
                String newVersionName = "";//新版本名
                int newVersionNumber = 0;//新版本编号
                int versionNumber = 0 ;//当前版本号
                try {
                    //服务器版本文件内容。响应格式     ： 1:0.0.1:安装包名.apk|1:0.0.1:安装包名_scan.apk
                    String versionText = HttpUtils.get("http://47.96.48.160/download/animaltraceversion");
                    System.out.println(versionText);
                    //包管理器
                    PackageManager manager = context.getPackageManager();
                    //获取服务器版本信息。本文件对应为扫描（scan）版，在split的第二顺位。
                    //第一顺位位不带扫描版，以后其他硬件，逐步增加。
                    //versionText = "7:0.0.7:AnimalTrace_0.0.7.scan.apk|7:0.0.7:AnimalTrace_0.0.7.scan.apk";//模拟
                    versionText = versionText.split("\\|")[1];
                    System.out.println("111 " + versionText);
                    String versionTemp[] = versionText.split(":");
                    System.out.println(Arrays.toString(versionTemp));
                    newVersionNumber = Integer.parseInt(versionTemp[0]); //版本号，角标0
                    newVersionName = versionTemp[1];//版本名。角标1
                    newApkFileName = versionTemp[2];//新版文件。角标2
                    PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                    versionNumber = info.versionCode;//本地版本号

                    if (newVersionNumber > versionNumber ) { //服务器版本号大于本地版本号，则升级
                        LOG.i("TRACE_UPDATE_START","发现新版本");
                        callBackMsg = Util.formatStringToUI(code,set,"发现新版本，自动升级中");
                        CameraLauncher.awakenMe();//唤醒界面
                        UpdateAPK.url = UpdateAPK.URLHEAD+newApkFileName;//UpdateAPK.URLHEAD : 服务器目录，已经带/，后面拼接就好。
                        context.startService(new Intent(context, UpdateAPK.class));

                    }else {
                        callBackMsg = Util.formatStringToUI(code,set,"无需更新");
                        CameraLauncher.awakenMe();
                        LOG.i("TRACE_UPDATE_CANCEL","未发现新版本");
                    }
                    LOG.i("TRACE_UPDATE_SUCCESS","新版本："+newVersionName+" 升级操作完成");//注意，此处只能说明升级操作无异常。实际成功与否，看具体升级记录
                } catch (Exception e) {
                    callBackMsg = Util.formatStringToUI(code,set,"检查更新失败");
                    CameraLauncher.awakenMe();
                    LOG.e("TRACE_UPDATE_ERROR", e.getMessage());
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
