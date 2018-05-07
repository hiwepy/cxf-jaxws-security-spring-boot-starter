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

@AutoConfigureAfter(name = { "org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration" })
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ SpringBus.class, CXFServlet.class })
@ConditionalOnProperty(prefix = CxfJaxwsSecurityProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ CxfJaxwsSecurityProperties.class })
public class CxfJaxwsSecurityConfiguration implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(CxfJaxwsSecurityConfiguration.class);
	private ApplicationContext applicationContext;

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

	public UsernamePwdAuthInterceptor UsernamePwdAuthInterceptor() {
		UsernamePwdAuthInterceptor s = new UsernamePwdAuthInterceptor();

		return s;
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
