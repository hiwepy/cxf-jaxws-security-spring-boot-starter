package org.apache.cxf.spring.boot.jaxws;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.lang3.builder.Builder;
import org.apache.cxf.spring.boot.utils.JavassistUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * 
 * @className	： JaxwsApiCtClassBuilder
 * @description	：动态构建ws接口
 * @see http://www.cnblogs.com/sunfie/p/5154246.html
 * @see http://blog.csdn.net/youaremoon/article/details/50766972
 * @see https://my.oschina.net/GameKing/blog/794580
 * @see http://wsmajunfeng.iteye.com/blog/1912983
 */
public class JaxwsApiCtClassBuilderBak implements Builder<CtClass> {
	
	private static final String PROXY_HANDLER = "proxyHandler";
	
	// 构建动态类
	private ClassPool pool = null;
	private CtClass ctclass  = null;
	private ClassFile ccFile = null;
	//private Loader loader = new Loader(pool);
	
	public JaxwsApiCtClassBuilderBak(final String classname) throws CannotCompileException, NotFoundException  {
		this(ClassPool.getDefault(), classname);
	}
	
	public JaxwsApiCtClassBuilderBak(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		
		this.pool = pool;
		this.ctclass = this.pool.getOrNull(classname);
		if( null == this.ctclass) {
			this.ctclass = this.pool.makeClass(classname);
		}
		
		/* 获得 JaxwsHandler 类作为动态类的父类 */
		CtClass superclass = pool.get(JaxwsApiHandler.class.getName());
		ctclass.setSuperclass(superclass);
		
		// 默认添加无参构造器  
		CtConstructor cons = new CtConstructor(null, ctclass);  
		cons.setBody("{}");  
		ctclass.addConstructor(cons);
		
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
	public JaxwsApiCtClassBuilderBak annotationForType(final String name, final String targetNamespace) {
		return this.annotationForType(targetNamespace, targetNamespace, null, null, null, null);
	}
	
	public JaxwsApiCtClassBuilderBak annotationForType(final String name, final String targetNamespace, String serviceName) {
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
	public JaxwsApiCtClassBuilderBak annotationForType(final String name, final String targetNamespace, String serviceName,
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
	public <T> JaxwsApiCtClassBuilderBak makeField(final String src) throws CannotCompileException {
		//创建属性
        ctclass.addField(CtField.make(src, ctclass));
		return this;
	}
	
	public <T> JaxwsApiCtClassBuilderBak newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		
		// 检查字段是否已经定义
		if(JavassistUtils.hasField(ctclass, fieldName)) {
			return this;
		}
		
		/** 添加属性字段 */
		CtField field = new CtField(this.pool.get(fieldClass.getName()), fieldName, ctclass);
        field.setModifiers(Modifier.PRIVATE);

        //新增Field
        ctclass.addField(field, fieldValue);
        
		return this;
	}
	
	public <T> JaxwsApiCtClassBuilderBak newFieldWithValue(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		
		// 检查字段是否已经定义
		if(JavassistUtils.hasField(ctclass, fieldName)) {
			return this;
		}
		
		ConstPool constPool = this.ccFile.getConstPool();
		
		/** 添加属性字段 */
		CtField field = new CtField(this.pool.get(fieldClass.getName()), fieldName, ctclass);
        field.setModifiers(Modifier.PRIVATE);

        /** 在属性上添加注解(Value) */
        FieldInfo fieldInfo = field.getFieldInfo();
        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation value = new Annotation(Value.class.getName(),constPool);
        value.addMemberValue("value", new StringMemberValue(fieldValue, constPool));
        fieldAttr.addAnnotation(value);
        fieldInfo.addAttribute(fieldAttr);
		
        //新增Field
        ctclass.addField(field);
        
		return this;
	}
	
	public <T> JaxwsApiCtClassBuilderBak newFieldWithAutowired(final Class<T> fieldClass, final String fieldName) throws CannotCompileException, NotFoundException  {
		
		// 检查字段是否已经定义
		if(JavassistUtils.hasField(ctclass, fieldName)) {
			return this;
		}
		
		ConstPool constPool = this.ccFile.getConstPool();
		
		/** 添加属性字段 */
		CtField field = new CtField(this.pool.get(fieldClass.getName()), fieldName, ctclass);
        field.setModifiers(Modifier.PRIVATE);

        /** 在属性上添加注解(Autowired) */
        FieldInfo fieldInfo = field.getFieldInfo();
        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation autowired = new Annotation(Autowired.class.getName(), constPool);
        fieldAttr.addAnnotation(autowired);
        fieldInfo.addAttribute(fieldAttr);
        
        //新增Field
        ctclass.addField(field);
		
		return this;
	}
	
	public <T> JaxwsApiCtClassBuilderBak removeField(final String fieldName) throws NotFoundException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(ctclass, fieldName)) {
			return this;
		}
		
		ctclass.removeField(ctclass.getDeclaredField(fieldName));
		
		return this;
	}
	
	
	/**
     * Compiles the given source code and creates a method.
     * The source code must include not only the method body
     * but the whole declaration, for example,
     *
     * <pre>"public Object id(Object obj) { return obj; }"</pre>
     *
     * @param src               the source text. 
     */
	public <T> JaxwsApiCtClassBuilderBak makeMethod(final String src) throws CannotCompileException {
		//创建方法 
		ctclass.addMethod(CtMethod.make(src, ctclass));
		return this;
	}
	
