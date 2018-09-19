package fredine.reactive.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Config;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import fredine.reactive.client.OAuth2PlatformClientFactory;
import fredine.reactive.client.SessionTokenStore;
import fredine.reactive.client.TokenStore;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.hateos.JsonError;
import io.micronaut.http.hateos.Link;
import io.micronaut.session.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@Controller("/")
public class QBOController {

	@Value("${intuit.accounting.api.host}")
	private String intuitAccountingAPIHost;

	@Inject
	private
	OAuth2PlatformClientFactory factory;
	
	private static final Logger logger = LoggerFactory.getLogger(QBOController.class);

	public class NoRealmIdException extends Exception {

        NoRealmIdException() {
            super("No realm ID in session.  QBO calls only work if the accounting scope was passed!");
        }
    }

    @Get("/getCompanyInfo")
    public String callQBOCompanyInfo(Session session) throws NoRealmIdException {
        TokenStore tokenStore = new SessionTokenStore(session);
        String realmId = tokenStore.getRealmId().orElseThrow(NoRealmIdException::new);
    	String failureMsg="Failed";
    	String url =  intuitAccountingAPIHost + "/v3/company";
        try {
        	
        	// set custom config
        	Config.setProperty(Config.BASE_URL_QBO, url);

    		//get DataService
    		DataService service = getDataService(realmId, tokenStore.getAccessToken());
			
			// get all companyinfo
			String sql = "select * from companyinfo";
			QueryResult queryResult = service.executeQuery(sql);
			return processResponse(failureMsg, queryResult);
			
		}
	        /*
	         * Handle 401 status code - 
	         * If a 401 response is received, refresh tokens should be used to get a new access token,
	         * and the API call should be tried again.
	         */
	        catch (InvalidTokenException e) {			
				logger.error("Error while calling executeQuery :: " + e.getMessage());
				
				//refresh tokens
	        	logger.info("received 401 during companyinfo call, refreshing tokens now");
	        	OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
	        	
				try {
					BearerTokenResponse bearerTokenResponse = client.refreshToken(tokenStore.getRefreshToken());
					tokenStore.setAccessToken(bearerTokenResponse.getAccessToken());
		            tokenStore.setRefreshToken(bearerTokenResponse.getRefreshToken());
		            
		            //call company info again using new tokens
		            logger.info("calling companyinfo using new tokens");
		            DataService service = getDataService(realmId, tokenStore.getAccessToken());
					
					// get all companyinfo
					String sql = "select * from companyinfo";
					QueryResult queryResult = service.executeQuery(sql);
					return processResponse(failureMsg, queryResult);
					
				} catch (OAuthException e1) {
					logger.error("Error while calling bearer token :: " + e.getMessage());
					return new JSONObject().put("response",failureMsg).toString();
				} catch (FMSException e1) {
					logger.error("Error while calling company currency :: " + e.getMessage());
					return new JSONObject().put("response",failureMsg).toString();
				}
	            
			} catch (FMSException e) {
				List<Error> list = e.getErrorList();
				list.forEach(error -> logger.error("Error while calling executeQuery :: " + error.getMessage()));
				return new JSONObject().put("response",failureMsg).toString();
			}
		
    }

    @io.micronaut.http.annotation.Error(global = true)
    public String error(HttpRequest request, Exception noRealmIdException) {
        JsonError error = new JsonError("Bad Things Happened: " + noRealmIdException.getMessage());
//                .link(Link.SELF, Link.of(request.getUri()));

//        return HttpResponse.<JsonError>serverError()
//                .body(error);
        return error.toString();
    }

	private String processResponse(String failureMsg, QueryResult queryResult) {
		if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
			CompanyInfo companyInfo = (CompanyInfo) queryResult.getEntities().get(0);
			logger.info("Companyinfo -> CompanyName: " + companyInfo.getCompanyName());
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(companyInfo);
			} catch (JsonProcessingException e) {
				logger.error("Exception while getting company info ", e);
				return new JSONObject().put("response", failureMsg).toString();
			}
			
		}
		return new JSONObject().put("response", failureMsg).toString();
	}

	private DataService getDataService(String realmId, String accessToken) throws FMSException {
		
		//create oauth object
		OAuth2Authorizer oauth = new OAuth2Authorizer(accessToken);
		//create context
		Context context = new Context(oauth, ServiceType.QBO, realmId);

		// create dataservice
		return new DataService(context);
	}

}
