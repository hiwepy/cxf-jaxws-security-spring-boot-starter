package org.apache.cxf.spring.boot;
/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */


import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.zfsoft.boot.datay.setup.ws.Product;
import com.zfsoft.boot.datay.setup.ws.ProductService;

public class JAXRSClient1 {


    public static void main(String[] args) {  
        String baseAddress = "http://localhost:8082/services/ws/rest";  
  
        List<Object> providerList = new ArrayList<Object>();  
        providerList.add(new JacksonJsonProvider());  
  
        ProductService productService = JAXRSClientFactory.create(baseAddress, ProductService.class, providerList);  
        List<Product> productList = productService.retrieveAllProducts();  
        for (Product product : productList) {  
            System.out.println(product);  
        }  
    }  
    
}
