package org.apache.cxf.spring.boot;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.rs.security.saml.sso.SamlRedirectBindingFilter;
import org.apache.cxf.rs.security.saml.sso.state.EHCacheSPStateManager;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//http://cxf.apache.org/docs/saml-web-sso.html

@AutoConfigureAfter( name = {
	"org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration"
})
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ SpringBus.class, CXFServlet.class })
@ConditionalOnProperty(prefix = CxfJaxwsProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ CxfJaxwsProperties.class })
public class CxfSamlAutoConfiguration implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(CxfSamlAutoConfiguration.class);
	private ApplicationContext applicationContext;


	@Bean
	public SamlRedirectBindingFilter redirectGetFilter(SPStateManager stateProvider) {
		
		SamlRedirectBindingFilter redirectGetFilter = new SamlRedirectBindingFilter();
		redirectGetFilter.setAddWebAppContext(addWebAppContext);
		redirectGetFilter.setAddEndpointAddressToContext(add);
		redirectGetFilter.setAssertionConsumerServiceAddress("/racs/sso");
		//redirectGetFilter.setAuthnRequestBuilder(authnRequestBuilder);
		redirectGetFilter.setCallbackHandler(callbackHandler);
		redirectGetFilter.setCallbackHandlerClass(callbackHandlerClass);
		redirectGetFilter.setIdpServiceAddress("https://localhost:9443/idp");
		redirectGetFilter.setIssuerId(issuerId);
		redirectGetFilter.setSignatureCrypto(crypto);
		redirectGetFilter.setSignaturePropertiesFile(signaturePropertiesFile);
		redirectGetFilter.setSignatureUsername(signatureUsername);
		redirectGetFilter.setSignRequest(signRequest);
		redirectGetFilter.setStateProvider(stateProvider);
		
		return redirectGetFilter;
	}
	
	
	@Bean
	public SPStateManager stateProvider(Bus bus) {

		EHCacheSPStateManager feature = new EHCacheSPStateManager(bus);
		
		return feature;
    }
	
	@Bean
	public WSPolicyFeature policyFeature() {

		WSPolicyFeature feature = new WSPolicyFeature();
		
		return feature;
    }
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
