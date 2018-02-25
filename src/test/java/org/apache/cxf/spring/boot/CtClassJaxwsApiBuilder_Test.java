package org.apache.cxf.spring.boot;

import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.cxf.spring.boot.jaxws.JaxwsApiCtClassBuilder;
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
			ctClass = new JaxwsApiCtClassBuilder("com.zfsoft.jaxws.FirstCase")
					.annotationForType("get", "http://ws.zfsoft.com", "getxx")
					.makeField("public int k = 3;")
					.newField(String.class, "uid", UUID.randomUUID().toString())
					//.newFieldWithAutowired(fieldClass, fieldName)
					//.newFieldWithValue(fieldClass, fieldName, fieldValue)
					.makeMethod("public void sayHello(String txt) { System.out.println(uid);  System.out.println(txt); }")
					.proxyMethod(Object.class, action, exclude, methodName, params)
					.build();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	public JaxwsApiCtClassBuilder method(String apiname) throws NotFoundException, CannotCompileException {
		
		
		// 创建方法  
        CtClass ccStringType = pool.get("java.lang.String");
        // 参数：  1：返回类型  2：方法名称  3：传入参数类型  4：所属类CtClass 
        CtMethod ctMethod=new CtMethod(ccStringType,"sayHello",new CtClass[]{ccStringType},ctclass); 
        ctMethod.setModifiers(Modifier.PUBLIC); 
        StringBuffer body=new StringBuffer(); 
        body.append("{");
        body.append("\n    System.out.println($1);"); 
        body.append("\n    return \"Hello, \" + $1;"); 
        body.append("\n}"); 
        ctMethod.setBody(body.toString());
        ctclass.addMethod(ctMethod); 
         
        ClassFile ccFile = ctclass.getClassFile();
        ConstPool constPool = ccFile.getConstPool();
         
        // 添加类注解
        AnnotationsAttribute bodyAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation bodyAnnot = new Annotation("javax.jws.WebService", constPool);
        
        bodyAnnot.addMemberValue("name", new StringMemberValue("HelloWoldService", constPool));
       // 包名倒叙，并且和接口定义保持一致
        bodyAnnot.addMemberValue("targetNamespace", new StringMemberValue("http://service.jaxws.api.zfsoft.com", constPool));
        //bodyAnnot.addMemberValue("serviceName", new StringMemberValue("HelloWoldService", constPool));
        //bodyAnnot.addMemberValue("portName", new StringMemberValue("HelloWoldService", constPool));
        //bodyAnnot.addMemberValue("wsdlLocation", new StringMemberValue("HelloWoldService", constPool));
        //bodyAnnot.addMemberValue("endpointInterface", new StringMemberValue("HelloWoldService", constPool));
        
        bodyAttr.addAnnotation(bodyAnnot);
         
        ccFile.addAttribute(bodyAttr);
 
        // 添加方法注解
        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation methodAnnot = new Annotation("javax.jws.WebMethod", constPool);
        
        //methodAnnot.addMemberValue("operationName", new StringMemberValue("sayHelloWorld", constPool));
        methodAnnot.addMemberValue("action", new StringMemberValue("sayHelloWorld", constPool));
        methodAnnot.addMemberValue("exclude", new BooleanMemberValue(false, constPool));
        
        methodAttr.addAnnotation(methodAnnot);
         
        Annotation resultAnnot = new Annotation("javax.jws.WebResult", constPool);
        resultAnnot.addMemberValue("name", new StringMemberValue("result", constPool));
        resultAnnot.addMemberValue("partName", new StringMemberValue("result", constPool));
        // 包名倒叙，并且和接口定义保持一致
        resultAnnot.addMemberValue("targetNamespace", new StringMemberValue("http://service.jaxws.api.zfsoft.com", constPool));
        resultAnnot.addMemberValue("header", new BooleanMemberValue(false, constPool));
       
        methodAttr.addAnnotation(resultAnnot);
         
        ctMethod.getMethodInfo().addAttribute(methodAttr);
         
        // 添加参数注解
        ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
        Annotation paramAnnot = new Annotation("javax.jws.WebParam", constPool);
        paramAnnot.addMemberValue("name", new StringMemberValue("name",constPool));
        
        
        Annotation[][] paramArrays = new Annotation[1][1];
        paramArrays[0][0] = paramAnnot;
        parameterAtrribute.setAnnotations(paramArrays);
         
        ctMethod.getMethodInfo().addAttribute(parameterAtrribute);
		
	}
	 */
	
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

}
