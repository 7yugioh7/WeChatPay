package gohnstudio.common.pay.wechat.utils;

/**
 * Created by lieber on 2017/6/16.
 * <p/>
 * 选择方案类型
 */
public enum ChangeType {
    /**
     * 随机
     */
    RANDOM("random"),
    /**
     * 最稳定
     */
    STABLE("stable"),
    /**
     * 平均
     */
    AVG("avg");

    private String value;

    ChangeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isChooseType(String type) {
        if (type == null) return false;
        type = type.trim().toLowerCase();
        ChangeType[] chooseTypes = ChangeType.values();
        for (ChangeType chooseType : chooseTypes) {
            if (chooseType.getValue().equals(type)) return true;
        }
        return false;
    }

}
