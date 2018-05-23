package gohnstudio.common.pay.weixin.utils;

import java.util.Stack;

public class TenTo62 {

    /**
     * 字符数组,此处可将字符顺序打乱,减少被猜出后解密机率
     */
    private static char[] charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static void main(String[] args) {
        long number = System.currentTimeMillis();
        String number64 = _10_to_62(number);
        System.out.println("转换前 " + number);
        System.out.println("转换后 " + number64);
    }

    /**
     * 10进制转62进制
     *
     * @param number 10进制数字
     * @return 62进制字符串
     */
    public static String _10_to_62(long number) {
        Long rest = number;
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(charSet[new Long((rest - (rest / 62) * 62)).intValue()]);
            rest = rest / 62;
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        return result.toString();

    }

    /**
     * 62进制转10进制
     *
     * @param str 62进制字符串
     * @return 10进制数字
     */
    public static long _62_to_10(String str) {
        int multiple = 1;
        long result = 0;
        Character c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(str.length() - i - 1);
            result += _62_value(c) * multiple;
            multiple = multiple * 62;
        }
        return result;
    }

    private static int _62_value(Character c) {
        for (int i = 0; i < charSet.length; i++) {
            if (c == charSet[i]) {
                return i;
            }
        }
        return -1;
    }


}
