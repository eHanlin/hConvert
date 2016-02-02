package com.ehanlin.reflect;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import javax.management.AttributeList;

import org.junit.Test;
import org.junit.Assert;

import com.ehanlin.reflect.GenericToolTest.LongList;
import com.ehanlin.reflect.GenericToolTest.LongListList;

public class ReflectTest {
    
    //boolean、byte、char、short、int、long、float 和 double。
    boolean booleanP = true;
    byte byteP = 1;
    char charP = 'a';
    short shortP = 1;
    int intP = 1;
    long longP = 1L;
    float floatP = 1.0F;
    double doubleP = 1.0;
    
    Boolean booleanO = true;
    Byte byteO = 1;
    Character charO = 'a';
    Short shortO = 1;
    Integer intO = 1;
    Long longO = 1L;
    Float floatO = 1.0F;
    Double doubleO = 1.0;
    
    boolean[] booleanArr = {true, false, true, false};
    boolean[][] booleanArr2 = {{true, false}, {true, false}};
    
    @Test
    public void testPrimitive() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
        Field booleanPF = getField("booleanP");
        Class booleanPC = booleanPF.getType();
        Assert.assertTrue(booleanPC.isPrimitive());
        Type booleanPT = booleanPF.getGenericType();
        Assert.assertTrue(booleanPT instanceof Class);
        Assert.assertEquals(booleanPT, booleanPC);
        Field booleanOF = getField("booleanO");
        Class booleanOC = booleanOF.getType();
        Assert.assertFalse(booleanOC.isPrimitive());
        Type booleanOT = booleanOF.getGenericType();
        Assert.assertTrue(booleanOT instanceof Class);
        Assert.assertEquals(booleanOT, booleanOC);
        Assert.assertNotEquals(booleanOT, booleanPT);
        Assert.assertNotEquals(booleanOC, booleanPC);
        
        Class booleanPVC = booleanPF.get(this).getClass();
        //就算是基本型別的值，用 reflect 抽出後，就會變成 Object 子類。
        Assert.assertTrue(Boolean.class.isAssignableFrom(booleanPVC));
        
        Field booleanArrF = getField("booleanArr");
        Class booleanArrC = booleanArrF.getType();
        Type booleanArrT = booleanArrF.getGenericType();
        Assert.assertTrue(booleanArrC.isArray());
        Assert.assertFalse(booleanArrC.isPrimitive());
        Assert.assertEquals(booleanArrC, booleanArrT);
        Class booleanArrCC = booleanArrC.getComponentType();
        //Array 取出來的內容型別，還會維持在基本型別。
        Assert.assertTrue(booleanArrCC.isPrimitive());
        //因此所以沒辦法轉型成 Object[]
        Assert.assertFalse(Object[].class.isAssignableFrom(booleanArrCC));
        Assert.assertEquals(boolean[].class.getName(), "[Z");
        Assert.assertEquals(byte[].class.getName(), "[B");
        Assert.assertEquals(char[].class.getName(), "[C");
        Assert.assertEquals(short[].class.getName(), "[S");
        Assert.assertEquals(int[].class.getName(), "[I");
        Assert.assertEquals(long[].class.getName(), "[J");
        Assert.assertEquals(float[].class.getName(), "[F");
        Assert.assertEquals(double[].class.getName(), "[D");
        Assert.assertEquals(double[][].class.getName(), "[[D");
        Assert.assertEquals(double[][][].class.getName(), "[[[D");
        //基本型別也可以硬來
        Assert.assertEquals(boolean.class.getName(), "boolean");
        Assert.assertEquals(byte.class.getName(), "byte");
        Assert.assertEquals(char.class.getName(), "char");
        Assert.assertEquals(short.class.getName(), "short");
        Assert.assertEquals(int.class.getName(), "int");
        Assert.assertEquals(long.class.getName(), "long");
        Assert.assertEquals(float.class.getName(), "float");
        Assert.assertEquals(double.class.getName(), "double");
        
