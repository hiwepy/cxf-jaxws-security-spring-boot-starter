package org.apache.cxf.spring.boot;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.spring.boot.jaxws.EndpointApiCtClassBuilder;
import org.apache.cxf.spring.boot.jaxws.EndpointApiCtClassBuilder.CtWebParam;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import javassist.CtClass;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JaxwsApiCtClassBuilder_Test {

	//@Test
	public void testClass() throws Exception{
		
		CtClass ctClass = new EndpointApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCase1")
				.annotationForType("get", "http://ws.cxf.com", "getxx")
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.makeMethod("public void sayHello(String txt) { System.out.println(txt); }")
				.build();
		
		Class clazz = ctClass.toClass();
		
		System.err.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}
		
		System.err.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========Methods======================");
		for (Method element : clazz.getDeclaredMethods()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========sayHello======================");
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(BeanUtils.instantiateClass(clazz),  " hi Hello " );
		
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
		
		byte[] byteArr = ctClass.toBytecode();
		FileOutputStream output = new FileOutputStream(new File("D://FirstCaseV2.class"));
		
		IOUtils.write(byteArr, output);
		IOUtils.closeQuietly(output);
		
	}
	
	@Test
	public void testInstance() throws Exception{
		
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				System.err.println("=========Method Invoke ======================");
				
				System.out.println(method.getName());
				for (Annotation anno : method.getAnnotations()) {
					System.out.println(anno.toString());
				}
				for (Annotation[] anno : method.getParameterAnnotations()) {
					System.out.println(anno[0].toString());
				}
				
				for (Object arg : args) {
					System.out.println(arg.toString());
				}
				
				return null;
			}
		};
		
		Object ctObject = new EndpointApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCaseV2")
				.annotationForType("get", "http://ws.cxf.com", "getxx")
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.newMethod(String.class, "sayHello", new CtWebParam(String.class, "text"))
				.newMethod(String.class, "sayHello2", new CtWebParam(String.class, "text"))
				.toInstance(handler);
		
		Class clazz = ctObject.getClass();
		
		System.err.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}
		
		System.err.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========Methods======================");
		for (Method method : clazz.getDeclaredMethods()) {
			System.out.println(method.getName());
			System.err.println("=========Method Annotations======================");
			for (Annotation anno : method.getAnnotations()) {
				System.out.println(anno.toString());
			}
			System.err.println("=========Method Parameter Annotations======================");
			for (Annotation[] anno : method.getParameterAnnotations()) {
				System.out.println(anno[0].toString());
			}
		}
		System.err.println("=========sayHello======================");
		Method sayHello = clazz.getMethod("sayHello", String.class);
		sayHello.invoke(ctObject,  " hi Hello " );
		System.err.println("=========sayHello2======================");
		Method sayHello2 = clazz.getMethod("sayHello2", String.class);
		sayHello2.invoke(ctObject,  " hi Hello2 " );
	}

}
