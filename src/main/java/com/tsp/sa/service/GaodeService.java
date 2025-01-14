package com.tsp.sa.service;

import com.tsp.sa.entity.GaodeTransitDirectionRequestParameters;
import com.tsp.sa.entity.GaodeTransitDirectionResponseData;
import com.tsp.sa.entity.GeocodeRegeoRequestParameters;
import com.tsp.sa.entity.GeocodeRegeoResponseData;
import com.tsp.sa.properties.GaodeProperties;
import com.tsp.sa.utils.HttpUtil;
import com.tsp.sa.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.*;

@Service
public class GaodeService {

    @Autowired
    GaodeProperties gaodeProperties;

    /**
     *  高德地址编码接口
     * @param parameters
     * @return
     */
    public GeocodeRegeoResponseData getGeocode(GeocodeRegeoRequestParameters parameters){

        Map<String,Object> map = new HashMap<>();
        map.put("key",gaodeProperties.getDirectionTransitKey());
        map.put("location",parameters.getLocation());
        map.put("batch",true);

        String sig = getGaodeSign(map,gaodeProperties.getDirectionTransitPrivatekey());

        String url = gaodeProperties.getGeocodeRegeoUrl() + "key=" + gaodeProperties.getDirectionTransitKey() + "&"
                + "location=" + parameters.getLocation() + "&"
                + "batch=true&"
                + "sig=" + sig;

        String rest = HttpUtil.rest(map,url,HttpMethod.GET);

        GeocodeRegeoResponseData data = JsonUtil.json2RegeoData(rest);

        return data;
    }

    /**
     *  高德路线规划接口
     * @param parameters
     * @return
     */
    public GaodeTransitDirectionResponseData getDistance(GaodeTransitDirectionRequestParameters parameters){

        Map<String,Object> map = new HashMap<>();
        map.put("key",gaodeProperties.getDirectionTransitKey());
        map.put("origin",parameters.getOrigin());
        map.put("destination",parameters.getDestination());
        map.put("city",parameters.getCity());
        map.put("cityd",parameters.getCity());
        map.put("strategy",parameters.getCity());

        String sig = getGaodeSign(map,gaodeProperties.getDirectionTransitPrivatekey());

        String requestUrl = gaodeProperties.getDirectionTransitUrl() + "key=" + gaodeProperties.getDirectionTransitKey() + "&"
                + "origin=" + parameters.getOrigin() + "&"
                + "destination=" + parameters.getDestination() + "&"
                + "city=" + parameters.getCity() + "&"
                + "cityd="+ parameters.getCityd() + "&"
                + "strategy=" + parameters.getStrategy() + "&"
                + "sig=" + sig;

        String rest = HttpUtil.rest(map, requestUrl, HttpMethod.GET);

        return JsonUtil.json2TransitDirectionData(rest);
    }

    /**
     *  计算高德数字签名
     * @param paramMap
     * @param privateKey
     * @return
     */
    private String getGaodeSign(Map<String,Object> paramMap,String privateKey) {
        Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator();
        List<String> paramKeyList = new ArrayList<>();
        while(it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            paramKeyList.add(entry.getKey());
        }
        String[] array = new String[paramKeyList.size()];
        paramKeyList.toArray(array);
        Arrays.sort(array);
        StringBuffer sb = new StringBuffer();
        for (String anArray : array) {
            sb.append(anArray);
            sb.append("=");
            sb.append(paramMap.get(anArray));
            sb.append("&");
        }
        String param = sb.substring(0, sb.length() - 1);
        param = param + privateKey;
        String sign = getMD5(param);
        return sign;
    }

    /**
     *  生成MD5
     * @param message
     * @return
     */
    private String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     *  二进制转十六进制
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (byte aByte : bytes) {
            num = aByte;
            if (num < 0) {
                num += 256;
            }
            if (num < 16) {
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

}
