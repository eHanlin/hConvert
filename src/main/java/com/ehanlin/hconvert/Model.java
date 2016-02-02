package com.ehanlin.hconvert;

import java.lang.reflect.Type;

import com.ehanlin.hconvert.annotation.WeakReferencePolicy;

/**
 * <p>用類別為基準，來轉換二種物件。</p>
 * <p>以 Map 和 Mongodb 的物件轉換來舉例，則 T 就是 DBObject ，而 S 則是來源類別 Map。</p>
 * <p> convert 會把 Map 變成 DBObject。而 revert 則是把 DBObject 變成 Map。</p>
 * 
 * @param <S> 來源物件類別
 * @param <T> 目標物件類別
 */
public interface Model<S, T> {
    
    public T convert(Object value, Type target);
    public T convert(Object value, Type source, Type target);
    public T convert(Object value, Type target, WeakReferencePolicy weakRef);
    public T convert(Object value, Type source, Type target, WeakReferencePolicy weakRef);
    
    public S revert(Object value, Type source);
    public S revert(Object value, Type source, Type target);
    
    public Converter getConverter();
    public void setConverter(Converter converter);
}
