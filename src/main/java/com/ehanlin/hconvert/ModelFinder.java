package com.ehanlin.hconvert;

import java.lang.reflect.Type;

/**
 * 用來尋找轉換器。
 */
public interface ModelFinder {
    
    /**
     * 註冊轉換器。
     * @param source
     * @param target
     * @param model
     */
    public void registerModel(Type source, Type target, Model<?,?> model);
    
    /**
     * 尋找轉換器。
     * @param source
     * @param target
     * @return
     */
    public Model<?, ?> findModel(Type source, Type target);
    
}
