package gohnstudio.common.pay.weixin.utils;

import java.util.Map;

/**
 * 参数校验器
 */
public class ParamChecker {

    /**
     * 工具对象
     */
    private static volatile ParamChecker checker;

    /**
     * 获取实例
     *
     * @return 参数校验实例对象
     */
    public static ParamChecker getInstance() {
        if (checker == null) {
            synchronized (ParamChecker.class) {
                if (checker == null) {
                    checker = new ParamChecker();
                }
            }
        }
        return checker;
    }

    /**
     * 校验支付配置
     *
     * @param payConfigMap 校验支付配置
     * @return true/false 支付校验是否成功
     */
    public boolean checkPayConfig(Map<String, String> payConfigMap) {
        return (payConfigMap == null || payConfigMap.size() == 0);
    }

    /**
     * 校验传入下单的参数
     *
     * @param orderId   订单号
     * @param openId    微信openId
     * @param appId     微信appId
     * @param mchId     商户号
     * @param body      商品描述
     * @param totalFee  总价
     * @param ip        下单ip
     * @param notifyUrl 通知地址
     * @return 下单参数校验结果
     */
    public Map<String, String> checkPlaceOrder(String orderId, String openId, String appId, String mchId, String body, double totalFee, String ip, String notifyUrl) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32)) return resultMap.place("message", "订单号错误");
        if (!StringUtils.isLength(openId, 32)) return resultMap.place("message", "openId错误");
        if (totalFee < 0.0F) return resultMap.place("message", "支付金额错误");
        if (!StringUtils.isLength(notifyUrl, 256)) return resultMap.place("message", "通知回调地址错误");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        if (!StringUtils.isLength(body, 128)) return resultMap.place("message", "商品描述错误");
        if (!StringUtils.isLength(ip, 16)) return resultMap.place("message", "终端IP错误");
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * 校验传入订单查询的参数
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param appId     微信账号
     * @param mchId     商户号
     * @return 订单查询参数校验结果
     */
    public Map<String, String> checkQueryOrder(String orderId, String wxOrderId, String appId, String mchId) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32) && !StringUtils.isLength(wxOrderId, 32))
            return resultMap.place("message", "订单号和微信订单号不能同时为空");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * 校验传入关闭订单的参数
     *
     * @param orderId 订单号
     * @param appId   公众号
     * @param mchId   商户号
     * @return 关闭订单参数校验结果
     */
    public Map<String, String> checkCloseOrder(String orderId, String appId, String mchId) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32))
            return resultMap.place("message", "订单号错误");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * 校验传入申请退款的参数
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param refundFee 退款金额
     * @param appId     公众号id
     * @param mchId     商户号
     * @return 申请退款参数校验结果
     */
    public Map<String, String> checkRefundOrder(String orderId, String wxOrderId, double refundFee, String appId, String mchId) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32) && !StringUtils.isLength(wxOrderId, 32))
            return resultMap.place("message", "订单号和微信订单号不能同时为空");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        if (refundFee < 0.0D) return resultMap.place("message", "退款金额错误");
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * 校验传入子商户号下单的参数
     *
     * @param orderId   订单号
     * @param openId    微信openId
     * @param appId     微信appId
     * @param mchId     商户号
     * @param body      商品描述
     * @param totalFee  总价
     * @param ip        下单ip
     * @param notifyUrl 通知地址
     * @param subOpenId 用户子标识
     * @param subAppId  子商户公众账号ID
     * @param subMchId  子商户号
     * @return 下单参数校验结果
     */
    public Map<String, String> checkPlaceOrderBySub(String orderId, String openId, String appId, String mchId, String subOpenId, String subAppId, String subMchId, String body, double totalFee, String ip, String notifyUrl) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32)) return resultMap.place("message", "订单号错误");
        if (!StringUtils.isLength(openId, 32)) return resultMap.place("message", "openId错误");
        if (totalFee < 0.0F) return resultMap.place("message", "支付金额错误");
        if (!StringUtils.isLength(notifyUrl, 256)) return resultMap.place("message", "通知回调地址错误");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        if (!StringUtils.isLength(subMchId, 32)) return resultMap.place("message", "子商户号错误");
        if (!StringUtils.isLength(body, 128)) return resultMap.place("message", "商品描述错误");
        if (!StringUtils.isLength(ip, 16)) return resultMap.place("message", "终端IP错误");
        if (!StringUtils.isNull(subOpenId)) {
            if (StringUtils.isNull(subAppId)) return resultMap.place("message", "选择传sub_openid,则必须传sub_appid");
        }
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * @param orderId   我们系统订单号
     * @param wxOrderId 微信订单号
     * @param appId     主商户公众号id
     * @param mchId     主商户号id
     * @param subAppId  子商户公众号id
     * @param subMchId  子商户号id
     * @return 订单查询参数校验结果
     */
    public Map<String, String> checkQueryOrderBySub(String orderId, String wxOrderId, String appId, String mchId, String subAppId, String subMchId) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32) && !StringUtils.isLength(wxOrderId, 32))
            return resultMap.place("message", "订单号和微信订单号不能同时为空");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        if (!StringUtils.isLength(subAppId, 32)) return resultMap.place("message", "子商户公众号ID错误");
        if (!StringUtils.isLength(subMchId, 32)) return resultMap.place("message", "子商户号ID错误");
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * @param orderId  我们系统订单号
     * @param appId    主商户公众号id
     * @param mchId    主商户号id
     * @param subAppId 子商户公众号id
     * @param subMchId 子商户号id
     * @return 关闭订单参数校验结果
     */
    public Map<String, String> checkCloseOrderBySub(String orderId, String appId, String mchId, String subAppId, String subMchId) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32))
            return resultMap.place("message", "订单号错误");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        if (!StringUtils.isLength(subAppId, 32)) return resultMap.place("message", "子商户公众号ID错误");
        if (!StringUtils.isLength(subMchId, 32)) return resultMap.place("message", "子商户号ID错误");
        resultMap.put("success", "success");
        return resultMap;
    }

    /**
     * 校验传入申请退款的参数
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param refundFee 退款金额
     * @param appId     公众号id
     * @param mchId     商户号
     * @param subAppId  子商户公众号id
     * @param subMchId  子商户号id
     * @return 申请退款参数校验结果
     */
    public Map<String, String> checkRefundOrderBySub(String orderId, String wxOrderId, double refundFee, String appId, String mchId, String subAppId, String subMchId) {
        ContinuousHashMap<String, String> resultMap = new ContinuousHashMap<>();
        resultMap.put("success", "false");
        if (!StringUtils.isLength(orderId, 32) && !StringUtils.isLength(wxOrderId, 32))
            return resultMap.place("message", "订单号和微信订单号不能同时为空");
        if (!StringUtils.isLength(appId, 32)) return resultMap.place("message", "公众号ID错误");
        if (!StringUtils.isLength(mchId, 32)) return resultMap.place("message", "商户号ID错误");
        if (!StringUtils.isLength(subAppId, 32)) return resultMap.place("message", "子商户公众号ID错误");
        if (!StringUtils.isLength(subMchId, 32)) return resultMap.place("message", "子商户号ID错误");
        if (refundFee < 0.0D) return resultMap.place("message", "退款金额错误");
        resultMap.put("success", "success");
        return resultMap;
    }
}
