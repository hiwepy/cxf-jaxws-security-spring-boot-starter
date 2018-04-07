/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.cxf.spring.boot.proxy;


import java.util.Collection;  
import java.util.Set;  

/** 
* 两个键的复合map 
* <pre> 
* key------+ 
*          |-->value 
* param----+ 
* <pre> 
*  
* @author lixiaohui 
* @date 2016年10月1日 上午10:58:40 
*  
*/  
public interface CompoundKeyMap<K, P, V> {  
   
 V get(K key, P param);  
 V get(K key, P param, V defValue);  
   
 V put(K key, P param, V value);  
 V putIfAbsent(K key, P param, V value);  
   
 Set<java.util.Map.Entry<CompoundKey<K, P>, V>> entrySet();  
 Set<CompoundKey<K, P>> keys();  
 Collection<V> values();  
   
 int size();  
 boolean isEmpty();  
   
 public interface CompoundKey<K, P> {  
     K getKey();  
     P getParam();  
 }  
   
}  