package gohnstudio.common.pay.weixin;

import com.alibaba.fastjson.JSONObject;
import gohnstudio.common.pay.WeChatPayAdaptor;

import java.util.Map;

/**
 * Created by lieber on 2017/6/26.
 * <p/>
 * 祥付宝微信支付
 */
public class WeiXinPayAdaptor implements WeChatPayAdaptor {
    @Override
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsBody, String goodsDetail, String desc) {
//        WeixinpayUtils xiangfupayUtils = WeixinpayUtils.getInstance();
//        return xiangfupayUtils.placeOrderByScan(orderId, totalFee, openId, goodsDetail, goodsBody);
        return null;
    }

    @Override
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsBody, String goodsDetail, String desc, Map<String, Map<String, String>> payConfigMap) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", "WECHATPAY");
        resultObject.put("success", false);
        resultObject.put("message", "微信支付配置信息错误");
        if (payConfigMap == null || payConfigMap.size() == 0) {
            return resultObject.toJSONString();
        }
        Map<String, String> weiXinPay = payConfigMap.get("weChatPay");
        if (weiXinPay == null || weiXinPay.size() == 0) {
            return resultObject.toJSONString();
        }
        if (!weiXinPay.containsKey("appId") || !weiXinPay.containsKey("mchId") || !weiXinPay.containsKey("notifyUrl") || !weiXinPay.containsKey("payKey")) {
            return resultObject.toJSONString();
        }
        WeiXinPayUtils weixinpayUtils = WeiXinPayUtils.getInstance();
        return weixinpayUtils.placeOrder(orderId, totalFee, openId, ip, goodsBody, weiXinPay);
    }
}
