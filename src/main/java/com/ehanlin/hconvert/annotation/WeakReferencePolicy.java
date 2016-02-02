package com.ehanlin.hconvert.annotation;

/**
 * <p>用來決定 WeakReference 欄位的轉換策略。</p>
 * <p>舉例說明：</p>
 * <p>有一個 model 如下<br/>
 * class Model{<br/>
 *  @WeakReference(WeakReferencePolicy.IMMEDIATE)<br/>
 *  public model m1;<br/>
 *  @WeakReference(WeakReferencePolicy.MEDIATE)<br/>
 *  public model m2;<br/>
 * }<br/>
 * <br/>
 * m1 = new Model();<br/>
 * m2 = new Model();<br/>
 * m1.m1 = m1;<br/>
 * m1.m2 = m2;<br/>
 * m2.m1 = m1;<br/>
 * m2.m2 = m2;<br/>
 * <br/>
 * 假設轉換器在弱參照時轉出來的結果會是一個 Hash String。<br/>
 * 則 m1 轉換結果如下：<br/>
 * {<br/>
 *  m1 : m1Hash,<br/>
 *  m2 : { m1 : m1Hash, m2 : m2Hash }<br/>
 * }</p>
 */
public enum WeakReferencePolicy {
    /**
     * 直接在轉這個欄位時，就依弱參照的規則來轉換。
     */
    IMMEDIATE,
    /**
     * 在轉這個欄位的內容時，才依弱參照的規則來轉換。
     */
    MEDIATE,
    /**
     * 不使用弱參照的規則。
     */
    NONE
}
