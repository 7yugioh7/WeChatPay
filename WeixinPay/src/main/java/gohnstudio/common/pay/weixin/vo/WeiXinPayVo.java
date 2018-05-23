package gohnstudio.common.pay.weixin.vo;

/**
 * Created by lieber on 2018/1/26.
 */
public class WeiXinPayVo {

    /**
     * 创建订单
     */
    private String createOrder;
    /**
     * 订单查询
     */
    private String queryOrder;
    /**
     * 关闭订单
     */
    private String closeOrder;
    /**
     * 订单退款
     */
    private String refundOrder;
    /**
     * 退款订单查询
     */
    private String refundOrderQuery;
    /**
     * 下载对账单
     */
    private String downBill;
    /**
     * 证书根路径
     */
    private String certificatePath;
    /**
     * 证书名称
     */
    private String certificateName;
    /**
     * 服务商支付证书根路径
     */
    private String subCertificatePath;
    /**
     * 服务商支付证书名称
     */
    private String subCertificateName;

    public WeiXinPayVo() {

    }

    public WeiXinPayVo(String createOrder, String queryOrder, String closeOrder, String refundOrder, String refundOrderQuery, String downBill, String certificatePath, String certificateName, String subCertificatePath, String subCertificateName) {
        this.createOrder = createOrder;
        this.queryOrder = queryOrder;
        this.closeOrder = closeOrder;
        this.refundOrder = refundOrder;
        this.refundOrderQuery = refundOrderQuery;
        this.downBill = downBill;
        this.certificatePath = certificatePath;
        this.certificateName = certificateName;
        this.subCertificatePath = subCertificatePath;
        this.subCertificateName = subCertificateName;
    }

    public String getCreateOrder() {
        return createOrder;
    }

    public void setCreateOrder(String createOrder) {
        this.createOrder = createOrder;
    }

    public String getQueryOrder() {
        return queryOrder;
    }

    public void setQueryOrder(String queryOrder) {
        this.queryOrder = queryOrder;
    }

    public String getCloseOrder() {
        return closeOrder;
    }

    public void setCloseOrder(String closeOrder) {
        this.closeOrder = closeOrder;
    }

    public String getRefundOrder() {
        return refundOrder;
    }

    public void setRefundOrder(String refundOrder) {
        this.refundOrder = refundOrder;
    }

    public String getRefundOrderQuery() {
        return refundOrderQuery;
    }

    public void setRefundOrderQuery(String refundOrderQuery) {
        this.refundOrderQuery = refundOrderQuery;
    }

    public String getDownBill() {
        return downBill;
    }

    public void setDownBill(String downBill) {
        this.downBill = downBill;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getSubCertificatePath() {
        return subCertificatePath;
    }

    public void setSubCertificatePath(String subCertificatePath) {
        this.subCertificatePath = subCertificatePath;
    }

    public String getSubCertificateName() {
        return subCertificateName;
    }

    public void setSubCertificateName(String subCertificateName) {
        this.subCertificateName = subCertificateName;
    }
}
