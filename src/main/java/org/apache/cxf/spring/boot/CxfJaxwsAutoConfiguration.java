package org.apache.cxf.spring.boot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.spring.boot.jaxws.annotation.WebServiceEndpoint;
import org.apache.cxf.spring.boot.jaxws.security.UsernamePwdAuthInterceptor;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

//http://cxf.apache.org/docs/springboot.html

@AutoConfigureAfter( name = {
	"org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration"
})
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ SpringBus.class, CXFServlet.class })
@ConditionalOnProperty(prefix = CxfJaxwsProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ CxfJaxwsProperties.class })
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CxfJaxwsAutoConfiguration implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(CxfJaxwsAutoConfiguration.class);
	private ApplicationContext applicationContext;

	@Autowired
    private Bus bus;
	
	@Bean
	@ConditionalOnMissingBean(Bus.class)
	public Bus bus(){
	    return new SpringBus();
	}
	
	public WSS4JInInterceptor WSS4JInInterceptor() {
		WSS4JInInterceptor s = new WSS4JInInterceptor();
		
		
		return s;
    }
	
	public UsernamePwdAuthInterceptor UsernamePwdAuthInterceptor() {
		UsernamePwdAuthInterceptor s = new UsernamePwdAuthInterceptor();
		
		
		return s;
    }
	
	/** 
	 * JAX-WS 
	 * 
	 * // 销毁指定的Ws
	 *	ServerImpl server = endpoint.getServer(addr);
	 *	server.destroy();
	 * 
	 */
	@Bean
	public Map<String, Endpoint> endpoints() {
		
		Map<String, Endpoint> endpoints = new LinkedHashMap<String, Endpoint>();
		
		// 动态创建、发布 Ws
		Map<String, Object> beansOfType = getApplicationContext().getBeansWithAnnotation(WebService.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			
			Map<String, PhaseInterceptor> interceptorsOfType = getApplicationContext().getBeansOfType(PhaseInterceptor.class);
			Map<String, Feature> featuresOfType = getApplicationContext().getBeansOfType(Feature.class);
			
			Iterator<Entry<String, Object>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, Object> entry = ite.next();
				//查找该实现上的自定义注解
				WebServiceEndpoint annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), WebServiceEndpoint.class);
				if(annotationType == null) {
					// 注解为空，则跳过该实现，并打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", WebServiceEndpoint.class, entry.getValue().getClass(), entry.getKey());
					continue;
				}
				
				EndpointImpl endpoint = new EndpointImpl(bus, entry.getValue());
				
				//接口发布在 addr 目录下
				endpoint.publish(annotationType.addr());
				
				// 数据上行拦截器
				for (String name : annotationType.inInterceptors()) {
					PhaseInterceptor interceptor = interceptorsOfType.get(name);
					if(!ObjectUtils.isEmpty(interceptor)) {
						endpoint.getInInterceptors().add(interceptor);
					}
				}
				// 数据下行拦截器
				for (String name : annotationType.outInterceptors()) {
					PhaseInterceptor interceptor = interceptorsOfType.get(name);
					if(!ObjectUtils.isEmpty(interceptor)) {
						endpoint.getOutInterceptors().add(interceptor);
					}
				}
				// 数据上行Fault拦截器
				for (String name : annotationType.inFaultInterceptors()) {
					PhaseInterceptor interceptor = interceptorsOfType.get(name);
					if(!ObjectUtils.isEmpty(interceptor)) {
						endpoint.getInFaultInterceptors().add(interceptor);
					}
				}
				// 数据下行Fault拦截器
				for (String name : annotationType.outFaultInterceptors()) {
					PhaseInterceptor interceptor = interceptorsOfType.get(name);
					if(!ObjectUtils.isEmpty(interceptor)) {
						endpoint.getOutFaultInterceptors().add(interceptor);
					}
				}
				
				// Feature
				for (String name : annotationType.features()) {
					Feature feature = featuresOfType.get(name);
					if(!ObjectUtils.isEmpty(feature)) {
						endpoint.getFeatures().add(feature);
					}
				}
				
				endpoints.put(annotationType.addr(), endpoint);
				
			}
		}
		
		return endpoints;
			
	}
    
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
