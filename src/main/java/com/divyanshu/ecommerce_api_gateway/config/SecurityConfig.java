package com.divyanshu.ecommerce_api_gateway.config;

import com.divyanshu.ecommerce_api_gateway.filter.JwtWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    private final JwtWebFilter jwtWebFilter;

    public SecurityConfig(JwtWebFilter jwtWebFilter) {
        this.jwtWebFilter = jwtWebFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.csrf(csrf -> csrf.disable());

        // Add JWT filter before authentication
        http.addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        http.authorizeExchange(exchange -> exchange

                .pathMatchers("/auth/**").permitAll()

                .pathMatchers(HttpMethod.POST, "/products/**")
                .hasRole("ADMIN")

                .pathMatchers(HttpMethod.PUT, "/products/**")
                .hasRole("ADMIN")

                .pathMatchers(HttpMethod.DELETE, "/products/**")
                .hasRole("ADMIN")

                .pathMatchers(HttpMethod.GET, "/products/**")
                .hasAnyRole("ADMIN", "CUSTOMER")


                .pathMatchers(HttpMethod.POST, "/orders/place")
                .hasRole("CUSTOMER")

                .pathMatchers(HttpMethod.GET, "/orders/my")
                .hasRole("CUSTOMER")

                .pathMatchers(HttpMethod.GET, "/orders/all")
                .hasRole("ADMIN")

                .pathMatchers(HttpMethod.PUT, "/orders/status/**")
                .hasRole("ADMIN")

                .pathMatchers(HttpMethod.PUT, "/orders/cancel/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .anyExchange().authenticated()
        );

        return http.build();
    }
}