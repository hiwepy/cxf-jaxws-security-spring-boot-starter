package org.apache.cxf.spring.boot;

import java.lang.reflect.Method;

import org.apache.cxf.spring.boot.utils.DummyClassJaxwsApiBuilder;
import org.junit.Test;

public class DummyClassJaxwsApiBuilder_Test {

	@Test
	public void testM() throws Exception{
		
		/**
		 * 1、api名称
		 * 2、参数名称
		 * 
		 */
		Class<?> FirstCaseClass = new DummyClassJaxwsApiBuilder("FirstCaseV2").output(".//target//asmsupport-test-generated")
				.method(null).build().build();
		Method mainMethod = FirstCaseClass.getMethod("main", String[].class);
		mainMethod.invoke(FirstCaseClass, new Object[] { null });
	}

}
