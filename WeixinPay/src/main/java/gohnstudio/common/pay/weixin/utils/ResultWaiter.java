package gohnstudio.common.pay.weixin.utils;

import com.alibaba.fastjson.JSONObject;
import gohnstudio.common.log.LogManager;
import gohnstudio.common.log.enums.NoticeType;

import java.util.Map;
import java.util.TreeMap;

/**
 * 微信返回结果处理器
 */
public class ResultWaiter {

    /**
     * 工具对象
     */
    private static volatile ResultWaiter resultWaiter;

    /**
     * 日志记录工具
     */
    private final static LogManager log = LogManager.getInstance();

    /**
     * 来源
     */
    public String SOURCE = "WECHATPAY";
    /**
     * 子商户来源
     */
    public String SUB_SOURCE = "WECHATSUBPAY";

    /**
     * 获取实例
     *
     * @return 结果处理器实例对象
     */
    public static ResultWaiter getInstance() {
        if (resultWaiter == null) {
            synchronized (ResultWaiter.class) {
                if (resultWaiter == null) {
                    resultWaiter = new ResultWaiter();
                }
            }
        }
        return resultWaiter;
    }

    public String payConfigError() {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", SOURCE);
        resultObject.put("success", false);
        resultObject.put("message", "微信支付配置错误");
        return resultObject.toJSONString();
    }

    /**
     * 处理下单结果
     *
     * @param retString 下单结果字符串
     * @param orderId   传入订单号
     * @param weiXinKey 微信支付签名字符串
     * @return 下单结果处理结果
     */
    public String placeOrder(String retString, String orderId, String weiXinKey) {
        return this.placeOrder(retString, orderId, weiXinKey, SOURCE);
    }

    /**
     * 处理子商户下单结果
     *
     * @param retString 下单结果字符串
     * @param orderId   传入订单号
     * @param weiXinKey 微信支付签名字符串
     * @return 下单结果处理结果
     */
    public String placeOrderBySub(String retString, String orderId, String weiXinKey) {
        return this.placeOrder(retString, orderId, weiXinKey, SUB_SOURCE);
    }

    /**
     * 处理下单结果
     *
     * @param retString 下单结果字符串
     * @param orderId   传入订单号
     * @param weiXinKey 微信支付签名字符串
     * @param source    订单下单来源
     * @return 下单结果处理结果
     */
    private String placeOrder(String retString, String orderId, String weiXinKey, String source) {
        JSONObject jsonObject = XmlUtils.xmlChangeJson(retString);
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", source);
        if ("SUCCESS".equals(jsonObject.getString("return_code")) && "SUCCESS".equals(jsonObject.getString("result_code"))) {
            if (verifySign(jsonObject, weiXinKey)) {
                resultObject.put("success", true);
                resultObject.put("message", orderId);
                TreeMap<String, String> payParams = new TreeMap<>();
                payParams.put("appId", jsonObject.getString("appid"));
                String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                payParams.put("timeStamp", timeStamp);
                payParams.put("nonceStr", StringUtils.getRandomString(16));
                payParams.put("package", "prepay_id=" + jsonObject.getString("prepay_id"));
                payParams.put("signType", "MD5");
                payParams.put("paySign", Signer.signMap2String(payParams, weiXinKey));
                resultObject.put("payInfo", JSONObject.toJSONString(payParams));
            } else {
                resultObject.put("success", false);
                resultObject.put("message", "解析下单结果签名错误");
            }
        } else {
            resultObject.put("success", false);
            resultObject.put("message", jsonObject.getString("err_code_des"));
        }
        return resultObject.toJSONString();
    }

    /**
     * 处理查询订单结果
     *
     * @param retString 查询结果字符串
     * @param weiXinKey 微信支付签名字符串
     * @return 查询订单结果处理
     */
    public String queryOrder(String retString, String weiXinKey) {
        return queryOrder(retString, weiXinKey, SOURCE);
    }

    /**
     * 处理子商户查询订单结果
     *
     * @param retString 查询结果字符串
     * @param weiXinKey 微信支付签名字符串
     * @return 查询订单结果处理
     */
    public String queryOrderBySub(String retString, String weiXinKey) {
        return queryOrder(retString, weiXinKey, SUB_SOURCE);
    }