        //基本型別用 reflect 塞值回去會自動包裝轉換
        booleanPF.set(this, new Boolean(false));
        Assert.assertTrue(booleanP == false);
        Field longPF = getField("longP");
        longPF.set(this, new Long(1000L));
        Assert.assertTrue(longP == 1000L);
        booleanArr[0] = new Boolean(false);
        Assert.assertTrue(booleanArr[0] == false);
        Field charPF = getField("charP");
        charPF.set(this, new Character('b'));
        Assert.assertTrue(charP == 'b');
        
        Field booleanArr2F = getField("booleanArr2");
        Class booleanArr2FC = booleanArr2F.getType();
        Type booleanArr2FT = booleanArr2F.getGenericType();
        //巢狀 Array, boolean[] 和 boolean[][] 是完全不同的類別。
        Assert.assertFalse(boolean[].class.isAssignableFrom(booleanArr2FC));
        Assert.assertFalse(booleanArr2FC.isAssignableFrom(boolean[].class));
        
        //巢狀的 Array 用 Array.newInstance 不斷的遞迴傳入來建構。
        Class booleanArrDC = Array.newInstance(boolean.class, 0).getClass();
        Class booleanArr2DC = Array.newInstance(booleanArrDC, 0).getClass();
        Class booleanArr3DC = Array.newInstance(booleanArr2DC, 0).getClass();
        Assert.assertTrue(boolean[].class.isAssignableFrom(booleanArrDC));
        Assert.assertTrue(boolean[][].class.isAssignableFrom(booleanArr2DC));
        Assert.assertTrue(boolean[][][].class.isAssignableFrom(booleanArr3DC));
        
