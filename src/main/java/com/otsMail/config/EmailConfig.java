package com.otsMail.config;

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
	private String port;
	private String from;
	private String subject;
	private String attachmentname;
    private Boolean useHtmlTemplate = false;

}
