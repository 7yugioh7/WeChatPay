package gohnstudio.common.pay.weixin.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * httpclient util.
 * it contains:
 * <ul>
 * <li>send request by POST</li>
 * <li>send String by POST</li>
 * </ul>
 */
public class HttpClientUtil {
    // default charset 'utf-8'
    private static final String DEFAULT_CHARSET = "utf-8";

    // make constructor private
    private HttpClientUtil() {
    }

    /**
     * close <code>CloseableHttpResponse</code> and <code>CloseableHttpClient</code>
     *
     * @param response   <code>CloseableHttpResponse</code>
     * @param httpclient <code>CloseableHttpClient</code>
     */
    private static void close(CloseableHttpResponse response,
                              CloseableHttpClient httpclient) {
        if (response != null) {
            try {
                response.close();
                response = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (httpclient != null) {
            try {
                httpclient.close();
                httpclient = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * do post request
     *
     * @param url     url
     * @param params  params map
     * @param charset charset, {@link HttpClientUtil#DEFAULT_CHARSET} is default
     * @return
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        RequestConfig config = getProxyConfig(url);

        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String ret = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            System.out.println("配置config为" + JSONObject.toJSONString(config));
            httpPost.setConfig(config);
            System.out.println("请求内容为" + httpPost.getRequestLine());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (params != null && !params.isEmpty()) {
                Set<Entry<String, String>> entrySet = params.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs.add(new BasicNameValuePair(key, value));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, charset));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            ret = EntityUtils.toString(entity, charset);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(response, httpclient);
        }

        return ret;
    }


    public static final String STATUS_NOT200 = "returnNot200";
    public static final String STATUS_CONNECTEXCEPTION = "connectExceptionOccurs";

    /**
     * @param url
     * @param string
     * @param charset
     * @return
     */
    public static String doPostString(String url, String string, String charset) {
        RequestConfig config = getProxyConfig(url);
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String ret = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            System.out.println("配置config为" + JSONObject.toJSONString(config));
            httpPost.setConfig(config);
            System.out.println("请求内容为" + httpPost.getRequestLine());
            HttpEntity entityReq = new StringEntity(string,
                    CharsetUtils.get("utf-8"));
            httpPost.setEntity(entityReq);
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            // Long s = System.currentTimeMillis();
            response = httpclient.execute(httpPost);
            // System.out.println();
//            System.out.println( "<<<" + response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                ret = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                ret = new String(ret.getBytes("iso8859-1"), "utf-8");
            } else {
                ret = STATUS_NOT200;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = STATUS_CONNECTEXCEPTION;
        } finally {
            close(response, httpclient);
        }

        return ret;
    }

    public static String doSSLPostString(String url, String string, String charset, String path, String password) {
        RequestConfig config = getProxyConfig(url);
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
//         CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String ret = null;

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(path));
            try {
                keyStore.load(instream, password.toCharArray());
            } finally {
                instream.close();
            }

            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, password.toCharArray())
                    .build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            HttpPost httpPost = new HttpPost(url);
            System.out.println("配置config为" + JSONObject.toJSONString(config));
            httpPost.setConfig(config);
            System.out.println("请求内容为" + httpPost.getRequestLine());
            HttpEntity entityReq = new StringEntity(string,
                    CharsetUtils.get("utf-8"));
            httpPost.setEntity(entityReq);
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            // Long s = System.currentTimeMillis();
            response = httpclient.execute(httpPost);
            // System.out.println();
//            System.out.println( "<<<" + response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                ret = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                ret = new String(ret.getBytes("iso8859-1"), "utf-8");
            } else {
                ret = STATUS_NOT200;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = STATUS_CONNECTEXCEPTION;
        } finally {
            close(response, httpclient);
        }

        return ret;
    }

    /**
     * 获取代理配置
     *
     * @return
     */
    private static RequestConfig getProxyConfig(String url) {
        if (url == null) return null;
        RequestConfig config = null;
        // 从配置文件中读取是否需要使用代理
        String isUseProxy = PropertyUtils.getValue("/conf/proxy.properties", "isUseProxy");
        // 从配置文件中读取如果当前url在配置的urls中,是否需要使用代理
        String isUseProxyWhenContainUrl = PropertyUtils.getValue("/conf/proxy.properties", "containUrlIsUse");
        if (isUseProxy != null && "true".equals(isUseProxy) &&
                isUseProxyWhenContainUrl != null &&
                (("true".equals(isUseProxyWhenContainUrl) && isContainUrl(url))
                        || ("false".equals(isUseProxyWhenContainUrl) && !isContainUrl(url)))) {  // 如果需要使用代理
            // 判断当前url是否需要使用代理
            String proxyIp = null;
            String port = null;
            // 判断当前使用http代理还是https代理
            if (url.toLowerCase().startsWith("https")) {    // 如果以https开头
                proxyIp = PropertyUtils.getValue("/conf/proxy.properties", "httpsProxyIp");
                port = PropertyUtils.getValue("/conf/proxy.properties", "httpsProxyPort");
            } else if (url.toLowerCase().startsWith("http")) {  // 如果以http开头
                proxyIp = PropertyUtils.getValue("/conf/proxy.properties", "httpProxyIp");
                port = PropertyUtils.getValue("/conf/proxy.properties", "httpProxyPort");
            }
            try {
                Integer proxyPort = Integer.parseInt((port == null ? "" : port).replaceAll(" ", ""));
                if (proxyIp != null && !"".equals(proxyIp.trim()) && proxyPort > 0) {
                    HttpHost proxy = new HttpHost(proxyIp, proxyPort);
                    config = RequestConfig.custom().setProxy(proxy).build();
                } else {
                    config = RequestConfig.custom().build();
                }
            } catch (Exception e) {
                e.printStackTrace();
                config = RequestConfig.custom().build();
            }
        }
        return config;
    }

    /**
     * 判断当前url是否在配置文件中
     *
     * @param url 当前的url
     * @return
     */
    private static boolean isContainUrl(String url) {
        if (url == null) return false;
        // 从配置文件中获取需要使用代理的ip或者域名
        String tempUrl = new String(url);
        String urls = PropertyUtils.getValue("/conf/proxy.properties", "urls");
        if (urls == null) return false;
        urls = urls.replaceAll("，", ",");
        String[] urlArray = urls.split(",");
        tempUrl = tempUrl.replace("https://", "").replace("http://", "");
        tempUrl = tempUrl.substring(0, tempUrl.indexOf("/"));
        for (String str : urlArray) {
            if (tempUrl.equals(str)) return true;
        }
        return false;
    }

}
