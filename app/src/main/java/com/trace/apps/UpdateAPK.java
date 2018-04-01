package com.trace.apps;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.apache.cordova.LOG;

import java.io.File;

/**
 * 系统更新下载
 */
public class UpdateAPK extends Service {
    public static final String URLHEAD = "http://47.96.48.160/download/";
    public static String url ;
    /**
     * 下载管理工具
     **/
    private DownloadManager manager;
    /**
     * 下载完成广播
     **/
    private DownloadCompleteReceiver receiver;
    /**
     * 初始化下载管理工具
     **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 下载时，通知栏显示途中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(this.getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "AnimalTrace.apk");
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.d("TRACE_UPDATE","start download apk file");
        // 调用下载
        initDownManager();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }
    /**
     * 接收下载完成后的intent
     */
    private class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //自动安装apk
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Uri uriForDownloadedFile = manager.getUriForDownloadedFile(downId);
                    Log.d("TRACE_MSG", "uri=" + uriForDownloadedFile);
                    installApkNew(uriForDownloadedFile);
                }
                //停止服务并关闭广播
                UpdateAPK.this.stopSelf();
            }
        }
        /**
         * 安装apk
         */
        protected void installApkNew(Uri uri) {
//            Intent intent = new Intent();
//            //执行动作
//            intent.setAction(Intent.ACTION_VIEW);
//            //执行的数据类型
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//            //不加，部分情况下会导致单击崩溃
//            //android.os.Process.killProcess(android.os.Process.myPid());
//            startActivity(intent);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                String filePath = getRealPathFromURI(uri);
                File file = new File(filePath);
                Uri apkUri = FileProvider.getUriForFile( getApplicationContext(), "com.trace.apps.fileprovider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            }else{ //低版本
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            startActivity(intent);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}