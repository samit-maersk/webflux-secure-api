package com.example.webfluxsecureapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;
import reactor.test.StepVerifier;

import java.nio.file.AccessDeniedException;

@SpringBootTest
@AutoConfigureWebTestClient
class WebfluxSecureApiApplicationTests {
	@Autowired
	MessageService messageService;
	@Autowired
	WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
	}
	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("user role=ADMIN can only access getAdminMessage")
	@WithMockUser(roles = "ADMIN")
	void getAdminMessageTest() {
		messageService.getAdminMessage()
				.as(StepVerifier::create)
				.consumeNextWith(message -> Assertions.assertEquals("Hello Admin", message.message()))
				.verifyComplete();
	}

	@Test
	@DisplayName("user role=USER can't access getAdminMessage")
	@WithMockUser(roles = "USER")
	void getAdminMessageForbidenTest() {
		messageService.getAdminMessage()
				.as(StepVerifier::create)
				.expectError()
				.verify();
	}

	@Test
	@DisplayName("user with role=ADMIN, USER can able to access getUserMessage")
	@WithMockUser(roles = {"ADMIN", "USER"})
	void getUserMessageTest() {
		messageService.getUserMessage()
				.as(StepVerifier::create)
				.consumeNextWith(message -> Assertions.assertEquals("Hello User", message.message()))
				.verifyComplete();
	}

	@Test
	@DisplayName("/user router 401 Test")
	void userRouterTest() {
		webTestClient
				.get()
				.uri("/user")
				.exchange()
				.expectStatus()
				.isUnauthorized();
	}

	@Test
	@DisplayName("/user router Test")
	@WithMockUser(roles = {"ADMIN", "USER"})
	void userRouterTest01() {
		webTestClient
				.get()
				.uri("/user")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("""
						{
							"message": "Hello User"
						}
						""");
	}

	@Test
	@DisplayName("/admin router Test")
	@WithMockUser(roles = {"ADMIN"})
	void adminRouterTest() {
		webTestClient
				.get()
				.uri("/admin")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("""
						{
							"message": "Hello Admin"
						}
						""");
	}

	@Test
	@DisplayName("/admin router can not access by user with role=USER")
	@WithMockUser(roles = {"USER"})
	void adminRouterTest01() {
		webTestClient
				.get()
				.uri("/admin")
				.exchange()
				.expectStatus()
				.isForbidden();
	}
}
