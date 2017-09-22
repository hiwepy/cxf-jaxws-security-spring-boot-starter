package org.apache.cxf.spring.boot.endpoint;

public class APIEndpointMethod {

	// javax.jws.WebMethod
	
	/**
	 * 操作的名称。映射到 WSDL 文件中的 <wsdl:operation> 元素。默认值为该方法的名称。 
	 */
	private String operationName;
	/**
	 * 此操作的操作。对于 SOAP 绑定，此特性的值决定 SOAP 消息中 SOAPAction 头的值。
	 */
	private String action;
	private boolean exclude;

	// javax.jws.WebParam
	private APIEndpointParam[] params;
	
	// javax.jws.WebResult
	
	private APIEndpointResult result;

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

	public APIEndpointParam[] getParams() {
		return params;
	}

	public void setParams(APIEndpointParam... params) {
		this.params = params;
	}

	public APIEndpointResult getResult() {
		return result;
	}

	public void setResult(APIEndpointResult result) {
		this.result = result;
	}
	
}
