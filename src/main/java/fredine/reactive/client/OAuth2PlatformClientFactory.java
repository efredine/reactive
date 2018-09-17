package fredine.reactive.client;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.Environment;
import com.intuit.oauth2.config.OAuth2Config;
import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class OAuth2PlatformClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2PlatformClientFactory.class);


    @Value("${oauth.client.id}")
	private
	String clientId;

	@Value("${oauth.client.secret}")
	private
	String clientSecret;


	private OAuth2PlatformClient client;

	private OAuth2Config oauth2Config;
	
	@PostConstruct
	public void init() {

	    logger.info(String.format(">>>>>>>>>>> clientId: %s", clientId));
		// initialize a single thread executor, this will ensure only one thread processes the queue
		oauth2Config = new OAuth2Config.OAuth2ConfigBuilder(clientId, clientSecret) //set client id, secret
				.callDiscoveryAPI(Environment.SANDBOX) // call discovery API to populate urls
				.buildConfig();
		client  = new OAuth2PlatformClient(oauth2Config);
	}
	
	
	public OAuth2PlatformClient getOAuth2PlatformClient()  {
		return client;
	}
	
	public OAuth2Config getOAuth2Config()  {
		return oauth2Config;
	}

}
