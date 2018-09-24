package fredine.reactive.controller;

import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.exception.InvalidRequestException;
import fredine.reactive.client.OAuth2PlatformClientFactory;
import fredine.reactive.client.SessionTokenStore;
import fredine.reactive.client.TokenStore;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.session.annotation.SessionValue;
import io.micronaut.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author dderose
 *
 */
@Controller("/")
public class OAuth2Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

	@Value("${oauth.redirect.url}")
	private String redirectUri;
	
	@Inject
	private
	OAuth2PlatformClientFactory factory;

	@View("home")
	@Get
	public HttpResponse home() {
		return HttpResponse.ok(Collections.emptyMap());
	}

	@View("connected")
	@Get("/connected")
	public HttpResponse connected(@SessionValue Optional<String> givenName) {

		return HttpResponse.ok(givenName.map(name -> CollectionUtils.mapOf("givenName", name)).orElse(Collections.emptyMap()));
	}

	@Get("/connectToQuickbooks")
	public HttpResponse connectToQuickbooks(Session session) {
		logger.info("inside connectToQuickbooks ");
		return getAuthorization(session, Collections.singletonList(Scope.Accounting));
	}

	@Get("/signInWithIntuit")
	public HttpResponse signInWithIntuit(Session session) {
		logger.info("inside signInWithIntuit ");
		return getAuthorization(session, Collections.singletonList(Scope.OpenIdAll));
	}

	@Get("/getAppNow")
	public HttpResponse getAppNow(Session session) {
        logger.info("inside getAppNow "  );
        return getAuthorization(session, Arrays.asList(Scope.OpenIdAll, Scope.Accounting));
	}

	private HttpResponse getAuthorization(Session session, List<Scope> scopes) {
        TokenStore tokenStore = new SessionTokenStore(session);
        OAuth2Config oauth2Config = factory.getOAuth2Config();

        String csrf = oauth2Config.generateCSRFToken();
        tokenStore.setCSRFToken(csrf);

        try {
            URI finalURI = new URI(oauth2Config.prepareUrl(scopes, redirectUri, csrf));
            return HttpResponse.status(HttpStatus.FOUND)
                    .headers((headers) ->
                            headers.location(finalURI)
                    );
        } catch (URISyntaxException | InvalidRequestException e) {
            logger.error("Exception authorizing", e);
        }
        return null;
    }
}
