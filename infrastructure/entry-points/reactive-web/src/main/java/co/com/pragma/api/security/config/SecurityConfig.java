package co.com.pragma.api.security.config;

import co.com.pragma.api.security.filter.JwtValidationWebFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${spring.application.api-key}")
    private String apiKeyApp;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtValidationWebFilter jwtValidationWebFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/v1/healthcheck").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/applications/by-email-status").access(this::apiKeyOrJwtAuthorization)
                        .pathMatchers(HttpMethod.PUT, "/api/v1/application").access(this::apiKeyOrJwtAuthorization)
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public JwtValidationWebFilter jwtValidationWebFilter() {
        return new JwtValidationWebFilter();
    }

    private Mono<AuthorizationDecision> apiKeyOrJwtAuthorization(Mono<Authentication> authentication,
                                                                 AuthorizationContext context) {
        var request = context.getExchange().getRequest();

        String apiKey = request.getHeaders().getFirst("x-api-key");
        if (apiKeyApp.equalsIgnoreCase(apiKey)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        return authentication
                .map(auth -> new AuthorizationDecision(auth.isAuthenticated()))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}