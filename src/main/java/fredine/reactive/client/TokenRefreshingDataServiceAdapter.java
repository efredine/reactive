package fredine.reactive.client;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;

public class TokenRefreshingDataServiceAdapter {

    private OAuth2PlatformClientFactory factory;
    private TokenStore tokenStore;

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws FMSException, OAuthException;
    }

    TokenRefreshingDataServiceAdapter(OAuth2PlatformClientFactory factory, TokenStore tokenStore) {
        this.factory = factory;
        this.tokenStore = tokenStore;
    }

    public QueryResult executeQuery(String query) throws FMSException, OAuthException {
        return withRefresh((dataService) -> dataService.executeQuery(query));
    }

    private <R> R withRefresh(CheckedFunction<DataService, R> callable) throws FMSException, OAuthException {
        try {
            return callable.apply(getDataService(tokenStore.getRealmId(), tokenStore.getAccessToken()));
        } catch (InvalidTokenException e) {
            OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();

            BearerTokenResponse bearerTokenResponse = client.refreshToken(tokenStore.getRefreshToken());
            tokenStore.setAccessToken(bearerTokenResponse.getAccessToken());
            tokenStore.setRefreshToken(bearerTokenResponse.getRefreshToken());
            return callable.apply(getDataService(tokenStore.getRealmId(), tokenStore.getAccessToken()));
        }
    }

    private DataService getDataService(String realmId, String accessToken) throws FMSException {
        OAuth2Authorizer oauth = new OAuth2Authorizer(accessToken);
        Context context = new Context(oauth, ServiceType.QBO, realmId);
        return new DataService(context);
    }
}
