package fredine.reactive.controller;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.data.UserInfoResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.intuit.oauth2.exception.OpenIdException;
import fredine.reactive.client.OAuth2PlatformClientFactory;
import fredine.reactive.client.SessionTokenStore;
import fredine.reactive.client.TokenStore;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.hateos.JsonError;
import io.micronaut.http.hateos.Link;
import io.micronaut.session.Session;
import io.micronaut.session.annotation.SessionValue;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


@Controller("/")
public class CallbackController {

    @Value("${oauth.redirect.url}")
    private String redirectUri;

	@Inject
	private OAuth2PlatformClientFactory factory;

    private static final Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @Get("/oauth2redirect")
    public HttpResponse callBackFromOAuth(
            @QueryValue("code") String authCode,
            @QueryValue("state") String state,
            @QueryValue("realmId") Optional<String> realmId,
            Session session)
    {
        logger.info("inside oauth2redirect of sample"  );
        TokenStore tokenStore = new SessionTokenStore(session);
        try {
	        if (tokenStore.getCSRFToken().equals(state)) {
                realmId.ifPresent(tokenStore::setRealmId);
	            session.put("auth_code", authCode);
	
	            OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
	            logger.info("inside oauth2redirect of sample -- redirectUri " + redirectUri  );
	            BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);

	            tokenStore.setAccessToken(bearerTokenResponse.getAccessToken());
	            tokenStore.setRefreshToken(bearerTokenResponse.getRefreshToken());

	            /*
	                Update your Data store here with user's AccessToken and RefreshToken along with the realmId
	    
	                However, in case of OpenIdConnect, when you request OpenIdScopes during authorization,
	                you will also receive IDToken from Intuit.
	                You first need to validate that the IDToken actually came from Intuit.
	             */
	    
	            if (StringUtils.isNotBlank(bearerTokenResponse.getIdToken())) {
	               try {
						if(client.validateIDToken(bearerTokenResponse.getIdToken())) {
						       logger.info("IdToken is Valid");
						       //get user info
						       saveUserInfo(bearerTokenResponse.getAccessToken(), session, client);
						   }
					} catch (OpenIdException e) {
						logger.error("Exception validating id token ", e);
					}
	            }
	            URI redirectURI = new URIBuilder()
                        .setPath("/connected")
                        .build();
	            return HttpResponse.redirect(redirectURI);
	        }
	        logger.info("csrf token mismatch " );
        } catch (OAuthException e) {
        	logger.error("Exception in callback handler ", e);
		} catch (URISyntaxException e) {
            logger.error("Unexpected URI syntax error: ", e);
        }
        return HttpResponse.badRequest();
    }

    
    private void saveUserInfo(String accessToken, Session session, OAuth2PlatformClient client) {
        //Ideally you would fetch the realmId and the accessToken from the data store based on the user account here.
 
        try {
        	UserInfoResponse response = client.getUserInfo(accessToken); 

        	session.put("sub", response.getSub());
            session.put("givenName", response.getGivenName());
            session.put("email", response.getEmail());
            
        }
        catch (Exception ex) {
            logger.error("Exception while retrieving user info ", ex);
        }
    }

}
