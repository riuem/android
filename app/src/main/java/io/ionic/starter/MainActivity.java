/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package io.ionic.starter;

import android.os.Bundle;
import com.trace.apps.ReadUHFTag;
import com.trace.apps.UpdateStarter;
import com.trace.apps.Util;

import org.apache.cordova.*;
import org.apache.cordova.camera.CameraLauncher;

import java.io.FileOutputStream;

public class MainActivity extends CordovaActivity  {
    private void traceInit() {
        ReadUHFTag.context = getApplicationContext();
        Util.context = getApplicationContext();
        CameraLauncher.context = this;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        traceInit();
        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }
        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
        UpdateStarter.start(getApplicationContext());//启动更新检查
    }

    /**
     * 反序列化蓝牙对象
     */
    private void initBlu(){

    }

}