package com.ddbank.tradeapi.client;

import com.ddbank.tradeapi.Configuration;
import com.ddbank.tradeapi.util.MapSort;
import com.ddbank.tradeapi.util.SignUtil;
import com.ddbank.tradeapi.util.URLParamUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by damon3588#163.com on 2019/09/01.
 * 查询裸钻库存
 */
public class QueryStockClient {

    public static void main(String[] args) {

        Map<String,String> param = new HashMap<>();
        param.put("pageNum","1");
        param.put("pageSize","10");
        param.put("customCode",Configuration.CUSTOM_CODE);
        //param.put("color","D,E,F");
        //param.put("cut","EX,VG");
        //param.put("weight","0.3,0.49");

        //查询裸钻库存
        query(Configuration.API_KEY,Configuration.API_SECRET,param);


    }

    public static void query(String apiKey,String apiSecret,Map<String,String> param) {
        //API Secret 进行SHA1加密
        String secretSHA1 = DigestUtils.sha1Hex(apiSecret);
        //API请求方式（大写）
        String reqMethod = "GET";
        //API请求地址(domain + uri)
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Configuration.API_ENDPOINT).append(Configuration.URI_STOCK_QUERY);
        //GET参数URL参数化
        String getParam = URLParamUtil.formatting(MapSort.sortMapByKey(param));
        String bodyParam = "";//无
        String url = urlBuilder.append("?").append(getParam).toString();
        //API请求Headers
        HttpHeaders headers = new HttpHeaders();
        //unix时间戳
        Long timeStamp = System.currentTimeMillis();
        //一次性随机字符串
        String nonce = RandomStringUtils.randomAlphanumeric(16);
        headers.add(Configuration.HEADER_KEY, apiKey);
        headers.add(Configuration.HEADER_TS, String.valueOf(timeStamp));
        headers.add(Configuration.HEADER_NONCE,nonce);
        MediaType mediaType = MediaType.parseMediaType(Configuration.MEDIA_TYPE_JSON);
        headers.setContentType(mediaType);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        //API请求签名
        StringBuilder readySign = new StringBuilder();
        readySign.append(reqMethod).append(url).append(nonce).append(timeStamp).append(bodyParam);
        String signStr = SignUtil.sign(readySign.toString(), secretSHA1);
        headers.add(Configuration.HEADER_SIGN, signStr);
        //发送GET请求
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        RestTemplate client = new RestTemplate();
        client.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        ResponseEntity<String> response;
        try {
            response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.getBody());
    }

}
