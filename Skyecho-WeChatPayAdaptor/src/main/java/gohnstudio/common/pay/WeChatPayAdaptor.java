package gohnstudio.common.pay;

import java.util.Map;

/**
 * Created by lieber on 2017/6/12.
 */
public interface WeChatPayAdaptor {

    /**
     * 微信支付下单接口
     *
     * @param orderId      我们系统的订单号
     * @param totalFee     总费用
     * @param openId       用户的openId
     * @param ip           下单ip
     * @param goodsSubject 商品名称
     * @param goodsBody    商品详情
     * @param desc         备注
     * @return
     */
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsSubject, String goodsBody, String desc);

    /**
     * 微信支付下单接口
     *
     * @param orderId      我们系统的订单号
     * @param totalFee     总费用
     * @param openId       用户的openId
     * @param ip           下单ip
     * @param goodsSubject 商品名称
     * @param goodsBody    商品详情
     * @param desc         备注
     * @param payConfigMap 微信支付配置
     * @return
     */
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsSubject, String goodsBody, String desc, Map<String, Map<String, String>> payConfigMap);


}
