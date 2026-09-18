package com.rtravez.auth.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@SpringBootTest
class AuthServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RegisteredClientRepository registeredClientRepository;

	@Test
	void webClientIsConfiguredForRefreshTokens() {
		var client = registeredClientRepository.findByClientId("MSC-WEB");

		assertThat(client).isNotNull();
		if (client == null) {
			return;
		}
		assertThat(client.getClientAuthenticationMethods()).contains(ClientAuthenticationMethod.NONE);
		assertThat(client.getAuthorizationGrantTypes())
				.contains(AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN);
		assertThat(client.getRedirectUris())
				.contains("http://localhost:4200/callback", "https://oauth.pstmn.io/v1/browser-callback");
		assertThat(client.getClientSettings().isRequireProofKey()).isTrue();
		assertThat(client.getScopes()).contains("offline_access");
		assertThat(client.getTokenSettings().getRefreshTokenTimeToLive()).isEqualTo(Duration.ofDays(1));
	}

	@Test
	void mscClientIsConfiguredForClientCredentials() {
		var client = registeredClientRepository.findByClientId("MSC-WS");

		assertThat(client).isNotNull();
		if (client == null) {
			return;
		}
		assertThat(client.getClientAuthenticationMethods()).contains(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
		assertThat(client.getAuthorizationGrantTypes()).contains(AuthorizationGrantType.CLIENT_CREDENTIALS);
		assertThat(client.getScopes()).contains("openid", "profile", "email");
	}

	@Test
	void msaClientIsConfiguredForClientCredentials() {
		var client = registeredClientRepository.findByClientId("MSA-WS");

		assertThat(client).isNotNull();
		if (client == null) {
			return;
		}
		assertThat(client.getClientAuthenticationMethods()).contains(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
		assertThat(client.getAuthorizationGrantTypes()).contains(AuthorizationGrantType.CLIENT_CREDENTIALS);
		assertThat(client.getScopes()).contains("openid", "profile", "email");
	}

}