        //未知型態的 Array 設值、取值。
        Array.set(booleanArr, 3, new Boolean(true));
        Assert.assertTrue((boolean) Array.get(booleanArr, 3) == true); 
        Array.set(booleanArr, 3, new Boolean(false));
        Assert.assertTrue((boolean) Array.get(booleanArr, 3) == false); 
    }
    
    
    List list = new ArrayList();
    List<?> list_ = new ArrayList<String>();
    List<? extends List> list_list = new ArrayList<List>();
    List<? super List> list$list = new ArrayList<Collection>();
    List<List<List<String>>> list_list_list_String = new ArrayList<List<List<String>>>();
    List<? extends List<List<?>>> list_list_list_ = new ArrayList<List<List<?>>>(); 
    //下面這個檢查不通過
    //List<? extends List<List<?>>> list_list_list_ = new ArrayList<List<List<Object>>>();
    //原因出在 List<List<?>> 和 List<List<Object>> 並非 extends 的關係。
    //若改成下面這個
    //List<? extends List<?>> list_list_list_ = new ArrayList<List<List<Object>>>();
    //就可以通過
    //因為 List<?> 包括了 List<List<Object>>。
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testOneGenericBounds() throws NoSuchFieldException, SecurityException{
        //List
        Field listF = getField("list");
        Class listC = listF.getType();
        Type listT = listF.getGenericType();
        Assert.assertFalse(listT instanceof ParameterizedType);
        Assert.assertFalse(listT instanceof TypeVariable);
        Assert.assertFalse(listT instanceof WildcardType);
        Assert.assertFalse(listT instanceof GenericArrayType);
        Assert.assertTrue(listT instanceof Class);
        Assert.assertEquals(listT, listC);
        TypeVariable[] listCTV = listC.getTypeParameters();
        //List 原本就有的一個泛型參數
        Assert.assertEquals(listCTV.length, 1);
        //取得邊界類別，因為這個 List 沒傳入泛型資訊，所以應該會取得預設的 Object ，也就能放所有的東西。
        Type[] listCTV0B = listCTV[0].getBounds();
        Assert.assertEquals(listCTV0B.length, 1);
        Assert.assertEquals(listCTV0B[0], Object.class);
        
        //List<?>
        Field list_F = getField("list_");
        Class list_C = list_F.getType();
        Type list_T = list_F.getGenericType();
        //和沒有指定泛型類別不同使用 ? 泛型類別雖然和不指定等價，但 type 的部份已經會變成 ParameterizedType
        Assert.assertTrue(list_T instanceof ParameterizedType);
        Assert.assertFalse(list_T instanceof TypeVariable);
        Assert.assertFalse(list_T instanceof WildcardType);
        Assert.assertFalse(list_T instanceof GenericArrayType);
        Assert.assertFalse(list_T instanceof Class);
        Assert.assertNotEquals(list_T, list_C);
        ParameterizedType list_PT = (ParameterizedType) list_T;
        //他的 rawtype 還是 List
        Assert.assertEquals(List.class, list_PT.getRawType());
        //取得泛型的清單，因為是List，所以長度為 1
        Type[] list_PTA = list_PT.getActualTypeArguments();
        Assert.assertTrue(list_PTA.length == 1);
        //因為是 ? 所以會取得一個 WildcardType
        Type list_PTA0 = list_PTA[0];
        Assert.assertFalse(list_PTA0 instanceof ParameterizedType);
        Assert.assertFalse(list_PTA0 instanceof TypeVariable);
        Assert.assertTrue(list_PTA0 instanceof WildcardType);
        Assert.assertFalse(list_PTA0 instanceof GenericArrayType);
        Assert.assertFalse(list_PTA0 instanceof Class);
        WildcardType list_PTA0W = (WildcardType) list_PTA0;
        //?沒有指定上下邊界，所以下邊界的清單長度是0，而上邊界的長度是 1 ，類別為 Object
        Type[] list_PTA0WL = list_PTA0W.getLowerBounds();
        Assert.assertTrue(list_PTA0WL.length == 0);
        Type[] list_PTA0WU = list_PTA0W.getUpperBounds();
        Assert.assertTrue(list_PTA0WU.length == 1);
        Assert.assertEquals(Object.class, list_PTA0WU[0]);
        TypeVariable[] list_CTV = list_C.getTypeParameters();
        Assert.assertEquals(list_CTV.length, 1);
        Type[] list_CTV0B = list_CTV[0].getBounds();
        Assert.assertEquals(list_CTV0B.length, 1);
        Assert.assertEquals(list_CTV0B[0], Object.class);
        
        //List<? extends List>
        Field list_listF = getField("list_list");
        Class list_listC = list_listF.getType();
        Type list_listT = list_listF.getGenericType();
        Assert.assertTrue(list_listT instanceof ParameterizedType);
        Assert.assertFalse(list_listT instanceof TypeVariable);
        Assert.assertFalse(list_listT instanceof WildcardType);
        Assert.assertFalse(list_listT instanceof GenericArrayType);
        Assert.assertFalse(list_listT instanceof Class);
        Assert.assertNotEquals(list_listT, list_listC);
        ParameterizedType list_listPT = (ParameterizedType) list_listT;
        Assert.assertEquals(List.class, list_listPT.getRawType());
        Type[] list_listPTA = list_listPT.getActualTypeArguments();
        Assert.assertTrue(list_listPTA.length == 1);
        //因為是 ? 所以會取得一個 WildcardType
        Type list_listPTA0 = list_listPTA[0];
        Assert.assertFalse(list_listPTA0 instanceof ParameterizedType);
        Assert.assertFalse(list_listPTA0 instanceof TypeVariable);
        Assert.assertTrue(list_listPTA0 instanceof WildcardType);
        Assert.assertFalse(list_listPTA0 instanceof GenericArrayType);
        Assert.assertFalse(list_listPTA0 instanceof Class);
        WildcardType list_listPTA0W = (WildcardType) list_listPTA0;
        //? extends List 指定了上邊界，但未設下邊界，上邊界為 List
        Type[] list_listPTA0WL = list_listPTA0W.getLowerBounds();
        Assert.assertTrue(list_listPTA0WL.length == 0);
        Type[] list_listPTA0WU = list_listPTA0W.getUpperBounds();
        Assert.assertTrue(list_listPTA0WU.length == 1);
        Assert.assertEquals(List.class, list_listPTA0WU[0]);
        TypeVariable[] list_listCTV = list_listC.getTypeParameters();
        Assert.assertEquals(list_listCTV.length, 1);
        Type[] list_listCTV0B = list_listCTV[0].getBounds();
        Assert.assertEquals(list_listCTV0B.length, 1);
        Assert.assertEquals(list_listCTV0B[0], Object.class);
        
        //List<? super List>
        Field list$listF = getField("list$list");
        Class list$listC = list$listF.getType();
        Type list$listT = list$listF.getGenericType();
        Assert.assertTrue(list$listT instanceof ParameterizedType);
        Assert.assertFalse(list$listT instanceof TypeVariable);
        Assert.assertFalse(list$listT instanceof WildcardType);
        Assert.assertFalse(list$listT instanceof GenericArrayType);
        Assert.assertFalse(list$listT instanceof Class);
        Assert.assertNotEquals(list$listT, list$listC);
        ParameterizedType list$listPT = (ParameterizedType) list$listT;
        Assert.assertEquals(List.class, list$listPT.getRawType());
        Type[] list$listPTA = list$listPT.getActualTypeArguments();
        Assert.assertTrue(list$listPTA.length == 1);
        //因為是 ? 所以會取得一個 WildcardType
        Type list$listPTA0 = list$listPTA[0];
        Assert.assertFalse(list$listPTA0 instanceof ParameterizedType);
        Assert.assertFalse(list$listPTA0 instanceof TypeVariable);
        Assert.assertTrue(list$listPTA0 instanceof WildcardType);
        Assert.assertFalse(list$listPTA0 instanceof GenericArrayType);
        Assert.assertFalse(list$listPTA0 instanceof Class);
        WildcardType list$listPTA0W = (WildcardType) list$listPTA0;
        //? super List 指定了下邊界，但未設上邊界，上邊界為 Object，下邊界為 List
        Type[] list$listPTA0WL = list$listPTA0W.getLowerBounds();
        Assert.assertTrue(list$listPTA0WL.length == 1);
        Assert.assertEquals(List.class, list$listPTA0WL[0]);
        Type[] list$listPTA0WU = list$listPTA0W.getUpperBounds();
        Assert.assertTrue(list$listPTA0WU.length == 1);
        Assert.assertEquals(Object.class, list$listPTA0WU[0]);
        TypeVariable[] list$listCTV = list$listC.getTypeParameters();
        Assert.assertEquals(list$listCTV.length, 1);
        Type[] list$listCTV0B = list$listCTV[0].getBounds();
        Assert.assertEquals(list$listCTV0B.length, 1);
        Assert.assertEquals(list$listCTV0B[0], Object.class);
         
        //List<List<List<String>>>
        Field lllsF = getField("list_list_list_String");
        Class lllsC = lllsF.getType();
        Type lllsT = lllsF.getGenericType();
        //和沒有指定泛型類別不同使用 ? 泛型類別雖然和不指定等價，但 type 的部份已經會變成 ParameterizedType
        Assert.assertTrue(lllsT instanceof ParameterizedType);
        Assert.assertFalse(lllsT instanceof TypeVariable);
        Assert.assertFalse(lllsT instanceof WildcardType);
        Assert.assertFalse(lllsT instanceof GenericArrayType);
        Assert.assertFalse(lllsT instanceof Class);
        Assert.assertNotEquals(lllsT, lllsC);
        ParameterizedType lllsPT = (ParameterizedType) lllsT;
        //他的 rawtype 還是 List
        Assert.assertEquals(List.class, lllsPT.getRawType());
        //取得泛型的清單，因為是List，所以長度為 1
        Type[] lllsPTA = lllsPT.getActualTypeArguments();
        Assert.assertTrue(lllsPTA.length == 1);
        //因為是 List<List<String>> 所以會取得一個 ParameterizedType
        Type lllsPTA0 = lllsPTA[0];
        Assert.assertTrue(lllsPTA0 instanceof ParameterizedType);
        Assert.assertFalse(lllsPTA0 instanceof TypeVariable);
        Assert.assertFalse(lllsPTA0 instanceof WildcardType);
        Assert.assertFalse(lllsPTA0 instanceof GenericArrayType);
        Assert.assertFalse(lllsPTA0 instanceof Class);
        ParameterizedType lllsPTA0P = (ParameterizedType) lllsPTA0;
        Assert.assertEquals(List.class, lllsPTA0P.getRawType());
        //取得泛型的清單，因為是List<List<String>>，所以長度為 1
        Type[] lllsPTA0PTA = lllsPTA0P.getActualTypeArguments();
        Assert.assertTrue(lllsPTA0PTA.length == 1);
        //因為是 List<String> 所以會取得一個 ParameterizedType
        Type lllsPTA0PTA0 = lllsPTA0PTA[0];
        Assert.assertTrue(lllsPTA0PTA0 instanceof ParameterizedType);
        Assert.assertFalse(lllsPTA0PTA0 instanceof TypeVariable);
        Assert.assertFalse(lllsPTA0PTA0 instanceof WildcardType);
        Assert.assertFalse(lllsPTA0PTA0 instanceof GenericArrayType);
        Assert.assertFalse(lllsPTA0PTA0 instanceof Class);
        ParameterizedType lllsPTA0PTA0P = (ParameterizedType) lllsPTA0PTA0;
        Assert.assertEquals(List.class, lllsPTA0PTA0P.getRawType());
        //取得泛型的清單，因為是List<String>，所以長度為 1
        Type[] lllsPTA0PTA0PTA = lllsPTA0PTA0P.getActualTypeArguments();
        Assert.assertTrue(lllsPTA0PTA.length == 1);
        //因為是 String 所會取得一個 Class
        Type lllsPTA0PTA0PTA0 = lllsPTA0PTA0PTA[0];
        Assert.assertFalse(lllsPTA0PTA0PTA0 instanceof ParameterizedType);
        Assert.assertFalse(lllsPTA0PTA0PTA0 instanceof TypeVariable);
        Assert.assertFalse(lllsPTA0PTA0PTA0 instanceof WildcardType);
        Assert.assertFalse(lllsPTA0PTA0PTA0 instanceof GenericArrayType);
        Assert.assertTrue(lllsPTA0PTA0PTA0 instanceof Class);
        Assert.assertEquals(String.class, lllsPTA0PTA0PTA0);
        
        //List<? extends List<List<?>>>
        Field lllF = getField("list_list_list_");
        Class lllC = lllF.getType();
        Type lllT = lllF.getGenericType();
        //和沒有指定泛型類別不同使用 ? 泛型類別雖然和不指定等價，但 type 的部份已經會變成 ParameterizedType
        Assert.assertTrue(lllT instanceof ParameterizedType);
        Assert.assertFalse(lllT instanceof TypeVariable);
        Assert.assertFalse(lllT instanceof WildcardType);
        Assert.assertFalse(lllT instanceof GenericArrayType);
        Assert.assertFalse(lllT instanceof Class);
        Assert.assertNotEquals(lllT, lllC);
        TypeVariable[] lllCTV = lllC.getTypeParameters();
        Assert.assertEquals(lllCTV.length, 1);
        Type[] lllCTV0B = lllCTV[0].getBounds();
        Assert.assertEquals(lllCTV0B.length, 1);
        Assert.assertEquals(lllCTV0B[0], Object.class);
        ParameterizedType lllPT = (ParameterizedType) lllT;
        //他的 rawtype 還是 List
        Assert.assertEquals(List.class, lllPT.getRawType());
        //取得泛型的清單，因為是List，所以長度為 1
        Type[] lllPTA = lllPT.getActualTypeArguments();
        Assert.assertTrue(lllPTA.length == 1);
        //因為是 <? extends List<List<?>>> 所以會取得一個 WildcardType
        Type lllPTA0 = lllPTA[0];
        Assert.assertFalse(lllPTA0 instanceof ParameterizedType);
        Assert.assertFalse(lllPTA0 instanceof TypeVariable);
        Assert.assertTrue(lllPTA0 instanceof WildcardType);
        Assert.assertFalse(lllPTA0 instanceof GenericArrayType);
        Assert.assertFalse(lllPTA0 instanceof Class);
        WildcardType lllPTA0W = (WildcardType) lllPTA0;
        //? extends List<List<?>> 指定了上邊界為 List<List<?>> 而下邊界不設限。
        Type[] lllPTA0WL = lllPTA0W.getLowerBounds();
        Assert.assertTrue(lllPTA0WL.length == 0);
        Type[] lllPTA0WU = lllPTA0W.getUpperBounds();
        Assert.assertTrue(lllPTA0WU.length == 1);
        Type lllPTA0WU0 = lllPTA0WU[0];
        Assert.assertTrue(lllPTA0WU0 instanceof ParameterizedType);
        Assert.assertFalse(lllPTA0WU0 instanceof TypeVariable);
        Assert.assertFalse(lllPTA0WU0 instanceof WildcardType);
        Assert.assertFalse(lllPTA0WU0 instanceof GenericArrayType);
        Assert.assertFalse(lllPTA0WU0 instanceof Class);
        //上邊界 List<List<?>>
        ParameterizedType lllPTA0WU0P = (ParameterizedType) lllPTA0WU0;
        //他的 rawtype 還是 List
        Assert.assertEquals(List.class, lllPTA0WU0P.getRawType());
        //取得泛型的清單，因為是List，所以長度為 1
        Type[] lllPTA0WU0PTA = lllPTA0WU0P.getActualTypeArguments();
        Assert.assertTrue(lllPTA.length == 1);
        //因為是 List<?> 所以會取得一個 ParameterizedType
        Type lllPTA0WU0PTA0 = lllPTA0WU0PTA[0];
        Assert.assertTrue(lllPTA0WU0PTA0 instanceof ParameterizedType);
        Assert.assertFalse(lllPTA0WU0PTA0 instanceof TypeVariable);
        Assert.assertFalse(lllPTA0WU0PTA0 instanceof WildcardType);
        Assert.assertFalse(lllPTA0WU0PTA0 instanceof GenericArrayType);
        Assert.assertFalse(lllPTA0WU0PTA0 instanceof Class);
        ParameterizedType lllPTA0WU0PTA0P = (ParameterizedType) lllPTA0WU0PTA0;
        Assert.assertEquals(List.class, lllPTA0WU0PTA0P.getRawType());
        //取得泛型的清單，因為是List<?>，所以長度為 1
        Type[] lllPTA0WU0PTA0PTA = lllPTA0WU0PTA0P.getActualTypeArguments();
        Assert.assertTrue(lllPTA0WU0PTA0PTA.length == 1);
        //因為是 * ,所以會取得一個 WildcardType
        Type lllPTA0WU0PTA0PTA0 = lllPTA0WU0PTA0PTA[0];
        Assert.assertFalse(lllPTA0WU0PTA0PTA0 instanceof ParameterizedType);
        Assert.assertFalse(lllPTA0WU0PTA0PTA0 instanceof TypeVariable);
        Assert.assertTrue(lllPTA0WU0PTA0PTA0 instanceof WildcardType);
        Assert.assertFalse(lllPTA0WU0PTA0PTA0 instanceof GenericArrayType);
        Assert.assertFalse(lllPTA0WU0PTA0PTA0 instanceof Class);
        WildcardType lllPTA0WU0PTA0PTA0W = (WildcardType) lllPTA0WU0PTA0PTA0; 
        //?沒有指定上下邊界，所以下邊界的清單長度是0，而上邊界的長度是 1 ，類別為 Object
        Type[] lllPTA0WU0PTA0PTA0WL = lllPTA0WU0PTA0PTA0W.getLowerBounds();
        Assert.assertTrue(lllPTA0WU0PTA0PTA0WL.length == 0);
        Type[] lllPTA0WU0PTA0PTA0WU = lllPTA0WU0PTA0PTA0W.getUpperBounds();
        Assert.assertTrue(lllPTA0WU0PTA0PTA0WU.length == 1);
        Assert.assertEquals(Object.class, lllPTA0WU0PTA0PTA0WU[0]);
    }
    
    
    
    //如果不依泛型名稱 extends 而是亂換泛型名稱，則有機會出錯
    //如 private static class MyList1<E, S extends Collection> extends ArrayList<S>
    private static class MyLinkedList<E> extends LinkedList<E>{   
    }
    private static class MyList extends ArrayList<List>{
    }
    private static class MyList2<E extends Collection> extends ArrayList<E>{
    }
    private static class MyListG<E> extends MyList2<List>{
    }
    private static class MyList_List_List extends ArrayList<List<? extends List<?>>>{  
    }
    private static class My<E extends List<?> & Serializable & Cloneable>{
    }
    private static class My_List extends My<LinkedList<? super ArrayList<String>>>{
    }
    private static class My_LinkedList<E extends LinkedList<?>> extends My<E>{
    }
    
    private MyLinkedList<List> myLk = new MyLinkedList<List>();
    private MyList myL = new MyList();
    private MyListG<String> myLG = new MyListG<String>();
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testCollectionMethod() throws NoSuchFieldException, SecurityException, NoSuchMethodException{
        Type myLGT = getFieldType("myLG");
        Assert.assertTrue(myLGT instanceof ParameterizedType);
        Assert.assertFalse(myLGT instanceof TypeVariable);
        Assert.assertFalse(myLGT instanceof WildcardType);
        Assert.assertFalse(myLGT instanceof GenericArrayType);
        Assert.assertFalse(myLGT instanceof Class);
        
        Class myLGC = getFieldClass("myLG");
        TypeVariable[] myLGCTP = myLGC.getTypeParameters();
        Method[] methods = myLGC.getMethods();
        List<Method> methodsByName = new ArrayList<Method>();
        for(Method method : methods){
            if(method.getName().equals("add")){
                methodsByName.add(method);
            }
        }
        Assert.assertTrue(methodsByName.size() == 2);
        Method methodAdd = null;
        for(Method m : methodsByName){
            if(m.getGenericParameterTypes().length == 1){
                methodAdd = m;
                break;
            }
        }
        TypeVariable[] mAddTP = methodAdd.getTypeParameters();

        // add 方法傳入的泛型型別，將會被擦掉，所以會是 Object
        Type[] pTypes = methodAdd.getGenericParameterTypes();
        Type pTypes0 = pTypes[0];
        Assert.assertFalse(pTypes0 instanceof ParameterizedType);
        Assert.assertTrue(pTypes0 instanceof TypeVariable);
        Assert.assertFalse(pTypes0 instanceof WildcardType);
        Assert.assertFalse(pTypes0 instanceof GenericArrayType);
        Assert.assertFalse(pTypes0 instanceof Class);
        TypeVariable tv = (TypeVariable) pTypes0;
        Type tv0 = tv.getBounds()[0];
        Assert.assertEquals(tv0, Object.class);
        
        // add 方法被定義的類別，會是我們向下尋找傳入參數泛型類別的起點。
        Class declaringClass = methodAdd.getDeclaringClass();
        //想找 add 方法最限定的型別(List), 要從他被定義的類別開始向下找起, 但只需要找 ParameterizedType 的型別即可。
        Deque<ParameterizedType> spts = new ArrayDeque<ParameterizedType>();
        Type superType = myLGT;
        while(superType != null){
            if(superType instanceof ParameterizedType){
                spts.offerLast((ParameterizedType) superType);
            }
            if(GenericTool.getRawClass(superType).equals(declaringClass)){
                break;
            }
            superType = GenericTool.getRawClass(superType).getGenericSuperclass();
        }
        Type mtv = null;
        ParameterizedType spt =  spts.pollLast();
        while(spt != null){
            Type[] sptTA = spt.getActualTypeArguments();
            Method addM = null;
            Method[] ms = GenericTool.getRawClass(superType).getMethods();
            for(Method m : ms){
                if(m.getName().equals("add") && m.getParameterTypes().length==1){
                    addM = m;
                }
            }
            Type[] addMPT = addM.getGenericParameterTypes();
            spt = spts.pollLast();
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testGenericClass() throws NoSuchFieldException, SecurityException{
        //MyLinkedList<E> extends LinkedList<E>
        Class myLkC = getFieldClass("myLk");
        TypeVariable[] myLkTV = myLkC.getTypeParameters();
        Assert.assertEquals(myLkTV.length, 1);
        Type[] myLkTV0 = myLkTV[0].getBounds();
        Assert.assertEquals(myLkTV0.length, 1);
        Assert.assertEquals(myLkTV0[0], Object.class);
        Type myLkT = getFieldType("myLk");
        Assert.assertTrue(myLkT instanceof ParameterizedType);
        Assert.assertFalse(myLkT instanceof TypeVariable);
        Assert.assertFalse(myLkT instanceof WildcardType);
        Assert.assertFalse(myLkT instanceof GenericArrayType);
        Assert.assertFalse(myLkT instanceof Class);
        ParameterizedType myLkP = (ParameterizedType) myLkT;
        //因為是在類別中的類別，所以會有 owner
        Type myLkPO = myLkP.getOwnerType();
        Assert.assertFalse(myLkPO instanceof ParameterizedType);
        Assert.assertFalse(myLkPO instanceof TypeVariable);
        Assert.assertFalse(myLkPO instanceof WildcardType);
        Assert.assertFalse(myLkPO instanceof GenericArrayType);
        Assert.assertTrue(myLkPO instanceof Class);
        Assert.assertEquals(this.getClass(), myLkPO);
        Assert.assertEquals(myLkP.getRawType(), MyLinkedList.class);
        Type[] myLkPTA = myLkP.getActualTypeArguments();
        Assert.assertEquals(myLkPTA.length, 1);
        Type myLkPTA0 = myLkPTA[0];
        Assert.assertFalse(myLkPTA0 instanceof ParameterizedType);
        Assert.assertFalse(myLkPTA0 instanceof TypeVariable);
        Assert.assertFalse(myLkPTA0 instanceof WildcardType);
        Assert.assertFalse(myLkPTA0 instanceof GenericArrayType);
        Assert.assertTrue(myLkPTA0 instanceof Class);
        Assert.assertEquals(List.class, myLkPTA0);
        
        
        //MyList extends ArrayList<List>
        Class myLC = getFieldClass("myL");
        TypeVariable[] myLTV = myLC.getTypeParameters();
        Assert.assertEquals(myLTV.length, 0);
        Type myLT = getFieldType("myL");
        Assert.assertFalse(myLT instanceof ParameterizedType);
        Assert.assertFalse(myLT instanceof TypeVariable);
        Assert.assertFalse(myLT instanceof WildcardType);
        Assert.assertFalse(myLT instanceof GenericArrayType);
        Assert.assertTrue(myLT instanceof Class);
        Assert.assertEquals(myLT, myLC);
        Assert.assertEquals(MyList.class, myLT);
        
    }
    
    
    
    

    private Field getField(String name) throws NoSuchFieldException, SecurityException{
        return ReflectTest.class.getDeclaredField(name);
    }
    
    private Class<?> getFieldClass(String name) throws NoSuchFieldException, SecurityException{
        return ReflectTest.class.getDeclaredField(name).getType();
    }
    
    private Type getFieldType(String name) throws NoSuchFieldException, SecurityException{
        Type type = ReflectTest.class.getDeclaredField(name).getGenericType();
        return type;
    }
    
}
