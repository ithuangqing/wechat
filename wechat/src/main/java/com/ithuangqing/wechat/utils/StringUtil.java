package com.ithuangqing.wechat.utils;

public class StringUtil {
    /**
     * JAVA判断字符串数组中是否包含某字符串元素
     *
     * @param substring 某字符串
     * @param source 源字符串数组
     * @return 包含则返回true，否则返回false
     */
    public static boolean isIn(String substring, String[] source) {
        if (source == null || source.length == 0) {
            return false;
        }
        for (int i = 0; i < source.length; i++) {
            String aSource = source[i];
            if (aSource.equals(substring)) {
                return true;
            }
        }
        return false;
    }
}
