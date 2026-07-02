package CMS.sys.servicenow;

import CMS.sys.servicenow.config.ServiceNowProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceNowClient {

    private final WebClient webClient;
    private final ServiceNowProperties properties;
    private final ServiceNowOAuthClient oAuthClient;

    public ServiceNowClient(WebClient serviceNowWebClient, ServiceNowProperties properties, ServiceNowOAuthClient oAuthClient) {
        this.webClient = serviceNowWebClient;
        this.properties = properties;
        this.oAuthClient = oAuthClient;
    }

    public Mono<String> createContactRecord(Map<String, Object> payload) {
        return authenticated()
                .flatMap(authHeader -> webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/api/now/table/{table}").build(properties.contactTable()))
                        .header("Authorization", authHeader)
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(String.class)
                )
                .doOnSuccess(body -> logInfo("Created ServiceNow contact record: {}", body))
                .doOnError(error -> logWarn("ServiceNow create contact failed: {}", error.getMessage()));
    }

    public Mono<String> updateContactRecord(String sysId, Map<String, Object> payload) {
        return authenticated()
                .flatMap(authHeader -> webClient.put()
                        .uri(uriBuilder -> uriBuilder.path("/api/now/table/{table}/{sysId}").build(properties.contactTable(), sysId))
                        .header("Authorization", authHeader)
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(String.class)
                )
                .doOnSuccess(body -> logInfo("Updated ServiceNow contact record: {}", body))
                .doOnError(error -> logWarn("ServiceNow update contact failed: {}", error.getMessage()));
    }

    public Mono<String> ping() {
        return webClient.get()
                .uri("/api/now/table/sys_user?sysparm_limit=1")
                .retrieve()
                .bodyToMono(String.class);
    }

    private Mono<String> authenticated() {
        if (properties.username() != null && !properties.username().isBlank() && properties.password() != null && !properties.password().isBlank()) {
            return basicAuthHeader();
        }
        return oAuthClient.accessToken()
                .map(token -> "Bearer " + token)
                .doOnError(error -> Mono.<String>empty());
    }

    private Mono<String> basicAuthHeader() {
        String credential = properties.username() + ":" + properties.password();
        String encoded = java.util.Base64.getEncoder().encodeToString(credential.getBytes());
        return Mono.just("Basic " + encoded);
    }

    private void logInfo(String message, Object... args) {
        org.slf4j.LoggerFactory.getLogger(ServiceNowClient.class).info(message, args);
    }

    private void logWarn(String message, Object... args) {
        org.slf4j.LoggerFactory.getLogger(ServiceNowClient.class).warn(message, args);
    }
}
