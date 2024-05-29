package com.nampd.chat.jwt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtService jwtService;

    public JwtHandshakeInterceptor() {
    }

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) throws Exception {

        // Get the query parameters from the URI
        URI uri = request.getURI();
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
        String authHeader = queryParams.getFirst("token");

//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null) {
            // Decode the URL encoded token
            authHeader = URLDecoder.decode(authHeader, StandardCharsets.UTF_8);

//            System.out.println("Authorization Header: " + authHeader);

            if (authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);

                if (jwtService.isTokenValid(jwt)) {
                    return true;
                }
            }
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception
    ) {
//        String token = request.getHeaders().getFirst("Authorization");
        //Ko cần triển khai
    }
}
