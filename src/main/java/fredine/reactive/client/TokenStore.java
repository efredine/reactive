package fredine.reactive.client;

import java.util.Optional;

public interface TokenStore {
    String getCSRFToken();
    void setCSRFToken(String csrfToken);

    String getRefreshToken();
    void setRefreshToken(String refreshToken);

    String getAccessToken();
    void setAccessToken(String accessToken);

    Optional<String> getRealmId();
    void setRealmId(String realmId);
}
