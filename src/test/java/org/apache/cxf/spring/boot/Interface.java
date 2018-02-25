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
package org.apache.cxf.spring.boot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.cxf.spring.boot.jaxws.proxy.Interceptor;


public interface Interface {
	void Action(int a);
}

class clazz implements Interface {

	@Override
	public void Action(int a) {
		System.out.println("do Action" + a);
	}
}

class MyInterceptor implements Interceptor {

	Object proxyed;

	public MyInterceptor(Object i) {
		proxyed = i;
	}

	@Override
	public int intercept(Object instance, Method method, Object[] Args) {
		try {
			System.out.println("before action");
			method.invoke(this.proxyed, Args);
			System.out.println("after action");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
