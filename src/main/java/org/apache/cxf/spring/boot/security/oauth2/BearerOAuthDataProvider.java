package com.zfsoft.ws.cxf.security.oauth2;

import java.util.List;

import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
 
public class BearerOAuthDataProvider implements AuthorizationCodeDataProvider {

	@Override
	public Client getClient(String clientId) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerAccessToken createAccessToken(AccessTokenRegistration reg)
        throws OAuthServiceException {
 
        ServerAccessToken token = new BearerAccessToken(reg.getClient(), 3600L);
         
        List<String> scope = reg.getApprovedScope().isEmpty() ? reg.getRequestedScope() 
                                                        : reg.getApprovedScope();
        token.setScopes(convertScopeToPermissions(reg.getClient(), scope));
        token.setSubject(reg.getSubject());
        token.setGrantType(reg.getGrantType());
         
        // persist or encrypt and return
 
        return token;
   }

	@Override
	public ServerAccessToken getAccessToken(String accessToken) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerAccessToken getPreauthorizedToken(Client client, List<String> requestedScopes, UserSubject subject,
			String grantType) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerAccessToken refreshAccessToken(Client client, String refreshToken, List<String> requestedScopes)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServerAccessToken> getAccessTokens(Client client, UserSubject subject) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RefreshToken> getRefreshTokens(Client client, UserSubject subject) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void revokeToken(Client client, String tokenId, String tokenTypeHint) throws OAuthServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<OAuthPermission> convertScopeToPermissions(Client client, List<String> requestedScopes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerAuthorizationCodeGrant createCodeGrant(AuthorizationCodeRegistration reg)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerAuthorizationCodeGrant removeCodeGrant(String code) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServerAuthorizationCodeGrant> getCodeGrants(Client c, UserSubject subject)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}
 
     
	
}