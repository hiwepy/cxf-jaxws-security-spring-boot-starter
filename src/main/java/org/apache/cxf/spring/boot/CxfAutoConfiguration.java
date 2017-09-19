package org.apache.cxf.spring.boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.cxf.rs.security.oauth2.grants.code.EHCacheCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.provider.DefaultEHCacheOAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

//http://cxf.apache.org/docs/springboot.html

@Configuration
@ConditionalOnClass({ Disruptor.class })
@ConditionalOnProperty(prefix = DisruptorProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ DisruptorProperties.class })
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 8)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CxfAutoConfiguration implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	/**
	 * 决定一个消费者将如何等待生产者将Event置入Disruptor的策略。用来权衡当生产者无法将新的事件放进RingBuffer时的处理策略。
	 * （例如：当生产者太快，消费者太慢，会导致生成者获取不到新的事件槽来插入新事件，则会根据该策略进行处理，默认会堵塞）
	 */
	@Bean
	@ConditionalOnMissingBean
	public DefaultEHCacheOAuthDataProvider oauthProvider() {
		
		/*<bean id="oauthProvider" class="org.apache.cxf.systest.jaxrs.security.oauth2.common.OAuthDataProviderImpl">
		       <property name="useJwtFormatForAccessTokens" value="true"/>
		       <property name="storeJwtTokenKeyOnly" value="true"/>
		</bean>
		*/
		/*<bean id="oauthProvider" class="org.apache.cxf.rs.security.oauth2.grants.code.EHCacheCodeDataProvider">
		       <property name="useJwtFormatForAccessTokens" value="true"/>
		</bean>*/
		
		DefaultEHCacheOAuthDataProvider dataProvider  = new EHCacheCodeDataProvider();
		
		dataProvider.setUseJwtFormatForAccessTokens(true);
		
		return dataProvider;
	}
	
    
	public WSS4JInInterceptor WSS4JInInterceptor() {
		WSS4JInInterceptor s = new WSS4JInInterceptor();
		
		
		return s;
    }

	
	@Bean
	@ConditionalOnMissingBean
	public AccessTokenService accessTokenService(OAuthDataProvider dataProvider) {
		/*
		<bean id="oauthProvider" class="oauth2.manager.OAuthManager"/>
		 
		<bean id="accessTokenService" class="org.apache.cxf.rs.security.oauth2.services.AccessTokenService">
		    <property name="dataProvider" ref="oauthProvider"/>
		    <property name="writeCustomErrors" value="true"/>
		</bean>
		*/
		
		AccessTokenService accessTokenService = new AccessTokenService();
		
		accessTokenService.setDataProvider(dataProvider);
		accessTokenService.setWriteCustomErrors(true);
		
		return accessTokenService;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
