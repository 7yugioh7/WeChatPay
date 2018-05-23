package gohnstudio.common.pay.weixin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyUtils {

    private final static String ENCODING = "UTF-8";

    /**
     * 获取配置文件
     *
     * @param path
     * @return
     */
    public static Properties getProperties(String path) {
        InputStream in = PropertyUtils.class.getResourceAsStream(path);
        if (in == null) {
            return null;
        }
        Properties property = new Properties();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, ENCODING));
            property.load(bf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return property;
    }

    /**
     * 获取值
     *
     * @param path
     * @param key
     * @return
     */
    public static String getValue(String path, String key) {
        Properties property = getProperties(path);
        if (property == null) return null;
        return (String) property.get(key);
    }

    /**
     * 设置值
     *
     * @param path
     * @param key
     * @param value
     */
    public static void setValue(String path, String key, String value) {
        Properties property = getProperties(path);
        if (property == null) return;
        property.put(key, value);
    }

}
