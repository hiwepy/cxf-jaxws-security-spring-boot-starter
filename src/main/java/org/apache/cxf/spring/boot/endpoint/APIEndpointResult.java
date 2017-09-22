package org.apache.cxf.spring.boot.endpoint;

public class APIEndpointResult {

	// javax.jws.WebResult

	/**
	 * WSDL 文件中参数的名称。
	 * 对于 RPC 样式的 Web Service，该名称映射到表示返回值的 <wsdl:part> 元素。对于文档样式的 Web Service，该名称为表示返回值的 XML 元素的本地名称。
	 * 默认值为硬编码名称 result。 
	 */
	private String name;

	private String partName;
	
	/**
	 * 返回值的 XML 名称空间。此值仅用于文档样式的 Web Service，其中返回值映射到 XML 元素。
	 * 默认值为该 Web Service 的 targetNamespace。 
	 */
	private String targetNamespace;

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

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

}
