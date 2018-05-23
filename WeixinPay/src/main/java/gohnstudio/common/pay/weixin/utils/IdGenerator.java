package gohnstudio.common.pay.weixin.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 分布式id生成器
 *
 * @author lzh
 */
public class IdGenerator {

    /**
     * 本机Mac地址
     */
    private static String MAC = null;
    /**
     * 未知主机
     */
    private final static String UNKNOWN_HOST = "IUnknownHost";
    /**
     * 当前的时间戳
     */
    private static long TIMESTAMP = 0;
    /**
     * 同一秒随机数set
     */
    private static Set<Integer> RANDOM_NUMBER_SET;
    /**
     * 随机数的最大范围
     */
    private static final int MAX_INT_VALUE = 10000000;
    /**
     * 如果随机数重复时,加的步长
     */
    private static final int STEP = 1;
    /**
     * 生成的id的长度
     */
    private final static int LENGTH = 32;
    /**
     * 随机数生成
     */
    private static Random RANDOM;
    /**
     * 实例对象
     */
    private static IdGenerator generator;

    /**
     * 获取实例
     *
     * @return id生成器实例
     */
    public static IdGenerator getInstance() {
        if (generator == null) {
            synchronized (IdGenerator.class) {
                if (generator == null) {
                    try {
                        generator = new IdGenerator();
                    } catch (Exception e) {
                        generator = null;
                    }
                }
            }
        }
        return generator;
    }

    /**
     * 构造方法
     */
    private IdGenerator() {
        RANDOM = new Random();
        RANDOM_NUMBER_SET = new HashSet<>();
        MAC = getLocalMac();
    }

    /**
     * 生成一个唯一id
     */
    public String getId() {
        if (MAC == null || UNKNOWN_HOST.equals(MAC)) {  // 如果mac地址为空或者是未知
            MAC = getLocalMac();
        }
        int random = RANDOM.nextInt(MAX_INT_VALUE);// 生成一个随机数
        while (true) {
            if (TIMESTAMP != System.currentTimeMillis()) { // 如果当前时间不能于上一个随机数产生的时间,那么将set清空,时间变为当前时间
                TIMESTAMP = System.currentTimeMillis();
                RANDOM_NUMBER_SET.clear();
            }
            boolean success = RANDOM_NUMBER_SET.add(random);
            if (!success) { // 如果往set中添加时失败,说明重复
                if (random + STEP < MAX_INT_VALUE) random += STEP;  // 如果当前随机数加步长仍旧小于最大值的范围,随机数加步长
                else random = RANDOM.nextInt(MAX_INT_VALUE);        // 如果大于最大值,重新生成随机数
            } else break;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(MAC).append(TIMESTAMP);
        int randomLen = String.valueOf(random).length();
        // 随机数位数不足时补0
        while (sb.length() + randomLen < LENGTH) {
            sb.append(0);
        }
        return sb.append(random).toString();
    }

    /**
     * 获取本地mac地址
     *
     * @return mac地址
     */
    private static String getLocalMac() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (byte b : mac) {
                // 字节转换为整数
                int temp = b & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1)
                    sb.append("0").append(str);
                else
                    sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            return UNKNOWN_HOST;
        }
    }
}
