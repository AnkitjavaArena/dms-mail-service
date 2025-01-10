package com.otsMail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "email")
@Data
public class EmailConfig {

	private String username;
	private String password;
	private String host;
	@Value("${EMAIL_PORT}")
	private int port;
	private String from;
	private String subject;
	private String attachmentname;

}
