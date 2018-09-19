package fredine.reactive.client;

import java.util.Optional;

public interface TokenStore {
    public String getCSRFToken();
    public void setCSRFToken(String csrfToken);

    public String getRefreshToken();
    public void setRefreshToken(String refreshToken);

    public String getAccessToken();
    public void setAccessToken(String accessToken);

    public Optional<String> getRealmId();
    public void setRealmId(String realmId);
}
