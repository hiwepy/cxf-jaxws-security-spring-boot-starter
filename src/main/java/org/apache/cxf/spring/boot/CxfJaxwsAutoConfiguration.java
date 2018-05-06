package org.apache.cxf.spring.boot;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.xml.ws.handler.Handler;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.management.InstrumentationManager;
import org.apache.cxf.management.counters.CounterRepository;
import org.apache.cxf.management.jmx.InstrumentationManagerImpl;
import org.apache.cxf.rs.security.saml.sso.SamlRedirectBindingFilter;
import org.apache.cxf.spring.boot.jaxws.EndpointApiTemplate;
import org.apache.cxf.spring.boot.jaxws.annotation.JaxwsEndpoint;
import org.apache.cxf.spring.boot.jaxws.security.UsernamePwdAuthInterceptor;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.validation.BeanValidationFeature;
import org.apache.cxf.validation.BeanValidationProvider;
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
@SuppressWarnings({ "rawtypes" })
public class CxfJaxwsAutoConfiguration implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(CxfJaxwsAutoConfiguration.class);
	private ApplicationContext applicationContext;

	@Autowired
    private Bus bus;
	
	@Bean
	@ConditionalOnMissingBean(Bus.class)
	public Bus bus(){
		SpringBus bus = new SpringBus();
		BusFactory.setDefaultBus(bus);
	    return bus;
	}
	
	@Bean
	@ConditionalOnMissingBean(InstrumentationManagerImpl.class)
	public InstrumentationManager instrumentationManager(){
		InstrumentationManagerImpl mgr = new InstrumentationManagerImpl();
		mgr.setBus(bus);
		/*mgr.setCreateMBServerConnectorFactory(true);
		mgr.setDaemon(false);
		mgr.setEnabled(false);
		mgr.setJMXServiceURL(value);
		mgr.setServerName(s);
		mgr.setThreaded(value);
		mgr.setUsePlatformMBeanServer(flag);*/
	    return mgr;
	}
	
	// Wiring the counter repository 
	@Bean
	@ConditionalOnMissingBean(InstrumentationManagerImpl.class)
	public CounterRepository counterRepository(){
		CounterRepository r = new CounterRepository();
		r.setBus(bus);
		return r;
	}
	
	public WSS4JInInterceptor WSS4JInInterceptor() {
		WSS4JInInterceptor s = new WSS4JInInterceptor();
		
		return s;
    }
	
	public UsernamePwdAuthInterceptor UsernamePwdAuthInterceptor() {
		UsernamePwdAuthInterceptor s = new UsernamePwdAuthInterceptor();
		
		
		
		return s;
    }

	@Bean
	public BeanValidationProvider validationProvider() {
		return new BeanValidationProvider();
	}
	
	@Bean
	public BeanValidationFeature validationFeature(BeanValidationProvider validationProvider) {
		BeanValidationFeature validationFeature = new BeanValidationFeature();
		validationFeature.setProvider(validationProvider);
		return validationFeature;
	}
	
	@Bean
	public EndpointApiTemplate endpointTemplate(BeanValidationFeature validationFeature) {
		
		Map<String, Feature> featuresOfType = getApplicationContext().getBeansOfType(Feature.class);
		Map<String, Handler> handlersOfType = getApplicationContext().getBeansOfType(Handler.class);
		Map<String, Interceptor> interceptorsOfType = getApplicationContext().getBeansOfType(Interceptor.class);
		
		EndpointApiTemplate template = new EndpointApiTemplate(bus);
		
		template.setFeatures(featuresOfType);
		template.setHandlers(handlersOfType);
		template.setInterceptors(interceptorsOfType);
		
		// 动态创建、发布 Ws
		Map<String, Object> beansOfType = getApplicationContext().getBeansWithAnnotation(WebService.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			
			Iterator<Entry<String, Object>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, Object> entry = ite.next();
				//查找该实现上的自定义注解
				JaxwsEndpoint annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), JaxwsEndpoint.class);
				if(annotationType == null) {
					// 注解为空，则跳过该实现，并打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", JaxwsEndpoint.class, entry.getValue().getClass(), entry.getKey());
					continue;
				}
				template.publish(annotationType.addr(), entry.getValue());
			}
		}
		
		return template;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
