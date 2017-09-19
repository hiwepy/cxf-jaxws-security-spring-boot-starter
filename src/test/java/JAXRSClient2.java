/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.zfsoft.boot.datay.setup.ws.Product;

public class JAXRSClient2 {

	String baseAddress = "http://localhost:8080/ws/rest";  
    JacksonJsonProvider jsonProvider = new JacksonJsonProvider();  
    
    @Test  
    public void retrieveAll() {  
        List<Product> productList = ClientBuilder.newClient().register(jsonProvider)  
                .target(baseAddress).path("/products")  
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<Product>>(){});  
        for (Product product : productList) {  
            System.out.println(product);  
        }  
    }  
      
    /** 
     * 该方法测试参数查询， 
     * 如果测试表单参数，则需要提交表单 
     */  
    @Test  
    public void retrieveByName_param(){  
        List<Product> productList = ClientBuilder.newClient()  
        .register(jsonProvider)  
        .target(baseAddress)  
        .path("/products/name")  
        .queryParam("name", "iphone63")  
        .request(MediaType.APPLICATION_JSON)  
        .get(new GenericType<List<Product>>(){});  
          
        for (Product product : productList) {  
            System.out.println(product);  
        }  
    }  
      
    /** 
     * 添加数据 
     */  
    @Test  
    public void create(){  
        Product product = new Product();  
        product.setName("iphone7");  
        product.setPrice(6000);  
          
        Product resultProduct = ClientBuilder.newClient()  
        .register(jsonProvider)  
        .target(baseAddress)  
        .path("/product")  
        .request(MediaType.APPLICATION_JSON)  
        .post(Entity.entity(product, MediaType.APPLICATION_JSON), new GenericType<Product>(){});  
          
        System.out.println(resultProduct);  
          
        retrieveAll();  
    }  
      
    /** 
     * 修改数据1 
     */  
    @Test  
    public void update(){  
        Product product = new Product();  
        product.setId(1L);  
        product.setName("iphone6s");  
        product.setPrice(4000);  
          
        Product resultTarget = ClientBuilder.newClient()  
        .register(jsonProvider)  
        .target(baseAddress)  
        .path("/product")  
        .request(MediaType.APPLICATION_JSON)  
        .put(Entity.entity(product, MediaType.APPLICATION_JSON), new GenericType<Product>(){});  
          
        System.out.println(resultTarget);  
          
        System.out.println("==================");  
        retrieveAll();  
    }  
      
    /** 
     * 修改数据2 
     */  
    @Test  
    public void update2(){  
        Map<String,Object> fieldParam = new HashMap<>();  
        fieldParam.put("name", "ipad mini");  
        fieldParam.put("price", 1999);  
          
        Product product = ClientBuilder.newClient()  
        .register(jsonProvider)  
        .target(baseAddress)  
        .path("/product/2")  
        .request(MediaType.APPLICATION_JSON)  
        .put(Entity.entity(fieldParam, MediaType.APPLICATION_JSON), new GenericType<Product>(){});  
        System.out.println(product);  
          
        System.out.println("===========================");  
          
        retrieveAll();  
    }  
      
    /** 
     * 路径参数 
     * 删除数据:根据id删除 
     */  
    @Test  
    public void delete(){  
        Product product = ClientBuilder.newClient()  
        .register(jsonProvider)  
        .target(baseAddress)  
        .path("/product/1")  
        .request(MediaType.APPLICATION_JSON)  
        .delete(new GenericType<Product>(){});  
          
        System.out.println(product);  
          
        System.out.println("===============");  
          
        retrieveAll();  
    }  
      
    /** 
     * 查询参数 
     * 删除数据：根据id删除 
     */  
    @Test  
    public void delete_param(){  
        Product product = ClientBuilder.newClient()  
        .register(jsonProvider)  
        .target(baseAddress)  
        .path("/product")  
        .queryParam("id", 2L)  
        .request(MediaType.APPLICATION_JSON)  
        .delete(new GenericType<Product>(){});  
          
        System.out.println(product);  
          
        System.out.println("=================");  
          
        retrieveAll();  
    }  
    
	public static void main(String[] args) {  
        String baseAddress = "http://localhost:8080/ws/rest";  
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider();  
        List productList = ClientBuilder.newClient().register(jsonProvider)  
                .target(baseAddress).path("/products")  
                .request(MediaType.APPLICATION_JSON).get(List.class);  
        for (Object product : productList) {  
            System.out.println(product);  
        }  
    }  
	
}
