package org.apache.cxf.spring.boot.endpoint;

import javax.jws.WebParam.Mode;

public class APIEndpointParam {

	// javax.jws.WebParam

	/**
	 * WSDL 文件中参数的名称。
	 * 对于 RPC 样式的 Web Service，该名称映射到表示该参数的 <wsdl:part> 元素。
	 * 对于文档样式的 Web Service，该名称为表示该参数的 XML 元素的本地名称。
	 * 默认值为该方法的参数的名称。
	 */
	private String name;

	private String partName;

	/**
	 * 该参数的 XML 名称空间。此值仅用于文档样式的 Web Service，其中该参数映射到 XML 元素。 默认值为该 Web Service 的 targetNamespace。 
	 */
	private String targetNamespace;

	/**
	 * 该参数的流方向。
     *
	 * 有效值为：
	 *	
	 *	    § WebParam.Mode.IN
	 *	    WebParam.Mode.OUT
	 *	    WebParam.Mode.INOUT
	 *	
	 *	默认值为 WebParam.Mode.IN。
	 *	
	 *	如果指定 WebParam.Mode.OUT 或 WebParam.Mode.INOUT，则该参数的数据类型必须为 Holder 或扩展 Holder。有关详细信息，请参阅 JAX-RPC   specification。
	 *	
	 *	WebParam.Mode.OUT 和 WebParam.Mode.INOUT 模式仅对于 RPC 样式的 Web Service 或映射到头的参数受支持。 
	 * 
	 */
	private Mode mode = javax.jws.WebParam.Mode.IN;
	
	/**
	 * 指定该参数的值是否存在于 SOAP 头中。默认情况下，参数位于 SOAP 正文中。有效值为 true 和 false。默认值为 false。 
	 */
	private boolean header;

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

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

}
