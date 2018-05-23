package gohnstudio.common.pay.wechat;

import com.alibaba.fastjson.JSONObject;
import gohnstudio.common.log.LogManager;
import gohnstudio.common.log.enums.NoticeType;
import gohnstudio.common.pay.WeChatPayAdaptor;
import gohnstudio.common.pay.wechat.utils.ChangeType;
import gohnstudio.common.pay.wechat.utils.ClassUtils;
import gohnstudio.common.pay.wechat.utils.PropertiesUtils;
import gohnstudio.common.pay.wechat.utils.WeChatPayChangeUtils;
import gohnstudio.common.pay.wechat.vo.WeChatPayVo;

import java.util.*;


/**
 * Created by lieber on 2017/6/12.
 * <p/>
 * 微信支付工具类,对接各种微信支付
 */
public class WeChatPayUtils {

    /**
     * 微信支付工具实例
     */
    private static WeChatPayUtils payUtils;

    /**
     * 微信支付工具vo
     */
    private static Map<String, WeChatPayVo> payVo;

    /**
     * 切换方案key
     */
    private final static String CHANGE = "pay.change";

    /**
     * 默认切换方案
     */
    private final static String DEFAULT_CHANGE = ChangeType.STABLE.getValue();

    /**
     * 微信支付策略配置
     */
    private static Properties properties;

    /**
     * 指定的执行顺序
     */
    private static List<String> orders;

    /**
     * 日志记录工具
     */
    private final static LogManager LOG = LogManager.getInstance();

    /**
     * 获取实例
     *
     * @return 工具类实例
     */
    public static WeChatPayUtils getInstance() {
        if (payUtils == null) {
            synchronized (WeChatPayUtils.class) {
                if (payUtils == null) {
                    init();
                    payUtils = new WeChatPayUtils();
                }
            }
        }
        return payUtils;
    }

    /**
     * 初始化
     */
    private static void init() {
        properties = PropertiesUtils.getPropertiesByPath("conf/wechatpay.properties");
        // 解析执行顺序
        String order = PropertiesUtils.getValue(properties, "pay.order", "");
        // 弃用
        String abandoned = PropertiesUtils.getValue(properties, "pay.abandoned", "");
        if (!"".equals(abandoned)) {
            abandoned = abandoned.toUpperCase();
        }
        String[] temps = order.toUpperCase().split(",");
        orders = new ArrayList<>();
        if (temps.length > 0) {
            for (String temp : temps) {
                if (temp != null && temp.trim().length() > 0 && !abandoned.contains(temp)) {
                    orders.add(temp);
                }
            }
        }
        String packageUrl = PropertiesUtils.getValue(properties, "pay.package", "gohnstudio.common.pay");
        // 获取所有的适配器
        List<Class<WeChatPayAdaptor>> adaptors = ClassUtils.getAllAdaptor(packageUrl);
        LOG.info("调用下单adaptors = " + JSONObject.toJSONString(adaptors), NoticeType.NO.getValue(), false);
        payVo = new HashMap<>(8);
        if (adaptors != null && adaptors.size() > 0) {
            for (Class<WeChatPayAdaptor> clazz : adaptors) {
                if (clazz != null) {
                    String clazzName = clazz.getName();
                    String realClazzName = clazzName.substring(clazzName.lastIndexOf(".") + 1, clazzName.length());
                    if (!abandoned.contains(realClazzName.toUpperCase())) {
                        try {
                            // 实例化支付工具
                            WeChatPayAdaptor adaptor = clazz.newInstance();
                            payVo.put(realClazzName, new WeChatPayVo(adaptor));
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.error(e, NoticeType.NO.getValue(), false);
                            // continue;
                        }
                    }
                }
            }
        }
    }

