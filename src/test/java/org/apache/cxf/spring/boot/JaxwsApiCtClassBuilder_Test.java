package org.apache.cxf.spring.boot;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.cxf.spring.boot.jaxws.JaxwsApiCtClassBuilder;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import javassist.CtClass;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JaxwsApiCtClassBuilder_Test {

	@Test
	public void testClass() throws Exception{
		
		CtClass ctClass = new JaxwsApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCase1")
				.annotationForType("get", "http://ws.cxf.com", "getxx")
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.makeMethod("public void sayHello(String txt) { System.out.println(txt);  System.out.println(txt); }")
				.build();
		
		/**
		 * 1、api名称
		 * 2、参数名称
		 * 
		 */
		
		Class clazz = ctClass.toClass();
		Method mainMethod = clazz.getMethod("sayHello", String.class);
		mainMethod.invoke(BeanUtils.instantiateClass(clazz),  " Hello " );
		
		/**
		    当 CtClass 调用 writeFile()、toClass()、toBytecode() 这些方法的时候，Javassist会冻结CtClass Object，对CtClass object的修改将不允许。
		    这个主要是为了警告开发者该类已经被加载，而JVM是不允许重新加载该类的。如果要突破该限制，方法如下：
		*/
		ctClass.writeFile();
		ctClass.defrost();
		
		/**
		 * 1、api名称
		 * 2、参数名称
		 * 
		 */
		/* 
		byte[] byteArr = ctClass.toBytecode();
		FileOutputStream out = new FileOutputStream(new File("D://FirstCaseV2.class"));
		IOUtils.copy(byteArr, out);
		IOUtils.closeOutput(out);*/
		
	}
	
	@Test
	public void testInstance() throws Exception{
		
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				
				System.out.println("xssdasd");
				
				
				
				return null;
			}
		};
		
		Object ctClass = new JaxwsApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCaseV2")
				.annotationForType("get", "http://ws.cxf.com", "getxx")
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.newMethod(String.class, "sayHello")
				.toInstance(handler);
		
		Class clazz = ctClass.getClass();
		Method mainMethod = clazz.getMethod("sayHello", String.class);
		mainMethod.invoke(BeanUtils.instantiateClass(clazz),  " Hello " );
	 
		
	}

}
