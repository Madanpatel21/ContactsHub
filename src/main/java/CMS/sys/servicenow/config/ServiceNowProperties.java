package CMS.sys.servicenow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "servicenow")
public record ServiceNowProperties(
        String baseUrl,
        String username,
        String password,
        String clientId,
        String clientSecret,
        String contactTable,
        String interactionTable
) {
}
