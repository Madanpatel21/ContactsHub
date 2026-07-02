package CMS.sys.servicenow.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(ServiceNowProperties.class)
public class ServiceNowConfig {

    @Bean
    public WebClient serviceNowWebClient(ServiceNowProperties properties) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Accept", "application/json")
                .exchangeStrategies(strategies)
                .build();
    }
}
