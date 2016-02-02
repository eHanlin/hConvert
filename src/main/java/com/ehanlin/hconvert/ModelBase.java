package com.ehanlin.hconvert;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.ehanlin.reflect.GenericTool;

/**
 * 用來定義一些共用的方法。
 * 
 * @param <S> 來源物件類別
 * @param <T> 目標物件類別
 */
public abstract class ModelBase<S, T> implements Model<S, T> {
    
    private Converter converter = null;
    
    @SuppressWarnings("rawtypes")
    protected Map<String, Class> PrimitiveIndex = null;
    
    @SuppressWarnings("rawtypes")
    public ModelBase(){
        PrimitiveIndex = new HashMap<String, Class>();
        PrimitiveIndex.put("boolean", Boolean.class);
        PrimitiveIndex.put("byte", Byte.class);
        PrimitiveIndex.put("char", Character.class);
        PrimitiveIndex.put("short", Short.class);
        PrimitiveIndex.put("int", Integer.class);
        PrimitiveIndex.put("long", Long.class);
        PrimitiveIndex.put("float", Float.class);
        PrimitiveIndex.put("double", Double.class);
    }

    /**
     * <p>非泛型類別的轉換</p>
     * <p>會依順序經過以下的測試來呼對應的轉換方法。</p>
     * <ol>
     * <li>T 是否為 Object ，若是，呼叫 convert2Object 。這個方法原則上不作用，大多透過 convert2Super 實現。</li>
     * <li>S 是否為 T 的子類別，若是，呼叫 convert2Super 。</li>
     * <li>T 是否為 String ，若是，呼叫 convert2String 。</li>
     * <li>T 是否為 Boolean ，若是，呼叫 convert2Boolean 。</li>
     * <li>T 是否為 Number ，若是，呼叫 convert2Number 。</li>
     * <li>T 是否為 Array ，若是，呼叫 convert2Array 。</li>
     * <li>T 是否為 Collection ，若是，呼叫 convert2Collection 。</li>
     * <li>T 是否為 Map ，若是，呼叫 convert2Map 。</li>
     * <li>呼叫 convert2Other 。</li>
     * </ol>
     * <p>只要其中一個成功，立即返回。</p>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected T convertClass(S value, Class<? extends T> type){
        try{
            if(value == null){
                return null;
            }
            
            if(type.equals(Object.class)){
                try{
                    return convert2Object(value);
                }catch(Exception ex){
                    
                }
            }

            if(type.isInstance(value)){
                return convert2Super(value, type);
            }
            
            if(String.class.isAssignableFrom(type)){
                return convert2String(value);
            }
            
            if(Boolean.class.isAssignableFrom(type)){
                return convert2Boolean(value);
            }
            
            if(Number.class.isAssignableFrom(type)){
                return convert2Number(value, (Class<? extends Number>) type);
            }
            
            Class sourceClass = GenericTool.getRawClass(value.getClass());
            Type[] genericTypes = GenericTool.getGenericTypes(type);
            
            if(type.isArray()){
                return convert2Array(value, (Class<? extends S>) sourceClass, genericTypes[0]);
            }
            
            if(Collection.class.isAssignableFrom(type)){
                if(genericTypes.length < 1){
                    return convert2Collection(value, (Class<? extends S>) sourceClass, (Class<? extends Collection>) type, Object.class);
                }else{
                    return convert2Collection(value, (Class<? extends S>) sourceClass, (Class<? extends Collection>) type, genericTypes[0]);
                }
            }
            
            if(Map.class.isAssignableFrom(type)){
                if(genericTypes.length < 2){
                    return convert2Map(value, (Class<? extends S>) sourceClass, (Class<? extends Map>) type, Object.class, Object.class);
                }else{
                    return convert2Map(value, (Class<? extends S>) sourceClass, (Class<? extends Map>) type, genericTypes[0], genericTypes[1]);
                }
            }
            
            return convert2Other(value, (Class<? extends S>) sourceClass, type);
            
        }catch(Exception e){
            throw new ConvertException(); 
        }
    }
    
    /**
     * <p>泛型類別的轉換</p>
     * <p>會依順序經過以下的測試來呼對應的轉換方法。</p>
     * <ol>
     * <li>判斷 T 是否為泛型類別，若否，呼叫 convert 。</li>
     * <ol>
     */
    @SuppressWarnings("unchecked")
    @Override
    public T convert(Object value, Type type) {
        try{
            switch(GenericTool.checkGeneric(type)){
                case GenericTool.PARAMETERIZED:
                    break;
                case GenericTool.TYPE_VARIABLE:
                    break;
                case GenericTool.WILDCARD:
                    break;
                case GenericTool.GENERIC_ARRAY:
                    break;
                case GenericTool.ARRAY:
                case GenericTool.CLASS:
                    return convertClass((S) value, (Class<? extends T>) type);
            }
            throw new ConvertException(); 
        }catch(Exception e){
            throw new ConvertException(); 
        }
    }
    
    /**
     * <p>轉成 Object</p>
     * <p>注意!!因為 Object 是外卡類別，什麼類別都吃，所以這個方法原則上不作用，大多透過 convert2Super 實現。</p>
     * <p>但若複寫了這個方法，則這個方法會開始作用，並且在 convert2Super 前執行。</p>
     * @param value
     * @return
     */
    protected T convert2Object(S value){
        throw new ConvertException();
    }
    
