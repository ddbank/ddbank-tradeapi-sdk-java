package com.ddbank.tradeapi.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by damon3588#163.com on 2019/09/01.
 * map排序工具类
 */
public class MapSort {

    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }
}

class MapKeyComparator implements Comparator<String> {

    public int compare(String str1, String str2) {
        return str1.compareTo(str2);
    }
}
