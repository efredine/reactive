package fredine.reactive.controller;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.PlatformResponse;
import fredine.reactive.client.OAuth2PlatformClientFactory;
import fredine.reactive.client.SessionTokenStore;
import fredine.reactive.client.TokenStore;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.session.annotation.SessionValue;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


/**
 * @author dderose
 *
 */
@Controller("/")
public class RevokeTokenController {

    @Inject
	private OAuth2PlatformClientFactory factory;
	
	private static final Logger logger = LoggerFactory.getLogger(RevokeTokenController.class);
	

    @Get("/revokeToken")
    public String revokeToken(Session session) {

        TokenStore tokenStore = new SessionTokenStore(session);
        String failureMsg="Failed";
    	      
        try {

        	OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
            String refreshToken = tokenStore.getRefreshToken();
            PlatformResponse response  = client.revokeToken(refreshToken);
            logger.info("raw result for revoke token request= " + response.getStatus());
            return new JSONObject().put("response", "Revoke successful").toString();
        }
        catch (Exception ex) {
        	logger.error("Exception while calling revokeToken ", ex);
        	return new JSONObject().put("response",failureMsg).toString();
        }    
        
    }

}
