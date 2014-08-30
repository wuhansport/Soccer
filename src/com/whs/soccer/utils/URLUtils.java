package com.whs.soccer.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * Class URLUtils
 * @author antoniochen
 */
public final class URLUtils {
    private static final Pattern VALID_URL = Pattern.compile("((http://)?(\\w+[.])*|(www.))\\w+[.]"
            + "([a-z]{2,4})?[[.]([a-z]{2,4})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z]{2,4}+|/?)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_LOCAL_URL = Pattern.compile("(.+)localhost(:)?(\\d)*/(.+)(\\.)(.+)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_MTT_URL = Pattern.compile("mtt://(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_QBE_URL = Pattern.compile("qube://(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_IPADDRESS = Pattern.compile("(\\d){1,3}\\.(\\d){1,3}\\.(\\d){1,3}\\."
            + "(\\d){1,3}(:\\d{1,4})?(/(.*))?", Pattern.CASE_INSENSITIVE);

    /**
     * 判断是否有中文
     */
    public static boolean hasNotASCII(String str) {
        for (int i = 0, length = str.length(); i < length; ++i) {
            if (str.charAt(i) > 255) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断URL是否是一个有效的格式
     */
    public static boolean isCandidateUrl(String url) {
        if (TextUtils.isEmpty(url) || hasNotASCII(url)) {
            return false;
        }

        final String uri = url.trim();
        return (VALID_URL.matcher(uri).find() || VALID_LOCAL_URL.matcher(uri).find()
                || VALID_IPADDRESS.matcher(uri).find() || VALID_MTT_URL.matcher(uri).find()
                || VALID_QBE_URL.matcher(uri).find());
    }

    /**
     * 判断URL是否有一个有效的协议头
     */
    public static boolean hasValidProtocal(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        final String uri = url.trim().toLowerCase(Locale.getDefault());
        final int pos1 = uri.indexOf("://");
        final int pos2 = uri.indexOf('.');

        // 检测"wap.fchgame.com/2/read.jsp?url=http://www.zaobao.com/zg/zg.shtml"类型网址
        if (pos1 > 0 && pos2 > 0 && pos1 > pos2) {
            return false;
        }

        return uri.contains("://");
    }

    /**
     * 根据输入，得到一个有效URL 如果输入无法被解析为一个URL，返回NULL
     */
    public static String resolveValidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String tmpUrl = url.trim();

        // bug9422306：当url是“data:Image/jpg;base64,/9j/4AAQSkZJRgABAgAAZABkA...”这种
        // 字符串形式的图片数据时，作特殊处理，否则会发生ANR
        if (tmpUrl.length() > 11 && tmpUrl.substring(0, 11).toLowerCase(Locale.getDefault()).startsWith("data:image/")) {
            return tmpUrl;
        }

        if (isCandidateUrl(tmpUrl)) {
            if (!hasValidProtocal(tmpUrl)) {
                tmpUrl = "http://" + tmpUrl;
            }

            try {
                if (tmpUrl.startsWith("tencent://") || tmpUrl.startsWith("qube://")) {
                    return tmpUrl;
                } else {
                    new URL(tmpUrl).toString();
                    return tmpUrl;
                }
            } catch (MalformedURLException e) {
                // Nothing
            }
        }

        return null;
    }
    
    public static URL convertToURLEscapingIllegalCharacters(String string) {
         try {
             String decodedURL = URLDecoder.decode(string, "UTF-8");
             URL url = new URL(decodedURL);
             URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
             return uri.toURL();
         }
         catch(Exception ex) {
             ex.printStackTrace();
             return null;
         }
     }

    /**
     * This utility class cannot be instantiated.
     */
    private URLUtils() {
    }
}
