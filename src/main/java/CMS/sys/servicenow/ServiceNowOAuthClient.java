package CMS.sys.servicenow;

import CMS.sys.servicenow.config.ServiceNowProperties;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ServiceNowOAuthClient {

    private final WebClient webClient;
    private final ServiceNowProperties properties;

    private volatile String accessToken;
    private volatile long expiresAt;

    public ServiceNowOAuthClient(WebClient serviceNowWebClient, ServiceNowProperties properties) {
        this.webClient = serviceNowWebClient;
        this.properties = properties;
    }

    public Mono<String> accessToken() {
        if (accessToken == null || System.currentTimeMillis() >= expiresAt) {
            return refreshAccessToken();
        }
        return Mono.just(accessToken);
    }

    private Mono<String> refreshAccessToken() {
        String clientId = properties.clientId();
        String clientSecret = properties.clientSecret();
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            accessToken = null;
            expiresAt = 0;
            return Mono.empty();
        }

        return webClient.post()
                .uri("/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(Mono.just("grant_type=client_credentials"), String.class)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(body -> {
                    String token = extractString(body, "access_token");
                    String expiresIn = extractString(body, "expires_in");
                    long ttl = parseLongSafe(expiresIn, 3600) - 60;
                    this.accessToken = token;
                    this.expiresAt = System.currentTimeMillis() + Math.max(ttl, 0) * 1000;
                })
                .map(body -> accessToken);
    }

    private static String extractString(Map<?, ?> body, String key) {
        Object value = body.get(key);
        return value == null ? null : value.toString();
    }

    private static long parseLongSafe(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
