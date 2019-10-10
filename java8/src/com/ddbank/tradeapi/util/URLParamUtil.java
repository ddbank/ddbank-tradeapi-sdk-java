package com.ddbank.tradeapi.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by damon3588#163.com on 2019/09/01.
 * URL参数处理工具类
 */
public class URLParamUtil {

    public static String formatting(Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            try {
                stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ignored) {
            }

        }
        return stringBuilder.toString();
    }

}
