package org.apache.cxf.spring.boot.endpoint;

import java.io.Serializable;

/**
 * http://www.cnblogs.com/wanggd/archive/2013/04/19/3030480.html
 */
@SuppressWarnings("serial")
public class APIEndpoint implements Serializable {

	// javax.jws.WebService

	/**
	 * Web Service 的名称。映射到 WSDL 文件中的 <wsdl:portType> 元素。默认值为 JWS 文件中 Java 类的非限定名称。 
	 */
	private String name;

	/**
	 * 用于从此 Web Service 生成的 WSDL 和 XML 元素的 XML 名称空间。默认值由 JAX-RPC specification 指定。 
	 */
	private String targetNamespace;

	/**
	 * Web Service 的服务名。映射到 WSDL 文件中的 <wsdl:service> 元素。默认值为 JWS 文件中 Java 类的非限定名称，后面加上字符串 Service。 
	 */
	private String serviceName;

	private String portName;

	/**
	 * 预定义 WSDL 文件的相对或绝对 URL。如果指定此特性，则当 JWS 文件与 WSDL 文件中的端口类型和绑定不一致时，jwsc Ant 任务不生成 WSDL 文件，并且会返回错误。
	 * 注意：wsdlc Ant 任务从 WSDL 生成端点接口 JWS 文件时使用此特性。通常情况下，用户在其自己的 JWS 文件中永远不会使用该特性。
	 */
	private String wsdlLocation;
	
	/**
	 * 现有服务端点接口文件的完全限定名称。如果指定此特性，则假设您已经创建了该端点接口文件，并且该文件位于 CLASSPATH 中。 
	 */
	private String endpointInterface;

	/**
	 * Web Service 服务对外暴露地址
	 */
	private String addr;

	// javax.jws.WebMethod

	private APIEndpointMethod[] methods;
	
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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	public void setWsdlLocation(String wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}

	public String getEndpointInterface() {
		return endpointInterface;
	}

	public void setEndpointInterface(String endpointInterface) {
		this.endpointInterface = endpointInterface;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public APIEndpointMethod[] getMethods() {
		return methods;
	}

	public void setMethods(APIEndpointMethod... methods) {
		this.methods = methods;
	}
	
}
