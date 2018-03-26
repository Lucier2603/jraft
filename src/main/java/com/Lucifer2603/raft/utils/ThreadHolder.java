package com.Lucifer2603.raft.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ThreadHolder <br/>
 * Function: 线程执行的上下文内容
 *
 * @author Zhang Xu
 */
public final class ThreadHolder {

    /**
     * 线程上下文变量的持有者
     */
    private static final ThreadLocal<Map<String, Object>> CTX_HOLDER = new ThreadLocal<Map<String, Object>>() {

        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }

    };

    /**
     * 添加内容到线程上下文中
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            ctx = new HashMap<String, Object>();
            CTX_HOLDER.set(ctx);
        }
        ctx.put(key, value);
    }

    /**
     * 从线程上下文中获取内容
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            return null;
        }
        return (T) ctx.get(key);
    }

    /**
     * 获取线程上下文
     */
    public static Map<String, Object> getContext() {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            return null;
        }
        return ctx;
    }

    /**
     * 删除上下文中的key
     *
     * @param key
     */
    public static void remove(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx != null) {
            ctx.remove(key);
        }
    }

    /**
     * 上下文中是否包含此key
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx != null) {
            return ctx.containsKey(key);
        }
        return false;
    }

    /**
     * 清空线程上下文
     */
    public static void clean() {
        CTX_HOLDER.set(null);
    }

    //    /**
    //     * 初始化线程上下文
    //     */
    //    public static void init() {
    //        CTX_HOLDER.set(new HashMap<String, Object>());
    //    }

}
