package com.trace.apps;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.cordova.camera.CameraLauncher;

import io.ionic.starter.R;

public class Scanning extends FragmentActivity {
    public static int count = 30;
    private TextView txt_scan;
    public static boolean scan_flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        txt_scan =  findViewById(R.id.txt_scan);
        Button btn = findViewById(R.id.btn_cencel);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) { //按下改变颜色
                if(motionEvent.getAction()==motionEvent.ACTION_DOWN){
                    view.setBackgroundColor(Color.parseColor("#d0d0d0"));
                }
                return false;
            }
        });
       new Thread(){
           int count = Scanning.count; //交给外界控制
           @Override
           public void run() {
               int time = 0;
               while (scan_flag) {
                   try {
                       Thread.sleep(50);//100毫秒检查扫描标志，如果扫描到信号，就删除退出循环。
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   time +=50;
                   if(time % 1000 == 0) {
                       if (--count <= 0) {
                           break;//退出
                       }
                       //修改UI
                       Scanning.this.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               txt_scan.setText("扫描中，剩余 " + count + "秒");
                           }
                       });
                   }
               }
               Scanning.this.finish();//弹框消失。
           }
       }.start();
    }

    //点击关闭
   public void closeWin(View view){
        ReadUHFTag.scanFlag = false;//停止扫描
        finish();
   }

    @Override
    public void finish() {
        super.finish();
        CameraLauncher.awakenMe();
    }
}
