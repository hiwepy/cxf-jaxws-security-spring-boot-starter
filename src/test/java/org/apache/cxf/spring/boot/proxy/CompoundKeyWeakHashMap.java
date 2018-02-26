/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * 基于{@link java.util.HashMap}的CompoundKeyMap的实现, 所有key为弱引用
 *
 * @author lixiaohui
 * @date 2016年10月1日 下午12:37:08
 */
public class CompoundKeyWeakHashMap<K, P, V> implements CompoundKeyMap<K, P, V> {

	private Map<KeyHolder<CompoundKey<K, P>>, V> map = new HashMap<KeyHolder<CompoundKey<K, P>>, V>();

	/*
	 * @see
	 * cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#get(java.lang.Object,
	 * java.lang.Object)
	 */
	public V get(K key, P param) {
		key = Objects.requireNonNull(key, "key cannot be null");
		param = Objects.requireNonNull(param, "param cannot be null");

		return map.get(newKey(key, param));
	}

	private KeyHolder<CompoundKey<K, P>> newKey(K key, P param) {
		return new KeyHolder<CompoundKey<K, P>>(new CompoundKeyImpl<K, P>(key, param));
	}

	/*
	 * @see
	 * cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#get(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	public V get(K key, P param, V defValue) {
		key = Objects.requireNonNull(key, "key cannot be null");
		param = Objects.requireNonNull(param, "param cannot be null");

		V value = get(key, param);
		return value == null ? defValue : value;
	}

	/*
	 * @see
	 * cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#put(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	public V put(K key, P param, V value) {
		return map.put(newKey(key, param), value);
	}

	/*
	 * @see
	 * cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#putIfAbsent(java.lang.
	 * Object, java.lang.Object, java.lang.Object)
	 */
	public V putIfAbsent(K key, P param, V value) {
		return map.putIfAbsent(newKey(key, param), value);
	}

	/*
	 * @see cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#entrySet()
	 */
	public Set<Entry<CompoundKeyMap.CompoundKey<K, P>, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#keys()
	 */
	public Set<CompoundKeyMap.CompoundKey<K, P>> keys() {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#values()
	 */
	public Collection<V> values() {
		return map.values();
	}

	/*
	 * @see cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#size()
	 */
	public int size() {
		return map.size();
	}

	/*
	 * @see cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap#isEmpty()
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	static class KeyHolder<T> extends WeakReference<T> {

		public KeyHolder(T referent) {
			super(referent);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			return get().equals(((KeyHolder<T>) obj).get());
		}

		@Override
		public int hashCode() {
			return get().hashCode();
		}

	}

	static class CompoundKeyImpl<K, P> implements CompoundKey<K, P> {

		private K key;

		private P param;

		CompoundKeyImpl(K key, P param) {
			super();
			this.key = key;
			this.param = param;
		}

		/*
		 * @see
		 * cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap.CompoundKey#getKey()
		 */
		public K getKey() {
			return key;
		}

		/*
		 * @see
		 * cc.lixiaohui.demo.javassist.proxy.util.CompoundKeyMap.CompoundKey#getParam()
		 */
		public P getParam() {
			return param;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((param == null) ? 0 : param.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CompoundKeyImpl<?, ?> other = (CompoundKeyImpl<?, ?>) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (param == null) {
				if (other.param != null)
					return false;
			} else if (!param.equals(other.param))
				return false;
			return true;
		}

	}

}
