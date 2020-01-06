package com.ddbank.tradeapi.client;

import com.alibaba.fastjson.JSON;
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
 * 裸钻下单
 */
public class SubmitOrderClient {

    public static void main(String[] args){

        Map<String,String> param = new HashMap<>();
        param.put("stoneIds","5d8b0fefc7763355a358ad1c,5d82fcc431d5a71d1e37b642");
        param.put("brandName","品牌商名称test");
        param.put("storeName","店铺名称test");
        param.put("storeAddr","店铺地址test");
        param.put("storeType","2");//直营(1)加盟(2)
        param.put("customName","客户姓名test");
        param.put("brandOrder","NO201909010001");
        param.put("deliverDate","2019-09-01");
		param.put("contactName","某某某");
		param.put("contactPhone","13666666666");

        //提交下单请求
        submit(Configuration.API_KEY,Configuration.API_SECRET,param);
    }

    public static void submit(String apiKey,String apiSecret,Map<String,String> param){
        //API Secret 进行SHA1加密
        String secretSHA1 = DigestUtils.sha1Hex(apiSecret);
        //API请求方式（大写）
        String reqMethod = "POST";
        //API请求地址(domain + uri)
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Configuration.API_ENDPOINT).append(Configuration.URI_STOCK_SUBMIT);
        //POST参数key排序及URL参数化
        String bodyParam = URLParamUtil.formatting(MapSort.sortMapByKey(param));
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
        readySign.append(reqMethod).append(urlBuilder).append(nonce).append(timeStamp).append(bodyParam);
        String targetSign = SignUtil.sign(readySign.toString(), secretSHA1);
        headers.add(Configuration.HEADER_SIGN, targetSign);
        //post请求并调用
        String bodyJson = JSON.toJSONString(param);
        HttpEntity<String> requestEntity = new HttpEntity<>(bodyJson, headers);
        RestTemplate client = new RestTemplate();
        client.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        ResponseEntity<String> response;
        try {
            response = client.exchange(urlBuilder.toString(), HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.getBody());

    }


}
