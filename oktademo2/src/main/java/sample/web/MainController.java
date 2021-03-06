/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.web;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


@RestController
public class MainController {
	private final WebClient webClient;

	public MainController(WebClient webClient) {
		this.webClient = webClient;
	}

	@GetMapping("/")
	public String index(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
		model.addAttribute("userName", authorizedClient.getPrincipalName());
		model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
		return "index";
	}
	@GetMapping("/test")
	public String test(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
		return "test";
	}
	  @GetMapping("/admin")
	  @PreAuthorize("hasAuthority('Administrators')")
	  public String admin(java.security.Principal user) {
	    return "Hello, " + user.getName() + ". Would you like to play a game?";
	  }
	@GetMapping("/userinfo")
	@PreAuthorize("hasAuthority('Administrators')")
	public String userinfo(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
		String userInfoEndpointUri = authorizedClient.getClientRegistration()
				.getProviderDetails().getUserInfoEndpoint().getUri();
		Map userAttributes = this.webClient
				.get()
				.uri(userInfoEndpointUri)
				.attributes(oauth2AuthorizedClient(authorizedClient))
				.retrieve()
				.bodyToMono(Map.class)
				.block();
		model.addAttribute("userAttributes", userAttributes);
		return "userinfo";
	}
}