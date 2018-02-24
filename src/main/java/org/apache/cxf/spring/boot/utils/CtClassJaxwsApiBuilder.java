package org.apache.cxf.spring.boot.utils;

import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebService;

import org.apache.commons.lang3.builder.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
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
 */
public class CtClassJaxwsApiBuilder implements Builder<CtClass> {

	private static final ClassPool pool = ClassPool.getDefault();
	// 构建动态类
	private CtClass ctclass  = null;
	private ClassFile ccFile = null;
	//private Loader loader = new Loader(pool);
	
	public CtClassJaxwsApiBuilder(final String classname) {
		
		this.ctclass = pool.getOrNull(classname);
		if( null == this.ctclass) {
			this.ctclass = pool.makeClass(classname);
		}
		// 当 ClassPool.doPruning=true的时候，Javassist 在CtClass object被冻结时，会释放存储在ClassPool对应的数据。这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		this.ctclass.stopPruning(true);
		this.ccFile = this.ctclass.getClassFile();
        
	}
	
	public CtClassJaxwsApiBuilder(final ClassPath classPath, final String classname) {
		//从ClassPath中加载
		pool.insertClassPath(classPath);
		this.ctclass = pool.getOrNull(classname);
		if( null == this.ctclass) {
			this.ctclass = pool.makeClass(classname);
		}
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
	public CtClassJaxwsApiBuilder annotationForType(final String name, final String targetNamespace) {
		return this.annotationForType(targetNamespace, targetNamespace, null, null, null, null);
	}
	
	public CtClassJaxwsApiBuilder annotationForType(final String name, final String targetNamespace, String serviceName) {
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
	public CtClassJaxwsApiBuilder annotationForType(final String name, final String targetNamespace, String serviceName,
			String portName, String wsdlLocation, String endpointInterface) {

		ConstPool constPool = this.ccFile.getConstPool();
		
		/** 添加类注解(WebService) */
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
	
	public <T> CtClassJaxwsApiBuilder constantField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.ccFile.getConstPool();
		
		/** 添加属性字段 */
        CtField field = new CtField(pool.get(fieldClass.getName()), fieldName, ctclass);
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
	
	public <T> CtClassJaxwsApiBuilder autowiredField(final Class<T> fieldClass, final String fieldName) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.ccFile.getConstPool();
		
		/** 添加属性字段 */
        CtField field = new CtField(pool.get(fieldClass.getName()), fieldName, ctclass);
        field.setModifiers(Modifier.PRIVATE);

        /** 在属性上添加注解(Autowired) */
        FieldInfo fieldInfo = field.getFieldInfo();
        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation autowired = new Annotation(Autowired.class.getName(),constPool);
        fieldAttr.addAnnotation(autowired);
        fieldInfo.addAttribute(fieldAttr);
        
        //新增Field
        ctclass.addField(field);
		
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
	public <T> CtClassJaxwsApiBuilder makeMethod(final String src) throws CannotCompileException {
		//新增方法
		ctclass.addMethod(CtMethod.make(src, ctclass));
		return this;
	}
	
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
	 * 
	 * @description	：  注释表示作为一项 Web Service 操作的方法，将此注释应用于客户机或服务器服务端点接口（SEI）上的方法，或者应用于 JavaBeans 端点的服务器端点实现类。<br/>
   	 *	要点： 仅支持在使用 @WebService 注释来注释的类上使用 @WebMethod 注释
	 * @param action：定义此操作的行为。对于 SOAP 绑定，此值将确定 SOAPAction 头的值。缺省值为 Java 方法的名称。（字符串）
	 * @param exclude：指定是否从 Web Service 中排除某一方法。缺省值为 false。（布尔值）  
	 * @param methodName：指定与此方法相匹配的wsdl:operation 的名称。缺省值为 Java 方法的名称。（字符串）
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public <T> CtClassJaxwsApiBuilder proxyMethod(final Class<T> rtClass, final String action, final boolean exclude, final String methodName, CtWebParam<?>... params) throws CannotCompileException {
        
		ConstPool constPool = this.ccFile.getConstPool();
		
		// 有参方法
		if(params != null && params.length > 0) {
			
			 // 参数
			CtClass[] paramTypes = new CtClass[params.length + 1];
			for(int i = 0;i < paramTypes.length; i++) {
				paramTypes[i] = pool.get(String.class.getName());
			}
			
			CtMethod m = new CtMethod(pool.get(rtClass.getName()), methodName, paramTypes, ctclass);
			m.setModifiers(Modifier.PUBLIC); 
			
			// 构造方法体
			StringBuilder body = new StringBuilder(); 
	        body.append("{");
		        body.append("String[] arr = paramNames.split(\",\");");
		        body.append("Object res = null;");
		        body.append("Exception ex = null;");
		        body.append("long startTime = System.currentTimeMillis();");
		        body.append("try{");
			        body.append("if(!wsHandler.before(deployId,\"Axis\",arr,$args)){return null;}");
			        	body.append("res = wsHandler.exec(deployId,\"Axis\",arr,$args);");
			        body.append("}catch(Exception e){");
				        body.append("ex = e;");
				        body.append("throw e;");
			    body.append("} finally {");
		        	body.append("wsHandler.after(deployId,\"Axis\",arr,$args,res,ex,startTime);");
		        body.append("}");
	        body.append("return ($r) res;");
	        body.append("}");
	        m.setBody(body.toString());
	        
	        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
	        m.addCatch("{ System.out.println($e); throw $e; }", etype);
	        
	        /**Axis方法参数注解*/
	        ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
	        Annotation[][] paramArrays = new Annotation[params.length + 1][1];
	        EnumMemberValue mode = new EnumMemberValue(constPool);
	        mode.setType(Mode.class.getName());
	        mode.setValue(Mode.IN.name());
	        
	        
	        Annotation paramAnnot = new Annotation(WebParam.class.getName(), constPool);
	        paramAnnot.addMemberValue("name", new StringMemberValue("accessToken", constPool));
	        paramAnnot.addMemberValue("targetNamespace", new StringMemberValue(params[i].getTargetNamespace(), constPool));
	        paramAnnot.addMemberValue("header", new BooleanMemberValue(true, constPool));
	        paramArrays[params.length][0] = paramAnnot;
	        
	        for(int i = 0;i < params.length; i++) {
	        	paramAnnot = new Annotation(WebParam.class.getName(), constPool);
	            paramAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
	            paramAnnot.addMemberValue("targetNamespace", new StringMemberValue(params[i].getTargetNamespace(), constPool));
	            paramAnnot.addMemberValue("mode", mode);
	            paramArrays[i][0] = paramAnnot;
	        }
	        
	        parameterAtrribute.setAnnotations(paramArrays);
	        m.getMethodInfo().addAttribute(parameterAtrribute);
	        
	        //新增方法
	        ctclass.addMethod(m);
			
		} 
		/**无参方法 */
		else {
			
			CtMethod m = new CtMethod(pool.get(rtClass.getName()), methodName , null, ctclass);
			m.setModifiers(Modifier.PUBLIC); 
			
			m.
			
			StringBuilder body = new StringBuilder(); 
	        body.append("{");
	        body.append("Object res = null;");
	        body.append("Exception ex = null;");
	        body.append("long startTime = System.currentTimeMillis();");
	        body.append("try{");
	        body.append("if(!wsHandler.before($args)){return null;}");
	        
	        body.append("if(!wsHandler.before(arr,$args)){return null;}");
	        
	        
	        body.append("res = wsHandler.exec(deployId,\"Axis\",arr,$args);");
	        body.append("}catch(Exception e){");
	        body.append("ex = e;");
	        body.append("throw e;");
	        body.append("} finally{");
	        body.append("wsHandler.after(deployId,\"Axis\",arr,$args,res,startTime);");
	        body.append("}");
	        body.append("return res;");
	        body.append("}");
	        m.setBody(body.toString());
	        
	        //新增方法
	        ctclass.addMethod(m);
		}
        
        return this;
	}
	
	
	@Override
	public CtClass build() {
        return ctclass;
	}

}