    /**
     * 微信下单
     *
     * @param orderId      订单号
     * @param totalFee     总金额
     * @param openId       支付者openid
     * @param ip           下单用户ip
     * @param goodsBody    商品名称
     * @param goodsDetail  商品详情
     * @param desc         备注
     * @param payConfigMap 微信支付配置
     * @param justUseIt    指定使用的调用方式(就算失败,也不会调用其他的支付)
     * @return 下单记过
     */
    public String placeOrder(String orderId, double totalFee, String openId, String ip,
                             String goodsBody, String goodsDetail, String desc,
                             Map<String, Map<String, String>> payConfigMap, String justUseIt) {
        String param = "<orderId=" + orderId + ";totalFee=" + totalFee + ";openId=" + openId
                + ";ip=" + ip + ";goodsBody=" + goodsBody + ";goodsDetail=" + goodsDetail
                + ";desc=" + desc + ";justUseIt=" + justUseIt + ">";
        LOG.info("调用下单接口" + param, NoticeType.NO.getValue(), false);
        String result = null;
        if (payConfigMap == null || payConfigMap.size() == 0) {
            LOG.warn("支付配置信息为空", NoticeType.NO.getValue(), false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "支付配置信息为空");
            return jsonObject.toJSONString();
        }
        if (justUseIt != null && !"".equals(justUseIt.trim())) {
            WeChatPayVo weChatPayVo = null;
            WeChatPayAdaptor adaptor = null;
            for (Map.Entry<String, WeChatPayVo> entry : payVo.entrySet()) {
                String key = entry.getKey();
                if (key.toUpperCase().equals(justUseIt.toUpperCase())) {
                    weChatPayVo = entry.getValue();
                    break;
                }
            }
            if (weChatPayVo != null) {
                adaptor = weChatPayVo.getAdaptor();
            }
            if (adaptor != null) {
                LOG.info("指定使用<" + justUseIt + ">来执行", NoticeType.NO.getValue(), false);
                result = adaptor.placeOrder(orderId, totalFee, openId, ip,
                        goodsBody, goodsDetail, desc, payConfigMap);
                LOG.info("下单结果为<" + result + ">", NoticeType.NO.getValue(), false);
                return result;
            }
        }
        List<String> results = new ArrayList<>();
        // 从配置文件中读取配置的选择方案
        String change = PropertiesUtils.getValue(properties, CHANGE, DEFAULT_CHANGE);
        LOG.info("调用下单接口" + param, NoticeType.NO.getValue(), false);
        // 如果配置中填写的选择方案不合法,采用默认的选择方案
        if (!ChangeType.isChooseType(change)) {
            change = DEFAULT_CHANGE;
        }
        Set<String> set = new HashSet<>();
        WeChatPayVo weChatPayVo = null;
        WeChatPayAdaptor adaptor = null;
        boolean success = false;
        int index = 0;
        // 获取切换实例
        WeChatPayChangeUtils changeUtils = WeChatPayChangeUtils.getInstance();
        LOG.info("调用下单set = " + JSONObject.toJSONString(set), NoticeType.NO.getValue(), false);
        while (set.size() < payVo.size() && !success) {
            LOG.info("调用下单index = " + index, NoticeType.NO.getValue(), false);
            // 如果解析结果为下单失败,切换方案
            if (orders != null && orders.size() > index) {
                weChatPayVo = getUseAdaptorByOrder(payVo, set, orders.get(index));
                if (weChatPayVo == null) {
                    index = orders.size();
                }
                index++;
            } else {
                weChatPayVo = changeUtils.getUseAdaptor(payVo, set, change);
            }
            // 获取新方案的执行类
            if (weChatPayVo != null) {
                adaptor = weChatPayVo.getAdaptor();
            }
            // 如果执行类不为空
            if (adaptor != null) {
                LOG.info("使用<" + adaptor.getClass().getName() + ">下单", NoticeType.NO.getValue(), false);
                // 如果找到执行方法,则尝试下单
                result = adaptor.placeOrder(orderId, totalFee, openId, ip, goodsBody, goodsDetail, desc, payConfigMap);
                // 保存本次的下单结果
                results.add(result);
                // 判断下单是否成功
                success = judgeResult(result);
                LOG.info("下单结果为<" + result + ">,下单" + (success ? "成功" : "失败"), NoticeType.NO.getValue(), false);
            }
        }
        if (weChatPayVo == null) {
            // 如果没有找到执行方案
            LOG.info("没有找到执行方案", NoticeType.NO.getValue(), false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            List<String> mesList = new ArrayList<>();
            mesList.add("没有找到执行方案");
            jsonObject.put("message", JSONObject.toJSONString(mesList));
            return jsonObject.toJSONString();
        } else if (!success && adaptor != null) {
            // 如果所有方案执行完成也没有成功下单,可能情况为1.所有通道失败(几率低),2.传入参数错误
            // 所以讲每次执行的结果list返回回去,用于查找原因
            LOG.warn("执行完所有方案也没有成功", NoticeType.NO.getValue(), false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", JSONObject.toJSONString(results));
            return jsonObject.toJSONString();
        } else if (success) {
            // 执行成功次数加一
            weChatPayVo.setCount(weChatPayVo.getCount() + 1);
            // 返回调用的结果
            return result;
        } else {
            return result;
        }
    }

    /**
     * 微信下单
     *
     * @param orderId      订单号
     * @param totalFee     总金额
     * @param openId       支付者openid
     * @param ip           下单用户ip
     * @param goodsBody    商品名称
     * @param goodsDetail  商品详情
     * @param desc         备注
     * @param payConfigMap 微信支付配置
     * @return 下单结果
     */
    public String placeOrder(String orderId, double totalFee, String openId, String ip,
                             String goodsBody, String goodsDetail, String desc,
                             Map<String, Map<String, String>> payConfigMap) {
        return placeOrder(orderId, totalFee, openId, ip, goodsBody, goodsDetail, desc, payConfigMap, null);
    }

    /**
     * 通过指定的顺序获取执行方法
     *
     * @param map     所有的执行方法map
     * @param set     已经执行的方法集合
     * @param adaptor 本次需要的适配器
     * @return 微信支付工具模板实体
     */
    private WeChatPayVo getUseAdaptorByOrder(Map<String, WeChatPayVo> map, Set<String> set, String adaptor) {
        WeChatPayVo payVo = null;
        if (set == null) {
            set = new HashSet<>();
        }
        if (map != null && map.size() > 0) {
            Map<String, WeChatPayVo> tempMap = new HashMap<>();
            tempMap.putAll(map);
            if (set.size() > 0) {
                // 将map中已经使用过的去掉
                for (String str : set) {
                    tempMap.remove(str);
                }
            }
            if (tempMap.size() > 0) {
                String key = null;
                for (Map.Entry<String, WeChatPayVo> entry : tempMap.entrySet()) {
                    key = entry.getKey();
                    if (key.toUpperCase().equals(adaptor.toUpperCase())) {
                        payVo = entry.getValue();
                        break;
                    }
                }
                set.add(key);
            }
        }
        return payVo;
    }

    /**
     * H5调起微信支付API页面
     *
     * @param appid            公众号id
     * @param timeStamp        时间戳
     * @param nonceStr         随机字符串
     * @param packageInfo      订单详情扩展字符串
     * @param signType         签名方式
     * @param paySign          签名
     * @param paySuccessMethod 支付成功后执行的方法
     * @param payFailedMethod  支付失败执行的方法
     * @return 返回的唤起支付页面
     */
    public String getPageOfWeChatPay(String appid, String timeStamp, String nonceStr, String packageInfo,
                                     String signType, String paySign, String paySuccessMethod,
                                     String payFailedMethod) {
        return ("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" " +
                "content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\">" +
                "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">" +
                "<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\">" +
                "<title>微信支付</title>") +
                "<script type=\"text/javascript\">" +
                "function onBridgeReady(){" +
                "WeixinJSBridge.invoke('getBrandWCPayRequest', {" +
                "\"appId\": \"" + appid + "\"," +
                "\"timeStamp\": \"" + timeStamp + "\"," +
                "\"nonceStr\": \"" + nonceStr + "\"," +
                "\"package\": \"" + packageInfo + "\"," +
                "\"signType\": \"" + signType + "\"," +
                "\"paySign\": \"" + paySign + "\"" +
                "},function (res) {" +
                "if (res.err_msg == \"get_brand_wcpay_request:ok\") {" +
                paySuccessMethod +
                "}" +
                " else {" +
                payFailedMethod +
                "}" +
                "});}" +
                "if (typeof WeixinJSBridge == \"undefined\") {" +
                "if (document.addEventListener) {document.addEventListener('WeixinJSBridgeReady', " +
                "onBridgeReady, false);} " +
                "else if (document.attachEvent) {document.attachEvent('WeixinJSBridgeReady', " +
                "onBridgeReady);document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);}" +
                "}" +
                "else {onBridgeReady();}" +
                "</script>" +
                "</head><body>" +
                "</body></html>";
    }

    /**
     * 判断本次是否调用成功
     *
     * @param result 本地调用返回的结果
     * @return true/false
     */
    private boolean judgeResult(String result) {
        if (result == null) {
            return false;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            return jsonObject.getBooleanValue("success");
        } catch (Exception e) {
            return false;
        }
    }
}
