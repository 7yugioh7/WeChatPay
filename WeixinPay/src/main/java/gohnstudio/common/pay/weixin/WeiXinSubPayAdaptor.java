package gohnstudio.common.pay.weixin;

import com.alibaba.fastjson.JSONObject;
import gohnstudio.common.pay.WeChatPayAdaptor;

import java.util.Map;

/**
 *
 */
public class WeiXinSubPayAdaptor implements WeChatPayAdaptor {

    @Override
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsBody, String goodsDetail, String desc) {
        return null;
    }

    @Override
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsBody, String goodsDetail, String desc, Map<String, Map<String, String>> payConfigMap) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", "WECHATSUBPAY");
        resultObject.put("success", false);
        resultObject.put("message", "微信支付配置信息错误");
        if (payConfigMap == null || payConfigMap.size() == 0) {
            return resultObject.toJSONString();
        }
        Map<String, String> weiXinSubPay = payConfigMap.get("weChatSubPay");
        if (weiXinSubPay == null || weiXinSubPay.size() == 0) {
            return resultObject.toJSONString();
        }
        if (!weiXinSubPay.containsKey("appId") || !weiXinSubPay.containsKey("mchId") || !weiXinSubPay.containsKey("subAppId") || !weiXinSubPay.containsKey("subMchId") || !weiXinSubPay.containsKey("notifyUrl") || !weiXinSubPay.containsKey("payKey")) {
            return resultObject.toJSONString();
        }
        WeiXinSubPayUtils weiXinSubPayUtils = WeiXinSubPayUtils.getInstance();
        return weiXinSubPayUtils.placeOrder(orderId, totalFee, openId, ip, goodsBody, weiXinSubPay);
    }
}
