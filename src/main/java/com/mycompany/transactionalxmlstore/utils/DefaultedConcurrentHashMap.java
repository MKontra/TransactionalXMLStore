/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Administrator
 */
/**
 * public class DefaultHashMap<K,V> extends HashMap<K,V> { protected V
 * defaultValue; public DefaultHashMap(V defaultValue) { this.defaultValue =
 * defaultValue; } @Override public V get(Object k) { V v = super.get(k); return
 * ((v == null) && !this.containsKey(k)) ? this.defaultValue : v; } }
 *
 * @author Administrator
 */
public class DefaultedConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V>
{

    protected V defaultValue;

    public DefaultedConcurrentHashMap(V defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object k)
    {
        synchronized (this)
        {
            V v = super.get(k);
            return ((v == null) && !this.containsKey(k)) ? this.defaultValue : v;
        }
    }
}
