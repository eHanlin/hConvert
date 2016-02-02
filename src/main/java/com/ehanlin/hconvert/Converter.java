package com.ehanlin.hconvert;

/**
 * 轉換器
 */
public interface Converter extends ModelFinder, Model<Object, Object> {
    /**
     * 設定預設的轉換器。
     * @param defaultModel
     */
    public void setDefaultModel(Model<?, ?> defaultModel);
    public Model<?, ?> getDefaultModel();
}
