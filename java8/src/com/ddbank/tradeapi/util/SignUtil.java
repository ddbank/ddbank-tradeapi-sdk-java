package com.ddbank.tradeapi.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by damon3588#163.com on 2019/09/01.
 * DDBANK API签名工具类
 */
public class SignUtil {

    public static String sign(String message,String secret){
        try{
            message = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            mac.init(keySpec);
            mac.update(message.getBytes());
            byte[] digest = mac.doFinal();
            return Base64.getEncoder().encodeToString(digest);
        }catch (Exception e){
            throw new RuntimeException("Unable to sign message.",e);
        }
    }

}
