package fredine.reactive.controller;

import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.exception.InvalidRequestException;
import fredine.reactive.client.OAuth2PlatformClientFactory;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.session.Session;
import io.micronaut.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
		return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", false));
	}

	@View("connected")
	@Get("/connected")
	public HttpResponse connected() {
		return HttpResponse.ok();
	}

	@Get("/connectToQuickbooks")
	public HttpResponse connectToQuickbooks(Session session) {
		logger.info("inside connectToQuickbooks ");
		OAuth2Config oauth2Config = factory.getOAuth2Config();
		
		String csrf = oauth2Config.generateCSRFToken();
		session.put("csrfToken", csrf);
		try {
			List<Scope> scopes = new ArrayList<Scope>();
			scopes.add(Scope.Accounting);
			URI finalURI = new URI(oauth2Config.prepareUrl(scopes, redirectUri, csrf));
            return HttpResponseFactory.INSTANCE.status(HttpStatus.FOUND)
                    .headers((headers) ->
                            headers.location(finalURI)
                    );
		} catch (URISyntaxException | InvalidRequestException e) {
			logger.error("Exception calling connectToQuickbooks ", e);
		}
		return null;
	}

	@Get("/signInWithIntuit")
	public HttpResponse signInWithIntuit(Session session) {
		logger.info("inside signInWithIntuit ");
		OAuth2Config oauth2Config = factory.getOAuth2Config();
		
		String csrf = oauth2Config.generateCSRFToken();
		session.put("csrfToken", csrf);
		
		try {
			List<Scope> scopes = new ArrayList<Scope>();
			scopes.add(Scope.OpenIdAll);
			// todo: convert this one too
			return HttpResponse.redirect(new URI(oauth2Config.prepareUrl(scopes, redirectUri, csrf)));
		} catch (URISyntaxException | InvalidRequestException e) {
			logger.error("Exception calling signInWithIntuit ", e);
		}
		return null;
		
	}

	@Get("/getAppNow")
	public HttpResponse getAppNow(Session session) {
		logger.info("inside getAppNow "  );
		OAuth2Config oauth2Config = factory.getOAuth2Config();
		
		String csrf = oauth2Config.generateCSRFToken();
		session.put("csrfToken", csrf);

		try {
			List<Scope> scopes = new ArrayList<>();
			scopes.add(Scope.OpenIdAll);
			scopes.add(Scope.Accounting);
            URI finalURI = new URI(oauth2Config.prepareUrl(scopes, redirectUri, csrf));
            return HttpResponseFactory.INSTANCE.status(HttpStatus.FOUND)
                    .headers((headers) ->
                            headers.location(finalURI)
                    );
		} catch (URISyntaxException | InvalidRequestException e) {
			logger.error("Exception calling getAppNow ", e);
		}
		return null;
	}
}
