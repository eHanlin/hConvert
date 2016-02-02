package com.ehanlin.reflect;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ehanlin.reflect.GenericTool;

public class GenericToolTest {
    
    String str = "abc";
    int integer = 1;
    Integer integerObj = 1;
    String[] arr = {"abc", "def"};
    List[] listArr = {new ArrayList()};
    List list = new ArrayList();
    List<String> strList = new ArrayList<String>();
    List<List<String>> strListList = new ArrayList<List<String>>();
    List<String[]> arrList = new ArrayList<String[]>();
    Map map = new HashMap();
    Map<String, Integer> strMap = new HashMap<String, Integer>();
    Map<String, Map<String, Integer>> strMapMap = new HashMap<String, Map<String, Integer>>();
    Map<String, String[]> arrMap = new HashMap<String, String[]>();
    LongList longList = new LongList();
    LongListList longListList = new LongListList();
    
    public static class LongList extends ArrayList<Long>{
        
    }
    
    public static class LongListList extends ArrayList<List<Long>>{
        
    }

    @Test
    public void testCheckGeneric() throws NoSuchFieldException, SecurityException{
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("str")) == GenericTool.CLASS);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("integer")) == GenericTool.CLASS);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("integerObj")) == GenericTool.CLASS);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("arr")) == GenericTool.ARRAY);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("listArr")) == GenericTool.ARRAY);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("list")) == GenericTool.CLASS);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("strList")) == GenericTool.PARAMETERIZED);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("strListList")) == GenericTool.PARAMETERIZED);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("arrList")) == GenericTool.PARAMETERIZED);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("map")) == GenericTool.CLASS);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("strMap")) == GenericTool.PARAMETERIZED);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("strMapMap")) == GenericTool.PARAMETERIZED);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("arrMap")) == GenericTool.PARAMETERIZED);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("longList")) == GenericTool.CLASS);
        Assert.assertTrue(GenericTool.checkGeneric(getFieldType("longListList")) == GenericTool.CLASS);
    }
    
    @Test
    public void testCheckGenericNest() throws NoSuchFieldException, SecurityException{
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("str")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("integer")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("integerObj")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("arr")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("listArr")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("list")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("strList")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("strListList")) == true);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("arrList")) == true);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("map")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("strMap")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("strMapMap")) == true);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("arrMap")) == true);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("longList")) == false);
        Assert.assertTrue(GenericTool.checkGenericNest(getFieldType("longListList")) == false);
    }
    
    @Test
    public void testcheckGenericPlural() throws NoSuchFieldException, SecurityException{
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("str")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("integer")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("integerObj")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("arr")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("listArr")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("list")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("strList")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("strListList")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("arrList")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("map")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("strMap")) == true);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("strMapMap")) == true);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("arrMap")) == true);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("longList")) == false);
        Assert.assertTrue(GenericTool.checkGenericPlural(getFieldType("longListList")) == false);
    }
    
    @Test
    public void testGetGenericClass() throws NoSuchFieldException, SecurityException{
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("str")), String.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("integer")), Integer.TYPE);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("integerObj")), Integer.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("arr")), String.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("listArr")), List.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("list")), List.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("strList")), String.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("strListList")), getFieldType("strList"));
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("arrList")), String[].class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("map")), Map.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("strMap")), String.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("strMapMap")), String.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("arrMap")), String.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("longList")), LongList.class);
        Assert.assertEquals(GenericTool.getGenericType(getFieldType("longListList")), LongListList.class);
    }
    
    @Test
    public void testGetGenericClasses() throws NoSuchFieldException, SecurityException{
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("str"))[0], String.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("integer"))[0], Integer.TYPE);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("integerObj"))[0], Integer.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("arr"))[0], String.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("listArr"))[0], List.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("list"))[0], List.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("strList"))[0], String.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("strListList"))[0], getFieldType("strList"));
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("arrList"))[0], String[].class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("map"))[0], Map.class);
        
        Type[] types = GenericTool.getGenericTypes(getFieldType("strMap"));
        Assert.assertEquals(types[0], String.class);
        Assert.assertEquals(types[1], Integer.class);
        
        types = GenericTool.getGenericTypes(getFieldType("strMapMap"));
        Assert.assertEquals(types[0], String.class);
        Assert.assertEquals(types[1], getFieldType("strMap"));
        
        types = GenericTool.getGenericTypes(getFieldType("arrMap"));
        Assert.assertEquals(types[0], String.class);
        Assert.assertEquals(types[1], String[].class);
        
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("longList"))[0], LongList.class);
        Assert.assertEquals(GenericTool.getGenericTypes(getFieldType("longListList"))[0], LongListList.class);
    }
    
    @Test
    public void testGetRawClass() throws NoSuchFieldException, SecurityException{
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("str")), String.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("integer")), Integer.TYPE);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("integerObj")), Integer.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("arr")), String[].class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("listArr")), List[].class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("list")), List.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("strList")), List.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("strListList")), List.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("arrList")), List.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("map")), Map.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("strMap")), Map.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("strMapMap")), Map.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("arrMap")), Map.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("longList")), LongList.class);
        Assert.assertEquals(GenericTool.getRawClass(getFieldType("longListList")), LongListList.class);
    }
    
    private Type getFieldType(String name) throws NoSuchFieldException, SecurityException{
        Type type = GenericToolTest.class.getDeclaredField(name).getGenericType();
        return type;
    }
}
