package gohnstudio.common.pay.weixin.utils;

import gohnstudio.common.log.LogManager;
import gohnstudio.common.log.enums.NoticeType;

import java.util.Map;
import java.util.Set;

/**
 * 请求服务类
 */
public class HttpHelper {

    /**
     * 日志记录工具
     */
    private final static LogManager log = LogManager.getInstance();

    /**
     * 请求服务器
     *
     * @param reqParams 请求数据
     * @param url       请求地址
     * @return 请求结果
     */
    public static String doXmlPost(Map<String, String> reqParams, String url) {
        String result = StringUtils.EMPTY;
        try {
            String reqString = map2String(reqParams);
            result = HttpClientUtil.doPostString(url, reqString, null);
        } catch (Exception e) {
            log.error("发送请求出现异常" + e.getMessage(), NoticeType.NO.getValue(), false);
            log.error(e, NoticeType.NO.getValue(), false);
        }
        return result;
    }

    /**
     * 请求服务器
     *
     * @param reqParams 请求数据
     * @param url       请求地址
     * @return 请求结果
     */
    public static String doSSLXmlPost(Map<String, String> reqParams, String url, String path, String password) {
        String result = StringUtils.EMPTY;
        try {
            String reqString = map2String(reqParams);
            result = HttpClientUtil.doSSLPostString(url, reqString, null, path, password);
        } catch (Exception e) {
            log.error("发送请求出现异常" + e.getMessage(), NoticeType.NO.getValue(), false);
            log.error(e, NoticeType.NO.getValue(), false);
        }
        return result;
    }

    /**
     * map转字符串，用于签名
     *
     * @param reqParams 请求数据
     * @return xml格式字符串
     */
    private static String map2String(Map<String, String> reqParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        if (reqParams != null) {
            Set<Map.Entry<String, String>> entrySet = reqParams.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                sb.append("<").append(entry.getKey()).append(">")
                        .append(entry.getValue())
                        .append("</").append(entry.getKey()).append(">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
        /*String s = "<xml>";
        if (reqParams != null) {
            Set<Map.Entry<String, String>> entrySet = reqParams.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                s += "<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">";
            }
        }
        return s + "</xml>";*/
    }
}