    /**
     * 轉成父類別
     * @param value
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    protected T convert2Super(S value, Class<? extends T> type){
        return (T) value;
    }
    
    /**
     * 轉成 String 
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    protected T convert2String(S value){
        return (T) value.toString();
    }
    
    /**
     * 轉成 Boolean
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    protected T convert2Boolean(S value){
        try{
            return (T) Boolean.class.getConstructor(String.class).newInstance(value.toString());
        }catch(Exception e){
            throw new ConvertException();
        }
    }
    
    /**
     * 轉成 Number
     * @param value
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    protected T convert2Number(S value, Class<? extends Number> type){
        try{
            return (T) type.getConstructor(String.class).newInstance(value.toString());
        }catch(Exception e){
            throw new ConvertException();
        }
    }
    
    /**
     * 轉成 Array
     * @param value
     * @param type
     * @param itemType
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected T convert2Array(S value, Class<? extends S> type, Type itemType){
        try{
            Collection coll = new LinkedList();
            if(type.isArray()){
                List arr = Arrays.asList(value);
                for(Object item : arr){
                    coll.add(converter.convert(item, itemType));
                }
            }else if(Collection.class.isAssignableFrom(type)){
                for(Object item : (Collection) value){
                    coll.add(converter.convert(item, itemType));
                }
            }else{
                coll.add(converter.convert(value, itemType));
            }
            return (T) coll.toArray();
        }catch(Exception e){
            throw new ConvertException();
        }
    }
    
    /**
     * 轉成 Collection
     * @param value
     * @param type
     * @param collType
     * @param itemType
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected T convert2Collection(S value, Class<? extends S> type, Class<? extends Collection> collType, Type itemType){
        try{
            Collection coll = null;

            try{
                coll = collType.getConstructor().newInstance();
            }catch(Exception ex){
                //TODO 判斷各種 Collection 的子介面，然後用預設類別實作。
                if(List.class.isAssignableFrom(collType)){
                    coll = new ArrayList();
                }else if(Set.class.isAssignableFrom(collType)){
                    coll = new HashSet();
                }else if(Queue.class.isAssignableFrom(collType)){
                    coll = new LinkedList();
                }else{
                    coll = new ArrayList();
                }
            }

            if(type.isArray()){
                List arr = Arrays.asList(value);
                for(Object item : arr){
                    coll.add(converter.convert(item, itemType));
                }
            }else if(Collection.class.isAssignableFrom(type)){
                for(Object item : (Collection) value){
                    coll.add(converter.convert(item, itemType));
                }
            }else{
                coll.add(converter.convert(value, itemType));
            }
            return (T) coll;
        }catch(Exception e){
            throw new ConvertException();
        }
    }
    
    /**
     * 轉成 Map
     * @param value
     * @param type
     * @param mapType
     * @param keyType
     * @param itemType
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected T convert2Map(S value, Class<? extends S> type, Class<? extends Map> mapType, Type keyType, Type itemType){
        try{
            Map map = null;
            try{
                map = mapType.getConstructor().newInstance();
            }catch(Exception ex){
                //TODO 判斷各種 Map 的子介面，然後用預設類別實作。
                map = new HashMap();
            }
            
            if(Map.class.isAssignableFrom(type)){
                Map sourceMap = (Map) value;
                for(Object key : sourceMap.keySet()){
                    map.put(converter.convert(key, keyType), converter.convert(sourceMap.get(key), itemType));
                }
            }else{
                Field[] fields = type.getDeclaredFields();
                for(Field field : fields){
                    field.setAccessible(true);
                    map.put(converter.convert(field.getName(), keyType), converter.convert(field.get(value), itemType));
                }
            }
            return (T) map;
        }catch(Exception e){
            throw new ConvertException();
        }
    }
    
    /**
     * 轉成其他類別
     * @param value
     * @param type
     * @param targetType
     * @return
     */
    protected T convert2Other(S value, Class<? extends S> type, Class<? extends T> targetType){
        try{
            T result = targetType.getConstructor().newInstance();
            Field[] fields = targetType.getDeclaredFields();
            for(Field field : fields){
                Field sourceField = type.getField(field.getName());
                if(sourceField != null){
                    field.setAccessible(true);
                    sourceField.setAccessible(true);
                    field.set(result, converter.convert(sourceField.get(value), field.getGenericType()));
                }
            }
            return result;
        }catch(Exception e){
            throw new ConvertException();
        }
    }
    
    /**
     * 還原
     */
    public S revert(Object value, Type type){
        return null;
    }
    
    private Type targetType = null;
    /**
     * 取得 <T> 的型別。
     */
    protected Type getTargetType(){
        if(targetType != null){
            return targetType;
        }
        Type type[] = GenericTool.getGenericTypes(this.getClass());
        if(type.length < 2){
            targetType = Object.class;
        }else{
            targetType = type[1];
        }
        return targetType;
    }
    
    private Class<T> targetClass = null;
    /**
     * 取得 <T> 的類別。
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getTargetClass(){
        if(targetClass != null){
            return targetClass;
        }
        targetClass = (Class<T>) GenericTool.getRawClass(getTargetType());
        return targetClass;
    }

    @Override
    public Converter getConverter() {
        return converter;
    }

    @Override
    public void setConverter(Converter converter) {
        this.converter = converter;
    }
    
}