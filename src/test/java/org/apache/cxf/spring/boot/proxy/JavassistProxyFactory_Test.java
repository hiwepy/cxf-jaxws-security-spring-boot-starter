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

import java.lang.reflect.Modifier;

import org.apache.cxf.spring.boot.A;
import org.apache.cxf.spring.boot.Person;
import org.apache.cxf.spring.boot.Smileable;
import org.apache.cxf.spring.boot.Talkable;
import org.apache.cxf.spring.boot.jaxws.proxy.JavassistProxyFactory;
import org.junit.Test;

public class JavassistProxyFactory_Test {

	@Test
	public void testProxy1() throws Exception{
		
		A target = new A();
		A proxy = JavassistProxyFactory.getProxy(target);
		proxy.del();
		
	}
	
	@Test
	public void testProxy2() throws Exception{
		
		A proxy = JavassistProxyFactory.getProxy(A.class);
		proxy.del();
		
	}
	
	@Test
	public void testProxy3() throws Exception{
		
		Person person = new Person("小明");  
        Object proxy = new JavassistProxy(person).getProxy();  
                // System.gc(); // 主动触发gc  
        Object proxy1 = new JavassistProxy(person).getProxy();  
        ((Talkable) proxy).talk("hello world");  
        ((Smileable) proxy).smile();  
          
        System.out.println("package: " + proxy.getClass().getPackage().getName());  
        System.out.println("classname: " + proxy.getClass().getName());  
        System.out.println("modifiers: " + Modifier.toString(proxy.getClass().getModifiers()));  
                System.out.println(proxy.getClass() == proxy1.getClass()); // 测试缓存是否起作用  
		
	}
	
}
