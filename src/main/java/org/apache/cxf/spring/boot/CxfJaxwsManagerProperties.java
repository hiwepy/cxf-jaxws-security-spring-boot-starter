package org.apache.cxf.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the CXF JMX management integration.
 *
 * <p>Binds properties under the {@code cxf.manager} prefix, controlling whether MBeans
 * are registered with the platform MBean server, whether a connector is created and
 * the JMX service URL exposed by the instrumentation manager.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@ConfigurationProperties(CxfJaxwsManagerProperties.PREFIX)
public class CxfJaxwsManagerProperties {

	/** Configuration property prefix shared by all JMX management properties. */
	public static final String PREFIX = "cxf.manager";

	/**
	 * If the JMX integration should be enabled or not
	 */
	private boolean enabled = false;
	/**
	 * If true and no reference to an MBeanServer is supplied, the JMX extension
	 * registers MBeans with the platform MBean server.
	 */
	private boolean usePlatformMBeanServer = false;
	/**
	 * If true, a connector is created on the MBeanServer.
	 */
	private boolean createMBServerConnectorFactory = true;
	/**
	 * Determines if the creation of the MBean connector is performed in this thread
	 * or in a separate thread. Only relevant if createMBServerConnectorFactory is
	 * true.
	 */
	private boolean threaded = false;
	/**
	 * Determines if the MBean connector creation thread is marked as a daemon
	 * thread or not. Only relevant if createMBServerConnectorFactory is true.
	 */
	private boolean daemon = false;
	/**
	 * If supplied, usePlatformMBeanServer is false, and no reference to an
	 * MBeanServer is supplied, the JMX extension registers MBeans with the MBean
	 * server carrying this name.
	 */
	private String serverName;
	/**
	 * The URL of the connector to create on the MBeanServer. Only relevant if
	 * createMBServerConnectorFactory is true.
	 * <code>service:jmx:rmi:///jndi/rmi://localhost:9913/jmxrmi</code>
	 */
	private String jmxServiceURL;

	/** Returns whether the enabled is enabled.
	 * @return the result
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/** Sets the enabled.
	 * @param enabled the enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/** Returns whether the use platform m bean server is enabled.
	 * @return the result
	 */
	public boolean isUsePlatformMBeanServer() {
		return usePlatformMBeanServer;
	}

	/** Sets the use platform m bean server.
	 * @param usePlatformMBeanServer the usePlatformMBeanServer
	 */
	public void setUsePlatformMBeanServer(boolean usePlatformMBeanServer) {
		this.usePlatformMBeanServer = usePlatformMBeanServer;
	}

	/** Returns whether the create m b server connector factory is enabled.
	 * @return the result
	 */
	public boolean isCreateMBServerConnectorFactory() {
		return createMBServerConnectorFactory;
	}

	/** Sets the create m b server connector factory.
	 * @param createMBServerConnectorFactory the createMBServerConnectorFactory
	 */
	public void setCreateMBServerConnectorFactory(boolean createMBServerConnectorFactory) {
		this.createMBServerConnectorFactory = createMBServerConnectorFactory;
	}

	/** Returns whether the threaded is enabled.
	 * @return the result
	 */
	public boolean isThreaded() {
		return threaded;
	}

	/** Sets the threaded.
	 * @param threaded the threaded
	 */
	public void setThreaded(boolean threaded) {
		this.threaded = threaded;
	}

	/** Returns whether the daemon is enabled.
	 * @return the result
	 */
	public boolean isDaemon() {
		return daemon;
	}

	/** Sets the daemon.
	 * @param daemon the daemon
	 */
	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	/** Returns the server name.
	 * @return the result
	 */
	public String getServerName() {
		return serverName;
	}

	/** Sets the server name.
	 * @param serverName the serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/** Returns the jmx service u r l.
	 * @return the result
	 */
	public String getJmxServiceURL() {
		return jmxServiceURL;
	}

	/** Sets the jmx service u r l.
	 * @param jmxServiceURL the jmxServiceURL
	 */
	public void setJmxServiceURL(String jmxServiceURL) {
		this.jmxServiceURL = jmxServiceURL;
	}

}