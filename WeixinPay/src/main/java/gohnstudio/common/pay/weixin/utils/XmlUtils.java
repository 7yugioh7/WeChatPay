package gohnstudio.common.pay.weixin.utils;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;

/**
 * Created by lieber on 2017/5/14.
 * <p/>
 * xml操作工具类
 */
public class XmlUtils {

    public static void main(String[] arg) {
        String str="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\n" +
                "<xml>\n" +
                "  <appid><![CDATA[wx261671a6d70c4db5]]></appid>\n" +
                "  <attach><![CDATA[2]]></attach>\n" +
                "  <bank_type><![CDATA[CFT]]></bank_type>\n" +
                "  <cash_fee><![CDATA[1]]></cash_fee>\n" +
                "  <fee_type><![CDATA[CNY]]></fee_type>\n" +
                "  <is_subscribe><![CDATA[Y]]></is_subscribe>\n" +
                "  <mch_id><![CDATA[1332680901]]></mch_id>\n" +
                "  <nonce_str><![CDATA[6331ec735f9148fc937cd9c56a03fd91]]></nonce_str>\n" +
                "  <openid><![CDATA[otXptwaqkoGd5zLcGtvUJmW0nycI]]></openid>\n" +
                "  <out_trade_no><![CDATA[13201710161427087385921732]]></out_trade_no>\n" +
                "  <result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <sign><![CDATA[AAFE306DEF9CCB7D3ACE9C3AF21A5343]]></sign>\n" +
                "  <sub_mch_id><![CDATA[1333915801]]></sub_mch_id>\n" +
                "  <time_end><![CDATA[20171016142715]]></time_end>\n" +
                "  <total_fee>1</total_fee>  \n" +
                "  <trade_type><![CDATA[NATIVE]]></trade_type>\n" +
                "  <transaction_id><![CDATA[4200000017201710168407423934]]></transaction_id>\n" +
                "</xml>";
        JSONObject jsonObject = xmlChangeJson(str.replaceAll("\\n", ""));
        System.out.println(jsonObject);
        System.out.println(xmlChangeJsonStr(str.replaceAll("\\n", "")));
    }

    /**
     * 从request请求中获取携带的xml参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request) {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        InputStream inputStream = null;
        try {
            // 从request中取得输入流
            inputStream = request.getInputStream();
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList)
                map.put(e.getName(), e.getText());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 这个方法是将xml字符串转成Json
     *
     * @param XML
     * @return
     */
    public static JSONObject xmlChangeJson(String XML) {
        return JSONObject.parseObject(xmlChangeJsonStr(XML));
    }

    public static String xmlChangeJsonStr(String XML) {
        StringBuffer json = new StringBuffer("{");
        try {
            Document document = DocumentHelper.parseText(XML);
            Element root = document.getRootElement();
            Iterator it = root.elementIterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();
                String j = checkChildEle(element);
                if ("".equals(j)) {
                    json.append("\"" + element.getName() + "\":\"" + element.getText() + "\",");
                } else {
                    json.append(j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("}");
        return json.toString();
    }

    /**
     * 用于判断是否有子节点,若有就将子节点也进行拼接,若无则返回""
     *
     * @param element
     * @return
     * @throws DocumentException
     */
    private static String checkChildEle(Element element) throws DocumentException {
        StringBuffer json = new StringBuffer("");
        List<Element> list = new ArrayList<Element>();
        list = element.elements();
        if (list.size() > 0) {
            for (Element ele : list) {
                json.append("\"").append(ele.getName())
                        .append("\":\"").append(ele.getText())
                        .append("\",").append(checkChildEle(ele));
            }
        }
        return json.toString();
    }

}
