package org.apache.cxf.spring.boot;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.spring.boot.util.CtClassJaxwsApiBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public class CtClassJaxwsApiBuilder_Test {

	CtClass ctClass = null;
	
	@Before
	public void setup(){
		try {
			ctClass = new CtClassJaxwsApiBuilder("FirstCaseV").method("HelloWoldService2").build();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testAPIClass() throws Exception{
		
		/**
		 * 1、api名称
		 * 2、参数名称
		 * 
		 */
		
		Class clazz = ctClass.toClass();
		Method mainMethod = clazz.getMethod("sayHello", String.class);
		mainMethod.invoke(BeanUtils.instantiateClass(clazz),  " Hello " );
		
		/**
		 * 1、api名称
		 * 2、参数名称
		 * 
		 */
		byte[] byteArr = ctClass.toBytecode();
		 
		FileOutputStream out = new FileOutputStream(new File("D://FirstCaseV2.class"));
		IOUtils.copy(byteArr, out);
		IOUtils.closeOutput(out);
		
	}

}
