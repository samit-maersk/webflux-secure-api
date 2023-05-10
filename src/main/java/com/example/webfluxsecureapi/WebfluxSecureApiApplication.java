package com.example.webfluxsecureapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebfluxSecureApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxSecureApiApplication.class, args);
	}

	@Bean
	RouterFunction routerFunction(MessageService messageService) {
		return RouterFunctions
				.route()
				.GET("/admin", request -> ServerResponse.ok().body(messageService.getAdminMessage(), Message.class))
				.GET("/user", request -> ServerResponse.ok().body(messageService.getUserMessage(), Message.class))
				.build();
	}

}

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {
	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
		http.authorizeExchange((authorize) -> authorize
						.anyExchange().authenticated()
				)
				.oauth2ResourceServer(oauth2ResourceServer ->
						oauth2ResourceServer
								.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()));
		return http.build();
	}

	private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
		var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
		var jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
	}
}
record Message(String message) {}

@Service
class MessageService {
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<Message> getAdminMessage() {
		return Mono.just(new Message("Hello Admin"));
	}

	@PreAuthorize("hasRole('USER')")
	public Mono<Message> getUserMessage() {
		return Mono.just(new Message("Hello User"));
	}
}
