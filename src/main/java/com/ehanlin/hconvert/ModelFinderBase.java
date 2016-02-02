package com.ehanlin.hconvert;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ehanlin.reflect.GenericTool;

/**
 * <p>用來尋找轉換器，運作方法如下：</p>
 * <p>當使用 convert 時，依傳入的值的類別 S ，從已註冊的類別中，找出該對象的 Model ，接著呼叫 Model 中的 convert 傳入值 S 來取得結果 T 。</p>
 * <p>revert 時，依要轉成的類別 S ，從已註冊的類別中，找出該類別的 Model ，接著呼叫 Model 中的 revert 傳入值 T 來取得結果 S 。</p>
 * <p>若找不到轉換器，傳回 null 。</p>
 */
public abstract class ModelFinderBase implements ModelFinder {
    
    /**
     * 用來搜索配對的 Model 的索引表，第一層的 Key 是 S ，第二層的 Key 是 T。
     */
    protected Map<Type, Map<Type, Model<?, ?>>> modelIndex = new ConcurrentHashMap<Type, Map<Type, Model<?, ?>>>();

    /**
     * <p>註冊類別的轉換器。</p>
     * <p>若是要註冊 Array 的通用轉換類，請使用 Object[].class 做為 key 值。</p>
     * <p>注意!!這個操作是『非』執行緒安全的。因為大多的註冊會在一開始單緒初始化時就註冊好，所以不加鎖。</p>
     * @param model 轉換器
     * @param source 來源類別
     * @param target 要轉換成的類別
     */
    @Override
    public void registerModel(Type source, Type target, Model<?,?> model){
        if(!modelIndex.containsKey(source)){
            modelIndex.put(source, new ConcurrentHashMap<Type, Model<?, ?>>());
        }
        Map<Type, Model<?,?>> targetIndex = modelIndex.get(source);
        targetIndex.put(target, model);
    }

    /**
     * <p>尋找這個類別的轉換器。<br/>
     * source 比 target 先找。</p>
     */
    @Override
    public Model<?, ?> findModel(Type source, Type target) {
        try{
            Map<Type, Model<?,?>> modelMap = findMapValueByType(source, modelIndex);
            if(modelMap == null){
                return null;
            }
            
            Model<?,?> model = findMapValueByType(target, modelMap);
            if(model != null){
                return model;
            }
        }catch(Exception e){
            
        }
        return null;
    }
    
    /**
     * <p>用類別來找出在 map 中對應的物件。</p>
     * <p>可覆寫這個方法來改變尋找的演算法。</p>
     * @param type
     * @param map
     * @return
     */
    protected abstract <T> T findMapValueByType(Type type, Map<Type, T> map);
    
    public static class FindByInheritanceChain extends ModelFinderBase{
        
       /**
        * <p>以繼承鏈的方式來找出對應的轉換器。</p>
        * <p>尋找順序是：</p>
        * <ol>
        * <li>這個類別本身。</li>
        * <li>superclass。一路向上到 Object 為止，但不含 Object。</li>
        * <li>interface。一路向下到類別本身的 interface 為止。</li>
        * <li>Object</li>
        * </ol>
        * <p>會這樣定順序是因為類別本身是 interface 的集合體，所以比 interface 更有描述力。<br/>
        * 而 interface 則通常是越在繼承上層的越有代表性。<br/>
        * 以 ArrayList 來做例子，以下是它的繼承鍊，括號中的是這個類別加入的 interface：</p>
        * <ol>
        * <li>AbstractCollection ( Iterable, Collection )</li>
        * <li>AbstractList ( List )</li>
        * <li>ArrayList ( Serializable, Cloneable, RandomAccess )</li>
        * </ol>
        * <p>裡面所有的 interface 最有代表性的是 List ，但它位於中央基本上沒辦法自動被找到。<br/>
        * 去掉比較特定化的 List 後，最後的 Serializable, Cloneable, RandomAccess 對比 Iterable, Collection ，<br/>
        * 明顯的是上層的比較有代表性。</p>
        * <p>此外，還有幾種可能的演算法如下：</p>
        * <ul>
        * <li>以在繼承鍊中 interface 出現的數量為尋找的順序依據。</li>
        * <li>以 interface 繼承其他 interface 的數量為尋找的順序依據。</li>
        * <li>以 interface 繼承鍊的長度為尋找的順序依據。</li>
        * <ul>
        */
        @Override
        protected <T> T findMapValueByType(Type type, Map<Type, T> map){
            try{
                if(map.containsKey(type)){
                    return map.get(type);
                }
                
                Class<?> target = GenericTool.getRawClass(type);
                
                if(map.containsKey(target)){
                    return map.get(type);
                }
                
                if(target.isArray() && map.containsKey(Object[].class)){
                    return map.get(Object[].class);
                }
                
                Deque<Class<?>> ifaces = new ArrayDeque<Class<?>>();
                ifaces.addAll(Arrays.asList(target.getInterfaces()));
                
                Class<?> superClazz = target.getSuperclass();
                while(superClazz != null){
                    if(map.containsKey(superClazz)){
                        return map.get(superClazz);
                    }
                    ifaces.addAll(Arrays.asList(superClazz.getInterfaces()));
                    superClazz = superClazz.getSuperclass();
                }
                
                Class<?> iface = ifaces.pollLast();
                while(iface != null){
                    if(map.containsKey(iface)){
                        return map.get(iface);
                    }
                    Class<?> superIface = iface.getSuperclass();
                    if(superIface != null){
                        ifaces.addFirst(superIface);
                    }
                    iface = ifaces.pollLast();
                }
                
                if(map.containsKey(Object.class)){
                    return map.get(Object.class);
                }
                
                return null;
            }catch(Exception e){
                return null;
            }
        }
    }


}
