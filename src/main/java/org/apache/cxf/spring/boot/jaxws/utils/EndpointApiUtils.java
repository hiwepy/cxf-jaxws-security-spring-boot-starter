/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package org.apache.cxf.spring.boot.jaxws.utils;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.cxf.spring.boot.jaxws.annotation.WebBound;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapBound;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapMethod;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapParam;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapResult;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapService;
import org.springframework.util.StringUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class EndpointApiUtils {

	public static CtClass makeClass(final ClassPool pool, final String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeClass(classname);
		}

		// 当 ClassPool.doPruning=true的时候，Javassist 在CtClass
		// object被冻结时，会释放存储在ClassPool对应的数据。这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		declaring.stopPruning(true);

		return declaring;
	}
	
	public static CtConstructor makeConstructor(final ClassPool pool, final CtClass declaring) throws NotFoundException, CannotCompileException  {

		// 添加有参构造器，注入回调接口
    	CtClass[] parameters = new CtClass[] {pool.get(InvocationHandler.class.getName())};
    	CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
    	return CtNewConstructor.make(parameters, exceptions, "{super($1);}", declaring);
    	
	}

	public static CtClass makeInterface(final ClassPool pool, final String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeInterface(classname);
		}

		// 当 ClassPool.doPruning=true的时候，Javassist 在CtClass
		// object被冻结时，会释放存储在ClassPool对应的数据。这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		declaring.stopPruning(true);

		return declaring;
	}
	

	public static <T> void setSuperclass(final ClassPool pool, final CtClass declaring, final Class<T> clazz)
			throws Exception {

		/* 获得 JaxwsHandler 类作为动态类的父类 */
		CtClass superclass = pool.get(clazz.getName());
		declaring.setSuperclass(superclass);

	}
	
	
	

	public static Map<String, EnumMemberValue> modeMap(final ConstPool constPool, SoapParam<?>... params) {
		// 参数模式定义
		Map<String, EnumMemberValue> modeMap = new HashMap<String, EnumMemberValue>();
		// 无参
		if(params == null || params.length == 0) {
			return modeMap;
		}
		// 方法参数
		for(int i = 0;i < params.length; i++) {
			if(!modeMap.containsKey(params[i].getMode().name())) {
				
				EnumMemberValue modeEnum = new EnumMemberValue(constPool);
		        modeEnum.setType(Mode.class.getName());
		        modeEnum.setValue(params[i].getMode().name());
				modeMap.put(params[i].getMode().name(), modeEnum);
				
			}
		}

		return modeMap;
	}
	public static CtClass[] makeParams(final ClassPool pool, SoapParam<?>... params) throws NotFoundException {
		// 无参
		if(params == null || params.length == 0) {
			return null;
		}
		// 方法参数
		CtClass[] parameters = new CtClass[params.length];
		for(int i = 0;i < params.length; i++) {
			parameters[i] = pool.get(params[i].getType().getName());
		}

		return parameters;
	}
	
	/**
	 * 构造 @WebService 注解
	 */
	public static Annotation annotWebService(final ConstPool constPool, final SoapService service) {

		Annotation annot = new Annotation(WebService.class.getName(), constPool);
		annot.addMemberValue("name", new StringMemberValue(service.getName(), constPool));
		annot.addMemberValue("targetNamespace", new StringMemberValue(service.getTargetNamespace(), constPool));
		if (StringUtils.hasText(service.getServiceName())) {
			annot.addMemberValue("serviceName", new StringMemberValue(service.getServiceName(), constPool));
		}
		if (StringUtils.hasText(service.getPortName())) {
			annot.addMemberValue("portName", new StringMemberValue(service.getPortName(), constPool));
		}
		if (StringUtils.hasText(service.getWsdlLocation())) {
			annot.addMemberValue("wsdlLocation", new StringMemberValue(service.getWsdlLocation(), constPool));
		}
		if (StringUtils.hasText(service.getEndpointInterface())) {
			annot.addMemberValue("endpointInterface", new StringMemberValue(service.getEndpointInterface(), constPool));
		}

		return annot;
	}
	
	/**
	 * 为方法添加 @WebMethod、 @WebResult、@WebBound、@WebParam 注解
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param ctMethod
	 * @param constPool
	 * @param result
	 * @param method
	 * @param bound
	 * @param params
	 */
	public static <T> void methodAnnotations(final CtMethod ctMethod, final ConstPool constPool, final SoapResult<T> result, final SoapMethod method, final SoapBound bound, SoapParam<?>... params) {
		
		// 添加方法注解
        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
       
        // 添加 @WebMethod 注解	        
        methodAttr.addAnnotation(EndpointApiUtils.annotWebMethod(constPool, method));
        
        // 添加 @WebResult 注解
        if (StringUtils.hasText(result.getName())) {
	        methodAttr.addAnnotation(EndpointApiUtils.annotWebResult(constPool, result));
        }
        
        // 添加 @WebBound 注解
        if (bound != null) {
	        methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }
        
        ctMethod.getMethodInfo().addAttribute(methodAttr);
        
        // 添加 @WebParam 参数注解
        if(params != null && params.length > 0) {
        	
        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotWebParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            ctMethod.getMethodInfo().addAttribute(parameterAtrribute);
            
        }
        
	}
	
	/**
	 * 设置方法体
	 * @throws CannotCompileException 
	 */
	public static void methodBody(final CtMethod ctMethod, final SoapMethod method) throws CannotCompileException {
		
		// 构造方法体
		StringBuilder body = new StringBuilder(); 
        body.append("{\n");
        	body.append("if(getHandler() != null){\n");
        		body.append("Method method = this.getClass().getDeclaredMethod(\"" + method.getOperationName() + "\", $sig);");
        		body.append("return ($r)getHandler().invoke($0, method, $args);");
        	body.append("}\n"); 
	        body.append("return null;\n");
        body.append("}"); 
        // 将方法的内容设置为要写入的代码，当方法被 abstract修饰时，该修饰符被移除。
        ctMethod.setBody(body.toString());
        
	}
	
	/**
	 * 设置方法异常捕获逻辑
	 * @throws NotFoundException 
	 * @throws CannotCompileException 
	 */
	public static void methodCatch(final ClassPool pool, final CtMethod ctMethod) throws NotFoundException, CannotCompileException {
		
		// 构造异常处理逻辑
        CtClass etype = pool.get("java.lang.Exception");
        ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);
        
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static Annotation annotWebBound(final ConstPool constPool, final SoapBound bound) {
		
		Annotation annot = new Annotation(WebBound.class.getName(), constPool);
		annot.addMemberValue("uid", new StringMemberValue(bound.getUid(), constPool));
        if (StringUtils.hasText(bound.getJson())) {
        	annot.addMemberValue("json", new StringMemberValue(bound.getJson(), constPool));
        }
        
		return annot;
	}
	
	/**
	 * 构造 @WebMethod 注解
	 */
	public static Annotation annotWebMethod(final ConstPool constPool, final SoapMethod method) {
		
		Annotation annot = new Annotation(WebMethod.class.getName(), constPool);
		annot.addMemberValue("operationName", new StringMemberValue(method.getOperationName(), constPool));
        if (StringUtils.hasText(method.getAction())) {
        	annot.addMemberValue("action", new StringMemberValue(method.getAction(), constPool));
        }
        annot.addMemberValue("exclude", new BooleanMemberValue(method.isExclude(), constPool));
        
		return annot;
	}
	
	/**
	 * 构造 @WebParam 参数注解
	 */
	public static <T> Annotation[][] annotWebParams(final ConstPool constPool, SoapParam<?>... params) {

		// 添加 @WebParam 参数注解
		if (params != null && params.length > 0) {

			// 参数模式定义
			Map<String, EnumMemberValue> modeMap = modeMap(constPool, params);
			
			Annotation[][] paramArrays = new Annotation[params.length][1];
			
			Annotation paramAnnot = null;
			for (int i = 0; i < params.length; i++) {

				paramAnnot = new Annotation(WebParam.class.getName(), constPool);
				paramAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
				if (StringUtils.hasText(params[i].getPartName())) {
					paramAnnot.addMemberValue("partName", new StringMemberValue(params[i].getPartName(), constPool));
				}
				paramAnnot.addMemberValue("targetNamespace",
						new StringMemberValue(params[i].getTargetNamespace(), constPool));
				paramAnnot.addMemberValue("mode", modeMap.get(params[i].getMode().name()));
				if (params[i].isHeader()) {
					paramAnnot.addMemberValue("header", new BooleanMemberValue(true, constPool));
				}

				paramArrays[i][0] = paramAnnot;

			}

			return paramArrays;

		}
		return null;
	}
	
	/**
	 * 构造 @WebResult 注解
	 */
	public static <T> Annotation annotWebResult(final ConstPool constPool, final SoapResult<T> result) {
		
		Annotation annot = new Annotation(WebResult.class.getName(), constPool);
        annot.addMemberValue("name", new StringMemberValue(result.getName(), constPool));
        if (StringUtils.hasText(result.getPartName())) {
        	annot.addMemberValue("partName", new StringMemberValue(result.getPartName(), constPool));
        }
        if (StringUtils.hasText(result.getTargetNamespace())) {
        	annot.addMemberValue("targetNamespace", new StringMemberValue(result.getTargetNamespace(), constPool));
        }
        annot.addMemberValue("header", new BooleanMemberValue(result.isHeader(), constPool));
        
		return annot;
	}
	
	
	public static void rm(CtClass declaring) {

	}

}
