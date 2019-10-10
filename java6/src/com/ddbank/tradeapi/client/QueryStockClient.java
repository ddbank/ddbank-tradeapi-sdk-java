package com.ddbank.tradeapi.client;

import com.ddbank.tradeapi.Configuration;
import com.ddbank.tradeapi.util.MapSort;
import com.ddbank.tradeapi.util.MyX509TrustManager;
import com.ddbank.tradeapi.util.SignUtil;
import com.ddbank.tradeapi.util.URLParamUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by damon3588#163.com on 2019/09/01.
 * 查询裸钻库存
 */
public class QueryStockClient {

    public static void main(String[] args) throws Exception{

        Map<String,String> param = new HashMap();
        param.put("pageNum","1");
        param.put("pageSize","10");
        param.put("customCode",Configuration.CUSTOM_CODE);
        //param.put("color","D,E,F");
        //param.put("cut","EX,VG");
        //param.put("weight","0.3,0.49");

        //查询裸钻库存
        query(Configuration.API_KEY,Configuration.API_SECRET,param);


    }

    public static void query(String apiKey,String apiSecret,Map<String,String> param) throws Exception{
        //API Secret 进行SHA1加密
        String secretSHA1 = DigestUtils.sha1Hex(apiSecret);
        //API请求方式（大写）
        String reqMethod = "GET";
        //API请求地址(domain + uri)
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Configuration.API_ENDPOINT).append(Configuration.URI_STOCK_QUERY);
        //GET参数URL参数化
        String getParam = URLParamUtil.formatting(MapSort.sortMapByKey(param));
        String targetUrl = urlBuilder.append("?").append(getParam).toString();
        //unix时间戳
        Long timeStamp = System.currentTimeMillis();
        //一次性随机字符串
        String nonce = RandomStringUtils.randomAlphanumeric(16);
        //API请求签名
        StringBuilder readySign = new StringBuilder();
        readySign.append(reqMethod).append(targetUrl).append(nonce).append(timeStamp);
        String signStr = SignUtil.sign(readySign.toString(), secretSHA1);
        HttpsURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL","SunJSSE");
            sslcontext.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
            URL url = new URL(targetUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslcontext.getSocketFactory());
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            connection.setRequestMethod(reqMethod);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", Configuration.MEDIA_TYPE_JSON);
            connection.setRequestProperty(Configuration.HEADER_KEY,apiKey);
            connection.setRequestProperty(Configuration.HEADER_TS,String.valueOf(timeStamp));
            connection.setRequestProperty(Configuration.HEADER_NONCE,nonce);
            connection.setRequestProperty(Configuration.HEADER_SIGN,signStr);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                }
                System.out.println(sbf.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection){
                connection.disconnect();
            }
        }

    }

}
