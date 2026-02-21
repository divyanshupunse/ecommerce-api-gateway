package com.divyanshu.ecommerce_api_gateway.filter;

import com.divyanshu.ecommerce_api_gateway.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT Filter] Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            System.out.println("[JWT Filter] Invalid or expired token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String email = jwtUtil.getEmailFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        int userId = jwtUtil.getUserIdFromToken(token);


        System.out.println("[JWT Filter] Token: " + token);
        System.out.println("[JWT Filter] Email: " + email);
        System.out.println("[JWT Filter] Role: " + role);
        System.out.println("[JWT Filter] UserId: " + userId);


        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, List.of(authority));

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header("role", role)
                        .header("email", email)
                        .header("userId", String.valueOf(userId)))
                .build();

        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}
