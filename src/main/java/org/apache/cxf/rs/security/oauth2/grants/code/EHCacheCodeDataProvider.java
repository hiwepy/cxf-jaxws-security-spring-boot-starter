package org.apache.cxf.rs.security.oauth2.grants.code;

import org.apache.cxf.Bus;

public class EHCacheCodeDataProvider extends DefaultEHCacheCodeDataProvider {

	public EHCacheCodeDataProvider() {
		super();
	}

	public EHCacheCodeDataProvider(String configFileURL, Bus bus) {
		super(configFileURL, bus);
	}

	public EHCacheCodeDataProvider(String configFileURL, Bus bus, String clientCacheKey, String codeCacheKey,
			String accessTokenKey, String refreshTokenKey) {
		super(configFileURL, bus, clientCacheKey, codeCacheKey, accessTokenKey, refreshTokenKey);
	}

}