	/**
	 * @className	： CtWebMethod
	 * @description	： 注释表示作为一项 Web Service 操作的方法，将此注释应用于客户机或服务器服务端点接口（SEI）上的方法，或者应用于 JavaBeans 端点的服务器端点实现类。
	 * 要点： 仅支持在使用 @WebService 注释来注释的类上使用 @WebMethod 注释
	 * https://www.cnblogs.com/zhao-shan/p/5515174.html
	 */
	public static class CtWebMethod {
	    
	    public CtWebMethod() {
		}
	    
	    public CtWebMethod(String operationName) {
			this.operationName = operationName;
		}
	    
		public CtWebMethod(String operationName, String action, boolean exclude) {
			this.operationName = operationName;
			this.action = action;
			this.exclude = exclude;
		}

		/**
		 * 1、operationName：指定与此方法相匹配的wsdl:operation 的名称。缺省值为 Java 方法的名称。（字符串）
		 */
		private String operationName = "";

		/**
		 * 2、action：定义此操作的行为。对于 SOAP 绑定，此值将确定 SOAPAction 头的值。缺省值为 Java 方法的名称。（字符串）
		 */
		private String action = "";

		/**
		 * 3、exclude：指定是否从 Web Service 中排除某一方法。缺省值为 false。（布尔值）  
		 */
		private boolean exclude = false;

		public String getOperationName() {
			return operationName;
		}

		public void setOperationName(String operationName) {
			this.operationName = operationName;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public boolean isExclude() {
			return exclude;
		}

		public void setExclude(boolean exclude) {
			this.exclude = exclude;
		}

	}
	
	
	/**
	 * @className	： CtWebParam
	 * @description	： 注释用于定制从单个参数至 Web Service 消息部件和 XML 元素的映射。将此注释应用于客户机或服务器服务端点接口（SEI）上的方法，或者应用于 JavaBeans 端点的服务器端点实现类。
	 * https://www.cnblogs.com/zhao-shan/p/5515174.html
	 */
	public static class CtWebParam<T> {

		/**
		 * 参数对象类型
		 */
		private Class<T> type;
		/**
		 * 1、name ：参数的名称。如果操作是远程过程调用（RPC）类型并且未指定partName 属性，那么这是用于表示参数的 wsdl:part 属性的名称。
		 * 如果操作是文档类型或者参数映射至某个头，那么 -name 是用于表示该参数的 XML 元素的局部名称。如果操作是文档类型、 参数类型为 BARE
		 * 并且方式为 OUT 或 INOUT，那么必须指定此属性。（字符串）
		 */
		private String name = "";
		/**
		 * 2、partName：定义用于表示此参数的 wsdl:part属性的名称。仅当操作类型为 RPC 或者操作是文档类型并且参数类型为BARE
		 * 时才使用此参数。（字符串）
		 */
		private String partName = "";
		/**
		 * 3、targetNamespace：指定参数的 XML 元素的 XML 名称空间。当属性映射至 XML 元素时，仅应用于文档绑定。缺省值为 Web
		 * Service 的 targetNamespace。（字符串）
		 */
		private String targetNamespace = "";
		/**
		 * 4、mode：此值表示此方法的参数流的方向。有效值为 IN、INOUT 和 OUT。（字符串）
		 */
		private javax.jws.WebParam.Mode mode = javax.jws.WebParam.Mode.IN;
		/**
		 * 5、header：指定参数是在消息头还是消息体中。缺省值为 false。（布尔值）
		 */
		private boolean header = false;

		public Class<T> getType() {
			return type;
		}

		public void setType(Class<T> type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPartName() {
			return partName;
		}

		public void setPartName(String partName) {
			this.partName = partName;
		}

		public String getTargetNamespace() {
			return targetNamespace;
		}

		public void setTargetNamespace(String targetNamespace) {
			this.targetNamespace = targetNamespace;
		}

		public javax.jws.WebParam.Mode getMode() {
			return mode;
		}

		public void setMode(javax.jws.WebParam.Mode mode) {
			this.mode = mode;
		}

		public boolean isHeader() {
			return header;
		}

		public void setHeader(boolean header) {
			this.header = header;
		}
		
	}
	
