package gohnstudio.common.pay.weixin.utils;

import java.util.Random;

public class StringUtils extends org.apache.commons.lang.StringUtils {

    private final static String str = "befOPQRSTcdzABrsWXYaZ012tuvwV34xyGHIghiEFU567jklmnopqJKLMNCD89";

    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断的字符串
     * @return
     */
    public static boolean isNull(String str) {
        if (str == null)
            return true;
        else if (str.length() < 1)
            return true;
        return false;
    }

    public static boolean isLength(String str, int length) {
        if (str == null) return false;
        return str.length() <= length;
    }


    public static String getRandomString(int length) {
        Random random = new Random();//随机类初始化
        StringBuilder sb = new StringBuilder();//StringBuffer类生成，为了拼接字符串
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);// [0,62)
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
