package fredine.reactive.client;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataServiceFactory {

    @Inject
    private
    OAuth2PlatformClientFactory factory;

    public TokenRefreshingDataServiceAdapter getDataService(TokenStore tokenStore) {
        return new TokenRefreshingDataServiceAdapter(factory, tokenStore);
    }
}
