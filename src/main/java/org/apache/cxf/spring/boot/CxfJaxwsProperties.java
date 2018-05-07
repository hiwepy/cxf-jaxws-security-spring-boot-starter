package org.apache.cxf.spring.boot;

import org.apache.cxf.spring.boot.jaxws.property.LoggingFeatureProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(CxfJaxwsProperties.PREFIX)
public class CxfJaxwsProperties {

	public static final String PREFIX = "cxf.jaxws";

	/**
	 * If the Cxf Jaxws should be enabled or not
	 */
	private boolean enabled = false;

	@NestedConfigurationProperty
	private LoggingFeatureProperty loggingFeature = new LoggingFeatureProperty();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LoggingFeatureProperty getLoggingFeature() {
		return loggingFeature;
	}

	public void setLoggingFeature(LoggingFeatureProperty loggingFeature) {
		this.loggingFeature = loggingFeature;
	}
	
}