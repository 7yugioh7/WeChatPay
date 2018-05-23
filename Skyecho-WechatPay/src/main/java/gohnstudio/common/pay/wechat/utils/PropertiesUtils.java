package gohnstudio.common.pay.wechat.utils;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Created by admin on 2017/6/16.
 */
public class PropertiesUtils {
    /**
     * 编码方式
     */
    private final static String ENCODING = "UTF-8";

    /**
     * 通过路径获取
     *
     * @param path 配置文件路径
     * @return
     */
    public static Properties getPropertiesByPath(String path) {
        try {
            path = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
            path = URLDecoder.decode(path, "UTF-8");
            // InputStream in = PropertiesUtils.class.getResourceAsStream(path);
            InputStream in = new FileInputStream(new File(path));
            if (in == null) {
                return null;
            }
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, ENCODING));
            Properties property = new Properties();
            property.load(bf);
            return property;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过key获取value
     *
     * @param path 配置文件路径
     * @param key  键
     * @return
     */
    public static String getValue(String path, String key) {
        Properties properties = getPropertiesByPath(path);
        if (properties != null) {
            return properties.getProperty(key);
        } else {
            return "";
        }
    }

    /**
     * 通过key获取value
     *
     * @param path         配置文件路径
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public static String getValue(String path, String key, String defaultValue) {
        Properties properties = getPropertiesByPath(path);
        if (properties != null) {
            String str = properties.getProperty(key);
            if (str == null || str.trim().length() == 0) {
                str = defaultValue;
            }
            return str;
        } else {
            return defaultValue;
        }
    }

    /**
     * 通过key获取value
     *
     * @param properties 配置文件对象
     * @param key        键
     * @return
     */
    public static String getValue(Properties properties, String key) {
        if (properties != null) {
            return properties.getProperty(key);
        } else {
            return "";
        }
    }

    /**
     * 通过key获取value
     *
     * @param properties   配置文件对象
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public static String getValue(Properties properties, String key, String defaultValue) {
        if (properties != null) {
            String str = properties.getProperty(key);
            if (str == null || str.trim().length() == 0) {
                str = defaultValue;
            }
            return str;
        } else {
            return defaultValue;
        }
    }

}
