package fredine.reactive.client;

import io.micronaut.session.Session;

import java.util.Optional;

public class SessionTokenStore implements TokenStore {

    private Session session;

    public SessionTokenStore(Session session) {
        this.session = session;
    }

    @Override
    public String getCSRFToken() {
        return getRequiredValueByName("csrfToken");
    }

    @Override
    public void setCSRFToken(String csrfToken) {
        this.session.put("csrfToken", csrfToken);
    }

    @Override
    public String getRefreshToken() {
        return getRequiredValueByName("refreshToken");
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.session.put("refreshToken", refreshToken);
    }

    @Override
    public String getAccessToken() {
        return getRequiredValueByName("accessToken");
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.session.put("accessToken", accessToken);
    }

    @Override
    public String getRealmId() {
        return getRequiredValueByName("realmId");
    }

    @Override
    public void setRealmId(String realmId) {
        this.session.put("realmId", realmId);
    }

    private String getRequiredValueByName(String tokenKey)
    {
        return session.get(tokenKey).map(token -> (String) token).orElseThrow(() -> new IllegalStateException(
                String.format("Token '%s' not found in session.", tokenKey)
        ));
    }
}
