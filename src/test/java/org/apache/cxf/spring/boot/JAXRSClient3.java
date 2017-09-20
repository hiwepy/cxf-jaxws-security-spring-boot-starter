package org.apache.cxf.spring.boot;
/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.zfsoft.boot.datay.setup.ws.Product;

public class JAXRSClient3 {

	 public static void main(String[] args) {  
	        String baseAddress = "http://localhost:8080/ws/rest";  
	  
	        List<Object> providerList = new ArrayList<Object>();  
	        providerList.add(new JacksonJsonProvider());  
	  
	        /*List productList = WebClient.create(baseAddress, providerList)  
	                .path("/products").accept(MediaType.APPLICATION_JSON)  
	                .get(List.class);  */
	        
	        List<Product> productList = WebClient.create(baseAddress, providerList)  
	                .path("/products").accept(MediaType.APPLICATION_JSON)  
	                .get(new GenericType<List<Product>>(){});  
	        
	        for(Object product : productList){  
	            System.out.println(product);  
	        }  
	    }  
	 
}
