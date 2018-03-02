package org.apache.cxf.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(CxfJaxwsProperties.PREFIX)
public class CxfJaxwsProperties {

	public static final String PREFIX = "cxf.jaxws";

	/**
	 * Enable Disruptor.
	 */
	private boolean enabled = false;
	
	/**
	 * 全局启用的输入拦截器名称；默认可选择有 clustering、metrics、throttling、logging、、;多个名称使用符号,分割
	 */
	private String inInterceptors;

	private String outInterceptors;
	
	private String inFaultInterceptors;

	private String outFaultInterceptors;
	/**
	 * 全局启用的输入拦截器名称；默认可选择有 clustering、metrics、throttling、logging、、;多个名称使用符号,分割
	 */
	private String features;
	 
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}