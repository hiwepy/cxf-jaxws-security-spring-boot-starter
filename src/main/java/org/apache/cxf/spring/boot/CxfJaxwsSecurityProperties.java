package org.apache.cxf.spring.boot;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
/** Configuration properties for Cxf Jaxws.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */

@ConfigurationProperties(CxfJaxwsSecurityProperties.PREFIX)
public class CxfJaxwsSecurityProperties {

	public static final String PREFIX = "cxf.jaxws.saml";
	/** The Cxf Jaxws Saml E H Cache Property.
	 *
	 * @author [@Loong Wan](https://github.com/loong10k)
	 * @since 1.0.0
	 */

	class CxfJaxwsSamlEHCacheProperty {

		private String configFileUrl = "/cxf-samlp-ehcache.xml";

		/** Returns the config file url.
		 * @return the result
		 */
		public String getConfigFileUrl() {
			return configFileUrl;
		}

		/** Sets the config file url.
		 * @param configFileUrl the configFileUrl
		 */
		public void setConfigFileUrl(String configFileUrl) {
			this.configFileUrl = configFileUrl;
		}

	}

	/**
	 * If the JMX integration should be enabled or not
	 */
	private boolean enabled = false;

	

	/**
	 * EHCacheSPStateManager
	 */
	@NestedConfigurationProperty
	private CxfJaxwsSamlEHCacheProperty ehcache = new CxfJaxwsSamlEHCacheProperty();

	/**
	 * DefaultAuthnRequestBuilder
	 */
	private boolean forceAuthn;
	private boolean isPassive;
	private String protocolBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private String nameIDFormat = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";

	/** Returns whether the enabled is enabled.
	 * @return the result
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/** Sets the enabled.
	 * @param enabled the enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	 

}