package gohnstudio.common.pay.wechat.utils;

import gohnstudio.common.pay.wechat.vo.WeChatPayVo;

import java.util.*;

/**
 * Created by lieber on 2017/6/12.
 * <p/>
 * 切换方案工具类
 */
public class WeChatPayChangeUtils {

    /**
     * 切换工具实例
     */
    private static WeChatPayChangeUtils changeUtils;

    /**
     * 获取实例
     *
     * @return
     */
    public static WeChatPayChangeUtils getInstance() {
        if (changeUtils == null) {
            synchronized (WeChatPayChangeUtils.class) {
                if (changeUtils == null) {
                    changeUtils = new WeChatPayChangeUtils();
                }
            }
        }
        return changeUtils;
    }


    /**
     * 获取将要使用的支付工具
     *
     * @param map 所有的工具集合
     * @param set 已经使用过的工具集合
     * @return
     */
    public WeChatPayVo getUseAdaptor(Map<String, WeChatPayVo> map, Set<String> set, String change) {
        if (set == null) {
            set = new HashSet<String>();
        }
        if (ChangeType.STABLE.getValue().equals(change)) {  //最稳定优先切换
            return getUseAdaptorByStable(map, set);
        } else if (ChangeType.RANDOM.getValue().equals(change)) {   // 切随机切换
            return getUseAdaptorByRandom(map, set);
        } else if (ChangeType.AVG.getValue().equals(change)) {  // 平均切换
            return getUseAdaptorByAvg(map, set);
        } else {    // 默认选择一个
            return getUseAdaptorByAvg(map, set);
        }
    }

    /**
     * 平均分配切换
     *
     * @param map 所有的工具集合
     * @param set 已经使用过的工具集合
     * @return
     */
    public WeChatPayVo getUseAdaptorByAvg(Map<String, WeChatPayVo> map, Set<String> set) {
        WeChatPayVo min = null;
        if (set == null) {
            set = new HashSet<String>();
        }
        if (map != null && map.size() > 0) {
            Map<String, WeChatPayVo> tempMap = new HashMap<>();
            tempMap.putAll(map);
            if (set.size() > 0) {
                // 将map中已经使用过的去掉
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    tempMap.remove(iterator.next());
                }
            }
            if (tempMap.size() > 0) {
                String key = null;
                // 遍历还没有使用的map,使用平均分配,找到其中使用次数最少的那一个,如果使次数相同,按map中的遍历顺序
                for (Map.Entry<String, WeChatPayVo> entry : tempMap.entrySet()) {
                    if (min == null) {
                        key = entry.getKey();
                        min = entry.getValue();
                    } else {
                        WeChatPayVo temp = entry.getValue();
                        if (temp.getCount() < min.getCount()) {
                            key = entry.getKey();
                            min = temp;
                        }
                    }
                }
                set.add(key);
            }
        }
        return min;
    }

    /**
     * 随机分配
     *
     * @param map 所有的工具集合
     * @param set 已经使用过的工具集合
     * @return
     */
    public WeChatPayVo getUseAdaptorByRandom(Map<String, WeChatPayVo> map, Set<String> set) {
        WeChatPayVo adaptor = null;
        if (set == null) {
            set = new HashSet<String>();
        }
        if (map != null && map.size() > 0) {
            Map<String, WeChatPayVo> tempMap = new HashMap<>();
            tempMap.putAll(map);
            if (set.size() > 0) {
                // 将map中已经使用过的去掉
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    tempMap.remove(iterator.next());
                }
            }
            if (tempMap.size() > 0) {
                int index = 0;
                Random random = new Random();
                // 生成一个随机数
                int ran = random.nextInt(tempMap.size());
                // 遍历map中的
                for (Map.Entry<String, WeChatPayVo> entry : tempMap.entrySet()) {
                    if (index == ran) {
                        adaptor = entry.getValue();
                        set.add(entry.getKey());
                        break;
                    }
                    index += 1;
                }
            }
        }
        return adaptor;
    }


    /**
     * 最稳定优先
     *
     * @param map 所有的工具集合
     * @param set 已经使用过的工具集合
     * @return
     */
    public WeChatPayVo getUseAdaptorByStable(Map<String, WeChatPayVo> map, Set<String> set) {
        WeChatPayVo max = null;
        if (set == null) {
            set = new HashSet<String>();
        }
        if (map != null && map.size() > 0) {
            Map<String, WeChatPayVo> tempMap = new HashMap<>();
            tempMap.putAll(map);
            if (set.size() > 0) {
                // 将map中已经使用过的去掉
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    tempMap.remove(iterator.next());
                }
            }
            if (tempMap.size() > 0) {
                String key = null;
                for (Map.Entry<String, WeChatPayVo> entry : tempMap.entrySet()) {
                    if (max == null) {
                        key = entry.getKey();
                        max = entry.getValue();
                    } else {
                        WeChatPayVo temp = entry.getValue();
                        if (temp.getCount() > max.getCount()) {
                            key = entry.getKey();
                            max = temp;
                        }
                    }
                }
                set.add(key);
            }
        }
        return max;
    }

}
