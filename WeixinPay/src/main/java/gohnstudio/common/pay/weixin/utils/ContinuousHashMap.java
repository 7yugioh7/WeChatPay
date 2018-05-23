package gohnstudio.common.pay.weixin.utils;

import java.util.HashMap;

public class ContinuousHashMap<K, V> extends HashMap<K, V> {

    /**
     * 向map中放入数据
     *
     * @param key   键
     * @param value 值
     * @return 当前map对象
     */
    public ContinuousHashMap<K, V> place(K key, V value) {
        super.put(key, value);
        return this;
    }

}
