package com.dexter0218.zhihu.support.lib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import hugo.weaving.DebugLog;

/**
 * Created by Dexter0218 on 2016/7/21.
 */
public class Http {
    private static final String TAG ="ZHIHU_HTTP";
    public static final String CHARSET = "UTF-8";

    @DebugLog
    public static String get(String address) throws IOException {
        Log.i(TAG, "get: address"+address);
        URL url = new URL(address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        try {
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.i(TAG, "get: response"+response.toString());
                return response.toString();
            } else {
                Log.e(TAG, "Network Error - response code:" + con.getResponseCode());
                throw new IOException("Network Error - response code:" + con.getResponseCode());
            }
        } finally {
            con.disconnect();
        }
    }

    public static String get(String baseUrl, String key, String value) throws IOException {
        return get(baseUrl + "?" + concatKeyValue(key, value));
    }

    public static String get(String baseUrl, int suffix) throws IOException {
        return get(baseUrl + suffix);
    }

    public static String get(String baseUrl, String suffix) throws IOException {
        return get(baseUrl + encodeString(suffix));
    }

    private static String concatKeyValue(String key, String value) {
        return encodeString(key) + "=" + encodeString(value).replace("+", "%20");
    }

    private static String encodeString(String str) {
        try {
            return URLEncoder.encode(str, CHARSET);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
