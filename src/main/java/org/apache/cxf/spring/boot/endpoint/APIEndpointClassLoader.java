package org.apache.cxf.spring.boot.endpoint;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.spring.boot.repository.APIEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://sheungxin.iteye.com/blog/2352833
 */
public class APIEndpointClassLoader extends ClassLoader {
	
	private final Logger LOG = LoggerFactory.getLogger(APIEndpointClassLoader.class);
	private Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();
	private Map<String, APIEndpoint> endpointMap = new HashMap<String, APIEndpoint>();
	private final APIEndpointHandler handler;
	/**
	 * 初始化
	 */
	public APIEndpointClassLoader(final APIEndpointHandler handler,final List<APIEndpoint> endpoints) {
		super(APIEndpointClassLoader.class.getClassLoader().getParent().getParent());
		
		this.handler = handler;
		
		for (APIEndpoint apiEndpoint : endpoints) {
			endpointMap.put(apiEndpoint.getName(), apiEndpoint);
		}
		
		init();
		
	}

	
	public void init() {
		try {
			this.addThisToParentClassLoader(APIEndpointClassLoader.class.getClassLoader().getParent());
		} catch (Exception e) {
			System.err.println("设置classloader到容器中时出现错误！");
		}
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (loadedClasses.containsKey(name)) {
			return loadedClasses.get(name);
		}
		return super.loadClass(name, resolve);
	}

	/**
	 * 将this替换为指定classLoader的parent ClassLoader
	 * 
	 * @param classLoader
	 */
	private void addThisToParentClassLoader(ClassLoader classLoader) throws Exception {
		URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Field field = cl.getClass().getDeclaredField("parent");
		field.setAccessible(true);
		field.set(classLoader, this);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		
		// API接口描述信息
		APIEndpoint endpoint = endpointMap.get(name);
		
		LOG.trace("Class '{}' .", name);
		
		Exception exception = null;
		
		try {

			boolean continueChain = handler.preHandle(endpoint);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Invoked preHandle method.  Continuing chain?: [" + continueChain + "]");
			}
			if (continueChain) {
				
				byte[] classBytes = handler.postHandle(endpoint);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Successfully invoked postHandle method");
				}
				
				// 将字节流变成一个class
				return super.defineClass(name, classBytes, 0, classBytes.length);
			}
			
		} catch (Exception e) {
			exception = e;
		} finally {
			try {
				handler.afterCompletion(endpoint, exception);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Successfully invoked afterCompletion method.");
				}
			} catch (Exception e) {
				if (exception == null) {
					exception = e;
				} else {
					LOG.debug("afterCompletion implementation threw an exception.  This will be ignored to "
							+ "allow the original source exception to be propagated.", e);
				}
			}
			
		}
		return null;
	}
	
}
