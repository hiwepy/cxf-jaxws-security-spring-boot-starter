package org.apache.cxf.spring.boot;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.spring.boot.jaxws.security.UsernamePwdAuthInterceptor;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
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

/**
 * Auto-configuration for the CXF JAX-WS WS-Security / SAML SSO integration.
 *
 * <p>Activated when {@code cxf.jaxws.saml.enabled=true}. It exposes the WSS4J in/out
 * interceptors, the username/password authentication interceptor and a
 * {@link WSPolicyFeature} that can be attached to published endpoints.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@AutoConfigureAfter(name = { "org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration" })
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ SpringBus.class, CXFServlet.class })
@ConditionalOnProperty(prefix = CxfJaxwsSecurityProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ CxfJaxwsSecurityProperties.class })
public class CxfJaxwsSecurityConfiguration implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(CxfJaxwsSecurityConfiguration.class);
	private ApplicationContext applicationContext;

	/**
	 * Build a {@link WSS4JInInterceptor} for incoming WS-Security processing (e.g.
	 * UsernameToken validation).
	 * @return a new WSS4J in-interceptor
	 */
	public WSS4JInInterceptor WSS4JInInterceptor() {

		WSS4JInInterceptor s = new WSS4JInInterceptor();

		/*
		 * <entry key="action" value="UsernameToken"/> <!--
		 * 密码类型，PasswordText表示明文,密文是PasswordDigest --> <entry key="passwordType"
		 * value="PasswordText"/> <entry key="passwordCallbackRef"> <!-- 回调函数引用 --> <ref
		 * bean="myPasswordCallback"/> </entry>
		 */

		return s;
	}

	/**
	 * Build a {@link WSS4JOutInterceptor} for outgoing WS-Security processing (e.g.
	 * UsernameToken insertion).
	 * @return a new WSS4J out-interceptor
	 */
	public WSS4JOutInterceptor WSS4JOutInterceptor() {

		WSS4JOutInterceptor s = new WSS4JOutInterceptor();

		/*
		 * <entry key="action" value="UsernameToken"/> <!--
		 * 密码类型，PasswordText表示明文,密文是PasswordDigest --> <entry key="passwordType"
		 * value="PasswordText"/> <entry key="passwordCallbackRef"> <!-- 回调函数引用 --> <ref
		 * bean="myPasswordCallback"/> </entry>
		 */

		return s;
	}

	/**
	 * Build a {@link UsernamePwdAuthInterceptor} used to authenticate incoming SOAP
	 * requests against the configured username/password credentials.
	 * @return a new username/password authentication interceptor
	 */
	public UsernamePwdAuthInterceptor UsernamePwdAuthInterceptor() {
		UsernamePwdAuthInterceptor s = new UsernamePwdAuthInterceptor();

		return s;
	}

	/**
	 * Create the {@link WSPolicyFeature} used to attach WS-Policy assertions to the
	 * published endpoints.
	 * @return a new WS-Policy feature
	 */
	@Bean
	public WSPolicyFeature policyFeature() {

		WSPolicyFeature feature = new WSPolicyFeature();

		return feature;
	}

	/**
	 * Set the owning {@link ApplicationContext}.
	 * @param applicationContext the application context
	 * @throws BeansException never thrown
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Get the owning {@link ApplicationContext}.
	 * @return the application context
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
