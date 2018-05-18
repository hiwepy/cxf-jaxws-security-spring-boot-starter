package org.apache.cxf.spring.boot.jaxws.endpoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.lang3.builder.Builder;
import org.apache.cxf.spring.boot.jaxws.annotation.WebBound;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapBound;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapMethod;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapParam;
import org.apache.cxf.spring.boot.jaxws.endpoint.ctweb.SoapResult;
import org.springframework.util.StringUtils;

import com.github.vindell.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * 
 * 动态构建ws接口
 * <p>http://www.cnblogs.com/sunfie/p/5154246.html</p>
 * <p>http://blog.csdn.net/youaremoon/article/details/50766972</p>
 * <p>https://blog.csdn.net/tscyds/article/details/78415172</p>
 * <p>https://my.oschina.net/GameKing/blog/794580</p>
 * <p>http://wsmajunfeng.iteye.com/blog/1912983</p>
 */
public class EndpointApiInterfaceCtClassBuilder implements Builder<CtClass> {
	
	// 构建动态类
	private ClassPool pool = null;
	private CtClass ctclass  = null;
	private ClassFile ccFile = null;
	//private Loader loader = new Loader(pool);
	
	public EndpointApiInterfaceCtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		this(JavassistUtils.getDefaultPool(), classname);
	}
	
	public EndpointApiInterfaceCtClassBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		
		this.pool = pool;
		this.ctclass = this.pool.getOrNull(classname);
		if( null == this.ctclass) {
			this.ctclass = this.pool.makeInterface(classname);
		}
		
		/* 指定 Cloneable 作为动态接口的父类 */
		CtClass superclass = pool.get(Cloneable.class.getName());
		ctclass.setSuperclass(superclass);
		
		// 当 ClassPool.doPruning=true的时候，Javassist 在CtClass object被冻结时，会释放存储在ClassPool对应的数据。这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		this.ctclass.stopPruning(true);
		this.ccFile = this.ctclass.getClassFile();
	}
	
	/**
	 * @description ： 给动态类添加 @WebService 注解
	 * @param name： 此属性的值包含XML Web Service的名称。在默认情况下，该值是实现XML Web Service的类的名称，wsdl:portType 的名称。缺省值为 Java 类或接口的非限定名称。（字符串）
	 * @param targetNamespace：指定你想要的名称空间，默认是使用接口实现类的包名的反缀（字符串）
	 * @return
	 */
	public EndpointApiInterfaceCtClassBuilder annotationForType(final String name, final String targetNamespace) {
		return this.annotationForType(targetNamespace, targetNamespace, null, null, null, null);
	}
	
	public EndpointApiInterfaceCtClassBuilder annotationForType(final String name, final String targetNamespace, String serviceName) {
		return this.annotationForType(targetNamespace, targetNamespace, serviceName, null, null, null);
	}
	
	/**
	 * @description ： 给动态类添加 @WebService 注解
	 * @param name： 此属性的值包含XML Web Service的名称。在默认情况下，该值是实现XML Web Service的类的名称，wsdl:portType 的名称。缺省值为 Java 类或接口的非限定名称。（字符串）
	 * @param targetNamespace：指定你想要的名称空间，默认是使用接口实现类的包名的反缀（字符串）
	 * @param serviceName： 对外发布的服务名，指定 Web Service 的服务名称：wsdl:service。缺省值为 Java 类的简单名称 + Service。（字符串）
	 * @param portName：  wsdl:portName。缺省值为 WebService.name+Port。（字符串）
	 * @param wsdlLocation：指定用于定义 Web Service 的 WSDL 文档的 Web 地址。Web 地址可以是相对路径或绝对路径。（字符串）
	 * @param endpointInterface： 服务接口全路径, 指定做SEI（Service EndPoint Interface）服务端点接口（字符串）
	 * @return
	 */
	public EndpointApiInterfaceCtClassBuilder annotationForType(final String name, final String targetNamespace, String serviceName,
			String portName, String wsdlLocation, String endpointInterface) {

		ConstPool constPool = this.ccFile.getConstPool();
		
		// 添加类注解 @WebService 
		AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation ws = new Annotation(WebService.class.getName(), constPool);
		ws.addMemberValue("name", new StringMemberValue(name, constPool));
		ws.addMemberValue("targetNamespace", new StringMemberValue(targetNamespace, constPool));
		if (StringUtils.hasText(serviceName)) {
			ws.addMemberValue("serviceName", new StringMemberValue(serviceName, constPool));
		}
		if (StringUtils.hasText(portName)) {
			ws.addMemberValue("portName", new StringMemberValue(portName, constPool));
		}
		if (StringUtils.hasText(wsdlLocation)) {
			ws.addMemberValue("wsdlLocation", new StringMemberValue(wsdlLocation, constPool));
		}
		if (StringUtils.hasText(endpointInterface)) {
			ws.addMemberValue("endpointInterface", new StringMemberValue(endpointInterface, constPool));
		}
		classAttr.addAnnotation(ws);
		ccFile.addAttribute(classAttr);
		
		return this;
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param uid
	 * @param json
	 * @return
	 */
	public EndpointApiInterfaceCtClassBuilder bindDataForType(final String uid, final String json) {

		ConstPool constPool = this.ccFile.getConstPool();
		// 添加类注解 @WebBound
		AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation bound = new Annotation(WebBound.class.getName(), constPool);
		bound.addMemberValue("uid", new StringMemberValue(uid, constPool));
		bound.addMemberValue("json", new StringMemberValue(json, constPool));
		classAttr.addAnnotation(bound);
		ccFile.addAttribute(classAttr);
		
		return this;
	}
	
	/**
     * Compiles the given source code and creates a field.
     * Examples of the source code are:
     * 
     * <pre>
     * "public String name;"
     * "public int k = 3;"</pre>
     *
     * <p>Note that the source code ends with <code>';'</code>
     * (semicolon).
     *
     * @param src               the source text.
     */
	public <T> EndpointApiInterfaceCtClassBuilder makeField(final String src) throws CannotCompileException {
		//创建属性
        ctclass.addField(CtField.make(src, ctclass));
		return this;
	}
	
	public <T> EndpointApiInterfaceCtClassBuilder newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		
		// 检查字段是否已经定义
		if(JavassistUtils.hasField(ctclass, fieldName)) {
			return this;
		}
		
		/** 添加属性字段 */
		CtField field = new CtField(this.pool.get(fieldClass.getName()), fieldName, ctclass);
        field.setModifiers(Modifier.PRIVATE);

        //新增Field
        ctclass.addField(field, "\"" + fieldValue + "\"");
        
		return this;
	}
	
	public <T> EndpointApiInterfaceCtClassBuilder removeField(final String fieldName) throws NotFoundException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(ctclass, fieldName)) {
			return this;
		}
		
		ctclass.removeField(ctclass.getDeclaredField(fieldName));
		
		return this;
	}
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param rtClass 		：方法返回类型
	 * @param methodName 	：方法名称
	 * @param params		： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */
	public <T> EndpointApiInterfaceCtClassBuilder abstractMethod(final Class<T> rtClass, final String methodName, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.abstractMethod(new SoapResult<T>(rtClass), new SoapMethod(methodName), null, params);
	}
	
	/**
	 * 
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param rtClass 		：方法返回类型
	 * @param methodName 	：方法名称
	 * @param bound			：方法绑定数据信息
	 * @param params		： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public <T> EndpointApiInterfaceCtClassBuilder abstractMethod(final Class<T> rtClass, final String methodName, final SoapBound bound, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.abstractMethod(new SoapResult<T>(rtClass), new SoapMethod(methodName), bound, params);
	}
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param result ：返回结果信息
	 * @param method ：方法注释信息
	 * @param bound  ：方法绑定数据信息
	 * @param params ： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */ 
	public <T> EndpointApiInterfaceCtClassBuilder abstractMethod(final SoapResult<T> result, final SoapMethod method, final SoapBound bound, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.ccFile.getConstPool();
		
		// 创建抽象方法
		CtClass returnType = pool.get(result.getRtClass().getName());
		CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
		CtMethod ctMethod = null;
		// 参数模式定义
		Map<String, EnumMemberValue> modeMap = new HashMap<String, EnumMemberValue>();
		// 有参方法
		if(params != null && params.length > 0) {
			
			// 方法参数
			CtClass[] paramTypes = new CtClass[params.length];
			for(int i = 0;i < params.length; i++) {
				paramTypes[i] = this.pool.get(params[i].getType().getName());
				if(!modeMap.containsKey(params[i].getMode().name())) {
					
					EnumMemberValue modeEnum = new EnumMemberValue(constPool);
			        modeEnum.setType(Mode.class.getName());
			        modeEnum.setValue(params[i].getMode().name());
					
					modeMap.put(params[i].getMode().name(), modeEnum);
				}
			}
			
			// 构造抽象方法
			ctMethod = CtNewMethod.abstractMethod(returnType, method.getOperationName(), paramTypes , exceptions, ctclass);
			
		} 
		/**无参方法 */
		else {
			
			// 构造抽象方法
			ctMethod = CtNewMethod.abstractMethod(returnType, method.getOperationName(), null , exceptions, ctclass);
			
		}
		
        // 添加方法注解
        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
       
        // 添加 @WebMethod 注解	        
        Annotation methodAnnot = new Annotation(WebMethod.class.getName(), constPool);
        methodAnnot.addMemberValue("operationName", new StringMemberValue(method.getOperationName(), constPool));
        if (StringUtils.hasText(method.getAction())) {
        	methodAnnot.addMemberValue("action", new StringMemberValue(method.getAction(), constPool));
        }
        methodAnnot.addMemberValue("exclude", new BooleanMemberValue(method.isExclude(), constPool));
        
        methodAttr.addAnnotation(methodAnnot);
        
        // 添加 @WebResult 注解
        if (StringUtils.hasText(result.getName())) {
        	
        	Annotation resultAnnot = new Annotation(WebResult.class.getName(), constPool);
	        resultAnnot.addMemberValue("name", new StringMemberValue(result.getName(), constPool));
	        if (StringUtils.hasText(result.getPartName())) {
	        	resultAnnot.addMemberValue("partName", new StringMemberValue(result.getPartName(), constPool));
	        }
	        if (StringUtils.hasText(result.getTargetNamespace())) {
	        	resultAnnot.addMemberValue("targetNamespace", new StringMemberValue(result.getTargetNamespace(), constPool));
	        }
	        resultAnnot.addMemberValue("header", new BooleanMemberValue(result.isHeader(), constPool));
	        
	        methodAttr.addAnnotation(resultAnnot);
	        
        }
        
        // 添加 @WebBound 注解
        if (bound != null) {
        	
        	Annotation resultBound = new Annotation(WebBound.class.getName(), constPool);
	        resultBound.addMemberValue("uid", new StringMemberValue(bound.getUid(), constPool));
	        if (StringUtils.hasText(bound.getJson())) {
	        	resultBound.addMemberValue("json", new StringMemberValue(bound.getJson(), constPool));
	        }
	        methodAttr.addAnnotation(resultBound);
	        
        }
        
        
        ctMethod.getMethodInfo().addAttribute(methodAttr);
        
        // 添加 @WebParam 参数注解
        if(params != null && params.length > 0) {
        	
        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = new Annotation[params.length][1];
            
            Annotation paramAnnot = null;
            for(int i = 0;i < params.length; i++) {
            	
            	paramAnnot = new Annotation(WebParam.class.getName(), constPool);
                paramAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
                if (StringUtils.hasText(params[i].getPartName())) {
                	paramAnnot.addMemberValue("partName", new StringMemberValue(params[i].getPartName(), constPool));
        		}
                paramAnnot.addMemberValue("targetNamespace", new StringMemberValue(params[i].getTargetNamespace(), constPool));
                paramAnnot.addMemberValue("mode", modeMap.get(params[i].getMode().name()));
                if(params[i].isHeader()) {
                	 paramAnnot.addMemberValue("header", new BooleanMemberValue(true, constPool));
                }
                
                paramArrays[i][0] = paramAnnot;
                
            }
            
            parameterAtrribute.setAnnotations(paramArrays);
            ctMethod.getMethodInfo().addAttribute(parameterAtrribute);
            
        }
        
        //新增方法
        ctclass.addMethod(ctMethod);
        
        return this;
	}
	
	public <T> EndpointApiInterfaceCtClassBuilder removeMethod(final String methodName, SoapParam<?>... params) throws NotFoundException {
		
		// 有参方法
		if(params != null && params.length > 0) {
			
			// 方法参数
			CtClass[] paramTypes = new CtClass[params.length];
			for(int i = 0;i < params.length; i++) {
				paramTypes[i] = this.pool.get(params[i].getType().getName());
			}
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(ctclass, methodName, paramTypes)) {
				return this;
			}
			
			ctclass.removeMethod(ctclass.getDeclaredMethod(methodName, paramTypes));
			
		}
		else {
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(ctclass, methodName)) {
				return this;
			}
			
			ctclass.removeMethod(ctclass.getDeclaredMethod(methodName));
			
		}
		
		return this;
	}
	
	@Override
	public CtClass build() {
        return ctclass;
	}
	
	/**
	 * 
	 * javassist在加载类时会用Hashtable将类信息缓存到内存中，这样随着类的加载，内存会越来越大，甚至导致内存溢出。如果应用中要加载的类比较多，建议在使用完CtClass之后删除缓存
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @return
	 * @throws CannotCompileException
	 */
	public Class<?> toClass() throws CannotCompileException {
        try {
        	// 通过类加载器加载该CtClass
			return ctclass.toClass();
		} finally {
			// 将该class从ClassPool中删除
			ctclass.detach();
		} 
	}
	
	@SuppressWarnings("unchecked")
	public Object toInstance(final InvocationHandler handler) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        try {
        	// 添加有参构造器，注入回调接口
			CtConstructor cc = new CtConstructor(new CtClass[] { pool.get(InvocationHandler.class.getName()) }, ctclass);
			cc.setBody("{super($1);}");
			ctclass.addConstructor(cc);
			// proxy.writeFile();
			// 通过类加载器加载该CtClass，并通过构造器初始化对象
			return ctclass.toClass().getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
			// 将该class从ClassPool中删除
			ctclass.detach();
		} 
	}

}