    /**
     * 处理查询订单结果
     *
     * @param retString 查询结果字符串
     * @param weiXinKey 微信支付签名字符串
     * @param source    订单来源
     * @return 查询订单结果处理
     */
    private String queryOrder(String retString, String weiXinKey, String source) {
        JSONObject jsonObject = XmlUtils.xmlChangeJson(retString);
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", source);
        if ("SUCCESS".equals(jsonObject.getString("return_code")) && "SUCCESS".equals(jsonObject.getString("result_code"))) {
            if (verifySign(jsonObject, weiXinKey)) {
                resultObject.put("success", true);
                resultObject.put("message", "查询订单成功");
                resultObject.put("data", jsonObject.toJSONString());
            } else {
                resultObject.put("success", false);
                resultObject.put("message", "解析查询订单结果签名失败");
            }
        } else {
            resultObject.put("success", false);
            resultObject.put("message", jsonObject.getString("err_code_des"));
        }
        return resultObject.toJSONString();
    }

    /**
     * 处理关闭订单结果
     *
     * @param retString 结果字符串
     * @param weiXinKey 微信支付签名字符串
     * @return 关闭订单结果处理
     */
    public String closeOrder(String retString, String weiXinKey) {
        return this.closeOrder(retString, weiXinKey, SOURCE);
    }

    /**
     * 处理子商戶关闭订单结果
     *
     * @param retString 结果字符串
     * @param weiXinKey 微信支付签名字符串
     * @return 关闭订单结果处理
     */
    public String closeOrderBySub(String retString, String weiXinKey) {
        return this.closeOrder(retString, weiXinKey, SUB_SOURCE);
    }

    /**
     * 处理关闭订单结果
     *
     * @param retString 结果字符串
     * @param weiXinKey 微信支付签名字符串
     * @param source    订单来源
     * @return 关闭订单结果处理
     */
    private String closeOrder(String retString, String weiXinKey, String source) {
        JSONObject jsonObject = XmlUtils.xmlChangeJson(retString);
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", source);
        if ("SUCCESS".equals(jsonObject.getString("return_code")) && "SUCCESS".equals(jsonObject.getString("result_code"))) {
            if (verifySign(jsonObject, weiXinKey)) {
                resultObject.put("success", true);
                resultObject.put("message", "关闭订单成功");
            } else {
                resultObject.put("success", false);
                resultObject.put("message", "解析关闭订单结果签名失败");
            }
        } else {
            resultObject.put("success", false);
            resultObject.put("message", jsonObject.getString("err_code_des"));
        }
        return resultObject.toJSONString();
    }

    /**
     * 处理申请退款结果
     *
     * @param retString     结果字符串
     * @param refundOrderId 退款订单号
     * @param weiXinKey     微信支付签名字符串
     * @return 申请退款结果处理
     */
    public String refundOrder(String retString, String refundOrderId, String weiXinKey) {
        return this.refundOrder(retString, refundOrderId, weiXinKey, SOURCE);
    }

    /**
     * 处理子商户申请退款结果
     *
     * @param retString     结果字符串
     * @param refundOrderId 退款订单号
     * @param weiXinKey     微信支付签名字符串
     * @return 申请退款结果处理
     */
    public String refundOrderBySub(String retString, String refundOrderId, String weiXinKey) {
        return this.refundOrder(retString, refundOrderId, weiXinKey, SUB_SOURCE);
    }

    /**
     * 处理子商户申请退款结果
     *
     * @param retString     结果字符串
     * @param refundOrderId 退款订单号
     * @param weiXinKey     微信支付签名字符串
     * @param source        来源
     * @return 申请退款结果处理
     */
    private String refundOrder(String retString, String refundOrderId, String weiXinKey, String source) {
        JSONObject jsonObject = XmlUtils.xmlChangeJson(retString);
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", source);
        if ("SUCCESS".equals(jsonObject.getString("return_code")) && "SUCCESS".equals(jsonObject.getString("result_code"))) {
            if (verifySign(jsonObject, weiXinKey)) {
                resultObject.put("success", true);
                resultObject.put("message", refundOrderId);
            } else {
                resultObject.put("success", true);
                resultObject.put("message", "解析申请退款结果签名失败");
            }
        } else {
            resultObject.put("success", false);
            resultObject.put("message", jsonObject.getString("err_code_des"));
        }
        return resultObject.toJSONString();
    }

    /**
     * 校验签名
     *
     * @param jsonObject 参数
     * @param weiXinKey  微信配置签名字符串
     * @return 校验是否成功
     */
    private boolean verifySign(JSONObject jsonObject, String weiXinKey) {
        TreeMap<String, String> map = new TreeMap<>();
        map.putAll(jsonObject.toJavaObject(Map.class));
        String localSign = Signer.signMap2String(map, weiXinKey);
        boolean result = localSign.equalsIgnoreCase(map.get("sign"));
        if (!result) {
            log.info("本地签名<" + localSign + ">, 签名密钥<" + weiXinKey + ">", NoticeType.NO.getValue(), false);
            log.info("返回结果为" + map.toString(), NoticeType.NO.getValue(), false);
        }
        return result;
    }
}
