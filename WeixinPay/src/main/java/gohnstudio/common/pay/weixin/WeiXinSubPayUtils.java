package gohnstudio.common.pay.weixin;

import com.alibaba.fastjson.JSONObject;
import gohnstudio.common.log.LogManager;
import gohnstudio.common.log.enums.NoticeType;
import gohnstudio.common.pay.weixin.utils.*;
import gohnstudio.common.pay.weixin.vo.WeiXinPayVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by lieber on 2017/6/20.
 * <p/>
 * 直连微信支付对接(子商户号)工具类
 */
public class WeiXinSubPayUtils implements WeiXinPayCommonUtils {

    /**
     * 微信支付相关配置
     */
    private static WeiXinPayVo weixinpayVo = null;
    /**
     * 工具实例
     */
    private static WeiXinSubPayUtils weiXinPaySubUtils;
    /**
     * 日志记录工具
     */
    private final static LogManager log = LogManager.getInstance();
    /**
     * 结果处理器
     */
    private final static ResultWaiter resultWaiter = ResultWaiter.getInstance();
    /**
     * 参数校验工具
     */
    private final static ParamChecker checker = ParamChecker.getInstance();
    /**
     * 编码格式
     */
    private final static String ENCODING = "UTF-8";
    /**
     * 微信通知信息存储
     */
    private final static String WEIXIN_NOTIFY = "wechat-sub-notify-info";

    /**
     * 获取实例
     *
     * @return 直连微信支付工具对象实例
     */
    public static WeiXinSubPayUtils getInstance() {
        if (weiXinPaySubUtils == null) {
            synchronized (WeiXinSubPayUtils.class) {
                if (weiXinPaySubUtils == null) {
                    try {
                        init();
                        weiXinPaySubUtils = new WeiXinSubPayUtils();
                    } catch (Exception e) {
                        log.error(e, NoticeType.NO.getValue(), false);
                        weiXinPaySubUtils = null;
                    }
                }
            }
        }
        return weiXinPaySubUtils;
    }

