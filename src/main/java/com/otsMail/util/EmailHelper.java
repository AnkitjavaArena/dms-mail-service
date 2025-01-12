package com.otsMail.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otsMail.config.EmailConfig;
import com.otsMail.dao.EnrollRepository;
import com.otsMail.model.Enroll;
import com.otsMail.model.Recipient;
import com.otsMail.model.RecipientDetail;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class EmailHelper {
	private final @NonNull EmailConfig emailConfig;
	private final @NonNull EnrollRepository enrollRepository;
	private final @NonNull ObjectMapper objectMapper;

	public void registerRecipients(RecipientDetail recipientDetail) {
		List<Recipient> recipients = recipientDetail.getRecipients();
		Set<String> emailSet = recipients.stream()
                .map(Recipient::getEmail)
                .collect(Collectors.toSet());
		List<Enroll> existingEnrollments = enrollRepository.findByToIn(List.copyOf(emailSet));
		List<String> existingEmails = existingEnrollments.stream()
				                      .map(Enroll::getTo)
				                      .collect(Collectors.toList());
		
		existingEmails.forEach(email -> log.info("{} already Enrolled", email));
		List<Enroll> newEnrollments = this.filterUniqueReceipient(recipients).stream()
	                .filter(recipient -> !existingEmails.contains(recipient.getEmail()))  // Exclude existing emails
	                .map(recipient -> Enroll.builder()
	                        .salutation(this.getSalutationValue(recipient))
	                        .to(recipient.getEmail())
	                        .status(AppConstants.DEFAULT_STATUS)
	                        .time(LocalDateTime.now())
	                        .count(AppConstants.DEFAULT_COUNT)
	                        .subscribe(Boolean.TRUE)
	                        .build())
	                .collect(Collectors.toList());
		 
		 if (!newEnrollments.isEmpty()) {
	            enrollRepository.saveAll(newEnrollments);
	            List<String> emailAddresses = newEnrollments.stream()
	                    .map(Enroll::getTo)
	                    .collect(Collectors.toList());
	            
	            try {
	                String json = objectMapper.writeValueAsString(emailAddresses);
	                log.info("Recipients enrolled: {}", json);
	            } catch (Exception e) {
	                log.error("Error serializing new enrollments to JSON", e);
	            }
	            log.info("{} new recipients enrolled.", newEnrollments.size());
	        } else {
	            log.info("No new recipients to enroll.");
	        }
	}

	public String getSalutationValue(Recipient recipient) {
		String salutation = AppConstants.DEFAULT_SALUTATION;
		if (recipient.getSalutation() != null && !(recipient.getSalutation().isBlank())) {
			salutation = recipient.getSalutation();
		}
		return salutation;
	}

	public String getEmailContent(String salutation) throws IOException {
		String template = new String(
				Files.readAllBytes(Paths.get(new ClassPathResource("email-template.html").getURI())));
		return template.replace("{{salutation}}", salutation);
	}
	
	/**
	 * 
	 * @param recipients
	 * @return
	 * filters and removes duplicate recipient based on mailId's
	 */
	public List<Recipient> filterUniqueReceipient(List<Recipient> recipients) {
		List<Recipient> uniqueRecipients = new ArrayList<>();
		Set<String> unprocessedEmailSet = new HashSet<>();
		for (Recipient recipient : recipients) {
			if (unprocessedEmailSet.add(recipient.getEmail())) {
				uniqueRecipients.add(recipient);
			}
		}
		return uniqueRecipients;
	}
	
	public void updateCounterForRecipient(Recipient recipient) {
		try {
			Enroll enroll = getEnrollByEmail(recipient.getEmail());
			if (enroll != null) {
				Integer count = enroll.getCount() != null ? enroll.getCount() : 0;
				enroll.setCount(count + 1);
				enrollRepository.save(enroll);
				log.info("Updated count for recipient: {}", recipient.getEmail());
			}
		} catch (NoSuchElementException e) {
			log.info("Error: {}", e.getMessage());
		}
	}

	public Enroll getEnrollByEmail(String email) {
		return enrollRepository.findByTo(email)
				.orElseThrow(() -> new NoSuchElementException("No enrollment found for email: " + email));
	}
}
