package com.ehanlin.hconvert;

import java.lang.reflect.Type;

import com.ehanlin.hconvert.annotation.WeakReferencePolicy;

/**
 * <p>轉換器，運作方法如下：</p>
 * <p>當使用 convert 時，依傳入的值的類別 S ，從已註冊的類別中，找出該對象的 Model ，接著呼叫 Model 中的 convert 傳入值 S 來取得結果 T 。</p>
 * <p>revert 時，依要轉成的類別 S ，從已註冊的類別中，找出該類別的 Model ，接著呼叫 Model 中的 revert 傳入值 T 來取得結果 S 。</p>
 * <p>若找不到類別，將使用預設的 Model。</p>
 */
public abstract class ConverterBase implements Converter{

    private Model<?, ?> defaultModel = null;
    private ModelFinder modelFinder = null;
    private Converter converter = null;
    
    @Override
    public Object convert(Object value, Type target){
        if(value == null){
            return null;
        }
        return convert(value, value.getClass(), target, WeakReferencePolicy.NONE);
    }
    @Override
    public Object convert(Object value, Type source, Type target){
        if(value == null){
            return null;
        }
        return convert(value, source, target, WeakReferencePolicy.NONE);
    }
    @Override
    public Object convert(Object value, Type target, WeakReferencePolicy weakRef){
        if(value == null){
            return null;
        }
        return convert(value, value.getClass(), target, weakRef);
    }
    @Override
    public Object convert(Object value, Type source, Type target, WeakReferencePolicy weakRef){
        if(value == null){
            return null;
        }
        return findModel(source, target).convert(value, source, target, weakRef);
    }
    
    /**
     * 還原
     */
    @Override
    public Object revert(Object value, Type source) {
        if(value == null){
            return null;
        }
        return revert(value, source, value.getClass());
    }
    @Override
    public Object revert(Object value, Type source, Type target){
        if(value == null){
            return null;
        }
        return findModel(source, target).revert(value, source, target);
    }
    
    /**
     * <p>註冊類別的轉換器。</p>
     */
    @Override
    public void registerModel(Type source, Type target, Model<?,?> model){
        model.setConverter(this);
        modelFinder.registerModel(source, target, model);
    }
    
    @Override
    public Model<?, ?> findModel(Type source, Type target) {
        Model<?, ?> model = modelFinder.findModel(source, target);
        if(model == null){
            model = getDefaultModel();
        }
        return model;
    }

    @Override
    public Model<?, ?> getDefaultModel() {
        return defaultModel;
    }
    /**
     * 設定預設的類別轉換器。
     */
    @Override
    public void setDefaultModel(Model<?, ?> defaultModel) {
        defaultModel.setConverter(this);
        this.defaultModel = defaultModel;
    }
    
    public ModelFinder getModelFinder() {
        return modelFinder;
    }
    /**
     * 設定類別轉換器的搜索器。
     */
    public void setModelFinder(ModelFinder modelFinder) {
        this.modelFinder = modelFinder;
    }

    @Override
    public Converter getConverter() {
        return this.converter;
    }
    
    @Override
    public void setConverter(Converter converter) {
        this.converter = converter;
    }
    
}
