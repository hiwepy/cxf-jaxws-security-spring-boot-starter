package org.apache.cxf.spring.boot;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(CxfJaxwsSecurityProperties.PREFIX)
public class CxfJaxwsSecurityProperties {

	public static final String PREFIX = "cxf.jaxws.saml";

	class CxfJaxwsSamlEHCacheProperty {

		private String configFileUrl = "/cxf-samlp-ehcache.xml";

		public String getConfigFileUrl() {
			return configFileUrl;
		}

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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	 

}