package org.apache.cxf.spring.boot.api;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

public class DynamicWebServiceServer
{
   public static void main(String[] args) throws Exception
   {
       DynamicWebserviceGenerator javassistLearn = new DynamicWebserviceGenerator();
       Class<?> webservice = javassistLearn.createDynamicClazz();
        
       JaxWsServerFactoryBean factoryBean = new JaxWsServerFactoryBean();
        
       // Web服务的地址
       factoryBean.setAddress("http://localhost:8081/dynamicHello");

       // Web服务对象调用接口
       factoryBean.setServiceClass(webservice);
       Server server = factoryBean.create();
       server.start();
   }
}