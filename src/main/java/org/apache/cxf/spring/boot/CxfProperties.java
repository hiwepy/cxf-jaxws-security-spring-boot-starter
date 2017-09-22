package org.apache.cxf.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(CxfProperties.PREFIX)
public class CxfProperties {

	public static final String PREFIX = "cxf.api";

	/**
	 * Enable Disruptor.
	 */
	private boolean enabled = false;
	 
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}