package gohnstudio.common.pay.wechat.test;

import gohnstudio.common.pay.wechat.WeChatPayUtils;
import gohnstudio.common.pay.wechat.utils.PayWay;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lieber on 2017/6/16.
 */
public class Test {

    @org.junit.Test
    public void get1() {
        String orderId = String.valueOf(System.currentTimeMillis());
        String openId = "ooG3KwZrAeheSplp0HNbDexExBhE";
        float amount = 1.11f;
        String notifyUrl = "http://weixin.bflvx.com";
        String goosName = "百富旅行机票";
        String goodsDetail = "" + String.valueOf(System.currentTimeMillis());
        String desc = "";
        String ip = "127.0.0.1";
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<String, String> heliPayMap = new HashMap<>();
        heliPayMap.put("customerNumber", "C1800001029");
        heliPayMap.put("signKey", "gqhqsj1aRcWatuMrRkPKU7Gpot9NPorB");
        heliPayMap.put("appId", "wxca7065e805b3704c");
        heliPayMap.put("notifyUrl", "127.0.0.1");
        map.put("helipay", heliPayMap);

        Map<String, String> xiangfupaymap = new HashMap<>();
        xiangfupaymap.put("merId", "000201706220257411");
        xiangfupaymap.put("payKey", "c882b099da33f2bb85f4becd3d83eec4");
        xiangfupaymap.put("aesKey", "1102130405061708");
        xiangfupaymap.put("notifyUrl", "127.0.0.1");
        map.put("xiangfuPay", xiangfupaymap);


        WeChatPayUtils payUtils = WeChatPayUtils.getInstance();
        String str = payUtils.placeOrder(orderId, amount, openId, ip, goosName, goodsDetail, desc, map);
        System.out.println(str);
    }

    @org.junit.Test
    public void testPayWay() {
        System.out.println(PayWay.WECHATPAY.value());
    }
}
