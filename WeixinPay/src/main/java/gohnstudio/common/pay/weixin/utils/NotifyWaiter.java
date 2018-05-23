package gohnstudio.common.pay.weixin.utils;

import com.alibaba.fastjson.JSONObject;
import gohnstudio.common.log.LogManager;
import gohnstudio.common.log.enums.NoticeType;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * 通知处理器
 */
public class NotifyWaiter {

    /**
     * 工具对象
     */
    private static volatile NotifyWaiter notifyWaiter;

    /**
     * 日志记录工具
     */
    private final static LogManager log = LogManager.getInstance();

    /**
     * 获取实例
     *
     * @return 结果处理器实例对象
     */
    public static NotifyWaiter getInstance() {
        if (notifyWaiter == null) {
            synchronized (ResultWaiter.class) {
                if (notifyWaiter == null) {
                    notifyWaiter = new NotifyWaiter();
                }
            }
        }
        return notifyWaiter;
    }

    /**
     * 获取系统订单号
     *
     * @param request 请求
     * @return 系统订单号
     */
    public String getPayOrderId(HttpServletRequest request, String key) {
        JSONObject jo = JSONObject.parseObject(getRequestData(request, key));
        if (jo == null) return null;
        return jo.getString("out_trade_no");
    }

    /**
     * 获取微信订单号
     *
     * @param request 请求
     * @return 微信订单号
     */
    public String getPayWxOrderId(HttpServletRequest request, String key) {
        JSONObject jo = JSONObject.parseObject(getRequestData(request, key));
        if (jo == null) return null;
        return jo.getString("transaction_id");
    }

    /**
     * 处理支付通知
     *
     * @param request   请求
     * @param weiXinKey 微信配置签名字符串
     * @return 通知内容
     */
    public String dealPayNotify(HttpServletRequest request, String key, String source, String weiXinKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("source", source);
        try {
            TreeMap<String, String> requestMap = new TreeMap<>();
            JSONObject jo = JSONObject.parseObject(getRequestData(request, key));
            requestMap.putAll(jo.toJavaObject(Map.class));
            log.info("本次支付通知参数为：" + requestMap.toString(), NoticeType.NO.getValue(), false);
            String localSign = Signer.signMap2String(requestMap, weiXinKey);
            if (localSign.equalsIgnoreCase(requestMap.get("sign"))) {
                jsonObject.put("success", true);
                jsonObject.put("message", "签名验证成功");
                jsonObject.put("data", JSONObject.toJSONString(requestMap));
            } else {
                log.error("本次支付通知本地签名为<" + localSign + ">,接收到的签名为<" + requestMap.get("sign") + ">,签名匹配错误", NoticeType.NO.getValue(), false);
                jsonObject.put("success", false);
                jsonObject.put("message", "签名匹配错误");
            }
        } catch (Exception e) {
            log.error(e, NoticeType.NO.getValue(), false);
            jsonObject.put("success", false);
            jsonObject.put("message", "发生异常:" + e.getMessage());
        }
        return jsonObject.toJSONString();
    }

    /**
     * 解析退款通知
     * <p>
     * 解密方式：
     * （1）对加密串A做base64解码，得到加密串B
     * （2）对商户key做md5，得到32位小写key* ( key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
     * （3）用key*对加密串B做AES-256-ECB解密（PKCS7Padding）
     * </p>
     *
     * @param request   请求
     * @param weiXinKey 微信配置签名字符串
     * @return 微信请求参数
     */
    public String dealRefundNotify(HttpServletRequest request, String reqKey, String source, String weiXinKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("source", source);
        try {
            JSONObject jo = JSONObject.parseObject(getRequestData(request, reqKey));
            log.info("本次退款通知参数为：" + jo.toString(), NoticeType.NO.getValue(), false);
            String reqInfo = jo.getString("req_info");
            if (reqInfo == null) {
                jsonObject.put("success", false);
                jsonObject.put("message", "请求信息错误");
            } else {
                // （1）对加密串A做base64解码，得到加密串B
                reqInfo = new String(new BASE64Decoder().decodeBuffer(reqInfo));
                // （2）对商户key做md5，得到32位小写key*
                String key = MD5Util.MD5Encode(weiXinKey, "UTF-8").toLowerCase();
                // （3）用key*对加密串B做AES-256-ECB解密（PKCS7Padding）
                reqInfo = AES256EncryptionUtil.decrypt(reqInfo, key);
                jsonObject.put("success", true);
                jsonObject.put("message", "解析成功");
                jsonObject.put("data", XmlUtils.xmlChangeJson(reqInfo));
            }
        } catch (Exception e) {
            log.error(e, NoticeType.NO.getValue(), false);
            jsonObject.put("success", false);
            jsonObject.put("message", "发生异常:" + e.getMessage());
        }
        return jsonObject.toJSONString();
    }

    /**
     * 通知成功响应
     *
     * @return 响应内容
     */
    public String responseSuccessNotify() {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /**
     * 通知成功响应
     *
     * @param response 响应
     */
    public void responseSuccessNotify(HttpServletResponse response) {
        response(response, responseSuccessNotify());
    }

    /**
     * 通知失败响应
     *
     * @param errMsg 错误原因
     * @return 响应内容
     */
    public String responseFailNotify(String errMsg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml><return_code><![CDATA[FAIL]]></return_code>");
        if (errMsg != null && !errMsg.trim().equals("")) {
            sb.append("<return_msg><![CDATA[").append(errMsg).append("]]></return_msg>");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 通知失败响应
     *
     * @param errMsg   错误原因
     * @param response 响应
     */
    public void responseFailNotify(HttpServletResponse response, String errMsg) {
        response(response, responseFailNotify(errMsg));
    }

    /**
     * 响应
     *
     * @param response 响应对象
     * @param errMsg   响应消息
     */
    private void response(HttpServletResponse response, String errMsg) {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/xml; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            writer.print(errMsg);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * 从请求中获取请求信息
     *
     * @param request 请求对象
     * @return 请求信息
     */
    public String getRequestData(HttpServletRequest request, String key) {
        StringBuilder reqStr = new StringBuilder();
        try {
            String xml;
            if (request.getAttribute(key) == null) {
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    reqStr.append(line);
                }
                log.info("微信通知参数为：" + reqStr.toString(), NoticeType.NO.getValue(), false);
                xml = XmlUtils.xmlChangeJsonStr(reqStr.toString());
                request.setAttribute(key, xml);
            } else {
                xml = (String) request.getAttribute(key);
            }
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e, NoticeType.NO.getValue(), false);
        }
        return "{}";
    }

}
