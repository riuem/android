package com.trace.apps;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by ZZH on 2018-03-18.
 */
public class HttpUtils {
    /**
     * 获取指定URL内容
     *
     * @param urlString
     * @return
     */
    public static String get(String urlString) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            //请求
           // urlConnection.setRequestMethod("post");//要求更高的权限，高版本不要设定
            //超时
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(3000);
            //获取响应的状态码
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                InputStream in = urlConnection.getInputStream();
                byte[] buffer = new byte[4 * 1024];
                int len = -1;
                while ((len = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                //close(in);
                byte[] result = bos.toByteArray();
                close(bos);
                return new String(result);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "";
    }

    private static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