	/**
	 * @className	： CtWebResult
	 * @description	： 注释用于定制从返回值至 WSDL 部件或 XML 元素的映射。将此注释应用于客户机或服务器服务端点接口（SEI）上的方法，或者应用于 JavaBeans 端点的服务器端点实现类。
	 * https://www.cnblogs.com/zhao-shan/p/5515174.html
	 */
	public static class CtWebResult<T> {

		public CtWebResult(Class<T> rtClass) {
			this.rtClass = rtClass;
		}
		
		public CtWebResult(Class<T> rtClass, String name, String targetNamespace, boolean header, String partName) {
			this.rtClass = rtClass;
			this.name = name;
			this.targetNamespace = targetNamespace;
			this.header = header;
			this.partName = partName;
		}

		/**
		 * 返回结果对象类型
		 */
		private Class<T> rtClass;
		
		/**
		 * 1、name：当返回值列示在 WSDL 文件中并且在连接上的消息中找到该返回值时，指定该返回值的名称。对于 RPC 绑定，这是用于表示返回值的
		 * wsdl:part属性的名称。对于文档绑定，-name 参数是用于表示返回值的 XML 元素的局部名。对于 RPC 和 DOCUMENT/WRAPPED
		 * 绑定，缺省值为 return。对于 DOCUMENT/BARE 绑定，缺省值为方法名 + Response。（字符串）
		 */
		private String name = "";

		/**
		 * 2、targetNamespace：指定返回值的 XML 名称空间。仅当操作类型为 RPC 或者操作是文档类型并且参数类型为 BARE
		 * 时才使用此参数。（字符串）
		 */
		private String targetNamespace = "";

		/**
		 * 3、header：指定头中是否附带结果。缺省值为false。（布尔值）
		 */
		private boolean header = false;
		/**
		 * 4、partName：指定 RPC 或 DOCUMENT/BARE 操作的结果的部件名称。缺省值为@WebResult.name。（字符串）
		 */
		private String partName = "";

		public Class<T> getRtClass() {
			return rtClass;
		}

		public void setRtClass(Class<T> rtClass) {
			this.rtClass = rtClass;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTargetNamespace() {
			return targetNamespace;
		}

		public void setTargetNamespace(String targetNamespace) {
			this.targetNamespace = targetNamespace;
		}

		public boolean isHeader() {
			return header;
		}

		public void setHeader(boolean header) {
			this.header = header;
		}

		public String getPartName() {
			return partName;
		}

		public void setPartName(String partName) {
			this.partName = partName;
		}

	}
	
	/**
	 * 
	 * @description			： 根据参数构造一个新的方法
	 * @param rtClass 		：方法返回类型
	 * @param methodName 	：方法名称
	 * @param params		： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */
	public <T> JaxwsApiCtClassBuilderBak newMethod(final Class<T> rtClass, final String methodName, CtWebParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(new CtWebResult<T>(rtClass), new CtWebMethod(methodName), params);
	}
	
	/**
	 * 
	 * @description	： 根据参数构造一个新的方法
	 * @param method ：方法注释信息
	 * @param result ：返回结果信息
	 * @param params ： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */
	public <T> JaxwsApiCtClassBuilderBak newMethod(final CtWebResult<T> result, final CtWebMethod method, CtWebParam<?>... params) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.ccFile.getConstPool();
		
		// 自动注入对象proxyHandler
		this.newFieldWithAutowired(JaxwsApiHandler.class, PROXY_HANDLER);
		
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
			
			// 构造方法
			ctMethod = new CtMethod(this.pool.get(result.getRtClass().getName()), method.getOperationName(), paramTypes, ctclass);
			ctMethod.setModifiers(Modifier.PUBLIC); 
			
		} 
		/**无参方法 */
		else {
			
			ctMethod = new CtMethod(pool.get(result.getRtClass().getName()), method.getOperationName() , null, ctclass);
			ctMethod.setModifiers(Modifier.PUBLIC);
			
		}
		
		// 构造方法体
		StringBuilder body = new StringBuilder(); 
        body.append("{\n");
        	body.append("if(handler != null){return ($r)handler.invoke($0, Method method, $args);}\n");
	        body.append("return ($r) proxyHandler.doHandler(uid, $$);\n");
        body.append("}"); 
        // 将方法的内容设置为要写入的代码，当方法被 abstract修饰时，该修饰符被移除。
        ctMethod.setBody(body.toString());
        
        // 构造异常处理逻辑
        CtClass etype = pool.get("java.lang.Exception");
        ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);
        
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
        
        ctMethod.getMethodInfo().addAttribute(methodAttr);
        
        // 添加参数注解
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
	
	public <T> JaxwsApiCtClassBuilderBak removeMethod(final String methodName, CtWebParam<?>... params) throws NotFoundException {
		
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
	 * @description	： javassist在加载类时会用Hashtable将类信息缓存到内存中，这样随着类的加载，内存会越来越大，甚至导致内存溢出。如果应用中要加载的类比较多，建议在使用完CtClass之后删除缓存
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
	public Object newInstance(final InvocationHandler handler) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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