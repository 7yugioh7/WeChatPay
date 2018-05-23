package gohnstudio.common.pay.weixin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface WeiXinPayCommonUtils {

    /**
     * 获取系统订单号
     *
     * @param request 请求
     * @return 系统订单号
     */
    String getPayOrderId(HttpServletRequest request);

    /**
     * 获取微信订单号
     *
     * @param request 请求
     * @return 微信订单号
     */
    String getPayWxOrderId(HttpServletRequest request);

    /**
     * 处理支付通知
     *
     * @param request   请求
     * @param weiXinKey 微信配置签名字符串
     * @return 通知内容
     */
    String dealPayNotify(HttpServletRequest request, String weiXinKey);

    /**
     * 处理支付通知
     *
     * @param request      请求
     * @param payConfigMap 微信支付配置
     * @return 通知内容
     */
    String dealPayNotify(HttpServletRequest request, Map<String, String> payConfigMap);

    /**
     * 获取通知微信公众号id
     *
     * @param request 请求
     * @return 微信公众号id
     */
    String getAppId(HttpServletRequest request);

    /**
     * 解析退款通知
     *
     * @param request   请求
     * @param weiXinKey 微信配置签名字符串
     * @return 微信请求参数
     */
    String dealRefundNotify(HttpServletRequest request, String weiXinKey);

    /**
     * 解析退款通知
     *
     * @param request      请求
     * @param payConfigMap 微信支付配置
     * @return 微信请求参数
     */
    String dealRefundNotify(HttpServletRequest request, Map<String, String> payConfigMap);

    /**
     * 通知成功响应
     *
     * @param response 响应
     */
    void responseSuccessNotify(HttpServletResponse response);

    /**
     * 通知成功响应
     */
    String responseSuccessNotify();

    /**
     * 通知失败响应
     *
     * @param errMsg   错误原因
     * @param response 响应
     */
    void responseFailNotify(HttpServletResponse response, String errMsg);

    /**
     * 通知失败响应
     *
     * @param errMsg 错误原因
     */
    String responseFailNotify(String errMsg);

    /**
     * 从请求中获取请求信息
     *
     * @param request 请求对象
     * @return 请求信息
     */
    String getRequestData(HttpServletRequest request);

}
