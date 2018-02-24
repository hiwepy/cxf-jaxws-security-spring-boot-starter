package org.apache.cxf.spring.boot.util;

import org.apache.commons.lang3.builder.Builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class CtClassJaxrsApiBuilder implements Builder<CtClass> {

	ClassPool pool = ClassPool.getDefault();
	// 构建动态类
	private CtClass ctclass  = null;
	
	public CtClassJaxrsApiBuilder(final String classname) throws NotFoundException {
		this.ctclass = pool.getOrNull(classname);
		if( null == this.ctclass) {
			this.ctclass = pool.makeClass(classname);
		}
	}
	
	public CtClassJaxrsApiBuilder method(String apiname) throws NotFoundException, CannotCompileException {
		
		
		
		
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
        bodyAnnot.addMemberValue("name", new StringMemberValue(apiname, constPool));
        bodyAttr.addAnnotation(bodyAnnot);
         
        ccFile.addAttribute(bodyAttr);
 
        // 添加方法注解
        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation methodAnnot = new Annotation("javax.jws.WebMethod", constPool);
        methodAnnot.addMemberValue("operationName", new StringMemberValue("sayHelloWorld", constPool));
        methodAttr.addAnnotation(methodAnnot);
         
        Annotation resultAnnot = new Annotation("javax.jws.WebResult", constPool);
        resultAnnot.addMemberValue("name", new StringMemberValue("result", constPool));
        methodAttr.addAnnotation(resultAnnot);
         
        ctMethod.getMethodInfo().addAttribute(methodAttr);
         
        // 添加参数注解
        ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(
                constPool, ParameterAnnotationsAttribute.visibleTag);
        Annotation paramAnnot = new Annotation("javax.jws.WebParam", constPool);
        paramAnnot.addMemberValue("name", new StringMemberValue("name",constPool));
        Annotation[][] paramArrays = new Annotation[1][1];
        paramArrays[0][0] = paramAnnot;
        parameterAtrribute.setAnnotations(paramArrays);
         
        ctMethod.getMethodInfo().addAttribute(parameterAtrribute);
		
		return this;
	}
	
	@Override
	public CtClass build() {
        return ctclass;
	}

}
