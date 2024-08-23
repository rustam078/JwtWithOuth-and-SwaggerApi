package com.abc.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfigDoc {

	@Bean
	public OpenAPI openAPI() {

		return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("bearerScheme"))
				.components(new Components().addSecuritySchemes("bearerScheme",
						new SecurityScheme().name("bearerScheme").type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")))
				.info(new Info().title("JWT Spring Security API").description(
						"Implementing Jwt , providing endpoints for creating, analyzing, and accessing insights.")
						.version("2.0")
						.contact(new Contact().name("Rustam Ali").email("rustam@voicera-analytics.in")
								.url("www.voicera-analytics.com"))
						.license(new License().name("Apache 2.0")))
				.servers(List.of(new Server().url("http://localhost:8080").description("Local"),
						new Server().url("https://mpairavat.in/learningPortal").description("Dev Server")))
				.externalDocs(new ExternalDocumentation().url("https://springdoc.org/migrating-from-springfox.html")
						.description("Click Here to Know more about documentation."));
	}

}