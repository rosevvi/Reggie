package com.rosevii.common;

/**
 * @author: rosevvi
 * @date: 2023/3/7 10:56
 * @version: 1.0
 * @description:
 * 基于ThreadLocal封装工具类，用户保存和获取当前用户id
 */
public class BaseContext {
    public static final ThreadLocal<Long> threadLocal=new InheritableThreadLocal<>();
    /**
     * 设置值
     * @param id
     */
    public static void setCurrent(Long id){
        threadLocal.set(id);
    }
    /**
     * 获取值
     * @return
     */
    public static Long getCurrent(){
        return threadLocal.get();
    }
}
