package CMS.sys.servicenow.controller;

import CMS.sys.constant.ApiConstants;
import CMS.sys.dto.ApiResponse;
import CMS.sys.servicenow.config.ServiceNowProperties;
import CMS.sys.servicenow.ServiceNowClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(ApiConstants.API_VERSION + "/servicenow")
public class ServiceNowController {

    private final ServiceNowClient serviceNowClient;
    private final ServiceNowProperties serviceNowProperties;

    public ServiceNowController(ServiceNowClient serviceNowClient, ServiceNowProperties serviceNowProperties) {
        this.serviceNowClient = serviceNowClient;
        this.serviceNowProperties = serviceNowProperties;
    }

    @Operation(summary = "Ping ServiceNow instance")
    @GetMapping("/ping")
    public Mono<ResponseEntity<ApiResponse<String>>> ping() {
        return serviceNowClient.ping()
                .map(body -> ResponseEntity.ok(ApiResponse.success("ServiceNow reachable: " + body, "Ping successful")))
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(ApiResponse.error("ServiceNow unreachable: " + ex.getMessage()))));
    }

    @Operation(summary = "Create a ServiceNow contact from payload", description = "Forwards a contact payload to ServiceNow contact table")
    @PostMapping("/contacts")
    public Mono<ResponseEntity<ApiResponse<String>>> createContact(@RequestBody Map<String, Object> payload) {
        return serviceNowClient.createContactRecord(payload)
                .map(body -> ResponseEntity.ok(ApiResponse.success(body, "Forwarded to ServiceNow")))
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(ApiResponse.error("ServiceNow create failed: " + ex.getMessage()))));
    }

    @Operation(summary = "Get ServiceNow configuration status")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status() {
        Map<String, Object> status = Map.of(
                "baseUrl", serviceNowProperties.baseUrl() == null ? "not-configured" : serviceNowProperties.baseUrl(),
                "username", serviceNowProperties.username() == null || serviceNowProperties.username().isBlank() ? "not-configured" : "configured",
                "contactTable", serviceNowProperties.contactTable(),
                "interactionTable", serviceNowProperties.interactionTable()
        );
        return ResponseEntity.ok(ApiResponse.success(status, "ServiceNow integration status"));
    }
}
