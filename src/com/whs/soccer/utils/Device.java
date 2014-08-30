package com.whs.soccer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

public class Device {

    private Device() {
    }

    public static boolean isInWifiOrCabelMode(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null
                && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo
                .getType() == ConnectivityManager.TYPE_ETHERNET);
    }
    
    public static String getMyWifiIp(Context context) {
        WifiManager wifiMan = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        if(wifiMan == null)
            return "";

        WifiInfo info = wifiMan.getConnectionInfo();
        if(info == null) {
            return "";
        }

        String ipString = "";

        int ipAddress = info.getIpAddress();

        if(ipAddress != 0) {
            ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff)
                    + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        }

        return ipString;
    }

    public static String getSSID(Context context) {
        WifiManager wifiMan = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        if(wifiMan == null)
            return "";
        return wifiMan.getConnectionInfo().getSSID();
    }

    /**
     * 获取手机mac地址<br/>
     * 错误返回12个0
     */
    public static String getMacAddress(Context context) {
        // 获取mac地址：
        String macAddress = "000000000000";
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress()))
                    macAddress = info.getMacAddress().replace(":", "");
                else
                    return macAddress;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }
}
