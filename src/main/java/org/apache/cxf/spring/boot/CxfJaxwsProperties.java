package org.apache.cxf.spring.boot;

import org.apache.cxf.spring.boot.jaxws.property.LoggingFeatureProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Configuration properties for the CXF JAX-WS auto-configuration.
 *
 * <p>Binds properties under the {@code cxf.jaxws} prefix, including the
 * {@code enabled} flag, the default SOAP service namespace and the nested logging
 * feature settings exposed through {@link LoggingFeatureProperty}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ConfigurationProperties(CxfJaxwsProperties.PREFIX)
public class CxfJaxwsProperties {

	/** Configuration property prefix shared by all JAX-WS properties. */
	public static final String PREFIX = "cxf.jaxws";

	/**
	 * If the Cxf Jaxws should be enabled or not
	 */
	private boolean enabled = false;

	/**
	 * Service Namespace : Specifies the published soap interface service namespace
	 */
	private String namespace;

	@NestedConfigurationProperty
	private LoggingFeatureProperty loggingFeature = new LoggingFeatureProperty();

	/**
	 * Get whether the CXF JAX-WS auto-configuration is enabled.
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set whether the CXF JAX-WS auto-configuration is enabled.
	 * @param enabled whether to enable the JAX-WS auto-configuration
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Get the default service namespace used when publishing SOAP endpoints.
	 * @return the default service namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Set the default service namespace used when publishing SOAP endpoints.
	 * @param namespace the default service namespace
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Get the logging feature settings.
	 * @return the logging feature properties
	 */
	public LoggingFeatureProperty getLoggingFeature() {
		return loggingFeature;
	}

	/**
	 * Set the logging feature settings.
	 * @param loggingFeature the logging feature properties
	 */
	public void setLoggingFeature(LoggingFeatureProperty loggingFeature) {
		this.loggingFeature = loggingFeature;
	}

}