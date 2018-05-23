package gohnstudio.common.pay.weixin.utils;

import org.apache.http.util.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-256加解密工具类
 */
public class AES256EncryptionUtil {

    private static final String ALGORITHM = "AES/ECB/PKCS7Padding";

    /**
     * 生成key
     *
     * @param password 密码
     * @return 密钥
     * @throws Exception 异常
     */
    private static byte[] getKeyByte(String password) throws Exception {
        byte[] seed = new byte[24];
        if (!TextUtils.isEmpty(password)) {
            seed = password.getBytes();
        }
        return seed;
    }

    /**
     * 加密
     *
     * @param data     代加密数据
     * @param password 密码
     * @return 加密后的数据
     */
    public static String encrypt(String data, String password) throws Exception {
        byte[] keyByte = getKeyByte(password);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES"); //生成加密解密需要的Key
        byte[] byteContent = data.getBytes("utf-8");
        Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] result = cipher.doFinal(byteContent);
        return parseByte2HexStr(result);  //转成String
    }

    /**
     * 解密
     *
     * @param data     待解密数据
     * @param password 密码
     * @return 解密后的数据
     */
    public static String decrypt(String data, String password) throws Exception {
        byte[] keyByte = getKeyByte(password);
        byte[] byteContent = parseHexStr2Byte(data);  //转成byte
        if (byteContent == null) return "";
        Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
        SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES"); //生成加密解密需要的Key
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = cipher.doFinal(byteContent);
        return new String(decoded);
    }

    /**
     * 转化为String
     *
     * @param buf 待转化byte数组
     * @return 转化后的字符串
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr 16进制字符串
     * @return 二进制字符串
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
