package com.ehanlin.hconvert;

/**
 * 轉換發生錯誤時，會丟出這個例外。
 */
public class ConvertException extends RuntimeException {
    private static final long serialVersionUID = 5576416714197199441L;
    
    private String msg = null;
    private Exception ex = null;
    
    public ConvertException(){
        this(null, null);
    }
    
    public ConvertException(String msg){
        this(msg, null);
    }
    
    public ConvertException(Exception ex){
        this(null, ex);
    }
    
    public ConvertException(String msg, Exception ex){
        this.msg = msg;
        this.ex = ex;
    }

    public String getMsg() {
        return msg;
    }

    public Exception getEx() {
        return ex;
    }

}
