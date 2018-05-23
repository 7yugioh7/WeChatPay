package gohnstudio.common.pay.wechat.vo;

import gohnstudio.common.pay.WeChatPayAdaptor;

/**
 * Created by lieber on 2017/6/12.
 * <p/>
 * 微信支付工具模板实体
 */
public class WeChatPayVo {

    /**
     * 成功调用次数
     */
    private long count;

    /**
     * 支付工具适配器实现类
     */
    private WeChatPayAdaptor adaptor;

    public WeChatPayVo() {

    }

    public WeChatPayVo(int count, WeChatPayAdaptor adaptor) {
        this.count = count;
        this.adaptor = adaptor;
    }

    public WeChatPayVo(WeChatPayAdaptor adaptor) {
        this.count = 0;
        this.adaptor = adaptor;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public WeChatPayAdaptor getAdaptor() {
        return adaptor;
    }

    public void setAdaptor(WeChatPayAdaptor adaptor) {
        this.adaptor = adaptor;
    }
}