    /**
     * 初始化
     *
     * @throws IOException 读取配置文件IO异常
     */
    private static void init() throws IOException {
        InputStream in = WeiXinPayUtils.class.getResourceAsStream("/conf/weixinpay.properties");
        if (in == null) return;
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, ENCODING));
        Properties property = new Properties();
        property.load(bf);
        String createOrder = property.getProperty("createOrder");
        String queryOrder = property.getProperty("queryOrder");
        String closeOrder = property.getProperty("closeOrder");
        String refundOrder = property.getProperty("refundOrder");
        String refundOrderQuery = property.getProperty("refundOrderQuery");
        String downBill = property.getProperty("downBill");
        String certificatePath = property.getProperty("certificatePath");
        String certificateName = property.getProperty("certificateName");
        String subCertificatePath = property.getProperty("subCertificatePath");
        String subCertificateName = property.getProperty("subCertificateName");
        weixinpayVo = new WeiXinPayVo(createOrder, queryOrder, closeOrder, refundOrder, refundOrderQuery, downBill, certificatePath, certificateName, subCertificatePath, subCertificateName);
    }

    /**
     * 微信支付下单
     *
     * @param orderId   订单号
     * @param openId    下单openID
     * @param appId     公众号ID
     * @param mchId     商户号
     * @param body      商品描述
     * @param totalFee  总价
     * @param ip        下单ip
     * @param notifyUrl 通知地址
     * @param weiXinKey 微信配置签名字符串
     * @return 下单结果json格式字符串
     */
    public String placeOrder(String orderId, String openId, String appId, String mchId, String subOpenId, String subAppId, String subMchId, String body, double totalFee, String ip, String notifyUrl, String weiXinKey) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", resultWaiter.SUB_SOURCE);
        Map<String, String> validResult = checker.checkPlaceOrderBySub(orderId, openId, appId, mchId, subOpenId, subAppId, subMchId, body, totalFee, ip, notifyUrl);
        resultObject.put("success", false);
        if (validResult == null) {
            resultObject.put("message", "参数错误");
            return resultObject.toJSONString();
        }
        if ("false".equals(validResult.get("success"))) {
            resultObject.put("message", validResult.get("message"));
            return resultObject.toJSONString();
        }
        int fee = (int) (totalFee * 100);
        Map<String, String> reqParams = this.getCreateOrderReqMap(orderId, openId, appId, mchId, subOpenId, subAppId, subMchId, body, fee, ip, notifyUrl, weiXinKey);
        log.info("微信直连下单参数为  " + reqParams.toString(), NoticeType.NO.getValue(), false);
        String retString = HttpHelper.doXmlPost(reqParams, weixinpayVo.getCreateOrder());
        log.info("微信直连下单结果为  " + retString, NoticeType.NO.getValue(), false);
        return resultWaiter.placeOrderBySub(retString, orderId, weiXinKey);
    }

    /**
     * 微信支付下单
     *
     * @param orderId      订单号
     * @param totalFee     总价
     * @param openId       微信openid
     * @param ip           下单ip
     * @param goodsBody    商品描述
     * @param payConfigMap 支付配置
     * @return 下单结果json格式字符串
     */
    public String placeOrder(String orderId, double totalFee, String openId, String ip, String goodsBody, Map<String, String> payConfigMap) {
        if (checker.checkPayConfig(payConfigMap)) {
            return resultWaiter.payConfigError();
        }
        return this.placeOrder(orderId, "", payConfigMap.get("appId"), payConfigMap.get("mchId"), openId, payConfigMap.get("subAppId"), payConfigMap.get("subMchId"), goodsBody, totalFee, ip, payConfigMap.get("notifyUrl"), payConfigMap.get("payKey"));
    }

    /**
     * 拼接请求数据
     *
     * @param orderId   订单号
     * @param openId    主商户号openId
     * @param appId     主商户公众号Id
     * @param mchId     主商户商户号
     * @param subOpenId 子商户opeId
     * @param subAppId  子商户公众号id
     * @param subMchId  子商户商户号
     * @param body      商品描述
     * @param fee       总价
     * @param ip        下单ip
     * @param notifyUrl 通知地址
     * @param weiXinKey 服务号配置的支付地址
     * @return 请求微信下单map
     */
    private Map<String, String> getCreateOrderReqMap(String orderId, String openId, String appId, String mchId, String subOpenId, String subAppId, String subMchId, String body, int fee, String ip, String notifyUrl, String weiXinKey) {
        TreeMap<String, String> reqParams = new TreeMap<>();
        reqParams.put("appid", appId);
        reqParams.put("mch_id", mchId);
        reqParams.put("device_info", "WEB");
        reqParams.put("nonce_str", StringUtils.getRandomString(16));
        reqParams.put("sign_type", "MD5");
        reqParams.put("body", body);
        reqParams.put("out_trade_no", orderId);
        reqParams.put("total_fee", String.valueOf(fee));
        reqParams.put("spbill_create_ip", ip);
        reqParams.put("notify_url", notifyUrl);
        reqParams.put("trade_type", "JSAPI");
        reqParams.put("openid", openId);
        reqParams.put("sub_mch_id", subMchId);
        reqParams.put("sub_appid", subAppId);
        reqParams.put("sub_openid", subOpenId);
        reqParams.put("sign", Signer.signMap2String(reqParams, weiXinKey));
        log.info(reqParams.toString(), NoticeType.NO.getValue(), false);
        return reqParams;
    }

    /**
     * 订单查询
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param appId     公众号
     * @param mchId     商户号
     * @param weiXinKey 微信支付配置
     * @return 订单查询json格式字符串
     */
    public String queryOrder(String orderId, String wxOrderId, String appId, String mchId, String subAppId, String subMchId, String weiXinKey) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", resultWaiter.SUB_SOURCE);
        Map<String, String> validResult = checker.checkQueryOrderBySub(orderId, wxOrderId, appId, mchId, subAppId, subMchId);
        resultObject.put("success", false);
        if (validResult == null) {
            resultObject.put("message", "参数错误");
            return resultObject.toJSONString();
        }
        if ("false".equals(validResult.get("success"))) {
            resultObject.put("message", validResult.get("message"));
            return resultObject.toJSONString();
        }
        Map<String, String> reqParams = getQueryOrderReqMap(orderId, wxOrderId, appId, mchId, subAppId, subMchId, weiXinKey);
        log.info("微信直连请求订单参数为  " + reqParams.toString(), NoticeType.NO.getValue(), false);
        String retString = HttpHelper.doXmlPost(reqParams, weixinpayVo.getQueryOrder());
        log.info("微信直连请求订单结果为  " + retString, NoticeType.NO.getValue(), false);
        return resultWaiter.queryOrderBySub(retString, weiXinKey);
    }

    /**
     * 订单查询
     *
     * @param orderId      系统订单号
     * @param wxOrderId    微信订单号
     * @param payConfigMap 微信支付配置
     * @return 订单查询json格式字符串
     */
    public String queryOrder(String orderId, String wxOrderId, Map<String, String> payConfigMap) {
        if (checker.checkPayConfig(payConfigMap)) {
            return resultWaiter.payConfigError();
        }
        return this.queryOrder(orderId, wxOrderId, payConfigMap.get("appId"), payConfigMap.get("mchId"), payConfigMap.get("subAppId"), payConfigMap.get("subMchId"), payConfigMap.get("payKey"));
    }

    /**
     * 拼接微信订单查询请求数据
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param appId     公众号id
     * @param mchId     商户号id
     * @param weiXinKey 微信配置
     * @return 查询订单请求map
     */
    private Map<String, String> getQueryOrderReqMap(String orderId, String wxOrderId, String appId, String mchId, String subAppId, String subMchId, String weiXinKey) {
        TreeMap<String, String> reqParams = new TreeMap<>();
        reqParams.put("appid", appId);
        reqParams.put("mch_id", mchId);
        reqParams.put("sub_appid", subAppId);
        reqParams.put("sub_mch_id", subMchId);
        reqParams.put("transaction_id", wxOrderId);
        reqParams.put("out_trade_no", orderId);
        reqParams.put("nonce_str", StringUtils.getRandomString(16));
        reqParams.put("sign_type", "MD5");
        reqParams.put("sign", Signer.signMap2String(reqParams, weiXinKey));
        return reqParams;
    }

    /**
     * 关闭订单
     *
     * @param orderId   订单号
     * @param appId     主商户公众号id
     * @param mchId     主商户号id
     * @param subAppId  子商户公众号id
     * @param subMchId  子商户号id
     * @param weiXinKey 微信配置签名字符串
     * @return 关闭订单json格式字符串
     */
    public String closeOrder(String orderId, String appId, String mchId, String subAppId, String subMchId, String weiXinKey) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", resultWaiter.SUB_SOURCE);
        Map<String, String> validResult = checker.checkCloseOrderBySub(orderId, appId, mchId, subAppId, subMchId);
        resultObject.put("success", false);
        if (validResult == null) {
            resultObject.put("message", "参数错误");
            return resultObject.toJSONString();
        }
        if ("false".equals(validResult.get("success"))) {
            resultObject.put("message", validResult.get("message"));
            return resultObject.toJSONString();
        }
        Map<String, String> reqParams = this.getCloseOrderReqMap(orderId, appId, mchId, subAppId, subMchId, weiXinKey);
        log.info("微信直连关闭订单参数为  " + reqParams.toString(), NoticeType.NO.getValue(), false);
        String retString = HttpHelper.doXmlPost(reqParams, weixinpayVo.getCloseOrder());
        log.info("微信直连关闭订单结果为  " + retString, NoticeType.NO.getValue(), false);
        return resultWaiter.closeOrderBySub(retString, weiXinKey);
    }

    /**
     * 订单查询
     *
     * @param orderId      系统订单号
     * @param payConfigMap 微信支付配置
     * @return 订单查询json格式字符串
     */
    public String closeOrder(String orderId, Map<String, String> payConfigMap) {
        if (checker.checkPayConfig(payConfigMap)) {
            return resultWaiter.payConfigError();
        }
        return this.closeOrder(orderId, payConfigMap.get("appId"), payConfigMap.get("mchId"), payConfigMap.get("subAppId"), payConfigMap.get("subMchId"), payConfigMap.get("payKey"));
    }

    /**
     * 拼接微信订单查询请求数据
     *
     * @param orderId   系统订单号
     * @param appId     公众号id
     * @param mchId     商户号id
     * @param weixinKey 微信配置
     * @return 关闭订单请求map
     */
    private Map<String, String> getCloseOrderReqMap(String orderId, String appId, String mchId, String subAppId, String subMchId, String weixinKey) {
        TreeMap<String, String> reqParams = new TreeMap<>();
        reqParams.put("appid", appId);
        reqParams.put("mch_id", mchId);
        reqParams.put("sub_appid", subAppId);
        reqParams.put("sub_mch_id", subMchId);
        reqParams.put("out_trade_no", orderId);
        reqParams.put("nonce_str", StringUtils.getRandomString(16));
        reqParams.put("sign_type", "MD5");
        reqParams.put("sign", Signer.signMap2String(reqParams, weixinKey));
        return reqParams;
    }

    /**
     * 微信直连订单退款
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param appId     公众号ID
     * @param mchId     商户号
     * @param refundFee 退款金额
     * @param weiXinKey 微信配置签名字符串
     * @return 退款申请结果
     */
    public String refundOrder(String orderId, String wxOrderId, double refundFee, String appId, String mchId, String subAppId, String subMchId, String weiXinKey) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("source", resultWaiter.SUB_SOURCE);
        Map<String, String> validResult = checker.checkRefundOrderBySub(orderId, wxOrderId, refundFee, appId, mchId, subAppId, subMchId);
        resultObject.put("success", false);
        if (validResult == null) {
            resultObject.put("message", "参数错误");
            return resultObject.toJSONString();
        }
        if ("false".equals(validResult.get("success"))) {
            resultObject.put("message", validResult.get("message"));
            return resultObject.toJSONString();
        }
        int fee = (int) (refundFee * 100);
        int totalFee = getOrderTotalFee(orderId, wxOrderId, appId, mchId, subAppId, subMchId, weiXinKey);
        if (totalFee < 1 || fee > totalFee) {
            resultObject.put("message", "参数错误");
            return resultObject.toJSONString();
        }
        String refundOrderId = IdGenerator.getInstance().getId();
        Map<String, String> reqParams = getRefundOrderReqMap(orderId, wxOrderId, refundOrderId, fee, totalFee, appId, mchId, subAppId, subMchId, weiXinKey);
        log.info("微信直连申请退款参数为  " + reqParams.toString(), NoticeType.NO.getValue(), false);
        String certificatePath = weixinpayVo.getSubCertificatePath() + "/" + appId + "/" + weixinpayVo.getSubCertificateName();
        String retString = HttpHelper.doSSLXmlPost(reqParams, weixinpayVo.getRefundOrder(), certificatePath, mchId);
        log.info("微信直连申请退款结果为  " + retString, NoticeType.NO.getValue(), false);
        return resultWaiter.refundOrderBySub(retString, refundOrderId, weiXinKey);
    }

    /**
     * 订单退款
     *
     * @param orderId      系统订单号
     * @param payConfigMap 微信支付配置
     * @return 订单查询json格式字符串
     */
    public String refundOrder(String orderId, String wxOrderId, double refundFee, Map<String, String> payConfigMap) {
        if (checker.checkPayConfig(payConfigMap)) {
            return resultWaiter.payConfigError();
        }
        return this.refundOrder(orderId, wxOrderId, refundFee, payConfigMap.get("appId"), payConfigMap.get("mchId"), payConfigMap.get("subAppId"), payConfigMap.get("subMchId"), payConfigMap.get("payKey"));
    }

    /**
     * 构造退款申请请求参数
     *
     * @param orderId       订单号
     * @param wxOrderId     微信订单号
     * @param refundOrderId 退款单号
     * @param refundFee     退款金额
     * @param totalFee      订单总金额
     * @param appId         公众号id
     * @param mchId         商户号id
     * @param weiXinKey     微信配置签名字符串
     * @return 退款申请map
     */
    private Map<String, String> getRefundOrderReqMap(String orderId, String wxOrderId, String refundOrderId, int refundFee, int totalFee, String appId, String mchId, String subAppId, String subMchId, String weiXinKey) {
        TreeMap<String, String> reqParams = new TreeMap<>();
        reqParams.put("appid", appId);
        reqParams.put("mch_id", mchId);
        reqParams.put("sub_appid", subAppId);
        reqParams.put("sub_mch_id", subMchId);
        reqParams.put("out_trade_no", orderId);
        reqParams.put("transaction_id", wxOrderId);
        reqParams.put("out_refund_no", refundOrderId);
        reqParams.put("total_fee", String.valueOf(totalFee));
        reqParams.put("refund_fee", String.valueOf(refundFee));
        reqParams.put("nonce_str", StringUtils.getRandomString(16));
        reqParams.put("sign_type", "MD5");
        reqParams.put("sign", Signer.signMap2String(reqParams, weiXinKey));
        return reqParams;
    }

    /**
     * 获取订单总价
     *
     * @param orderId   系统订单号
     * @param wxOrderId 微信订单号
     * @param appId     公众号ID
     * @param mchId     商户号
     * @param weiXinKey 微信签名字符串
     * @return 订单总金额
     */
    private int getOrderTotalFee(String orderId, String wxOrderId, String appId, String mchId, String subAppId, String subMchId, String weiXinKey) {
        try {
            String result = this.queryOrder(orderId, wxOrderId, appId, mchId, subAppId, subMchId, weiXinKey);
            JSONObject resultObject = JSONObject.parseObject(result);
            if (resultObject.getBooleanValue("success")) {
                return resultObject.getJSONObject("data").getIntValue("total_fee");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取系统订单号
     *
     * @param request 请求
     * @return 系统订单号
     */
    @Override
    public String getPayOrderId(HttpServletRequest request) {
        return NotifyWaiter.getInstance().getPayOrderId(request, WEIXIN_NOTIFY);
    }

    /**
     * 获取微信订单号
     *
     * @param request 请求
     * @return 微信订单号
     */
    @Override
    public String getPayWxOrderId(HttpServletRequest request) {
        return NotifyWaiter.getInstance().getPayWxOrderId(request, WEIXIN_NOTIFY);
    }

    /**
     * 处理支付通知
     *
     * @param request   请求
     * @param weiXinKey 微信配置签名字符串
     * @return 通知内容
     */
    @Override
    public String dealPayNotify(HttpServletRequest request, String weiXinKey) {
        return NotifyWaiter.getInstance().dealPayNotify(request, WEIXIN_NOTIFY, resultWaiter.SOURCE, weiXinKey);
    }

    /**
     * 处理支付通知
     *
     * @param request      请求
     * @param payConfigMap 微信支付配置
     * @return 通知内容
     */
    @Override
    public String dealPayNotify(HttpServletRequest request, Map<String, String> payConfigMap) {
        if (checker.checkPayConfig(payConfigMap)) {
            return resultWaiter.payConfigError();
        }
        return this.dealPayNotify(request, payConfigMap.get("payKey"));
    }

    /**
     * 获取通知主商户微信公众号id
     *
     * @param request 请求
     * @return 主商户微信公众号id
     */
    @Override
    public String getAppId(HttpServletRequest request) {
        JSONObject jo = JSONObject.parseObject(getRequestData(request));
        if (jo == null) return null;
        return jo.getString("appid");
    }

    /**
     * 获取通知子商户微信公众号id
     *
     * @param request 请求
     * @return 子商户微信公众号id
     */
    public String getSubAppId(HttpServletRequest request) {
        JSONObject jo = JSONObject.parseObject(getRequestData(request));
        if (jo == null) return null;
        return jo.getString("sub_appid");
    }

    /**
     * 解析退款通知
     *
     * @param request   请求
     * @param weiXinKey 微信配置签名字符串
     * @return 微信请求参数
     */
    @Override
    public String dealRefundNotify(HttpServletRequest request, String weiXinKey) {
        return NotifyWaiter.getInstance().dealRefundNotify(request, WEIXIN_NOTIFY, resultWaiter.SOURCE, weiXinKey);
    }

    /**
     * 解析退款通知
     *
     * @param request      请求
     * @param payConfigMap 微信支付配置
     * @return 微信请求参数
     */
    @Override
    public String dealRefundNotify(HttpServletRequest request, Map<String, String> payConfigMap) {
        if (checker.checkPayConfig(payConfigMap)) {
            return resultWaiter.payConfigError();
        }
        return this.dealRefundNotify(request, payConfigMap.get("payKey"));
    }

    /**
     * 通知成功响应
     *
     * @param response 响应
     */
    @Override
    public void responseSuccessNotify(HttpServletResponse response) {
        NotifyWaiter.getInstance().responseSuccessNotify(response);
    }

    /**
     * 通知成功响应
     */
    @Override
    public String responseSuccessNotify() {
        return NotifyWaiter.getInstance().responseSuccessNotify();
    }

    /**
     * 通知失败响应
     *
     * @param errMsg   错误原因
     * @param response 响应
     */
    @Override
    public void responseFailNotify(HttpServletResponse response, String errMsg) {
        NotifyWaiter.getInstance().responseFailNotify(response, errMsg);
    }

    /**
     * 通知失败响应
     *
     * @param errMsg 错误原因
     */
    @Override
    public String responseFailNotify(String errMsg) {
        return NotifyWaiter.getInstance().responseFailNotify(errMsg);
    }


    /**
     * 从请求中获取请求信息
     *
     * @param request 请求对象
     * @return 请求信息
     */
    @Override
    public String getRequestData(HttpServletRequest request) {
        return NotifyWaiter.getInstance().getRequestData(request, WEIXIN_NOTIFY);
    }

}
