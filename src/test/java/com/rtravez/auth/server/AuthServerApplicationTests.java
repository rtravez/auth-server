package com.rtravez.auth.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RegisteredClientRepository registeredClientRepository;

	@Test
	void webClientIsConfiguredForRefreshTokens() {
		var client = registeredClientRepository.findByClientId("rtravez-web");

		assertThat(client).isNotNull();
		assertThat(client.getAuthorizationGrantTypes())
				.contains(AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN);
		assertThat(client.getScopes()).contains("offline_access");
		assertThat(client.getTokenSettings().getRefreshTokenTimeToLive()).isEqualTo(Duration.ofDays(1));
	}

}
