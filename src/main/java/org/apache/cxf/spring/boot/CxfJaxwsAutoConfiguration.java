package org.apache.cxf.spring.boot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.spring.boot.endpoint.APIEndpoint;
import org.apache.cxf.spring.boot.endpoint.APIEndpointRepository;
import org.apache.cxf.spring.boot.jaxws.security.UsernamePwdAuthInterceptor;
import org.apache.cxf.spring.boot.utils.CtClassJaxwsApiBuilder;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
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

import javassist.CannotCompileException;
import javassist.NotFoundException;

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

	private ApplicationContext applicationContext;

	@Autowired
    private Bus bus;
	
	@Autowired
	private APIEndpointRepository endpointRepository;
	
	@Bean
	@ConditionalOnMissingBean(Bus.class)
	public Bus bus(){
	    return new SpringBus();
	}
	
	@Bean
	public List<APIEndpoint> apiEndpoints() {
		return getEndpointRepository().getEndpoints();
	}
	
	/** 
	 * JAX-WS 
	 * 
	 * // 销毁指定的Ws
	 *	ServerImpl server = endpoint.getServer(addr);
	 *	server.destroy();
	 * 
	 */
	@Bean("endpointMap")
	public Map<String,Endpoint> endpoints(List<APIEndpoint> apiEndpoints) {
		
		Map<String, Endpoint> endpointMap = new HashMap<String, Endpoint>();
		
		for (APIEndpoint apiEndpoint : apiEndpoints) {
			// 动态创建、发布 Ws
			try {
				
				Class jaxwsApiClass = new CtClassJaxwsApiBuilder("JaxwsApi" + apiEndpoint.getName()).method("HelloWoldService2").build().toClass();
				
				EndpointImpl endpoint = new EndpointImpl(bus, BeanUtils.instantiateClass(jaxwsApiClass));
				
				//接口发布在 addr 目录下
				endpoint.publish(apiEndpoint.getAddr());
				
				endpoint.getInInterceptors().add(new UsernamePwdAuthInterceptor());

				endpointMap.put(apiEndpoint.getAddr(), endpoint);
				
			} catch (BeanInstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return endpointMap;
	}
	
	/*
	@Bean
    public Server rsServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/");
        // Register 2 JAX-RS root resources supporting "/sayHello/{id}" and "/sayHello2/{id}" relative paths
        endpoint.setServiceBeans(Arrays.<Object>asList(new HelloServiceImpl1(), new HelloServiceImpl2()));
        endpoint.setFeatures(Arrays.asList(new Swagger2Feature()));
        return endpoint.create();
    }*/
	
	
    
	public WSS4JInInterceptor WSS4JInInterceptor() {
		WSS4JInInterceptor s = new WSS4JInInterceptor();
		
		
		return s;
    }
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public APIEndpointRepository getEndpointRepository() {
		return endpointRepository;
	}

	public void setEndpointRepository(APIEndpointRepository endpointRepository) {
		this.endpointRepository = endpointRepository;
	}
	
}
