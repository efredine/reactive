package fredine.reactive.services;

import fredine.reactive.client.SessionTokenStore;
import fredine.reactive.client.TokenStore;
import io.micronaut.core.bind.ArgumentBinder;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.convert.value.MutableConvertibleValues;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.server.binding.binders.TypedRequestArgumentBinder;
import io.micronaut.session.Session;
import io.micronaut.session.SessionStore;
import io.micronaut.session.http.HttpSessionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Optional;

@SuppressWarnings("unused")
@Singleton
public class TokenStoreSessionArgumentBinder implements TypedRequestArgumentBinder<TokenStore> {

    private final SessionStore<Session> sessionStore;

    private static final Logger logger = LoggerFactory.getLogger(TokenStoreSessionArgumentBinder.class);

    public TokenStoreSessionArgumentBinder(SessionStore<Session> sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public Argument<TokenStore> argumentType() {
        return Argument.of(TokenStore.class);
    }

    @Override
    public BindingResult<TokenStore> bind(ArgumentConversionContext<TokenStore> context, HttpRequest<?> source) {
        if (!source.getAttributes().contains(OncePerRequestHttpServerFilter.getKey(HttpSessionFilter.class))) {
            // the filter hasn't been executed
            //noinspection unchecked
            return ArgumentBinder.BindingResult.EMPTY;
        }

        MutableConvertibleValues<Object> attrs = source.getAttributes();
        Optional<Session> existing = attrs.get(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class);

        logger.info("Calling logger, session " + (existing.isPresent() ? "is present" : "missing"));

        if (existing.isPresent()) {
            return () -> Optional.of(new SessionTokenStore(existing.get()));
        } else {
            Session newSession = sessionStore.newSession();
            attrs.put(HttpSessionFilter.SESSION_ATTRIBUTE, newSession);
            return () -> Optional.of(new SessionTokenStore(newSession));
        }
    }
}
