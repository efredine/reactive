package fredine.reactive.controller;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import fredine.reactive.client.OAuth2PlatformClientFactory;
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
public class RefreshTokenController {
	
	@Inject
    private OAuth2PlatformClientFactory factory;
	
	private static final Logger logger = LoggerFactory.getLogger(RefreshTokenController.class);
	

    @Get("/refreshToken")
    public String refreshToken(@SessionValue("refresh_token") String refreshToken, Session session) {
		
    	String failureMsg="Failed";
 
        try {
        	
        	OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
        	BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
            session.put("access_token", bearerTokenResponse.getAccessToken());
            session.put("refresh_token", bearerTokenResponse.getRefreshToken());
            String jsonString = new JSONObject()
                    .put("access_token", bearerTokenResponse.getAccessToken())
                    .put("refresh_token", bearerTokenResponse.getRefreshToken()).toString();
            return jsonString;
        }
        catch (Exception ex) {
        	logger.error("Exception while calling refreshToken ", ex);
        	return new JSONObject().put("response",failureMsg).toString();
        }    
    }

}
