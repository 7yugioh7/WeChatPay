package gohnstudio.common.pay.wechat.utils;

/**
 * Created by admin on 2017/6/28.
 * <p/>
 * 支付方式
 */
public enum PayWay {
    /**
     * 祥付宝
     */
    XIANGFUPAY(1),
    /**
     * 合利宝
     */
    @Deprecated
    HELIPAY(0),
    /**
     * 多友财(云网支付)
     */
    YUNWANGPAY(4),
    /**
     * 直连微信
     */
    WECHATPAY(2),
    /**
     * 微信服务商模式支付,
     */
    WECHATSUBPAY(3);

    private int number;

    PayWay(int number) {
        this.number = number;
    }

    public int value() {
        return this.number;
    }

    public static PayWay getValue(String value) {
        PayWay[] payWays = PayWay.values();
        for (PayWay payWay : payWays) {
            if (payWay.name().equals(value)) {
                return payWay;
            }
        }
        return null;
    }
}
