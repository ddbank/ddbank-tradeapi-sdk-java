package com.ddbank.tradeapi.client;


import com.alibaba.fastjson.JSON;
import com.ddbank.tradeapi.Configuration;
import com.ddbank.tradeapi.util.MapSort;
import com.ddbank.tradeapi.util.MyX509TrustManager;
import com.ddbank.tradeapi.util.SignUtil;
import com.ddbank.tradeapi.util.URLParamUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by damon3588#163.com on 2019/09/01.
 * 裸钻下单
 */
public class SubmitOrderClient {

    public static void main(String[] args) throws Exception{

        Map<String,String> param = new HashMap();
        param.put("stoneIds","5d9d525baf7d653df2d347f5,5d9d5265af7d653df2d3481a");
        param.put("brandName","品牌商名称test");
        param.put("storeName","店铺名称test");
        param.put("storeAddr","店铺地址test");
        param.put("storeType","");//填空字符即可
        param.put("customName","客户姓名test");
        param.put("brandOrder","NO201909010001");
        param.put("deliverDate","2019-09-01");

        //提交下单请求
        submit(Configuration.API_KEY,Configuration.API_SECRET,param);
    }

    public static void submit(String apiKey,String apiSecret,Map<String,String> param) throws Exception{
        //API Secret 进行SHA1加密
        String secretSHA1 = DigestUtils.sha1Hex(apiSecret);
        //API请求方式（大写）
        String reqMethod = "POST";
        //API请求地址(domain + uri)
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Configuration.API_ENDPOINT).append(Configuration.URI_STOCK_SUBMIT);
        String targetUrl = urlBuilder.toString();
        //POST参数key排序及URL参数化
        String bodyParam = URLParamUtil.formatting(MapSort.sortMapByKey(param));
        //unix时间戳
        Long timeStamp = System.currentTimeMillis();
        //一次性随机字符串
        String nonce = RandomStringUtils.randomAlphanumeric(16);
        //API请求签名
        StringBuilder readySign = new StringBuilder();
        readySign.append(reqMethod).append(urlBuilder).append(nonce).append(timeStamp).append(bodyParam);
        String targetSign = SignUtil.sign(readySign.toString(), secretSHA1);
        //HTTP请求
        HttpsURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        String bodyJson = JSON.toJSONString(param);
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
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", Configuration.MEDIA_TYPE_JSON);
            connection.setRequestProperty(Configuration.HEADER_KEY,apiKey);
            connection.setRequestProperty(Configuration.HEADER_TS,String.valueOf(timeStamp));
            connection.setRequestProperty(Configuration.HEADER_NONCE,nonce);
            connection.setRequestProperty(Configuration.HEADER_SIGN,targetSign);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            os = connection.getOutputStream();
            os.write(bodyJson.getBytes());
            os.flush();
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
            if (null != os) {
                try {
                    os.close();
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
