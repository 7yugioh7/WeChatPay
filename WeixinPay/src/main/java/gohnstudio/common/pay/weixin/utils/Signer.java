package gohnstudio.common.pay.weixin.utils;

import java.util.TreeMap;

/**
 * 签名处理器
 */
public class Signer {

    /**
     * 签名
     *
     * @param reqParams 有序map
     * @param weixinKey 签名串
     * @return 签名字符串
     */
    public static String signMap2String(TreeMap<String, String> reqParams, String weixinKey) {
        StringBuilder sb = new StringBuilder();
        for (String key : reqParams.keySet()) {
            String value = reqParams.get(key);
            if (value != null && !"".equals(value) && !"sign".equals(key)) {
                sb.append(key).append("=").append(value).append("&");
            }
        }
        sb.append("key").append("=").append(weixinKey);
        return MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
    }

}
