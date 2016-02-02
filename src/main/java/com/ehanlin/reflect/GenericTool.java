package com.ehanlin.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * 處理泛型反射用的工具
 */
public class GenericTool {
    
    public static final int CLASS = 0;
    public static final int ARRAY = 1;
    public static final int GENERIC_ARRAY = 2;
    public static final int PARAMETERIZED = 3;
    public static final int TYPE_VARIABLE = 4;
    public static final int WILDCARD = 5;
    
    /**
     * 確認泛型類別
     */
    @SuppressWarnings("rawtypes")
    public static Integer checkGeneric(Object type){
        if(type instanceof ParameterizedType){
            return PARAMETERIZED;
        }
        if(type instanceof TypeVariable){
            return TYPE_VARIABLE;
        }
        if(type instanceof WildcardType){
            return WILDCARD;
        }
        if(type instanceof GenericArrayType){
            return GENERIC_ARRAY;
        }
        if(((Class)type).isArray()){
            return ARRAY;
        }
        return CLASS;
    }
    
    /**
     * 是否為泛型型別
     */
    public static Boolean checkHasGeneric(Object type){
        return checkGeneric(type) > 0;
    }
    
    /**
     * <p>查看這個泛型型別是否為巢狀的。</p>
     * <p>如 List<List<String>> 會傳回 true。</p>
     * <p>List<String> 會傳回 false。</p>
     */
    @SuppressWarnings("rawtypes")
    public static Boolean checkGenericNest(Type type){
        switch(checkGeneric(type)){
            case PARAMETERIZED:
                Type[] generics = ((ParameterizedType) type).getActualTypeArguments();
                for(Type generic : generics){
                    if(checkHasGeneric(generic)){
                        return true;
                    }
                }
                return false;
            case TYPE_VARIABLE:
                return checkGenericNest(((TypeVariable) type).getBounds()[0]);
            case WILDCARD:
                return checkGenericNest(((WildcardType) type).getUpperBounds()[0]);
            case GENERIC_ARRAY:
            case ARRAY:
            case CLASS:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * <p>查看這個泛型型別是否有一個以上的型別。</p>
     * <p>如 Map<String, Object> 會傳回 true。</p>
     * <p>List<String> 會傳回 false。</p>
     */
    @SuppressWarnings("rawtypes")
    public static Boolean checkGenericPlural(Type type){
        switch(checkGeneric(type)){
            case PARAMETERIZED:
                Type[] generics = ((ParameterizedType) type).getActualTypeArguments();
                if(generics.length > 1){
                    return true;
                }
                return false;
            case TYPE_VARIABLE:
                return checkGenericNest(((TypeVariable) type).getBounds()[0]);
            case WILDCARD:
                return checkGenericNest(((WildcardType) type).getUpperBounds()[0]);
            case GENERIC_ARRAY:
            case ARRAY:
            case CLASS:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * 取得這個泛型型別中的第一個型別。
     */
    @SuppressWarnings("rawtypes")
    public static Type getGenericType(Type type){
        switch(checkGeneric(type)){
            case PARAMETERIZED:
                Type[] generics = ((ParameterizedType) type).getActualTypeArguments();
                return generics[0];
            case TYPE_VARIABLE:
                return getGenericType(((TypeVariable) type).getBounds()[0]);
            case WILDCARD:
                return getGenericType(((WildcardType) type).getUpperBounds()[0]);
            case GENERIC_ARRAY:
                return (Class) ((GenericArrayType) type).getGenericComponentType();
            case ARRAY:
                return ((Class)type).getComponentType();
            case CLASS:
                return type;
            default:
                return null;
        }
    }
    
    /**
     * 取得這個泛型型別中全部的型別。
     */
    @SuppressWarnings("rawtypes")
    public static Type[] getGenericTypes(Type type){
        switch(checkGeneric(type)){
            case PARAMETERIZED:
                return ((ParameterizedType) type).getActualTypeArguments();
            case TYPE_VARIABLE:
                return getGenericTypes(((TypeVariable) type).getBounds()[0]);
            case WILDCARD:
                return getGenericTypes(((WildcardType) type).getUpperBounds()[0]);
            case GENERIC_ARRAY:
                return new Type[]{((GenericArrayType) type).getGenericComponentType()};
            case ARRAY:
                return new Type[]{((Class)type).getComponentType()};
            case CLASS:
                return new Type[]{type};
            default:
                return null;
        }
    }
    
    /**
     * 取得這個泛型型別的類別。
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> getRawClass(Type type){
        switch(checkGeneric(type)){
            case PARAMETERIZED:
                return getRawClass(((ParameterizedType) type).getRawType());
            case TYPE_VARIABLE:
                return getRawClass(((TypeVariable) type).getBounds()[0]);
            case WILDCARD:
                return getRawClass(((WildcardType) type).getUpperBounds()[0]);
            case GENERIC_ARRAY:
            case ARRAY:
            case CLASS:
                return (Class) type;
            default:
                return null;
        }
    }
}
