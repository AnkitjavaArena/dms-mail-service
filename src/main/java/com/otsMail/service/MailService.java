package com.otsMail.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.otsMail.config.EmailConfig;
import com.otsMail.model.Receipient;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class MailService {

	private final @NonNull EmailConfig mailConfig;
	private static String salutation = "Team";

	private JavaMailSender getMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailConfig.getHost());
		mailSender.setPort(mailConfig.getPort());
		mailSender.setUsername(mailConfig.getUsername());
		mailSender.setPassword(mailConfig.getPassword());

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false"); // Set to true if you want email logs

		return mailSender;
	}

	@Async
	public void sendEmailToRecipient(Receipient recipient) {
		this.sendEmail(recipient);
	}

	public String getEmailContent(String salutation) throws IOException {
		String template = new String(
				Files.readAllBytes(Paths.get(new ClassPathResource("email-template.html").getURI())));
		return template.replace("{{salutation}}", salutation);
	}

	public void sendEmail(Receipient recipient) {
		try {
			JavaMailSender mailSender = getMailSender();
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(mailConfig.getFrom());
			helper.setTo(recipient.getEmail());
			helper.setSubject(mailConfig.getSubject());

			if (recipient.getSalutation() != null && !(recipient.getSalutation().isBlank())) {
				salutation = recipient.getSalutation();
			}
			String emailContent = getEmailContent(salutation);
			helper.setText(emailContent, true);
			ClassPathResource attachmenLocation = new ClassPathResource("Ankit_Resume_Java.pdf");
			if (attachmenLocation.exists()) {
				helper.addAttachment(mailConfig.getAttachmentname(), attachmenLocation);
			} else {
				throw new IOException("Referred file not found in classpath.");
			}
			mailSender.send(message);
			log.info("mail sent: {}", recipient.getEmail());
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
			log.info("Fail to send mail to :{}", recipient.getEmail());
		}

	}
}
